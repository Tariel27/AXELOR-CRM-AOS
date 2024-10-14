package com.axelor.apps.pbproject.service.strategy.emailStrategy;

import com.axelor.app.AppSettings;
import com.axelor.apps.pbproject.db.WorkLeave;
import com.axelor.mail.MailSender;

import javax.mail.MessagingException;
import java.time.format.DateTimeFormatter;

public class WaitingStrategy  implements LeaveRequestStrategy{
    private static final AppSettings settings = AppSettings.get();

    @Override
    public void handleRequest(WorkLeave workLeave, MailSender mailSender) {
        String content = "Статус запроса: " + "Ожидание" + "\n" +
                "Кто хочет отпросится: " + workLeave.getRequester().getFullName() + "\n" +
                "Кому: " + workLeave.getApprover().getFullName() + "\n" +
                "Дата создания заявки: " + workLeave.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n" +
                "Причина отсутствия: " + workLeave.getReason() + "\n" +
                "Дата начала отсутствия: " + workLeave.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n" +
                "Дата окончания отсутствия: " + workLeave.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n";

        String recipientAddresses = settings.get("accounts.for.send.message");
        if (workLeave.getApprover().getEmail() != null){
            recipientAddresses += "/" + workLeave.getApprover().getEmail();
        }
        String[] recipients = recipientAddresses.split("/");

        try {
            mailSender.send("PetaPlan сотрудник хочет отпросится", content, recipients);
        } catch (MessagingException e){
            System.err.println("Error sending email: " + e.getMessage());
        }

    }
}
