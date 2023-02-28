package com.example.aws.sesintegration.helper;

import com.example.aws.sesintegration.service.email.EmailService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public interface ResourceFileGetter {

	default String getTemplate(String templatePath) throws IOException, IllegalArgumentException {
		ClassLoader classLoader = EmailService.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(templatePath);
		if (is == null) {
			throw new IllegalArgumentException(templatePath + " não encontrado");
		}

		return this.readFromInputStream(is);
	}

	private String readFromInputStream(InputStream inputStream) throws IOException {
		StringBuilder resultStringBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				resultStringBuilder.append(line).append("\n");
			}
		}
		return resultStringBuilder.toString();
	}

}
