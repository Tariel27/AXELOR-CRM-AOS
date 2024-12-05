package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.base.db.ICalendarEvent;
import com.axelor.apps.base.db.repo.ICalendarEventRepository;
import com.axelor.apps.base.service.exception.TraceBackService;
import com.axelor.apps.pbproject.db.Holiday;
import com.axelor.apps.pbproject.db.repo.HolidayRepository;
import com.axelor.apps.pbproject.service.HolidayService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;

public class HolidayServiceImpl implements HolidayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HolidayServiceImpl.class);
    private final HolidayRepository holidayRepository;
    private final ICalendarEventRepository calendarEventRepository;
    private static final String API_URL = "https://calendarific.com/api/v2/holidays?api_key=MAOu6Qy1LKHFMsUbOCp93P5ZLIyvDq88&country=KG";

    @Inject
    public HolidayServiceImpl(HolidayRepository holidayRepository, ICalendarEventRepository calendarEventRepository) {
        this.holidayRepository = holidayRepository;
        this.calendarEventRepository = calendarEventRepository;
    }

    @Override
    @Transactional
    public void fetchHolidaysFromApi() {
        LocalDate localDate = LocalDate.now(ZoneId.of("UTC+06:00"));
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_URL + "&year=" + localDate.getYear());
            String response = EntityUtils.toString(httpClient.execute(request).getEntity());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode holidays = jsonResponse.path("response").path("holidays");

            for (JsonNode holidayNode : holidays) {
                Holiday holiday = new Holiday();
                holiday.setHolidayName(holidayNode.path("name").asText());
                holiday.setDescription(holidayNode.path("description").asText(""));
                holiday.setIsPublic(true);

                String dateText = holidayNode.path("date").path("iso").asText();
                LocalDate holidayDate;
                try {
                    holidayDate = dateText.length() > 10 ? LocalDate.parse(dateText.substring(0, 10)) : LocalDate.parse(dateText);
                    holiday.setHolidayDate(holidayDate);
                } catch (DateTimeParseException e) {
                    LOGGER.warn("Unable to recognize the date: {}", dateText);
                    continue;
                }

                LOGGER.debug("Saving holiday: {} on {}", holiday.getHolidayName(),holiday.getHolidayDate());
                holidayRepository.save(holiday);
            }
        } catch (Exception e) {
            TraceBackService.trace(e);
        }
    }

    @Override
    @Transactional
    public void addHolidaysToCalendar() {
        LocalDate localDate = LocalDate.now(ZoneId.of("UTC+06:00"));

        List<Holiday> holidays = holidayRepository.all()
                .filter("YEAR(self.holidayDate) = :currentYear")
                .bind("currentYear", localDate.getYear())
                .fetch();

        for (Holiday holiday : holidays) {
            // Проверка, существует ли событие в календаре
            LocalDate holidayDate = holiday.getHolidayDate();
            boolean eventExists = calendarEventRepository.all()
                    .filter("self.subject like :subject AND DATE(self.startDateTime) = :holidayDate")
                    .bind("subject", "%" + holiday.getHolidayName() + "%")
                    .bind("holidayDate", holidayDate)
                    .fetchOne() != null;

            if (!eventExists) {
                ICalendarEvent calendarEvent = getCalendarEvent(holiday, holidayDate);

                calendarEventRepository.save(calendarEvent);
            }
        }
    }

    private static ICalendarEvent getCalendarEvent(Holiday holiday, LocalDate holidayDate) {
        ICalendarEvent calendarEvent = new ICalendarEvent();
        calendarEvent.setSubject(holiday.getHolidayName());

        calendarEvent.setStartDateTime(
                LocalDateTime.of(
                        holidayDate.getYear(),
                        holidayDate.getMonth(),
                        holidayDate.getDayOfMonth(), 0,0
                )
        );

        LocalDate plussedDay = holidayDate.plusDays(1);
        calendarEvent.setEndDateTime(LocalDateTime.of(
                plussedDay.getYear(),
                plussedDay.getMonth(),
                plussedDay.getDayOfMonth(), 0,0
        ));
        return calendarEvent;
    }
}
