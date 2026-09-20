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

import base.BaseTest;
import constants.TestData;

public class AtsResultTest extends BaseTest {

	AtsResultPage atsresultPage;
	HomePage homePage;
	String jdText;

	@BeforeClass(alwaysRun = true)
	public void loadJD() throws IOException {
		jdText = Files.readString(Paths.get(TestData.JD_File));
	}

	@BeforeMethod(alwaysRun = true)
	public void init() {
		atsresultPage = new AtsResultPage(driver);
		homePage = new HomePage(driver);

		homePage.clickUploadForm();
		homePage.selectFile(TestData.DASHBOARD_RESUME_PATH);
		homePage.clickAnalyzeBtn();
		homePage.waitForAtsResultPage();
	}


	// ---------------------------------------------------------
	// Page layout
	// ---------------------------------------------------------

	@Test
	public void verifyMainNavbarDisplayed() {
		Assert.assertTrue(
				atsresultPage.isMainNavbarDisplayed(),
				"Navbar not displayed."
				);
	}

	// ---------------------------------------------------------
	// Resume details
	// ---------------------------------------------------------

	@Test
	public void verifyResumeFileExtension() {
		String fileName = atsresultPage.getResumeFileName().toLowerCase();

		Assert.assertTrue(
				fileName.endsWith(".pdf") || fileName.endsWith(".docx"),
				"Resume file should have .pdf or .docx extension"
				);
	}

	@Test
	public void verifyResumeFileName() {
		String expectedFileName = new File(TestData.DASHBOARD_RESUME_PATH).getName();

		Assert.assertEquals(
				atsresultPage.getResumeFileName(),
				expectedFileName,
				"Resume filename is not displayed correctly."
				);
	}


	// ---------------------------------------------------------
	// ATS score / grade
	// ---------------------------------------------------------

	@Test(groups = {"smoke"})
	public void verifyAtsScoreIsDisplayed() {
		Assert.assertTrue(atsresultPage.isAtsScoreValueDisplayed(), "Ats Score Value not Displayed");
	}

	@Test
	public void verifyAtsScoreRange() {
		double score = atsresultPage.getAtsScoreValue();

		Assert.assertTrue(
				score >= 0 && score <= 100,
				"Invalid ATS Score : " + score
				);
	}

	@Test
	public void verifyAtsGradeValueDisplayed() {
		Assert.assertTrue(atsresultPage.isAtsGradeValueDisplayed(), "Ats grade Value not Displayed");
	}

	@Test
	public void verifyGradeNotEmpty() {
		String atsGrade = atsresultPage.getAtsGradeValue();

		Assert.assertFalse(
		        atsGrade.isBlank(),
		        "ATS Grade is empty."
		);
	}

	@Test
	public void verifyAtsGradeValueIsValid() {
		String grade = atsresultPage.getAtsGradeValue();

		Assert.assertTrue(
		        TestData.VALID_ATS_GRADES.contains(grade),
		        "Invalid ATS Grade : " + grade
		);
	}

	@Test
	public void verifyProgressBarDisplayed() {
		Assert.assertTrue(atsresultPage.isAtsScoreProgressBarDisplayed(), "Progress Bar not Displayed");
	}

	@Test
	public void verifyProgressBarValueMatchesAtsScore() {
	    double progressValue = Double.parseDouble(
	        atsresultPage.getProgressBarValue()
	    );

	    Assert.assertEquals(
	        progressValue,
	        atsresultPage.getAtsScoreValue(),
	        "Progress bar value does not match the ATS score value."
	    );
	}


	// ---------------------------------------------------------
	// Domain
	// ---------------------------------------------------------

	@Test
	public void verifyDomainBadgeDisplayed() {
		Assert.assertTrue(atsresultPage.isDetectedDomainDisplayed(), "Domain Badge not Displayed");
	}

	@Test
	public void verifyDetectedDomainValue() {

	    String domain = atsresultPage.getDetectedDomain();

	    Assert.assertFalse(
	            domain.isBlank(),
	            "Detected domain is empty."
	    );

	    Assert.assertNotEquals(
	            domain,
	            "Unknown",
	            "Detected domain should not be Unknown."
	    );
	}


	// ---------------------------------------------------------
	// Matched / Missing skills
	// ---------------------------------------------------------

	@Test
	public void verifyMatchedSkillsDisplayed() {
		Assert.assertTrue(atsresultPage.isAtsMatchedSkillsBlockDisplayed(), "Matched Skills not Displayed");
	}

	@Test
	public void verifyMatchedCountDisplayed() {
		Assert.assertTrue(atsresultPage.isAtsMatchedCountDisplayed(), "Match count Not Displayed");
	}

	@Test
	public void verifyMissingSkillsDisplayed() {
		Assert.assertTrue(atsresultPage.isAtsMissingSkillsBlockDisplayed(), "Missing Skills not displayed");
	}

	@Test
	public void verifyMissingCountDisplayed() {
		Assert.assertTrue(atsresultPage.isAtsMissingCountDisplayed(), "Missing Count not Displayed");
	}


	// ---------------------------------------------------------
	// AI Summary
	// ---------------------------------------------------------

	@Test
	public void verifyAiSummaryTextDisplayed() {
		Assert.assertTrue(atsresultPage.isAiSummaryDisplayed(), "Ai Summary not Displayed");
	}

	@Test
	public void verifyAiSummaryBlockDisplayed() {
		Assert.assertTrue(
				atsresultPage.isAiSummaryBlockDisplayed(),
				"Ai Summary block is not displayed."
				);
	}

	@Test
	public void verifyAiSummaryNotEmpty() {
		String atsAiSummary = atsresultPage.getAiSummaryText();
		Assert.assertFalse(atsAiSummary.isBlank(), "atsAiSummary is Empty");
	}


	// ---------------------------------------------------------
	// JD Match form
	// ---------------------------------------------------------

	@Test
	public void verifyJDMatchFormDisplayed() {
		Assert.assertTrue(atsresultPage.isJDMatchFormDisplayed(),
				"JD Form Not Displayed"
				);
	}

	@Test
	public void verifyJDTextAreaPlaceholder() {
		Assert.assertEquals(atsresultPage.getJdTextAreaPlaceholder(),
				TestData.JD_TEXTAREA_PLACEHOLDER
				);
	}

	@Test
	public void verifyJDTextAreaEnabled() {
		Assert.assertTrue(atsresultPage.isJDTextAreaEnabled());
	}

	@Test
	public void verifyJdTextAreaAcceptsText() {
		atsresultPage.enterJdText(jdText);

		Assert.assertEquals(
				atsresultPage.getJdText().replace("\r\n", "\n").trim(),
				jdText.replace("\r\n", "\n").trim()
				);
	}

	@Test
	public void verifyAnalyzeMatchButtonDisplayed() {
		Assert.assertTrue(atsresultPage.isJdAnalyzeBtnDisplayed(), "Analyze Button not Displayed");
	}

	@Test
	public void verifyJDAnalyzeBtnEnabledAfterEnteringJD() {
	    atsresultPage.enterJdText(jdText);

	    Assert.assertTrue(
	            atsresultPage.isJdAnalyzeBtnEnabled(),
	            "Analyze button should be enabled after entering JD."
	    );
	}

	@Test(groups = {"smoke"})
	public void verifyAnalyzeMatchNavigatesToResultPage() {
		JdMatchResultPage jdresultpage = atsresultPage.enterJdAndAnalyze(jdText);
		atsresultPage.waitForJdPage();
		Assert.assertTrue(
				jdresultpage.isPageLoaded(),
				"Match Result Page is not displayed after JD analysis."
				);
	}


	// ---------------------------------------------------------
	// Upload Another
	// ---------------------------------------------------------

	@Test
	public void verifyUploadAnotherButtonDisplayed() {
		Assert.assertTrue(atsresultPage.isUploadAnotherBtnDisplayed(), "'Upload Another' Button is not Displayed");
	}

	@Test(groups = {"smoke"})
	public void verifyUploadButtonNavigateToHome() {
		homePage = atsresultPage.clickUploadAnotherBtn();

		Assert.assertTrue(
				homePage.isUploadFormDisplayed(),
				"Upload form should be displayed after clicking Upload Another."
				);
	}
}