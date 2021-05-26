package com.finadv.assets.service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.amazonaws.services.s3.AmazonS3;
import com.finadv.assets.config.AmazonConfig;
import com.finadv.assets.entities.NSDLUserData;
import com.finadv.assets.entities.UserAsset;
import com.finadv.assets.entities.UserAssets;
import com.finadv.assets.entities.UserInvestment;
import com.finadv.assets.entities.UserInvestmentList;
import com.finadv.assets.repository.NSDLUserDataRepository;
import com.finadv.assets.util.AssetUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class AsyncService {

	private static final Logger LOG = LoggerFactory.getLogger(AsyncService.class);

	private NSDLUserDataRepository nsdlUserDataRepository;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private AssetUtil assetUtil;

	@Autowired
	private AmazonS3 amazonS3Client;

	@Autowired
	public void setNsdlUserDataRepository(NSDLUserDataRepository nsdlUserDataRepository) {
		this.nsdlUserDataRepository = nsdlUserDataRepository;
	}

	@Async
	public void saveNSDLData(String userName, String email, String fileName, String password) {

		NSDLUserData nsdlUserData = new NSDLUserData();
		nsdlUserData.setEmail(email);
		nsdlUserData.setFileName(fileName);
		nsdlUserData.setName(userName);
		nsdlUserData.setPassword(password);

		nsdlUserDataRepository.save(nsdlUserData);

	}

	@Async
	public void callSaveInvestment(UserAsset userAsset) {
		LOG.info("Inside async call for saving user investment for userId " + userAsset.getUserId());

		StringBuilder postInvestmentURL = new StringBuilder(assetUtil.getProperty("user.base.url"));
		postInvestmentURL.append(assetUtil.getProperty("user.investment.url.path")).append("/")
				.append(userAsset.getUserId());

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(postInvestmentURL.toString());

		UserInvestmentList userInvestmentList = new UserInvestmentList();

		userInvestmentList.setInvestmentList(createUserInvestment(userAsset.getAssets()));

		HttpEntity<?> entity = new HttpEntity<>(userInvestmentList, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(builder.build().toUri(), entity, String.class);

		LOG.info("API Response for Save Investment call for userId : " + userAsset.getUserId() + " "
				+ response.getStatusCodeValue());
		// private List<UserInvestment> investmentList;

	}

	private List<UserInvestment> createUserInvestment(List<UserAssets> assets) {

		List<UserInvestment> investmentList = new ArrayList<UserInvestment>();
		for (UserAssets ua : assets) {
			UserInvestment ui = new UserInvestment();
			if (ua.getAssetInstrument().getId() == 7l || ua.getAssetInstrument().getId() == 8l) {
				ui.setUserId(ua.getUserId());
				ui.setInvestmentName(ua.getEquityDebtName());
				ui.setInvestmentOn(ua.getEquityDebtName());
				ui.setAccountName(ua.getHolderName());
				ui.setInvestmentDate(Date.from(ua.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
				ui.setAction("BUY");
				ui.setInvestmentAmount(ua.getAmount());
				ui.setUnitPrice(ua.getUnits());
				ui.setInvestmentType(ua.getAssetType().getTypeName());
			} else {
				ui.setUserId(ua.getUserId());
				ui.setInvestmentName(ua.getDetails());
				ui.setInvestmentOn(ua.getDetails());
				ui.setAccountName(ua.getHolderName());
				ui.setInvestmentDate(Date.from(ua.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
				ui.setAction("INVESTED");
				ui.setInvestmentAmount(ua.getAmount());
				if (ua.getMaturityDate() != null)
					ui.setMaturityDate(Date.from(ua.getMaturityDate().atZone(ZoneId.systemDefault()).toInstant()));
				ui.setInvestmentType(ua.getAssetType().getTypeName());
			}

			ui.setCreatedOn(Calendar.getInstance().getTime());
			investmentList.add(ui);
		}

		return investmentList;

	}

	@Async
	public void uploadFile(File uploadedFile, String fileType) {
		LOG.info("Inside uploadFile : Upload file to S3 for record!!");
		// File file = convertMultiPartFileToFile(multipartFile);
		uploadFileToS3bucket(uploadedFile, "finacular-files", fileType);
		// Delete the file
		FileUtils.deleteQuietly(uploadedFile);
	}

	private void uploadFileToS3bucket(File file, String bucketName, String fileType) {
		amazonS3Client.putObject(new PutObjectRequest(bucketName, fileType + "/" + file.getName(), file));

	}

	//@Async
	public void uploadMultipartFile(MultipartFile uploadedFile, String fileType) {
		LOG.info("Inside uploadMultipartFile : Upload file to S3 for record!!");
		File file = convertMultiPartFileToFile(uploadedFile);
		final PutObjectRequest putObjectRequest = new PutObjectRequest("finacular-files", "zerodha/" + file.getName(),
				file);
		amazonS3Client.putObject(putObjectRequest);
		// Delete the file
		FileUtils.deleteQuietly(file);
	}

	//@Async
	private File convertMultiPartFileToFile(MultipartFile file) {
		File convertedFile = new File(file.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
			fos.write(file.getBytes());
		} catch (IOException e) {
			LOG.error("Error converting multipartFile to file", e);
		}
		return convertedFile;
	}

}
