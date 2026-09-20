package tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.AtsResultPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.JdMatchResultPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;

import base.BaseTest;
import constants.TestData;

public class JdMatchResultTest extends BaseTest {

	private HomePage homePage;
	private AtsResultPage atsResultPage;
	private JdMatchResultPage jdPage;
	private LoginPage loginPage;
	private String jdText;

	@BeforeClass(alwaysRun = true)
	public void loadData() throws IOException {
		jdText = Files.readString(Paths.get(TestData.JD_File));
	}

	@BeforeMethod(alwaysRun = true)
	public void init() {
	    homePage = new HomePage(driver);
	    loginPage = new LoginPage(driver);
	    atsResultPage = new AtsResultPage(driver);
	    
	    homePage.clickLogin();
	    loginPage.loginAsValidUser(
	            TestData.DASHBOARD_USER_EMAIL,
	            TestData.DASHBOARD_USER_PASSWORD
	    );

	    loginPage.waitForDashboard();
	    loginPage.clickHomeLink();
	    homePage.clickUploadForm();
	    homePage.selectFile(TestData.JD_RESUME_PATH);
	    homePage.clickAnalyzeBtn();
	    homePage.waitForAtsResultPage();

	    jdPage = atsResultPage.enterJdAndAnalyze(jdText);

	    jdPage.waitForPageToLoad();
	}


	// ---------------------------------------------------------
	// Page load
	// ---------------------------------------------------------

	@Test(groups = {"smoke"})
	public void verifyResultPageLoaded() {
		Assert.assertTrue(
				jdPage.isPageLoaded(),
				"JD Match Result page is not loaded.");
	}


	// ---------------------------------------------------------
	// Match score
	// ---------------------------------------------------------

	@Test
	public void verifyMatchScoreCardDisplayed() {
		Assert.assertTrue(
				jdPage.isMatchScoreCardDisplayed(),
				"Match Score Card is not displayed.");
	}

	@Test(groups = {"smoke"})
	public void verifyMatchPercentageDisplayed() {
		Assert.assertTrue(
				jdPage.isMatchPercentageDisplayed(),
				"Match Percentage is not displayed.");
	}

	@Test
	public void verifyMatchPercentageValue() {
		String percentage = jdPage.getMatchPercentage();

		Assert.assertTrue(
				percentage.matches("\\d+(\\.\\d+)?%"),
				"Invalid Match Percentage : " + percentage);
	}

	@Test
	public void verifyMatchPercentageRange() {
		String percentage = jdPage.getMatchPercentage()
				.replace("%", "")
				.trim();

		double value = Double.parseDouble(percentage);

		Assert.assertTrue(
				value >= 0 && value <= 100,
				"Match percentage should be between 0 and 100.");
	}

	@Test
	public void verifyScoreCircleColorClass() {
		String cls = jdPage.getScoreCircleClass();

		Assert.assertTrue(
				cls.contains("excellent")
						|| cls.contains("good")
						|| cls.contains("average")
						|| cls.contains("poor"),
				"Unexpected score circle class : " + cls);
	}

	@Test
	public void verifyScoreCircleClassNotBlank() {
		Assert.assertFalse(
				jdPage.getScoreCircleClass().isBlank(),
				"Score circle class is blank.");
	}


	// ---------------------------------------------------------
	// Resume details
	// ---------------------------------------------------------

	@Test
	public void verifyResumeFileName() {
		Assert.assertEquals(
				jdPage.getResumeFileName(),
				TestData.JD_RESUME_NAME,
				"Resume filename is incorrect.");
	}

	@Test
	public void verifyResumeFileNameNotBlank() {
		Assert.assertFalse(
				jdPage.getResumeFileName().isBlank(),
				"Resume filename is blank.");
	}


	// ---------------------------------------------------------
	// Detected domain
	// ---------------------------------------------------------

	@Test
	public void verifyDetectedDomain() {

	    String actualDomain = jdPage.getDetectedDomain();

	    Assert.assertTrue(
	            actualDomain.contains(TestData.EXPECTED_DOMAIN_IN_JDPAGE),
	            "Incorrect detected domain. Actual: " + actualDomain
	    );
	}

	@Test
	public void verifyDetectedDomainNotBlank() {
		Assert.assertFalse(
				jdPage.getDetectedDomain().isBlank(),
				"Detected domain is blank.");
	}


	// ---------------------------------------------------------
	// Progress bar / count summary
	// ---------------------------------------------------------

	@Test
	public void verifyProgressBarDisplayed() {
		Assert.assertTrue(
				jdPage.isProgressBarDisplayed(),
				"Progress bar is not displayed.");
	}

	@Test
	public void verifyProgressBarValueFormat() {
		String progress = jdPage.getProgressBarValue();

		Assert.assertTrue(
				progress.matches("\\d+(\\.\\d+)?%"),
				"Invalid progress bar value : " + progress);
	}

	@Test
	public void verifyProgressBarValue() {

	    String progress = jdPage.getProgressBarValue()
	            .replace("%", "")
	            .trim();

	    String percentage = jdPage.getMatchPercentage()
	            .replace("%", "")
	            .trim();

	    double progressValue = Double.parseDouble(progress);
	    double matchValue = Double.parseDouble(percentage);

	    Assert.assertEquals(
	            progressValue,
	            matchValue,
	            "Progress bar percentage mismatch."
	    );
	}

	@Test
	public void verifyCountSummary() {
		String summary = jdPage.getCountSummary();

		Assert.assertTrue(
				summary.matches("(?i).*\\d+.*matched.*\\d+.*missing.*"),
				"Invalid count summary : " + summary);
	}


	// ---------------------------------------------------------
	// Matched / Missing skills, feedback
	// ---------------------------------------------------------

	@Test
	public void verifyMatchedSkillsBlock() {
		Assert.assertTrue(
				jdPage.isMatchedSkillsDisplayed(),
				"Matched Skills block is not displayed.");
	}

	@Test
	public void verifyMissingSkillsBlock() {
		Assert.assertTrue(
				jdPage.isMissingSkillsDisplayed(),
				"Missing Skills block is not displayed.");
	}

	@Test
	public void verifyFeedbackCard() {
		Assert.assertTrue(
				jdPage.isFeedbackCardDisplayed(),
				"AI Feedback card is not displayed.");
	}

	@Test
	public void verifyAIRewriterCard() {
		Assert.assertTrue(
				jdPage.isAIRewriterDisplayed(),
				"AI Rewriter card is not displayed.");
	}

	@Test
	public void verifySuggestionCard() {
		Assert.assertTrue(
				jdPage.isSuggestionsCardDisplayed(),
				"Suggestion card is not displayed.");
	}


	// ---------------------------------------------------------
	// Analyze Another Resume
	// ---------------------------------------------------------

	@Test
	public void verifyAnalyzeAnotherResumeButtonDisplayed() {
		Assert.assertTrue(
				jdPage.isAnalyzeAnotherResumeButtonDisplayed(),
				"Analyze Another Resume button is not displayed.");
	}

	@Test
	public void verifyAnalyzeAnotherResumeButton() {
		HomePage page = jdPage.clickAnalyzeAnotherResume();

		Assert.assertTrue(
				page.isUploadFormDisplayed(),
				"Home page was not loaded after clicking Analyze Another Resume.");
	}


	// ---------------------------------------------------------
	// Download report
	// ---------------------------------------------------------

	@Test(groups = {"smoke"})
	public void verifyDownloadButtonsDisplayed() {
		Assert.assertTrue(
				jdPage.isDownloadButtonsDisplayed(),
				"Download buttons are not displayed.");
	}

	@Test
	public void verifyTopDownloadButtonDisplayed() {
		Assert.assertTrue(
				jdPage.isTopDownloadButtonDisplayed(),
				"Top Download Report button is not displayed.");
	}

	@Test
	public void verifyBottomDownloadButtonDisplayed() {
		Assert.assertTrue(
				jdPage.isBottomDownloadButtonDisplayed(),
				"Bottom Download Report button is not displayed.");
	}

	@Test
	public void verifyTopDownloadButtonDownloadsPdfSuccessfully() throws IOException {
		File downloadedFile = jdPage.clickTopDownloadReport();

		Assert.assertTrue(downloadedFile.exists(), "Downloaded file does not exist");
		Assert.assertTrue(downloadedFile.length() > 0, "Downloaded PDF is empty");
		Assert.assertTrue(downloadedFile.length() > TestData.MIN_PDF_SIZE,
				"Downloaded PDF is too small.");

		Assert.assertTrue(
				downloadedFile.getName().matches("ATS_Report_\\d+( \\(\\d+\\))?\\.pdf"),
				"Invalid file name: " + downloadedFile.getName());
	}

	@Test
	public void verifyBottomDownloadBtnDownloadsPdfSuccessfully() throws IOException {
		File downloadedFile = jdPage.clickBottomDownloadReport();

		Assert.assertNotNull(downloadedFile, "No PDF files found");
		Assert.assertTrue(downloadedFile.exists(), "Downloaded file does not exist");
		Assert.assertTrue(downloadedFile.length() > 0, "Downloaded PDF is empty");
		Assert.assertTrue(downloadedFile.length() > TestData.MIN_PDF_SIZE,
				"Downloaded PDF is too small.");

		Assert.assertTrue(
				downloadedFile.getName().matches("ATS_Report_\\d+( \\(\\d+\\))?\\.pdf"),
				"Invalid file name: " + downloadedFile.getName());
	}
}