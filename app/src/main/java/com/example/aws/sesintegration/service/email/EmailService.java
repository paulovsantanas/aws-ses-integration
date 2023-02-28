package com.example.aws.sesintegration.service.email;

import com.example.aws.sesintegration.communication.command.EmailSendRequestCommand;
import com.example.aws.sesintegration.helper.ResourceFileGetter;
import com.example.aws.sesintegration.service.email.senders.AWSEmailSenderService;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements ResourceFileGetter {

	@Autowired
	AWSEmailSenderService emailSenderService;

	public String sendEmail(EmailSendRequestCommand command) {
		try {
			String template = this.getTemplate("templates/" + command.templatePath());
			String result = StringSubstitutor.replace(template, command.templateData(), "${", "}");
			System.out.println(result);
			this.emailSenderService.send(command.sender(), command.recipients(), command.subject(), result);
			return result;
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return null;
	}

}
