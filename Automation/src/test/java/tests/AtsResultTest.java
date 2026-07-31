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

public class AtsResultTest extends BaseTest{
	AtsResultPage atsresultpage;
	HomePage homepage;
	String resumePath;
	String text;
	
	@BeforeClass
	public void loadJD() throws IOException {
	    text = Files.readString(
	        Paths.get(System.getProperty("user.dir"),
	        "src","test","resources","TestData","JD.txt")
	    );
	}
	@BeforeMethod
	public void init() {

	    atsresultpage = new AtsResultPage(driver);
	    homepage = new HomePage(driver);

	    homepage.clickUploadForm();

	    resumePath = System.getProperty("user.dir")
	            + "/src/test/resources/TestData/Pushpak_Pranav_QA_Resume.docx";

	    homepage.selectFile(resumePath);

	    homepage.clickAnalyzeBtn();
	}
	@Test
	public void verifyMainNavbarDisplayed() {
		Assert.assertTrue(atsresultpage.isMainNavbarDisplayed());
	}
	
	@Test
	public void verifyResumeFileExtension() {
	    String fileName = atsresultpage.getResumeFileName().toLowerCase();

	    Assert.assertTrue(
	        fileName.endsWith(".pdf") || fileName.endsWith(".docx"),
	        "Resume file should have .pdf or .docx extension"
	    );
	}
	
	@Test
	public void verifyResumeFileName() {
		
		String expectedFileName = new File(resumePath).getName();
		Assert.assertEquals(atsresultpage.getResumeFileName(), expectedFileName );
	}
	
	@Test
	public void verifyAtsScoreIsDisplayed() {
		Assert.assertTrue(atsresultpage.isAtsScoreValueDisplayed(),"Ats Score Value not Displayed");
	}
	@Test
	public void verifyAtsGradeValueDisplayed() {
	Assert.assertTrue(atsresultpage.isAtsGradeValueDisplayed(),"Ats grade Value not Displayed");
	}
	@Test
	public void verifyDomainBadgeDisplayed() {
	Assert.assertTrue(atsresultpage.isDetectedDomainDisplayed(),"Domain Badge not Displayed");
	}
	@Test
	public void verifyMatchedSkillsDisplayed() {
	Assert.assertTrue(atsresultpage.isAtsMatchedSkillsblockDisplayed(),"Matched Skills not Displayed");
	}
	
	@Test
	public void verifyMissingSkillsDisplayed() {
		Assert.assertTrue(atsresultpage.isAtsMissingSkillsBlockDisplayed(),"Missing Skills not displayed");
	}
	@Test
	public void verifyProgressBarDisplayed() {
	Assert.assertTrue(atsresultpage.isAtsScoreProgressBarDisplayed(),"Progress Bar not Displayed");
	}
	
	@Test
	public void verifyAiSummaryTextDisplayed() {
	Assert.assertTrue(atsresultpage.isAiSummaryDisplayed(),"Ai Summary not Displayed");
	}
	@Test
	public void verifyJDMatchFormDisplayed() {
		Assert.assertTrue(atsresultpage.isJDMatchFormDisplayed(),"JD Form Not Displayed");
	}
	@Test
	public void verifyJDTextAreaPlaceholder() {
		Assert.assertEquals(atsresultpage.getJdTextAreaPlaceholder(),"Paste the full job description here...");
	}
	
	@Test
	public void verifyJDTextAreaEnabled() {
		Assert.assertTrue(atsresultpage.isJDTextAreaEnabled());
	}
	
	
	@Test
	public void verifyJdTextAreaAcceptsText() {
				
		atsresultpage.enterJdText(text);
		
		Assert.assertEquals(
			    atsresultpage.getJDText(),
			    text
			);
	}
	
	@Test
	public void verifyAnalyzeMatchButtonDisplayed() {
		Assert.assertTrue(atsresultpage.isJdAnalyzeBtnDisplayed(),"Analyze Button not Displayed");
	}
	
	
	@Test
	public void verifyUploadAnotherButtonDisplayed() {
		Assert.assertTrue(atsresultpage.isUploadAnotherBtnDisplayed(),"'Upload Another' Button is not Displayed");
	}
	@Test
	public void verifyUploadButtonNavigateToHome() {
		homepage = atsresultpage.clickUploadAnotherBtn();
		Assert.assertTrue(homepage.isUploadFormDisplayed(), "Upload form should be displayed after clicking Upload Another.");
		
	}
	
	@Test
	public void verifyJDAnalyzeBtnEnabled() {
		Assert.assertTrue(
				atsresultpage.isJdAnalyzeBtnEnabled(),
				"Analyze button should be enabled");
	}
	
	
	
	@Test
	public void verifyGradeNotEmpty() {
		String atsGradeValue =  atsresultpage.getAtsGradeValue();
		Assert.assertFalse(atsGradeValue.trim().isEmpty(),"atsGradeValue is Empty");
		
	}
	@Test
	public void verifyAiSummaryNotEmpty() {
		String atsAiSummary =  atsresultpage.getAiSummaryText();
		Assert.assertFalse(atsAiSummary.trim().isEmpty(),"atsAiSummary is Empty");
	}
	@Test
	public void verifyMatchedCountDisplayed() {
		Assert.assertTrue(atsresultpage.isAtsMatchedCountDisplayed(),"Match count Not Displayed");
	}
	@Test
	public void verifyMissingCountDisplayed() {
		Assert.assertTrue(atsresultpage.isAtsMissingCountDisplayed(),"Missing Count not Displayed");
	}
	
	@Test
	public void verifyAtsScoreRange() {
		int score = atsresultpage.getAtsScoreValue();

		Assert.assertTrue(
		    score >= 0 && score <= 100,
		    "Invalid ATS Score : " + score
		);
	}
	
	@Test 
	public void verifyAtsGradeValueIsValid() {
		String grade=atsresultpage.getAtsGradeValue();

		Assert.assertTrue(
		        grade.equals("Excellent") ||
		        grade.equals("Good") ||
		        grade.equals("Average") ||
		        grade.equals("Poor"),
		        "Invalid ATS Grade : " + grade
		);
	}
	
	@Test

	public void verifyDetectedDomainValue()
	{
	String domain=
	atsresultpage.getDetectedDomain();

	Assert.assertFalse(domain.trim().isEmpty(),"Detected domain is empty");

	Assert.assertNotEquals(domain,"Unknown", "Detected domain should not be Unknown");
	}
	
	@Test
	public void verifyAnalyzeMatchNavigatesToResultPage() {
		JdMatchResultPage jdresultpage = atsresultpage.enterJdAndAnalyze(text);

		Assert.assertTrue(
		        jdresultpage.isPageLoaded(),
		        "Match Result Page is not displayed after JD analysis."
		    );
		
	}
	
	
	
	
	
	@Test
	public void verifyAllImportantElementsDisplayed() {
		Assert.assertTrue(atsresultpage.isMainNavbarDisplayed(),"Navbar not Displayed");
		Assert.assertTrue(atsresultpage.isAtsScoreValueDisplayed(),"Ats Score Value not Displayed");
		Assert.assertTrue(atsresultpage.isAtsGradeValueDisplayed(),"Ats grade Value not Displayed");
		Assert.assertTrue(atsresultpage.isDetectedDomainDisplayed(),"Domain Badge not Displayed");
		Assert.assertTrue(atsresultpage.isAtsMatchedSkillsblockDisplayed(),"Matched Skills not Displayed");
		Assert.assertTrue(atsresultpage.isAtsMissingSkillsBlockDisplayed(),"Missing Skills not displayed");
		Assert.assertTrue(atsresultpage.isAtsScoreProgressBarDisplayed(),"Progress Bar not Displayed");
		Assert.assertTrue(atsresultpage.isAiSummaryDisplayed(),"Ai Summary not Displayed");
		Assert.assertTrue(atsresultpage.isJDMatchFormDisplayed(),"JD Form Not Displayed");
		Assert.assertEquals(atsresultpage.getJdTextAreaPlaceholder(),"Paste the full job description here...");
		Assert.assertTrue(atsresultpage.isJdAnalyzeBtnDisplayed(),"Analyze Button not Displayed");
		Assert.assertTrue(atsresultpage.isUploadAnotherBtnDisplayed(),"'Upload Another' Button is not Displayed");
		Assert.assertTrue(atsresultpage.isAtsMatchedCountDisplayed(),"Match count Not Displayed");
		Assert.assertTrue(atsresultpage.isAtsMissingCountDisplayed(),"Missing Count not Displayed");
	}
	
	

}
