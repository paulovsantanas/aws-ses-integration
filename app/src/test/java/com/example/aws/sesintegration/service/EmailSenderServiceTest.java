package com.example.aws.sesintegration.service;

import com.example.aws.sesintegration.communication.command.EmailSendRequestCommand;
import com.example.aws.sesintegration.helper.ResourceFileGetter;
import com.example.aws.sesintegration.service.email.EmailService;
import com.example.aws.sesintegration.service.email.senders.AWSEmailSenderService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EmailSenderServiceTest implements ResourceFileGetter {

	@Spy
	AWSEmailSenderService awsEmailSenderServiceSpy;

	@InjectMocks
	EmailService emailService;

	@Captor
	ArgumentCaptor<String> bodyHTMLCaptor;

	@Captor
	ArgumentCaptor<List<File>> fileCaptor;

	@Test
	void deveEnviarEmailCorretamente() {
		doNothing().when(awsEmailSenderServiceSpy).sendEmailRequest(ArgumentMatchers.isA(SendEmailRequest.class));

		emailService.send(new EmailSendRequestCommand(
				"paulo@mail.com",
				List.of("juarez@gmail.com", "joaquim@hotmail.com"),
				"Greetings",
				"html/saudacoes.html",
				Map.of("nome", "Zezin", "situacao", "bom")
		));

		verify(awsEmailSenderServiceSpy, times(1)).sendEmailRequest(ArgumentMatchers.isA(SendEmailRequest.class));
		verify(awsEmailSenderServiceSpy).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), bodyHTMLCaptor.capture());

		assertEquals("""
				<!doctype html>
				<html lang="en">
				<head>
				\t<meta charset="UTF-8">
				\t<title>Document</title>
				</head>
				<body>
				<h1>Olá Zezin!</h1>
				<p>Você está bom?</p>
				</body>
				</html>""", bodyHTMLCaptor.getValue().trim());
	}

	@Test
	void deveEnviarEmailComAnexoCorretamente() {
		doNothing().when(awsEmailSenderServiceSpy).sendEmailRequest(ArgumentMatchers.isA(SendEmailRequest.class));

		EmailSendRequestCommand command = new EmailSendRequestCommand(
				"paulo@mail.com",
				List.of("juarez@gmail.com", "joaquim@hotmail.com"),
				"Greetings",
				"html/saudacoes-com-boleto.html",
				Map.of("nome", "Zezin")
		);

		File annex = null;
		try {
			ClassLoader classLoader = EmailService.class.getClassLoader();
			var url = classLoader.getResource("attachments/pdf-teste.pdf");
			assert url != null;
			annex = new File(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		assert annex != null;

		emailService.send(command, List.of(annex));

		verify(awsEmailSenderServiceSpy, times(1)).sendEmailRequest(ArgumentMatchers.isA(SendEmailRequest.class));
		verify(awsEmailSenderServiceSpy).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), bodyHTMLCaptor.capture(), fileCaptor.capture());

		assertEquals("""
				<!doctype html>
				<html lang="en">
				<head>
				\t<meta charset="UTF-8">
				\t<title>Document</title>
				</head>
				<body>
				<h1>Olá Zezin!</h1>
				<p>Segue o boleto do financiamento do seu veículo: </p>
				</body>
				</html>""", bodyHTMLCaptor.getValue().trim());

		assertEquals("pdf-teste.pdf", fileCaptor.getValue().get(0).getName());
	}

}
