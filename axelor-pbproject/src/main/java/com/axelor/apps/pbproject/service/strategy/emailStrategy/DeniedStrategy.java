package com.axelor.apps.pbproject.service.strategy.emailStrategy;

import com.axelor.app.AppSettings;
import com.axelor.apps.pbproject.db.WorkLeave;
import com.axelor.mail.MailAccount;
import com.axelor.mail.MailSender;

import javax.mail.MessagingException;
import java.time.format.DateTimeFormatter;

public class DeniedStrategy implements LeaveRequestStrategy{
    private static final AppSettings settings = AppSettings.get();
    @Override
    public void handleRequest(WorkLeave workLeave, MailSender mailSender) {
        String content = "Статус запроса: " + "Отклонено" + "\n" +
                "Кто хотел отпросится: " + workLeave.getRequester().getFullName() + "\n" +
                "Кем отклонено: " + workLeave.getApprover().getFullName() + "\n" +
                "Дата начала отсутствия: " + workLeave.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n" +
                "Дата окончания отсутствия: " + workLeave.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n" +
                "Отклонил причину: " + workLeave.getReason() + "\n\n";

        if (workLeave.getApproverComment() != null){
            content += "Комментарий отклонителя: " + workLeave.getApproverComment();
        }

        String recipientAddresses = settings.get("accounts.for.send.message");
        if (workLeave.getRequester().getEmail() != null){
            recipientAddresses += "/" + workLeave.getRequester().getEmail();
        }
        String[] recipients = recipientAddresses.split("/");

        try {
            mailSender.send("PetaPlan ответ на отпрос от работы", content, recipients);
        } catch (MessagingException e){
            System.err.println("Error sending email: " + e.getMessage());
        }

    }
}
