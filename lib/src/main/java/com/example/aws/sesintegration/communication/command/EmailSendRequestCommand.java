package com.example.aws.sesintegration.communication.command;

import java.util.List;
import java.util.Map;

public record EmailSendRequestCommand(
		String sender,
		List<String> recipients,
		String subject,
		String templatePath,
		Map<String, String> templateData
) {
}
