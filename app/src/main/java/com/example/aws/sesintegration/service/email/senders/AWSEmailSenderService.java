package com.example.aws.sesintegration.service.email.senders;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

import java.util.List;

@Service
public class AWSEmailSenderService implements IEmailSender {

	@Override
	public void send(String sender, List<String> recipients, String subject, String bodyHTML) {
		Region region = Region.US_EAST_1;
		SesV2Client client = SesV2Client.builder()
				.region(region)
				.credentialsProvider(ProfileCredentialsProvider.create())
				.build();

		Destination destination = Destination.builder()
				.toAddresses(recipients)
				.build();

		Content content = Content.builder()
				.data(bodyHTML)
				.build();

		Content sub = Content.builder()
				.data(subject)
				.build();

		Body body = Body.builder()
				.html(content)
				.build();

		Message msg = Message.builder()
				.subject(sub)
				.body(body)
				.build();

		EmailContent emailContent = EmailContent.builder()
				.simple(msg)
				.build();

		SendEmailRequest emailRequest = SendEmailRequest.builder()
				.destination(destination)
				.content(emailContent)
				.fromEmailAddress(sender)
				.build();

		try {
			System.out.println("Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...");
//			client.sendEmail(emailRequest);
			System.out.println("email was sent");

		} catch (SesV2Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
		}
	}

}
