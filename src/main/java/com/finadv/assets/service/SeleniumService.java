package com.finadv.assets.service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.finadv.assets.entities.CAMSEmailDB;
import com.finadv.assets.repository.CAMSEmailRepository;

/**
 * @author atanu
 *
 */
@Service
public class SeleniumService {

	private static final Logger LOG = LoggerFactory.getLogger(SeleniumService.class);
	private static final int PASSWORD_LENGTH = 8;
	private static SecureRandom random = new SecureRandom();
	private static final String CHAR_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	private static final String CHAR_UPPERCASE = CHAR_LOWERCASE.toUpperCase();
	private static final String DIGIT = "0123456789";
	
	private CAMSEmailRepository camsEmailRepository;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	 @Autowired
		private SpringTemplateEngine templateEngine;
	
	@Autowired
	public void setCamsEmailRepository(CAMSEmailRepository camsEmailRepository) {
		this.camsEmailRepository = camsEmailRepository;
	}
	
	

	public void triggerCAMSEmail(String email, long userId) {
		// Create PDF password for the user - Atleast 6 chars and 2 digits
		StringBuilder password = new StringBuilder(PASSWORD_LENGTH);	 
		password.append(generateRandomString(CHAR_UPPERCASE, 6));
        // at least 2 digits    
        password.append( generateRandomString(DIGIT, 2));
        LOG.info("Password generated !! ");
		// Create a record in DB for the email triggered
        CAMSEmailDB camsEmailDB = new CAMSEmailDB();
        camsEmailDB.setUserId(userId);
        camsEmailDB.setEmail(email);
        camsEmailDB.setPassword(password.toString());
        camsEmailRepository.save(camsEmailDB);
        LOG.info("Sucessfully saved data to DB !! ");

		// Automate CAMS
        LOG.info("Calling automate request !! ");
		//automateCAMSEmail(email, password.toString());
        
		// Send mail from Finacular with password in body
        LOG.info("Sending mail to user from Finacular server !! ");
        sendEmail(email , password.toString());
	}

	private void automateCAMSEmail(String email, String password) {
		System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
		ChromeOptions chromeOptions = new ChromeOptions();
		// chromeOptions.addArguments("--headless");
		WebDriver driver = new ChromeDriver(chromeOptions);

		LOG.info("Driver created");

		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

		driver.get("https://new.camsonline.com/Investors/Statements/Consolidated-Account-Statement");
		driver.manage().window().maximize();

		// This code will print the page title
		LOG.info("Page title is: " + driver.getTitle());

		/*
		 * driver.findElement(By.xpath(
		 * "//form[@id='formConsolidatedAccountStatement']/div/div/div/div[2]/div/div[3]/label"
		 * )).click();; driver.findElement(By.id("lblemail")).click();
		 * driver.findElement(By.id("txtEmailId")).sendKeys(email);
		 * driver.findElement(By.id("txtPassword")).sendKeys(password);
		 * driver.findElement(By.id("rpsd")).sendKeys(password);
		 * 
		 * WebElement frame = driver.findElement(By.tagName("iframe"));
		 * 
		 * LOG.info("" + frame.getTagName() + frame.getAttribute("name"));
		 * 
		 * driver.switchTo().frame(0);
		 * LOG.info(Boolean.toString(driver.findElement(By.xpath(
		 * "//span[@id='recaptcha-anchor']")).isDisplayed()));
		 * driver.findElement(By.xpath("//span[@id='recaptcha-anchor']")).click();
		 * 
		 * driver.switchTo().defaultContent();
		 * 
		 * 
		 * LOG.info("SUCCESS : " +
		 * Boolean.toString(driver.findElement(By.id("btnsubmit")).isDisplayed()));
		 * driver.findElement(By.id("btnsubmit")).click(); LOG.info("SUCCESS : " +
		 * Boolean.toString(driver.findElement(By.id("pAlertMessage_success")).
		 * isDisplayed())); driver.findElement(By.id("pAlertMessage_success")).click();
		 * driver.findElement(By.id("ModelDone")).click();
		 */

		LOG.info(Boolean.toString(driver.findElement(By.id("mat-radio-2")).isDisplayed()));

		WebElement element = driver.findElement(By.xpath("//mat-radio-button[@id='mat-radio-2']/label/div/div"));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click()", element);

		driver.findElement(By.xpath("//input[@value='PROCEED']")).click();

		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type=text]")));
		driver.findElement(By.cssSelector("input[type=text]")).sendKeys(email);

		WebDriverWait wait1 = new WebDriverWait(driver, 10);
		wait1.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type=password]")));
		driver.findElement(By.cssSelector("input[type=password]")).sendKeys(password);

		wait1.until(ExpectedConditions.visibilityOfElementLocated(By.id("mat-input-3")));
		driver.findElement(By.id("mat-input-3")).sendKeys(password);

		// wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[7]/button")));
		driver.findElement(By.xpath("//div[7]/button")).submit();

		// driver.quit();
	}
	
	// generate a random char[], based on `input`
	private static String generateRandomString(String input, int size) {

		if (input == null || input.length() <= 0)
			throw new IllegalArgumentException("Invalid input.");
		if (size < 1)
			throw new IllegalArgumentException("Invalid size.");

		StringBuilder result = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			// produce a random order
			int index = random.nextInt(input.length());
			result.append(input.charAt(index));
		}
		return result.toString();
	}
	
	
	private void sendEmail(String email, String password) {
		/*
		 * SimpleMailMessage msg = new SimpleMailMessage(); msg.setTo(email);
		 * msg.setFrom("noreply@finacular.in");
		 * msg.setSubject("CAMS generated successfully ");
		 * msg.setText("Your CAMS PDF password is :  " + password);
		 */ 
		MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());

			Context context = new Context();
			context.setVariable("name", "User");
			context.setVariable("password", password);

			String html = templateEngine.process("cams_email.html", context);
			helper.setTo(email);
			helper.setText(html, true);
			helper.setSubject("CAMS generated successfully ");
			helper.setFrom("noreply@finacular.in");
		} catch (MessagingException e) {
			LOG.error(e.getMessage());
		}

		javaMailSender.send(message);

	}

}
