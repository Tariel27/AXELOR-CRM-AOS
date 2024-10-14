package com.axelor.apps.pbproject.service.strategy.emailStrategy;

import com.axelor.app.AppSettings;
import com.axelor.apps.pbproject.db.WorkLeave;
import com.axelor.mail.MailSender;

import javax.mail.MessagingException;
import java.time.format.DateTimeFormatter;

public class ApprovedStrategy implements LeaveRequestStrategy{
    private static final AppSettings settings = AppSettings.get();

    @Override
    public void handleRequest(WorkLeave workLeave, MailSender mailSender) {
        String content = "Статус запроса: " + "Одобрено" + "\n" +
                "Кто хотел отпросится: " + workLeave.getRequester().getFullName() + "\n" +
                "Кем одобрено: " + workLeave.getApprover().getFullName() + "\n" +
                "Одобрил причину: " + workLeave.getReason() + "\n" +
                "Дата начала отсутствия: " + workLeave.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n" +
                "Дата окончания отсутствия: " + workLeave.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n";

        if (workLeave.getApproverComment() != null){
            content += "Комментарий: " + workLeave.getApproverComment();
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
