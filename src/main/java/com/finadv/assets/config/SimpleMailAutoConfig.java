package com.finadv.assets.config;

import org.springframework.cloud.aws.mail.simplemail.SimpleEmailServiceJavaMailSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;

/**
 * @author atanu
 *
 */
@Configuration
public class SimpleMailAutoConfig {

	@Bean
	public AmazonSimpleEmailService amazonSimpleEmailService() {
		return AmazonSimpleEmailServiceClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(
						new BasicAWSCredentials("AKIA4IRV3QFGL5KVZ6UO", "EuMJPM8sWXo7ljxR8YPRfoMhcBi/FdnB/V+PNkaa")))
				.withRegion(Regions.US_EAST_2).build();

	}

	@Bean
	public JavaMailSender mailSender(AmazonSimpleEmailService amazonSimpleEmailService) {
		
	        return new SimpleEmailServiceJavaMailSender(amazonSimpleEmailService);
	    }
	
		/*
		 * @Component public class EmailServiceImpl implements EmailService {
		 * 
		 * @Autowired private JavaMailSender emailSender;
		 * 
		 * public void sendSimpleMessage( String to, String subject, String text) { ...
		 * SimpleMailMessage message = new SimpleMailMessage();
		 * message.setFrom("noreply@baeldung.com"); message.setTo(to);
		 * message.setSubject(subject); message.setText(text);
		 * emailSender.send(message); ... } }
		 */
}
