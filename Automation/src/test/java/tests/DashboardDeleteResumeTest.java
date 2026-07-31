package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;

import base.BaseTest;
import components.NavbarComponent;

public class DashboardDeleteResumeTest extends BaseTest {

	LoginPage loginpage;
	HomePage homepage;
	DashboardPage dashboardpage;
	NavbarComponent navbar;

	String email = "test11@gmail.com";
	String password = "MyStr0ng@Pass!";
	String resumePath;

	@BeforeMethod
	public void init() {
		homepage = new HomePage(driver);
		loginpage = new LoginPage(driver);
		dashboardpage = new DashboardPage(driver);
		navbar = new NavbarComponent(driver);

		resumePath = System.getProperty("user.dir")
				+ "/src/test/resources/TestData/Pushpak_Pranav_QA_Resume.docx";

		homepage.clickLogin();
		loginpage.loginWithCredentials(email, password);
		if (!dashboardpage.isResumePresent()) {

	        navbar.clickHome();

	        homepage.clickUploadForm();
	        homepage.selectFile(resumePath);
	        homepage.clickAnalyzeBtn();

	        navbar.clickDashboard();
		}
	}
	@Test
	public void verifyDeleteResume() {

	    Assert.assertTrue(dashboardpage.isResumePresent());

	    dashboardpage.clickDeleteResume();
	    dashboardpage.confirmDelete();

	    driver.navigate().refresh();

	    Assert.assertFalse(
	            dashboardpage.isResumePresent(),
	            "Resume was not deleted successfully."
	    );
	}
	@Test
	public void verifyCancelDeleteResume() {

	    int before = dashboardpage.getResumeHistoryCount();

	    dashboardpage.clickDeleteResume();
	    dashboardpage.cancelDelete();

	    Assert.assertEquals(
	            dashboardpage.getResumeHistoryCount(),
	            before,
	            "Resume should not be deleted after cancelling."
	    );
	}
	@Test
	public void verifyDeleteSuccessMessageDisplayed() {

	    dashboardpage.clickDeleteResume();
	    dashboardpage.confirmDelete();

	    Assert.assertTrue(
	            dashboardpage.isDeleteSuccessMessageDisplayed(),
	            "Delete success message is not displayed."
	    );
	}
	@Test
	public void verifyNoResumeMessageDisplayed() {

	    dashboardpage.clickDeleteResume();
	    dashboardpage.confirmDelete();

	    Assert.assertEquals(
	            dashboardpage.getNoResumeMessage(),
	            "Upload Your First Resume"
	    );
	}
	@Test
	public void verifyUploadButtonDisplayedWhenNoResumeExists() {

	    dashboardpage.clickDeleteResume();
	    dashboardpage.confirmDelete();

	    Assert.assertTrue(
	            dashboardpage.isUploadBtnDisplayed()
	    );
	}
	@Test
	public void verifyDashboardStatisticsResetAfterDelete() {

	    dashboardpage.clickDeleteResume();
	    dashboardpage.confirmDelete();

	    Assert.assertEquals(
	            dashboardpage.getTotalResumeCount(),
	            "0"
	    );

	    Assert.assertEquals(
	            dashboardpage.getAverageScore(),
	            "0%"
	    );

	    Assert.assertEquals(
	            dashboardpage.getBestScore(),
	            "0%"
	    );

	    Assert.assertEquals(
	            dashboardpage.getDomainsTried(),
	            "0"
	    );
	}
}
