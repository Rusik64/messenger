package com.example.messenger.service;

import com.example.messenger.repository.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {
    private static String FROM_DEFAULT = "no-reply@mssngr.loc";
    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    public MailService(TemplateEngine templateEngine, JavaMailSender mailSender) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }
    private String build(String template, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(template, context);
    }
    private void sendMail(String from, String to, String  subject, String message) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setText(message, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    public void sendRegistrationMail(User user) {
        Map<String, Object> replaces = new HashMap<>();
        replaces.put("username", user.getUsername());
        replaces.put("token", user.getToken());
        String content = this.build("confirm-email", replaces);
        String subject = "Registration email confirm";
        sendMail(FROM_DEFAULT, user.getEmail(), subject, content);
    }
}
