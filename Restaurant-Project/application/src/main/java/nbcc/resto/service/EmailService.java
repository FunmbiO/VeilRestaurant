package nbcc.resto.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${veil.mail.from}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAdminEditNotification(String toEmail, String creatorUsername,
                                          String adminUsername, String contentType,
                                          String contentName, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("[Veil] Your " + contentType + " was edited by an admin");
        message.setText(
                "Hello " + creatorUsername + ",\n\n" +
                        "An administrator has edited your " + contentType + ": \"" + contentName + "\".\n\n" +
                        "Reason provided by admin (" + adminUsername + "):\n" +
                        reason + "\n\n" +
                        "If you have any questions, please contact the administrator.\n\n" +
                        "— Veil"
        );
        mailSender.send(message);
    }

    public void sendAdminDeleteNotification(String toEmail, String creatorUsername,
                                            String adminUsername, String contentType,
                                            String contentName, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("[Veil] Your " + contentType + " was deleted by an admin");
        message.setText(
                "Hello " + creatorUsername + ",\n\n" +
                        "An administrator has deleted your " + contentType + ": \"" + contentName + "\".\n\n" +
                        "Reason provided by admin (" + adminUsername + "):\n" +
                        reason + "\n\n" +
                        "If you have any questions, please contact the administrator.\n\n" +
                        "— Veil"
        );
        mailSender.send(message);
    }
}
