package com.example.aws.sesintegration.service.email.senders;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;

@Service
public class AWSEmailSenderService implements IRawEmailSender, IRawEmailWithAttachmentSender {

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

		SendEmailRequest emailRequest = this.buildSendEmailRequest(emailContent, sender, destination);

		try {
			System.out.println("Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...");
//			client.sendEmail(emailRequest);
			System.out.println("email was sent");
		} catch (SesV2Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
		}
	}

	@Override
	public void send(String sender, List<String> recipients, String subject, String bodyHTML, List<File> attachments) {
		try {
			MimeMessage message = this.buildMimeMessage(sender, recipients, subject);
			MimeMultipart msg = new MimeMultipart("mixed");
			message.setContent(msg);

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(bodyHTML, "text/html; charset=UTF-8");
			msg.addBodyPart(htmlPart);

			for (File file : attachments) {
				msg.addBodyPart(this.buildMimeBodyPart(file));
			}

			System.out.println("Attempting to send an email through Amazon SES using the AWS SDK for Java...");
			RawMessage rawMessage = buildRawMessage(message);
			EmailContent emailContent = buildEmailContent(rawMessage);
			SendEmailRequest request = this.buildSendEmailRequest(emailContent);

//			client.sendEmail(request);
			System.out.println("The email message was successfully sent with an attachment");
		} catch (SesV2Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
		} catch (MessagingException | IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private RawMessage buildRawMessage(final MimeMessage message) throws MessagingException, IOException {
		byte[] arr = getBytes(message);
		SdkBytes data = SdkBytes.fromByteArray(arr);
		return RawMessage.builder()
				.data(data)
				.build();
	}

	private byte[] getBytes(final MimeMessage message) throws MessagingException, IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		message.writeTo(outputStream);

		ByteBuffer buf = ByteBuffer.wrap(outputStream.toByteArray());
		byte[] arr = new byte[buf.remaining()];
		buf.get(arr);
		return arr;
	}

	private MimeBodyPart buildMimeBodyPart(final File file) throws MessagingException, IOException {
		MimeBodyPart att = new MimeBodyPart();
		byte[] fileContent = Files.readAllBytes(file.toPath());
		DataSource fds = new ByteArrayDataSource(fileContent, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // TODO get file mime
		att.setDataHandler(new DataHandler(fds));
		String reportName = "WorkReport.xls"; // TODO get file name
		att.setFileName(reportName);

		return att;
	}

	private MimeMessage buildMimeMessage(final String sender, final List<String> recipients, final String subject) throws MessagingException {
		Session session = Session.getDefaultInstance(new Properties());

		MimeMessage message = new MimeMessage(session);
		message.setSubject(subject, "UTF-8");
		message.setFrom(new InternetAddress(sender));
		Address[] addresses = InternetAddress.parse(String.join(",", recipients));
		message.setRecipients(javax.mail.Message.RecipientType.TO, addresses);
		return message;
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

	private EmailContent buildEmailContent(final RawMessage rawMessage) {
		return EmailContent.builder()
				.raw(rawMessage)
				.build();
	}

	private SendEmailRequest buildSendEmailRequest(final EmailContent emailContent, final String sender, final Destination destination) {
		return SendEmailRequest.builder()
				.destination(destination)
				.content(emailContent)
				.fromEmailAddress(sender)
				.build();
	}

	private SendEmailRequest buildSendEmailRequest(final EmailContent emailContent) {
		return SendEmailRequest.builder()
				.content(emailContent)
				.build();
	}

}
