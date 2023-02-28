package com.example.aws.sesintegration.service;

import com.example.aws.sesintegration.communication.command.EmailSendRequestCommand;
import com.example.aws.sesintegration.service.email.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EmailSenderServiceTest {

	@Autowired
	EmailService emailSenderService;

	@Test
	void deveEnviarEmailCorretamente() {
		String mensagem = emailSenderService.sendEmail(new EmailSendRequestCommand(
				"paulo@mail.com",
				List.of("juarez@gmail.com", "joaquim@hotmail.com"),
				"Greetings",
				"html/saudacoes.html",
				Map.of("nome", "Zezin", "situacao", "bom")
		));
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
				</html>""", mensagem.trim());
	}

}
