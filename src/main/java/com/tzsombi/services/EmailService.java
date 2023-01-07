package com.tzsombi.services;

import com.tzsombi.model.Todo;
import com.tzsombi.model.User;
import com.tzsombi.utils.Constants;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Service
public class EmailService implements IEmailService {

    @Override
    public void sendEmail(User user, Todo todo) throws MessagingException {
        Properties props = setProperties();

        Session session = Session.getDefaultInstance(props,
                new jakarta.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                System.getenv("EMAIL_ADDRESS"),
                                System.getenv("EMAIL_PASSWORD"));
                    }
                });

        MimeMessage message = composeMessage(session, user, todo);

        Transport transport = session.getTransport();
        transport.connect();
        Transport.send(message);
        transport.close();
    }

    private static Properties setProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.starttls.required","true");
        props.put("mail.smtp.host","smtp.gmail.com");
        props.put("mail.smtp.ssl.trust","smtp.gmail.com");
        props.put("mail.smtp.port","465");
        props.put("mail.smtp.socketFactory.port","465");
        props.put("mail.smtp.debug","true");
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        return props;
    }
    private MimeMessage composeMessage(Session session, User user, Todo todo) throws MessagingException {
        MimeMessage message = new MimeMessage(session);

        InternetAddress addressFrom = new InternetAddress(System.getenv("EMAIL_ADDRESS"));
        message.setSender(addressFrom);
        message.setSubject(String.format(Constants.EMAIL_SUBJECT, todo.getTitle()));
        message.setContent(String.format(Constants.EMAIL_MESSAGE, user.getName(),
                todo.getDueDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))), "text/html");
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));

        return message;
    }
}
