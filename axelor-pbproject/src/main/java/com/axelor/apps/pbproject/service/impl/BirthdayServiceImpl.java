package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.base.db.ICalendarEvent;
import com.axelor.apps.base.db.repo.ICalendarEventRepository;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.repo.PartnerRepository;
import com.axelor.apps.pbproject.service.BirthdayService;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;

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
            LocalDate birthday = partner.getDateOfBirth().withYear(LocalDate.now().getYear());

            ICalendarEvent calendarEvent = new ICalendarEvent();
            calendarEvent.setSubject("День рождения: " + partner.getFullName());
            calendarEvent.setStartDateTime(birthday.atStartOfDay());
            calendarEvent.setEndDateTime(birthday.atStartOfDay().plusHours(24));

            calendarEventRepository.save(calendarEvent);
        }
    }
}
