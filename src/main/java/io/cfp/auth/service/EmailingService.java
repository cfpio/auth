/*
 * Copyright (c) 2016 BreizhCamp
 * [http://breizhcamp.org]
 *
 * This file is part of CFP.io.
 *
 * CFP.io is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.cfp.auth.service;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import io.cfp.auth.entity.User;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class EmailingService {

    private final Logger log = LoggerFactory.getLogger(EmailingService.class);
 
    @Value("${cfp.app.hostname}")
    private String hostname;
    
    @Value("${cfp.email.emailsender}")
    private String emailSender;

    @Value("${cfp.email.sendgrid.apikey}")
    private String sendgridApiKey;

    @Value("${cfp.email.send}")
    private boolean send;

    @Autowired
    private VelocityEngine velocityEngine;
  

    @Async
    public void sendEmailValidation(User user, Locale locale) {
        log.debug("Sending email validation e-mail to '{}'", user.getEmail());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);

        createAndSendEmail("verify.html", user.getEmail(), parameters, locale);
    }
    
    public void createAndSendEmail(String template, String email, Map<String,Object> parameters, Locale locale) {
    	String templatePath = getTemplatePath(template, locale);

        String content = processTemplate(templatePath, parameters);
        String subject = (String) parameters.get("subject");

        sendEmail(email, subject, content, null, null);
    }
    
    public String getTemplatePath(final String emailTemplate, final Locale locale) {
    	String language = locale.getLanguage();
    	if (!"fr".equals(language)) {
    		language = "en";
    	}
        return "mails/" + language + "/" + emailTemplate;
    }
    
    public String processTemplate(String templatePath, Map<String, Object> parameters) {
        
        // adds global params
        parameters.put("hostname", hostname);
        
        VelocityContext context = new VelocityContext(parameters);

        Template template = velocityEngine.getTemplate(templatePath, "UTF-8");

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    public void sendEmail(String to, String subject, String content, List<String> cc, List<String> bcc) {
        if (!send) {
            log.info("Mail '{}' to {} not actually sent as mail service is disabled by configuration.", subject, to);
            return;
        }

        SendGrid sendgrid = new SendGrid(sendgridApiKey);

        SendGrid.Email email = new SendGrid.Email();

        email.setFrom(emailSender)
            .setFromName("CFP.io")
            .setReplyTo("no-reply@cfp.io")
            .addTo(to)
            .setSubject(subject)
            .setHtml(content);
        if (cc != null) {
            email.addCc(cc.toArray(new String[cc.size()]));
        }
        if (bcc != null) {
            email.addBcc(bcc.toArray(new String[bcc.size()]));
        }


        try {
            SendGrid.Response response = sendgrid.send(email);
            log.debug("Sent e-mail to User '{}' with status {}", to, response.getStatus());
        } catch (SendGridException e) {
            log.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
        }
    }
}
