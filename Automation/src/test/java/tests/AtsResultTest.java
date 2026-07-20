package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.AtsResultPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;

import base.BaseTest;

public class AtsResultTest extends BaseTest{
	AtsResultPage atsresultpage;
	HomePage homepage;
	
	
	
	
	@BeforeMethod
	public void init() {
		atsresultpage = new AtsResultPage(driver);
		homepage = new HomePage(driver);
		homepage.clickUploadForm();
		String resumePath = System.getProperty("user.dir")
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
		Assert.assertEquals(atsresultpage.getResumeFileName(), "Pushpak_Pranav_QA_Resume.docx");
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
	Assert.assertTrue(atsresultpage.isAiSummaryTextDisplayed(),"Ai Summary not Displayed");
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
		
		String text =  "Job Summary\r\n"
				+ "		\r\n"
				+ "		We are looking for a detail-oriented Software Tester to ensure the quality and reliability of our software applications. The ideal candidate will be responsible for planning, executing, and documenting test cases, identifying bugs, and working closely with developers to deliver high-quality software.\r\n"
				+ "		\r\n"
				+ "		Key Responsibilities\r\n"
				+ "		Review software requirements and prepare test scenarios and test cases.\r\n"
				+ "		Perform manual testing of web, mobile, or desktop applications.\r\n"
				+ "		Identify, document, and track software defects using bug-tracking tools.\r\n"
				+ "		Conduct functional, regression, integration, smoke, and system testing.\r\n"
				+ "		Verify bug fixes and ensure issues are resolved before release.\r\n"
				+ "		Collaborate with developers, business analysts, and product managers.\r\n"
				+ "		Prepare test reports and maintain testing documentation.\r\n"
				+ "		Participate in Agile/Scrum meetings and sprint activities.\r\n"
				+ "		Suggest improvements to enhance software quality and testing processes.\r\n"
				+ "		Required Skills\r\n"
				+ "		Strong understanding of Software Testing Life Cycle (STLC) and Software Development Life Cycle (SDLC).\r\n"
				+ "		Experience with manual testing methodologies.\r\n"
				+ "		Knowledge of defect tracking tools such as Jira, Bugzilla, or Azure DevOps.\r\n"
				+ "		Understanding of API testing using Postman (preferred).\r\n"
				+ "		Basic knowledge of SQL and databases.\r\n"
				+ "		Excellent analytical, problem-solving, and communication skills.\r\n"
				+ "		Ability to work independently and as part of a team.\r\n"
				+ "		Qualifications\r\n"
				+ "		Bachelor's degree in Computer Science, Information Technology, or a related field.\r\n"
				+ "		1\\u20133 years of experience in software testing (Freshers with strong testing knowledge may also apply).\r\n"
				+ "		ISTQB certification is a plus.\r\n"
				+ "		Preferred Skills\r\n"
				+ "		Knowledge of automation testing tools such as Selenium.\r\n"
				+ "		Familiarity with Agile methodologies.\r\n"
				+ "		Basic programming knowledge (Java, Python, or JavaScript) is an advantage.\r\n"
				+ "		Benefits\r\n"
				+ "		Competitive salary\r\n"
				+ "		Health insurance\r\n"
				+ "		Paid time off\r\n"
				+ "		Learning and certification support\r\n"
				+ "		Career growth opportunities." ;
				
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
		atsresultpage.clickUploadAnotherBtn();
		Assert.assertTrue(homepage.isUploadFormDisplayed());
		
	}
	
	@Test
	public void verifyJDAnalyzeBtnEnabled() {
		Assert.assertTrue(
				atsresultpage.isJdAnalyzeBtnEnabled(),
				"Analyze button should be enabled");
	}
	
	@Test
	public void verifyDetectedDomainNotEmpty() {
		String detectedDomain  =  atsresultpage.getDetectedDomain();
		Assert.assertFalse(detectedDomain.trim().isEmpty(),"Domain is Empty");
	}
	@Test
	public void verifyAtsScoreNotEmpty() {
		String atsScoreValue =  atsresultpage.getAtsScoreValue();
		Assert.assertFalse(atsScoreValue.trim().isEmpty(),"atsScoreValue is Empty");
		
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
	public void verifyAtsScoreFormat() {
		Assert.assertTrue(
				atsresultpage.getAtsScoreValue().matches("\\d+"));
	}
	
	@Test
	public void verifyAtsScoreRange() {
		int score=Integer.parseInt(atsresultpage.getAtsScoreValue());

		Assert.assertTrue(score>=0 && score<=100);
	}
	
	@Test 
	public void verifyAtsGradeValueIsValid() {
		String grade=atsresultpage.getAtsGradeValue();

		Assert.assertTrue(

		grade.equals("Excellent")

		||

		grade.equals("Good")

		||

		grade.equals("Average")

		||

		grade.equals("Poor")

		);
	}
	
	@Test

	public void verifyDetectedDomainValue()
	{
	String domain=
	atsresultpage.getDetectedDomain();

	Assert.assertFalse(domain.trim().isEmpty());

	Assert.assertNotEquals(domain,"Unknown");
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
		Assert.assertTrue(atsresultpage.isAiSummaryTextDisplayed(),"Ai Summary not Displayed");
		Assert.assertTrue(atsresultpage.isJDMatchFormDisplayed(),"JD Form Not Displayed");
		Assert.assertEquals(atsresultpage.getJdTextAreaPlaceholder(),"Paste the full job description here...");
		Assert.assertTrue(atsresultpage.isJdAnalyzeBtnDisplayed(),"Analyze Button not Displayed");
		Assert.assertTrue(atsresultpage.isUploadAnotherBtnDisplayed(),"'Upload Another' Button is not Displayed");
		Assert.assertTrue(atsresultpage.isAtsMatchedCountDisplayed(),"Match count Not Displayed");
		Assert.assertTrue(atsresultpage.isAtsMissingCountDisplayed(),"Missing Count not Displayed");
	}
	
	

}
