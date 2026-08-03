package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LogoutPage;

import base.BaseTest;

public class UnauthorizedAccessTest extends BaseTest {

    HomePage homepage;
    LoginPage loginpage;
    LogoutPage logoutpage;

    @BeforeMethod
    public void init() {

        homepage = new HomePage(driver);
        loginpage = new LoginPage(driver);
        logoutpage = new LogoutPage(driver);
    }
    
    
	@Test
	public void verifyDashboardRequiresLogin() {

	    driver.get("http://127.0.0.1:8000/dashboard");

	    Assert.assertTrue(
	            driver.getCurrentUrl().contains("/auth/login")
	    );
	}
	@Test
	public void verifyDashboardAfterLogout() {

	    homepage.clickLogin();
	    loginpage.loginWithCredentials(
	            "testname100@gmail.com",
	            "Test@123"
	    );

	    logoutpage.logout();

	    driver.get("http://127.0.0.1:8000/dashboard");

	    Assert.assertTrue(
	            driver.getCurrentUrl().contains("/auth/login"),
	            "Dashboard should not be accessible after logout."
	    );
	}
	@Test
	public void verifyDirectLoginPageAccessibleWithoutAuthentication() {

	    driver.get("http://127.0.0.1:8000/auth/login");

	    Assert.assertTrue(
	    	    driver.getCurrentUrl().contains("/auth/login"),
	    	    "Unauthenticated user should be redirected to Login page."
	    	);
	}
}
