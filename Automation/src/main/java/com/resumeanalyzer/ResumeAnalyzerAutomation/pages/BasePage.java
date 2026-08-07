package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {
	
	protected WebDriver driver;
	private static final Path DOWNLOAD_DIR = Paths.get(System.getProperty("user.dir"), "Downloads");
	
	public BasePage(WebDriver driver) {
		this.driver = driver;	
	}
	
	public void scrollToElement(WebElement ele) {
		Actions action = new Actions(driver);
		action.moveToElement(ele).perform();

	}
	
	public void click(WebElement element) {
	    scrollToElement(element);
	    element.click();
	}
	
	public void type(WebElement element, String text) {
	    element.clear();
	    element.sendKeys(text);
	}
	
	public String getText(WebElement ele) {
		return ele.getText();
	}
	
	protected int getPercentage(WebElement element){
	    return Integer.parseInt(
	            getText(element)
	            .replace("%","")
	            .trim());
	}
	
	public boolean isDisplayed(WebElement element) {
	    try {
	        return element.isDisplayed();
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public boolean isDisplayedSafely(By locator, int timeoutSeconds) {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
	        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)) != null;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public String getAttribute(WebElement ele, String attributeName) {
		return ele.getAttribute(attributeName);
	}
	
	public boolean isEnabled(WebElement ele) {
		return ele.isEnabled();
		
	}
	protected String waitForAlert() {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	    Alert alert = wait.until(ExpectedConditions.alertIsPresent());
	    String message = alert.getText();
	    alert.accept();
	    return message;
	}
	
	
	public void deleteOldReports() throws IOException {
		// Delete previously downloaded ATS reports

		try (var files = Files.list(DOWNLOAD_DIR)) {
		    files.filter(path -> path.getFileName().toString().startsWith("ATS_Report"))
		         .forEach(path -> {
		             try {
		                 Files.deleteIfExists(path);
		             } catch (IOException e) {
		            	 System.err.println("Unable to delete: " + path);
		             }
		         });
		}
	}
	
	public void waitUntilPdfDownloads(){
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Wait until any PDF appears in the Downloads folder
        wait.until(d -> {
         File[] pdfFiles = DOWNLOAD_DIR.toFile().listFiles((dir, name) ->
                    name.startsWith("ATS_Report")
                    && name.endsWith(".pdf")
                    && !name.endsWith(".crdownload"));

            return pdfFiles != null && pdfFiles.length > 0;
        }); 
	}
	
	public File clickDownloadReport(WebElement ele) throws IOException {
	    deleteOldReports();
	    click(ele);
	    waitUntilPdfDownloads();

	  
	    File[] pdfFiles = DOWNLOAD_DIR.toFile().listFiles(
	            (dir, name) -> name.startsWith("ATS_Report")
	                    && name.endsWith(".pdf"));

	    if (pdfFiles == null || pdfFiles.length == 0) {
	        throw new FileNotFoundException("ATS_Report PDF was not downloaded.");
	    }

	    return pdfFiles[0];
	}	
}