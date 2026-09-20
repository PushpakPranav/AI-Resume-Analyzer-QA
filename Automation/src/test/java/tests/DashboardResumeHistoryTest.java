package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.components.NavbarComponent;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;

import base.BaseTest;
import constants.TestData;

public class DashboardResumeHistoryTest extends BaseTest {

	LoginPage loginpage;
	HomePage homepage;
	DashboardPage dashboardpage;
	NavbarComponent navbar;

	String email = TestData.VALID_EMAIL;
	String password = TestData.VALID_PASSWORD;
	String resumePath;

	@BeforeMethod(alwaysRun = true)
	public void init() {
		homepage = new HomePage(driver);
		loginpage = new LoginPage(driver);
		dashboardpage = new DashboardPage(driver);
		navbar = new NavbarComponent(driver);

		resumePath = System.getProperty("user.dir")
				+ "/src/test/resources/TestData/TestResume1.pdf";

		homepage.clickLogin();
		loginpage.loginAsValidUser(email, password);
		loginpage.waitForDashboard();
		if (!dashboardpage.hasResumeHistory()) {

	        navbar.clickHome();

	        homepage.clickUploadForm();
	        homepage.selectFile(resumePath);
	        homepage.clickAnalyzeBtn();
	        homepage.waitForAtsResultPage();

	        navbar.clickDashboard();
		}
	}
	
	@Test
	public void verifyResumePresentOnDashboard() {
	    Assert.assertTrue(
	            dashboardpage.hasResumeHistory(),
	            "No resume is present in Resume History."
	    );
	}
	
	@Test
	public void verifyUploadedFilenameDisplayed() {

	    Assert.assertEquals(
	            dashboardpage.getResumeFileName(),
	            "TestResume1.pdf"
	    );
	}
	@Test
	public void verifyUploadDateDisplayed() {
	    Assert.assertTrue(
	            dashboardpage.isUploadDateDisplayed(),
	            "Upload date is not displayed."
	    );
	}
	@Test
	public void verifyATSScoreDisplayed() {
	    Assert.assertTrue(
	            dashboardpage.isATSScoreDisplayed(),
	            "ATS Score is not displayed."
	    );
	}
	@Test
	public void verifyResumeStatusDisplayed() {
	    Assert.assertTrue(
	            dashboardpage.isResumeStatusDisplayed(),
	            "Resume status is not displayed."
	    );
	}
	@Test
	public void verifyResumeHistoryCount() {
	    Assert.assertTrue(
	            dashboardpage.getResumeHistoryCount() > 0,
	            "Resume History should contain at least one resume."
	    );
	}
	@Test
	public void verifyResumeHistoryTableDisplayedAfterUploadingResume() {
		navbar.clickHome();
		homepage.clickUploadForm();
		homepage.selectFile(resumePath);
		homepage.clickAnalyzeBtn();
		homepage.waitForAtsResultPage();

		navbar.clickDashboard();

		Assert.assertTrue(
				dashboardpage.isResumeHistoryTableDisplayed(),
				"Resume History table is not displayed on the dashboard after uploading a resume.");
	}
	
}
