package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class BasePage {
	
	protected WebDriver driver;
	private static final long MIN_PDF_SIZE = 100;
	private static final Path DOWNLOAD_DIR = Paths.get(System.getProperty("user.dir"), "Downloads");
	
	public BasePage(WebDriver driver) {
		this.driver = driver;
		
	}
	public void click(WebElement ele) {
		ele.click();
	}
	
	public void type(WebElement ele,String txt) {
		ele.clear();
		ele.sendKeys(txt);
	}
	
	public String getText(WebElement ele) {
		return ele.getText();
	}
	
	public void checkText(String actual,String expected ) {
		Assert.assertEquals(actual,expected);
	}
	
	public boolean isDisplayed(WebElement ele) {
		return ele.isDisplayed();
		
	}
	public void scrollToElement(WebElement ele) {
	Actions action = new Actions(driver);
	action.moveToElement(ele).perform();

	}
	public String getAtribute(WebElement ele, String string) {
		return ele.getAttribute(string);
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
    

    // Delete old files
    Files.list(DOWNLOAD_DIR)
    .filter(path -> path.getFileName().toString().startsWith("ATS_Report"))
    .forEach(path -> {
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    });
	}
	
	public void waitUntillPdfDownloads() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Wait until any PDF appears in the Downloads folder
        wait.until(driver -> {
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
	    waitUntillPdfDownloads();

	  
	    File[] pdfFiles = DOWNLOAD_DIR.toFile().listFiles(
	            (dir, name) -> name.startsWith("ATS_Report")
	                    && name.endsWith(".pdf"));

	    if (pdfFiles == null || pdfFiles.length == 0) {
	        throw new FileNotFoundException("ATS_Report PDF was not downloaded.");
	    }

	    return pdfFiles[0];
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
