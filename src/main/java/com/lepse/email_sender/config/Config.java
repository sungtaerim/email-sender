package com.lepse.email_sender.config;

import com.lepse.email_sender.service.EmailSender;
import com.lepse.email_sender.service.PdfCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@PropertySource({"classpath:application.properties"})
@EnableScheduling
public class Config {
    final Environment environment;

    @Autowired
    public Config(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public EmailSender emailSenderConfig() {
        String host = environment.getProperty("host");
        String port = environment.getProperty("port");
        String auth = environment.getProperty("auth");
        String starttls = environment.getProperty("starttls.enable");

        return new EmailSender(host, port, auth, starttls);
    }

    @Bean
    public PdfCreator pdfCreatorConfig() {
        String filePath = environment.getProperty("file.path");

        return new PdfCreator(filePath);
    }
}
