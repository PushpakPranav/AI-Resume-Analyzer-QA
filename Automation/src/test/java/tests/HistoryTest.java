package tests;

import java.io.File;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HistoryPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;

import base.BaseTest;
import components.NavbarComponent;

public class HistoryTest extends BaseTest{
	HomePage homePage;
	LoginPage loginPage;
	DashboardPage dashboardPage;
	HistoryPage historyPage;
	NavbarComponent navbar;
	String email = "test11@gmail.com";
	String password = "MyStr0ng@Pass!";
	private static final long MIN_PDF_SIZE = 100;
	
	
	@BeforeMethod
	public void init() throws InterruptedException {
		homePage = new HomePage(driver);
		loginPage = new LoginPage(driver);
		navbar = new NavbarComponent(driver);
		
		homePage.clickLogin();
		dashboardPage = loginPage.loginWithCredentials(email, password);
        historyPage = dashboardPage.clickFirstHistoryButton();
		}
	@Test
	public void verifyHistoryPageLoaded() {
		Assert.assertTrue(
	            historyPage.isHistoryHeadingDisplayed(),
	            "History page not loaded."
	    );
	}
	
	@Test
	public void verifyResumeFileName() {
		Assert.assertTrue(
			    historyPage.getResumeFileName().endsWith(".docx")  || historyPage.getResumeFileName().endsWith(".pdf"),
			    "Wrong resume filename."
			);
	}
	@Test
	public void verifyDashboardButtonWorks() {
		historyPage.clickDashboardBtn();
		Assert.assertTrue(
	            driver.getCurrentUrl().contains("/dashboard"),
	            "Dashboard Button not working."
	    );
	}
	@Test
	public void verifyAnalysisCardsDisplayed() {
		Assert.assertTrue(historyPage.isAnalysisListDisplayed(),"Analysis Card not Displayed");
	}
	@Test
	public void verifyMatchPercentageDisplayed() {
		String percentage = historyPage.getFirstAnalysisMatchPercentage();
		Assert.assertFalse(percentage.isBlank(), "Match Percentage is not displayed.");
	}
	@Test
	public void verifyAnalysisDateDisplayed() {
		Assert.assertFalse(
			    historyPage.getFirstAnalysisDate().isBlank(),
			    "Analysis Date not displayed."
			);
		
	}
	@Test
	public void verifyMatchedSkillsDisplayed() {
		Assert.assertFalse(
			    historyPage.getFirstAnalysisMatchedSkills().isEmpty(),
			    "Matched skills are not displayed."
			);
		
	}
	@Test
	public void verifyProgressBarDisplayed() {
		Assert.assertTrue(historyPage.isFirstAnalysisProgressBarDisplayed(),"Progress Bar not Displayed");
		
	}
	@Test
	public void verifyDownloadReportSuccessfully() throws IOException {
		File downloadedFile = historyPage.clickFirstAnalysisDownloadReport();
    	Assert.assertTrue(downloadedFile.exists(), "Downloaded file does not exist");
    	Assert.assertTrue(downloadedFile.length() > 0, "Downloaded PDF is empty");
    	Assert.assertTrue(downloadedFile.length() > MIN_PDF_SIZE,
    	        "Downloaded PDF is too small.");

    	Assert.assertTrue(
    	    downloadedFile.getName().matches("ATS_Report_\\d+( \\(\\d+\\))?\\.pdf"),
    	    "Invalid file name: " + downloadedFile.getName());
    }
	@Test
	public void verifyAnalysisCountGreaterThanZero() {
		Assert.assertTrue(historyPage.getAnalysisCount() > 0,
		        "Analysis count should be greater than zero.");
	}
	

}
