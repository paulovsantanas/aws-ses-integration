package com.example.aws.sesintegration.controller;

import com.example.aws.sesintegration.communication.command.EmailSendRequestCommand;
import com.example.aws.sesintegration.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "mail")
public class EmailController {

	@Autowired
	EmailService emailService;

	@PostMapping(path = "/sendBasicMessage")
	void sendBasicMessage(@RequestBody  EmailSendRequestCommand command) {
		this.emailService.send(command);
	}

}
