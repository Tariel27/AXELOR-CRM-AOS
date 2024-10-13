package com.axelor.apps.pbproject.service.impl;

import com.axelor.app.AppSettings;
import com.axelor.apps.pbproject.db.WorkLeave;
import com.axelor.apps.pbproject.db.repo.WorkLeaveRepository;
import com.axelor.apps.pbproject.service.EmailService;
import com.axelor.apps.pbproject.service.strategy.emailStrategy.ApprovedStrategy;
import com.axelor.apps.pbproject.service.strategy.emailStrategy.DeniedStrategy;
import com.axelor.apps.pbproject.service.strategy.emailStrategy.LeaveRequestStrategy;
import com.axelor.apps.pbproject.service.strategy.emailStrategy.WaitingStrategy;
import com.axelor.inject.Beans;
import com.axelor.mail.MailAccount;
import com.axelor.mail.MailSender;
import com.axelor.mail.SmtpAccount;
import com.google.inject.Inject;

public class EmailServiceImpl implements EmailService {
    private static final AppSettings settings = AppSettings.get();
    private final WorkLeaveRepository workLeaveRepository;
    private final MailAccount mailAccount;
    private LeaveRequestStrategy leaveRequestStrategy;

    @Inject
    public EmailServiceImpl(WorkLeaveRepository workLeaveRepository) {
        this.workLeaveRepository = workLeaveRepository;
        this.mailAccount = createSmtpAccount();
    }

    @Override
    public void sendEmail(Long leaveRequestId) {
        WorkLeave workLeave = workLeaveRepository.find(leaveRequestId);

        if  (workLeave.getLeaveRequestStatus().equalsIgnoreCase("waiting")){
            leaveRequestStrategy = Beans.get(WaitingStrategy.class);
        } else if (workLeave.getLeaveRequestStatus().equalsIgnoreCase("approved")) {
            leaveRequestStrategy = Beans.get(ApprovedStrategy.class);
        } else {
            leaveRequestStrategy = Beans.get(DeniedStrategy.class);
        }

        leaveRequestStrategy.handleRequest(workLeave,new MailSender(mailAccount));
    }
    private MailAccount createSmtpAccount() {
        return new SmtpAccount(
                settings.get("mail.smtp.host"),
                "587",
                settings.get("mail.smtp.user"),
                settings.get("mail.smtp.password"),
                "starttls",
                settings.get("mail.smtp.user")
        );
    }
}
