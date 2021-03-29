package com.finadv.assets.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;

public class GoogleAPIService {

	private static final String APPLICATION_NAME = "Finacular - Google Mail API";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	// private static final String TOKENS_DIRECTORY_PATH = "tokens";

	private static final String user = "me";
	static Gmail service = null;
	/*
	 * private static File credentialFilePath = new File(
	 * System.getProperty(("user.dir") +
	 * "/src/main/resources/google_credential.json"));
	 */

	public static void main(String[] args) {

		try {

			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream in = classloader.getResourceAsStream("google_credential.json");// Read google_credential.json

			// InputStream in = new FileInputStream(credentialFilePath);
			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

			// Credential builder
			Credential authorize = new GoogleCredential.Builder()
					.setTransport(GoogleNetHttpTransport.newTrustedTransport()).setJsonFactory(JSON_FACTORY)
					.setClientSecrets(clientSecrets.getDetails().getClientId().toString(),
							clientSecrets.getDetails().getClientSecret().toString())
					.build()
					.setAccessToken(
							"ya29.a0AfH6SMBN5tgIMGS_Zdaq_KuuWgwM2i17fzqO5hkZr7frHaHXxNRsFRwwjTos9k1IE5ULNZZ9wasqiY4E98zXZzhXRBjmIj43AGejwekJ2RSJIJQlWUAwfO87ObHgNoHQvtdeq4NOB9KNUEzFso51TXvJyokBlFhHUbUguiiV8vI")
					.setRefreshToken(
							"1//0gZFOH795IgW4CgYIARAAGBASNwF-L9Ir0tPi13jgbyEkJY3OHWRvvOCzO2nzfr8fSUkJg6PIVDSjaOyRyMBu0D9hWkaQrke6eck");

			// Create gmail service
			final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, authorize)
					.setApplicationName(APPLICATION_NAME).build();

			// Access gmail inbox
			Gmail.Users.Messages.List request = service.users().messages().list(user)
					.setQ("from: " + "dude.atanu@gmail.com");

			ListMessagesResponse messageResponse = request.execute();
			request.setPageToken(messageResponse.getNextPageToken());

			// Get ID of the email you are lokking for
			String messageId = messageResponse.getMessages().get(0).getId();

			Message message = service.users().messages().get(user, messageId).execute();

			// Print email body
			if (message.getPayload().getMimeType().equals("text/plain")) {
				String emailBody = StringUtils
						.newStringUtf8(Base64.decodeBase64(message.getPayload().getBody().getData()));
				System.out.println(emailBody);
			}else {
				List<MessagePart> parts = message.getPayload().getParts();
				if (parts != null && parts.size() > 0)
                {
                     for(MessagePart part : parts)
                    {
                        if (part.getMimeType().equals("text/html"))
                        {
                        	String emailBody = StringUtils
            						.newStringUtf8(Base64.decodeBase64(part.getBody().getData()));
            				System.out.println(emailBody);                       
                    } else if(part.getMimeType().equals("application/pdf")) {
                    	System.out.println("File name is : " + part.getFilename());
                    }
                }
			}
			
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}

	}
}
