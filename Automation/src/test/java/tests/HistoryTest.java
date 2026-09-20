package tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.components.NavbarComponent;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.AtsResultPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HistoryPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.JdMatchResultPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;

import base.BaseTest;
import constants.TestData;

public class HistoryTest extends BaseTest {

	HomePage homePage;
	LoginPage loginPage;
	DashboardPage dashboardPage;
	HistoryPage historyPage;
	AtsResultPage atsResultPage;
	JdMatchResultPage jdMatchResultPage;
	NavbarComponent navbar;
	String jdText;
	
	
	@BeforeClass(alwaysRun = true)
	public void loadJD() throws IOException {
		jdText = Files.readString(Paths.get(TestData.JD_File));
	}
	
	@BeforeMethod(alwaysRun = true)
	public void init() {

	    homePage = new HomePage(driver);
	    loginPage = new LoginPage(driver);
	    dashboardPage = new DashboardPage(driver);
	    atsResultPage = new AtsResultPage(driver);
	    navbar = new NavbarComponent(driver);
	    jdMatchResultPage = new JdMatchResultPage(driver);

	    // Login
	    homePage.clickLogin();

	    dashboardPage = loginPage.loginAsValidUser(
	            TestData.DASHBOARD_USER_EMAIL,
	            TestData.DASHBOARD_USER_PASSWORD
	    );

	    loginPage.waitForDashboard();

	    // Fresh Resume Analysis
	    navbar.clickHome();

	    homePage.clickUploadForm();
	    homePage.selectFile(TestData.JD_RESUME_PATH);
	    homePage.clickAnalyzeBtn();
	    homePage.waitForAtsResultPage();

	    // Fresh JD Match Analysis
	    atsResultPage.enterJdAndAnalyze(jdText);
	    jdMatchResultPage.waitForPageToLoad();
	    
	    navbar.clickDashboard();

	    // Open History
	    historyPage = dashboardPage.clickFirstHistoryButton();
	    historyPage.waitForHistoryPage();
	}


	// ---------------------------------------------------------
	// Page load
	// ---------------------------------------------------------

	@Test
	public void verifyHistoryPageLoaded() {
		Assert.assertTrue(
				historyPage.isHistoryHeadingDisplayed(),
				"History page not loaded."
				);
	}

	@Test
	public void verifyDashboardButtonDisplayed() {
		Assert.assertTrue(
				historyPage.isDashboardButtonDisplayed(),
				"Dashboard button is not displayed on History page."
				);
	}

	@Test
	public void verifyDashboardButtonWorks() {
		historyPage.clickDashboardBtn();

		Assert.assertTrue(
				driver.getCurrentUrl().contains("/dashboard"),
				"Dashboard button not working."
				);
	}


	// ---------------------------------------------------------
	// Resume details
	// ---------------------------------------------------------

	@Test
	public void verifyResumeFileNameDisplayed() {
		Assert.assertTrue(
				historyPage.isResumeFileNameDisplayed(),
				"Resume filename is not displayed on History page."
				);
	}

	@Test
	public void verifyResumeFileName() {
		String fileName = historyPage.getResumeFileName();

		Assert.assertTrue(
				fileName.endsWith(".docx") || fileName.endsWith(".pdf"),
				"Wrong resume filename : " + fileName
				);
	}


	// ---------------------------------------------------------
	// Analysis cards
	// ---------------------------------------------------------

	@Test
	public void verifyAnalysisCardsDisplayed() {
		Assert.assertTrue(
				historyPage.isAnalysisListDisplayed(),
				"Analysis Card not displayed."
				);
	}

	@Test
	public void verifyAnalysisCountGreaterThanZero() {
		Assert.assertTrue(
				historyPage.getAnalysisCount() > 0,
				"Analysis count should be greater than zero."
				);
	}

	@Test
	public void verifyMatchPercentageDisplayed() {
		String percentage = historyPage.getFirstAnalysisMatchPercentage();

		Assert.assertFalse(
				percentage.isBlank(),
				"Match Percentage is not displayed."
				);
	}

	@Test
	public void verifyAnalysisDateDisplayed() {
		Assert.assertFalse(
				historyPage.getFirstAnalysisDate().isBlank(),
				"Analysis Date not displayed."
				);
	}

	@Test
	public void verifyMatchedSkillsDisplayed(){
		Assert.assertFalse(
				historyPage.getFirstAnalysisMatchedSkills().isEmpty(),
				"Matched skills are not displayed."
				);
	}

	@Test
	public void verifyProgressBarDisplayed() {
		Assert.assertTrue(
				historyPage.isFirstAnalysisProgressBarDisplayed(),
				"Progress Bar not displayed."
				);
	}


	// ---------------------------------------------------------
	// Download report
	// ---------------------------------------------------------

	@Test
	public void verifyDownloadReportButtonDisplayed() {
		Assert.assertTrue(
				historyPage.isDownloadReportButtonDisplayed(),
				"Download Report button is not displayed."
				);
	}

	@Test
	public void verifyDownloadReportSuccessfully() throws IOException {
		File downloadedFile = historyPage.clickFirstAnalysisDownloadReport();

		Assert.assertTrue(downloadedFile.exists(), "Downloaded file does not exist");
		Assert.assertTrue(downloadedFile.length() > 0, "Downloaded PDF is empty");
		Assert.assertTrue(downloadedFile.length() > TestData.MIN_PDF_SIZE,
				"Downloaded PDF is too small.");

		Assert.assertTrue(
		        downloadedFile.getName().matches(TestData.ATS_REPORT_FILE_NAME_PATTERN),
		        "Invalid file name: " + downloadedFile.getName()
		);
	}
}