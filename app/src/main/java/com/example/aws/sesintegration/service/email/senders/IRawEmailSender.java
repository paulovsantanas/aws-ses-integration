package com.example.aws.sesintegration.service.email.senders;

import java.util.List;

@FunctionalInterface
public interface IRawEmailSender {

	void send(String sender, List<String> recipients, String subject, String bodyHTML);

}
