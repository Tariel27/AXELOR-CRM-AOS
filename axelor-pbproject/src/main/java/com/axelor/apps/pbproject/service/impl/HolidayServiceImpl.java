package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.base.db.ICalendarEvent;
import com.axelor.apps.base.db.repo.ICalendarEventRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;
    private final ICalendarEventRepository calendarEventRepository;
    private static final String API_URL = "https://calendarific.com/api/v2/holidays?api_key=MAOu6Qy1LKHFMsUbOCp93P5ZLIyvDq88&country=KG&year=2024";

    @Inject
    public HolidayServiceImpl(HolidayRepository holidayRepository, ICalendarEventRepository calendarEventRepository) {
        this.holidayRepository = holidayRepository;
        this.calendarEventRepository = calendarEventRepository;
    }

    @Override
    @Transactional
    public void fetchHolidaysFromApi() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_URL);
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
                    System.err.println("Не удалось распознать дату: " + dateText);
                    continue;
                }

                System.out.println("Saving holiday: " + holiday.getHolidayName() + " on " + holiday.getHolidayDate());
                holidayRepository.save(holiday);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public void addHolidaysToCalendar() {
        List<Holiday> holidays = holidayRepository.all().fetch();

        for (Holiday holiday : holidays) {
            // Проверка, существует ли событие в календаре
            boolean eventExists = calendarEventRepository.all()
                    .filter("self.subject = :subject AND DATE(self.startDateTime) = :holidayDate")
                    .bind("subject", holiday.getHolidayName())
                    .bind("holidayDate", holiday.getHolidayDate())
                    .fetchOne() != null;

            if (!eventExists) {
                ICalendarEvent calendarEvent = new ICalendarEvent();
                calendarEvent.setSubject(holiday.getHolidayName());

                LocalDateTime startDateTime = holiday.getHolidayDate().atStartOfDay();
                calendarEvent.setStartDateTime(startDateTime);
                calendarEvent.setEndDateTime(startDateTime.plusDays(1));

                calendarEventRepository.save(calendarEvent);
            }
        }
    }
}
