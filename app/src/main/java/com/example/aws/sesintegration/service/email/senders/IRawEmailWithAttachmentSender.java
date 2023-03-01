package com.example.aws.sesintegration.service.email.senders;

import java.io.File;
import java.util.List;

@FunctionalInterface
public interface IRawEmailWithAttachmentSender {

	void send(String sender, List<String> recipients, String subject, String bodyHTML, List<File> attachments);

}
