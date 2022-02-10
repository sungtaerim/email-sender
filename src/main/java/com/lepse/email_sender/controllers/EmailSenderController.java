package com.lepse.email_sender.controllers;

import com.lepse.email_sender.models.EmplModel;
import com.lepse.email_sender.service.EmailSender;
import com.lepse.email_sender.service.PdfCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;

@RestController
public class EmailSenderController {

    private static final Logger logger = LoggerFactory.getLogger(EmailSenderController.class);

    private final EmailSender emailSender;
    private final PdfCreator pdfCreator;
    private final String active = "0";

    @Value("${data.url.notactive}")
    private String urlNotActive;
    @Value("${data.url.active}")
    private String urlActive;
    @Value("${data.url.all}")
    private String url;

    @Autowired
    public EmailSenderController(EmailSender emailSender, PdfCreator pdfCreator) {
        this.emailSender = emailSender;
        this.pdfCreator = pdfCreator;
    }

    /**
     * Receive data and send email to users by default.
     * Fire at 10.00 every 16 days every month, starting on the first day of the month
     * */
    @Scheduled(cron = "0 0 10 1/16 * *", zone = "Europe/Moscow")
    @GetMapping({"/email"})
    public String sendOnlyActive() {
        this.setDates();
        try {
            RestTemplate restTemplate = new RestTemplate();
            EmplModel result = restTemplate.getForObject(urlActive, EmplModel.class);
            String response = "";
            if (result != null) {
                response += sendEmail(result, emailSender.getUserDefault1()); // email to first recipient
                response += sendEmail(result, emailSender.getUserDefault2()); // email to second recipient
            }
            return response;
        } catch (Exception ex) {
            logger.error(Arrays.toString(ex.getStackTrace()));
        }
        return "Something went wrong. Email not send";
    }

    /**
     * Receiving data and sending an email to a user
     * @param user User id
     * */
    @GetMapping({"/email/{user}"})
    public String sendEmailTo(@PathVariable String user, @RequestParam(required = false) String status) {
        this.setDates();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = this.urlActive;
            if (status != null && !status.equals(active)) {
                url = this.urlNotActive;
            }
            EmplModel result = restTemplate.getForObject(url, EmplModel.class);
            return result != null ? sendEmail(result, user) : "Something went wrong. Email not send";
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return "Something went wrong. Email not send";
    }

    private String sendEmail(EmplModel result, String user) {
        String filePath = pdfCreator.createPdf(result.getEmployee());
        emailSender.sendEmail(filePath, user);
        logger.info("Email send to " + user);
        return "Email send to " + user;
    }

    private void setDates() {
        Date date = new Date();
        pdfCreator.setDate(date);
        emailSender.setDate(date);
    }
}
