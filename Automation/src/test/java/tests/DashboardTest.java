package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;

import base.BaseTest;
import components.NavbarComponent;

public class DashboardTest extends BaseTest {

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
		public void verifyDashboardUrl() {
		    Assert.assertTrue(
		            driver.getCurrentUrl().contains("/dashboard"),
		            "Dashboard URL is incorrect."
		    );
		}
		@Test
		public void verifyWelcomeMessageDisplayedAfterLogin() {
			Assert.assertEquals(dashboardpage.verifyWelcomeMessage(),"Welcome, Jha!","Welcome Message not displayed");
		}

		@Test
		public void verifyUsernameDisplayedOnDashboard() {
			Assert.assertTrue(
					dashboardpage.isUsernameDisplayed(),
					"Welcome/username text is not displayed on the dashboard.");
		}
		@Test
		public void verifyUserAvatarDisplayedOnDashboard() {
			Assert.assertTrue(
					dashboardpage.isUserAvatarDisplayed(),
					"User avatar is not displayed on the dashboard after login.");
		}
		@Test
		public void verifyUploadNewResumeBtnDisplayed() {
			Assert.assertTrue(
					dashboardpage.isUploadBtnDisplayed(),
					"'Upload New Resume' button is not displayed on the dashboard.");
		}

		@Test
		public void verifyResumeHistoryHeadingDisplayed() {
			Assert.assertTrue(
					dashboardpage.isResumeHistoryHeadingDisplayed(),
					"'Resume History' heading is not displayed on the dashboard.");
		}

		

		@Test
		public void verifyLoginOptionNotDisplayedOnDashboard() {
			Assert.assertFalse(
					dashboardpage.isLoginDisplayed(),
					"Login option should not be displayed once the user is logged in.");
		}

		@Test
		public void verifySignUpOptionNotDisplayedOnDashboard() {
			Assert.assertFalse(
					dashboardpage.isSignUpDisplayed(),
					"Sign Up option should not be displayed once the user is logged in.");
		}
		@Test
		public void verifyDashboardAfterRefresh() {

		    driver.navigate().refresh();

		    Assert.assertTrue(
		            dashboardpage.isUsernameDisplayed(),
		            "Dashboard should remain accessible after refresh."
		    );
		}
		@Test
		public void verifyDomainsDetectedCardDisplayed() {

		    Assert.assertTrue(
		            dashboardpage.isDomainsDetectedCardDisplayed()
		    );
		}

	}
