package com.lepse.email_sender.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class EmailSender {

    @Getter
    private final String host;
    @Getter
    private final String port;
    @Getter
    private final String auth;
    @Getter
    private final String starttls;

    @Getter
    @Setter
    private Date date;

    @Getter
    @Value("${user.to.default1}")
    private String userDefault1;
    @Getter
    @Value("${user.to.default2}")
    private String userDefault2;

    private final String userFrom = "Teamcenter";

    private String text = "Список уволившихся активных пользователей на ";
    private Message message = null;

    /**
     * A class for generating and sending emails
     * @param host Mail server address
     * @param port Mail server port
     * @param auth Need for authorization
     * @param starttls The need for encryption
     * */
    public EmailSender(String host, String port, String auth, String starttls) {
        this.host = host;
        this.port = port;
        this.auth = auth;
        this.starttls = starttls;
    }

    /**
     * Creating and sending emails
     * @param filePath Path to the .pdf file
     * @param user The ID of the recipient of the message. Null for the default user
     * */
    public void sendEmail(String filePath, String user) {
        try {
            Session session = getSession();
            session.setDebug(false);

            user = user == null ? userDefault1 : user;

            InternetAddress emailFrom = new InternetAddress(userFrom);
            InternetAddress emailTo = new InternetAddress(user);

            message = new MimeMessage(session);

            message.setFrom(emailFrom);
            message.setRecipient(Message.RecipientType.TO, emailTo);
            message.setSubject("Список уволившихся активных пользователей");

            // Message content
            Multipart multipart = new MimeMultipart();

            // Message text
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(text + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date), "text/plain; charset=utf-8");
            multipart.addBodyPart(bodyPart);

            // Attaching a file to a message
            if (filePath != null) {
                MimeBodyPart mbr = createFileAttachment(filePath);
                multipart.addBodyPart(mbr);
            }

            message.setContent(multipart); // Defining message content
            Transport.send(message); // Sending a message
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Receiving a session without authorization to send an email
     * @return Session
     */
    private Session getSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", auth);
        properties.put("mail.smtp.starttls.enable", starttls);

        return Session.getInstance(properties, null);
    }

    /**
     * Creating a file attachment
     * @param filePath file path
     * @return MimeBodyPart
     */
    private MimeBodyPart createFileAttachment(String filePath) throws MessagingException {
        MimeBodyPart mbp = new MimeBodyPart();
        FileDataSource fds = new FileDataSource(filePath);
        mbp.setDataHandler(new DataHandler(fds));
        mbp.setFileName(fds.getName());
        return mbp;
    }
}
