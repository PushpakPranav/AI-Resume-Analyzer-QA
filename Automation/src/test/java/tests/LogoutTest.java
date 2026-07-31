package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LogoutPage;

import base.BaseTest;

public class LogoutTest extends BaseTest {

    HomePage homepage;
    LoginPage loginpage;
    LogoutPage logoutpage;

    String email = "testname100@gmail.com";
    String password = "Test@123";

    @BeforeMethod
    public void init() {

        homepage = new HomePage(driver);
        loginpage = new LoginPage(driver);
        logoutpage = new LogoutPage(driver);

        homepage.clickLogin();
        loginpage.loginWithCredentials(email, password);
    }

    @Test
    public void verifyUserCanLogout() {

        logoutpage.logout();

        Assert.assertTrue(
                logoutpage.isLoginDisplayed(),
                "Login button should be visible after logout."
        );

        Assert.assertTrue(
                logoutpage.isSignUpDisplayed(),
                "Sign Up button should be visible after logout."
        );
    }

    @Test
    public void verifyLogoutRedirectsToHomePage() {

        logoutpage.logout();

        Assert.assertTrue(
                driver.getCurrentUrl().endsWith("/"),
                "User is not redirected to Home page after logout."
        );
    }

    @Test
    public void verifyDashboardNotAccessibleAfterLogout() {

        logoutpage.logout();

        driver.get("http://127.0.0.1:8000/dashboard");

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/auth/login"),
                "Unauthenticated user should be redirected to Login."
        );
    }
    @Test
    public void verifyDashboardNotAccessibleUsingBrowserBack() {

        logoutpage.logout();

        driver.navigate().back();

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/auth/login"),
                "User should be redirected to Login page after pressing browser back."
        );
    }
    @Test
    public void verifyRefreshAfterLogout() {

        logoutpage.logout();

        driver.navigate().refresh();

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/"),
                "User should remain logged out after refresh."
        );
    }
}
