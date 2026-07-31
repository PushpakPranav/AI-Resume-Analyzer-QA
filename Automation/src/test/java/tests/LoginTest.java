package tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;

import base.BaseTest;

public class LoginTest extends BaseTest{
	LoginPage loginpage;
	HomePage homepage;
	
	@BeforeMethod
	public void init() {
		loginpage = new LoginPage(driver);
		homepage = new HomePage(driver);
		homepage.clickLogin();
	}
	
	@Test
	public void verifyValidLogin() {
		
		loginpage.loginWithCredentials("testname100@gmail.com","Test@123");
		loginpage.verifyLoginSuccess("Welcome, testname100!");
	}
	

}


