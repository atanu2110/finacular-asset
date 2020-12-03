package com.finadv.assets.service;

import java.util.concurrent.TimeUnit;

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
import org.springframework.stereotype.Service;

/**
 * @author atanu
 *
 */
@Service
public class SeleniumService {
	
	private static final Logger LOG = LoggerFactory.getLogger(SeleniumService.class);

	public void triggerCAMSEmail(String email, String password) {
		System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
		ChromeOptions chromeOptions = new ChromeOptions();
	//	chromeOptions.addArguments("--headless");
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
		JavascriptExecutor js = (JavascriptExecutor)driver;
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
	
		//driver.quit();
	}

}
