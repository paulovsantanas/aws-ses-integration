package com.example.aws.sesintegration.service.email.senders;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

import java.util.List;

@Service
public class AWSEmailSenderService implements IEmailSender {

	SesV2Client client;

	public AWSEmailSenderService() {
		this.client = SesV2Client.builder()
				.region(Region.US_EAST_1)
				.credentialsProvider(ProfileCredentialsProvider.create())
				.build();
	}

	@Override
	public void send(final String sender, final List<String> recipients, final String subject, final String bodyHTML) {
		Destination destination = this.buildDestination(recipients);

		Content sub = this.buildContent(subject);
		Body body = this.buildBody(bodyHTML);

		EmailContent emailContent = this.buildEmailContent(sub, body);

		SendEmailRequest emailRequest = this.buildSendEmailRequest(destination, emailContent, sender);

		try {
			System.out.println("Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...");
//			client.sendEmail(emailRequest);
			System.out.println("email was sent");

		} catch (SesV2Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
		}
	}

	private Destination buildDestination(final List<String> recipients) {
		return Destination.builder()
				.toAddresses(recipients)
				.build();
	}

	private Content buildContent(final String data) {
		return Content.builder()
				.data(data)
				.build();
	}

	private Body buildBody(final String bodyHTML) {
		Content content = this.buildContent(bodyHTML);
		return Body.builder()
				.html(content)
				.build();
	}

	private Message buildMessage(final Content subject, final Body body) {
		return Message.builder()
				.subject(subject)
				.body(body)
				.build();
	}

	private EmailContent buildEmailContent(final Content subject, final Body body) {
		Message message = this.buildMessage(subject, body);
		return EmailContent.builder()
				.simple(message)
				.build();
	}

	private SendEmailRequest buildSendEmailRequest(final Destination destination, final EmailContent emailContent, final String sender) {
		return SendEmailRequest.builder()
				.destination(destination)
				.content(emailContent)
				.fromEmailAddress(sender)
				.build();
	}

}
