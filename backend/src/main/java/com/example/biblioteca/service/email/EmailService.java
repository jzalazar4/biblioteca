package com.example.biblioteca.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    @Async
    public void sendEmail(
            String nombre,
            String apellido,
            String email,
            String templateName,
            String confirmationUrl
    ) throws MessagingException {
        if (!StringUtils.hasLength(templateName)) {
            templateName = "confirm-email"; // plantilla de confirmar email
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );
        Map<String, Object> mailProperties = new HashMap<>();

        mailProperties.put("nombre", nombre);
        mailProperties.put("apellido", apellido);
        mailProperties.put("email", email);
        mailProperties.put("confirmationUrl", confirmationUrl);

        Context context = new Context();
        context.setVariables(mailProperties);

       // messageHelper.setFrom("bibliotecarg@mailfence.com");
        messageHelper.setFrom("juli.zalazar4@gmail.com");
        messageHelper.setTo(email);
        messageHelper.setSubject("Confirmar email");


        String template = templateEngine.process(templateName, context);
        messageHelper.setText(template, true);

        mailSender.send(mimeMessage);
    }
// EMAIL OLVIDE CONTRASEÑA
    @Async
    public void forgotPasswordEmail(
            String nombre,
            String apellido,
            String email,
            String templateName,
            String resetUrl
    ) throws MessagingException {
        if (!StringUtils.hasLength(templateName)) {
            templateName = "forgot-password";
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );
        Map<String, Object> mailProperties = new HashMap<>();

        mailProperties.put("nombre", nombre);
        mailProperties.put("apellido", apellido);
        mailProperties.put("email", email);
        mailProperties.put("resetUrl", resetUrl);

        Context context = new Context();
        context.setVariables(mailProperties);

       // messageHelper.setFrom("bibliotecarg@mailfence.com");
        messageHelper.setFrom("juli.zalazar4@gmail.com");
        messageHelper.setTo(email);
        messageHelper.setSubject("Olvide mi contraseña");

        // Cargar la plantilla forgot-password
        String template = templateEngine.process(templateName, context);
        messageHelper.setText(template, true);

        mailSender.send(mimeMessage);
    }

  /*  @Async
    public void sendEmail(
            String nombre,
            String apellido,
            String email,
            String templateName,
            String confirmationUrl
    ) throws MessagingException {
        if (!StringUtils.hasLength(templateName)) {
            templateName = "confirm-email";
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );
        Map<String, Object> mailProperties = new HashMap<>();
        mailProperties.put("email", email);
        mailProperties.put("nombre",nombre);
        mailProperties.put("apellido",apellido);
        mailProperties.put("confirmationUrl", confirmationUrl);

        Context context = new Context();
        context.setVariables(mailProperties);

        messageHelper.setFrom("juli.zalazar4@gmail.com");
        messageHelper.setTo(nombre);
        messageHelper.setSubject("Confirmar email");

      //  String template = buildEmail(email, confirmationUrl);
        String template = templateEngine.process(templateName, context);
        messageHelper.setText(template, true);

        mailSender.send(mimeMessage);
    }*/
}