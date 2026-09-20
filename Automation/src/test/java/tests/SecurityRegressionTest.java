package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.RegisterPage;

import base.BaseTest;
import constants.TestData;

public class SecurityRegressionTest extends BaseTest {

    HomePage homePage;
    LoginPage loginPage;
    RegisterPage registerPage;

    private String generateUniqueEmail(String prefix) {
        return prefix + System.currentTimeMillis() + "@gmail.com";
    }

    @BeforeMethod(alwaysRun = true)
    public void init() {
        homePage = new HomePage(driver);
        loginPage = new LoginPage(driver);
        registerPage = new RegisterPage(driver);
    }

    // ---------------------------------------------------------
    // SQL Injection — login email field
    // ---------------------------------------------------------

    @Test
    public void verifySqlInjectionInEmailFieldIsRejected() {
        homePage.clickLogin();
        loginPage.loginAsInvalidUser("'OR'1'='1'--@test.com", "anything123");

        Assert.assertEquals(
                loginPage.getErrorMessage(),
                TestData.INVALID_CREDENTIALS_MESSAGE,
                "SQL injection payload in the email field was not safely rejected."
        );
    }

    // ---------------------------------------------------------
    // SQL Injection — login password field
    // ---------------------------------------------------------

    @Test
    public void verifySqlInjectionInPasswordFieldIsRejected() {
        homePage.clickLogin();
        loginPage.loginAsInvalidUser(TestData.VALID_EMAIL, "' OR '1'='1' --");

        Assert.assertEquals(
                loginPage.getErrorMessage(),
                TestData.INVALID_CREDENTIALS_MESSAGE,
                "SQL injection payload in the password field was not safely rejected."
        );
    }

    // ---------------------------------------------------------
    // XSS — register name field rendered as safe literal text
    // ---------------------------------------------------------

    @Test
    public void verifyXssInNameFieldIsRenderedAsPlainText() {
        String maliciousName = "<script>alert('XSS')</script>";
        String email = generateUniqueEmail("xsstest");

        homePage.clickSignUp();
        registerPage.waitForRegisterForm();
        DashboardPage dashboardPage = registerPage.register(maliciousName, email, TestData.VALID_PASSWORD);

        // If the payload had actually executed as JS, a real alert() would
        // pop up here and the next call would throw UnhandledAlertException
        // before we even reach the assertion below.
        String welcomeText = dashboardPage.getWelcomeMessage();

        Assert.assertEquals(
                welcomeText,
                "Welcome, " + maliciousName + "!",
                "The <script> payload was not rendered as safe literal text — possible XSS vulnerability."
        );
    }
}