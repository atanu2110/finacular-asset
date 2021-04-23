package com.finadv.assets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * @author atanu
 *
 */
@Configuration
public class AmazonConfig {

	@Bean
	public AmazonS3 amazonS3Client() {
		AWSCredentials awsCredentials = new BasicAWSCredentials("AKIA4IRV3QFGHQEDC342",
				"CH+9aXko34k2rG87cX0J5QHk7TqFpfxux84zF/33");
		return AmazonS3ClientBuilder.standard().withRegion("ap-south-1")
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();

	}
}
