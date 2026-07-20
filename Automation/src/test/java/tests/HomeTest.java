package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;

import base.BaseTest;
import components.NavbarComponent;


public class HomeTest extends BaseTest{
	HomePage homepage;
	LoginPage loginpage;
	NavbarComponent navbar;
	DashboardPage dashboardpage;
	
	@BeforeMethod
	public void init() {
		homepage = new HomePage(driver);
		loginpage = new LoginPage(driver);
		navbar = new NavbarComponent(driver);
		dashboardpage = new DashboardPage(driver);
		
	}
	
	@Test
	public void verifyLogin() {
		
		homepage.clickLogin();
		Assert.assertTrue(driver.getCurrentUrl().contains("login"),"User is not redirected to Login page.");
	}
	
	@Test
	public void verifyResumeUploadFormDisplayed()  {
		Assert.assertTrue(homepage.isUploadFormDisplayed(),"Resume Upload Form is not displayed.");
		homepage.verifyUploadInstructionText();
	}
	@Test
	public void verifyAnalyzeBtnDisplayed() {
		Assert.assertTrue(homepage.isAnalyzeBtnDisplayed(),"Analyze Button is not displayed");
	}
	@Test
	public void verifyAnalyzeBtnDisabled() {
		Assert.assertFalse(homepage.isAnalyzeBtnEnabled(),"Analyze button is Enabled");
	}
	
	
	@Test
	public void verifyFileNamePreview() {
		homepage.selectFile("C:\\Users\\pushp\\Downloads\\StrongResume (1).pdf");
		
		Assert.assertEquals(
			    homepage.getSelectedFileName(),
			    "StrongResume (1).pdf",
			    "Selected file name is not displayed correctly."
			);
	}
	
	@Test
	public void verifyAnalyzeBtnEnabledAfterFileSelection() {
		homepage.selectFile("C:\\Users\\pushp\\Downloads\\StrongResume (1).pdf");
		Assert.assertTrue(
			    homepage.isAnalyzeBtnEnabled(),
			    "Analyze button did not become enabled after selecting a valid resume."
			);
	}
	
	@Test
	public void verifyPreviouslySelectedFileIsReplaced() {
		homepage.selectFile("C:\\Users\\pushp\\Downloads\\StrongResume (1).pdf");
		
		String firstFile = homepage.getSelectedFileName();
		homepage.selectFile("C:\\Users\\pushp\\Downloads\\Perfect_Match_Resume.pdf");
		String secondFile = homepage.getSelectedFileName();
		Assert.assertNotEquals(
			    firstFile,
			    secondFile,
			    "Previously selected file was not replaced."
			);

			Assert.assertEquals(
			    secondFile,
			    "Perfect_Match_Resume.pdf",
			    "Displayed file name is incorrect."
			);
		
		
	}
	
	@Test
	public void verifyFileNameUpdateAfterReselectingFile() {
		homepage.selectFile("C:\\Users\\pushp\\Downloads\\StrongResume (1).pdf");
		String firstFile = homepage.getSelectedFileName();
		
		homepage.selectFile("C:\\Users\\pushp\\Downloads\\Perfect_Match_Resume.pdf");
		String secondFile = homepage.getSelectedFileName();
		
		Assert.assertNotEquals(firstFile, secondFile, "File name did not update after reselecting a different file ");
	}
	@Test
	public void verifySupportedFileTypeText() {
		homepage.supportedFileTypeText("Supports PDF and DOCX");
	}
	@Test
	public void verifyHowItWorksSectionDisplayed() {
		
		Assert.assertTrue(homepage.howItWorksSectionDisplayed(),"How It Works section is noy Displayed");
	}
	@Test
	public void verifyAllFourStepsDisplayedInHowItWorksSection() {
		Assert.assertTrue(homepage.areAllFourStepsDisplayed(),"All four steps are not displayed in How It Works Section");
	}
	
	
	@Test
	public void verifyLoginAndSignupDisplayedWhenUserNotLoggedIn() {

	    Assert.assertTrue(
	            navbar.isLoginDisplayed(),
	            "Login option is not displayed."
	    );

	    Assert.assertTrue(
	            navbar.isSignUpDisplayed(),
	            "Sign Up option is not displayed."
	    );
	}
	
	
	@Test
	public void verifyAfterLoginSignUpLoginOptionsAreNotDisplayed() {
		navbar.clickLogin();
		loginpage.loginWithCredentials("testname100@gmail.com","Test@123");
		navbar.clickHome();
		Assert.assertFalse(navbar.isLoginDisplayed());
		Assert.assertFalse(navbar.isSignUpDisplayed());
	}
	
	
	@Test
	public void verifyUserAvatarDisplayedAfterLogin() {
		navbar.clickLogin();
		loginpage.loginWithCredentials("testname100@gmail.com","Test@123");
		Assert.assertTrue(navbar.isUserAvatarDisplayed(), "User avatar is not displayed after login");
	}
	
	
	
	
	
	
	@Test
	public void verifyUsernameDisplayedAfterLogin() {
		navbar.clickLogin();
		loginpage.loginWithCredentials("testname100@gmail.com","Test@123");
		Assert.assertTrue(dashboardpage.isUsernameDisplayed(),"Username is not displayed after Login");
		
	}
	
	
	
	@Test
	public void verifyDashboardDisplayedAfterLogin() {
		navbar.clickLogin();
		loginpage.loginWithCredentials("testname100@gmail.com","Test@123");
		navbar.clickHome();
		Assert.assertTrue(homepage.isDashboardDisplayed(),"dashboard is not displayed after login");
	}
	
	@Test
	public void verifyLogoutOptionDisplayed() {
		navbar.clickLogin();
		loginpage.loginWithCredentials("testname100@gmail.com","Test@123");
		navbar.clickHome();
		navbar.clickAvatar();
		Assert.assertTrue(navbar.isLogoutDisplayed(),"Logout option is not displayed after clicking on user avatar");
	}
	
	
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

