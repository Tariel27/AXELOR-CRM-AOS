package com.axelor.apps.pbproject.service.strategy.emailStrategy;

import com.axelor.apps.pbproject.db.WorkLeave;
import com.axelor.mail.MailSender;

public interface LeaveRequestStrategy {
    void handleRequest(WorkLeave workLeave, MailSender mailSender);
}
