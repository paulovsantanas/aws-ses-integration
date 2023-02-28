package com.example.aws.sesintegration.service.email.senders;

import java.util.List;

public interface IEmailSender {

	void send(String sender, List<String> recipients, String subject, String bodyHTML);

}
