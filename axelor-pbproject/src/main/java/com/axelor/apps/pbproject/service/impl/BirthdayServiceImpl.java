package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.base.db.ICalendarEvent;
import com.axelor.apps.base.db.repo.ICalendarEventRepository;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.repo.PartnerRepository;
import com.axelor.apps.pbproject.service.BirthdayService;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class BirthdayServiceImpl implements BirthdayService {

    private final PartnerRepository partnerRepository;
    private final ICalendarEventRepository calendarEventRepository;

    @Inject
    public BirthdayServiceImpl(PartnerRepository partnerRepository, ICalendarEventRepository calendarEventRepository) {
        this.partnerRepository = partnerRepository;
        this.calendarEventRepository = calendarEventRepository;
    }

    @Override
    @Transactional
    public void addBirthdaysToCalendar() {
        int currentMonth = LocalDate.now().getMonthValue();

        List<Partner> partnersWithBirthdays = partnerRepository.all()
                .filter("MONTH(self.dateOfBirth) = :currentMonth")
                .bind("currentMonth", currentMonth)
                .fetch();

        for (Partner partner : partnersWithBirthdays) {
            if (Objects.isNull(partner.getDateOfBirth())) continue;
            LocalDate birthday = partner.getDateOfBirth().withYear(LocalDate.now().getYear());

            // Проверяем, существует ли уже событие на тот же день для того же партнера
            boolean eventExists = calendarEventRepository.all()
                    .filter("self.subject like :subject AND DATE(self.startDateTime) = :birthday")
                    .bind("subject", "%День рождения%")
                    .bind("birthday", birthday)
                    .fetchOne() != null;

            if (!eventExists) {
                ICalendarEvent calendarEvent = getCalendarEvent(partner, birthday);

                calendarEventRepository.save(calendarEvent);
            }
        }
    }

    private static ICalendarEvent getCalendarEvent(Partner partner, LocalDate birthday) {
        ICalendarEvent calendarEvent = new ICalendarEvent();
        calendarEvent.setSubject("День рождения: " + partner.getSimpleFullName());

        calendarEvent.setStartDateTime(
                LocalDateTime.of(
                        birthday.getYear(),
                        birthday.getMonth(),
                        birthday.getDayOfMonth(), 0,0
                )
        );

        LocalDate plussedDay = birthday.plusDays(1);
        calendarEvent.setEndDateTime(LocalDateTime.of(
                plussedDay.getYear(),
                plussedDay.getMonth(),
                plussedDay.getDayOfMonth(), 0,0
        ));
        return calendarEvent;
    }
}
