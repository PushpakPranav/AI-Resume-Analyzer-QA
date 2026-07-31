package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.ForgotPasswordPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.RegisterPage;

import base.BaseTest;

public class LoginTest extends BaseTest {
    LoginPage loginpage;
    HomePage homepage;
    RegisterPage registerPage;

    @BeforeMethod
    public void init() {
        loginpage = new LoginPage(driver);
        homepage = new HomePage(driver);
        registerPage = new RegisterPage(driver);
        homepage.clickLogin();
    }

    @Test
    public void verifyValidLogin() {
        loginpage.loginWithCredentials("testname100@gmail.com", "Test@123");
        Assert.assertEquals(loginpage.getLoginSuccess(), "Welcome, testname100!");
    }

    @Test
    public void verifyInvalidPasswordShowsError() {
        loginpage.loginWithCredentials("testname100@gmail.com", "WrongPass@123");
        Assert.assertTrue(driver.getCurrentUrl().contains("auth/login"));
    }

    @Test
    public void verifyNonExistentEmailShowsError() {
        loginpage.loginWithCredentials("doesnotexist" + System.currentTimeMillis() + "@gmail.com", "Test@123");
        Assert.assertTrue(driver.getCurrentUrl().contains("auth/login"));
    }

    @Test
    public void verifyEmptyEmailFieldValidation() {
        loginpage.loginWithCredentials("", "Test@123");
        Assert.assertTrue(driver.getCurrentUrl().contains("auth/login"));
    }

    @Test
    public void verifyEmptyPasswordFieldValidation() {
        loginpage.loginWithCredentials("testname100@gmail.com", "");
        Assert.assertTrue(driver.getCurrentUrl().contains("auth/login"));
    }

    @Test
    public void verifyWelcomeTextDisplayedOnLoad() {
        Assert.assertNotNull(loginpage.getWelcomeText());
    }

    @Test
    public void verifyNoAccountTextDisplayed() {
        Assert.assertTrue(loginpage.getNoAccountText().toLowerCase().contains("account"));
    }

    @Test
    public void verifySignUpLinkNavigatesToRegisterPage() {
        loginpage.clickSignUpLink();
        Assert.assertTrue(driver.getCurrentUrl().contains("auth/register"));
    }

    @Test
    public void verifyForgotPasswordLinkNavigation() {
        ForgotPasswordPage forgotPasswordPage = loginpage.clickForgotPassword();
        Assert.assertTrue(driver.getCurrentUrl().contains("forgot-password"));
    }
}