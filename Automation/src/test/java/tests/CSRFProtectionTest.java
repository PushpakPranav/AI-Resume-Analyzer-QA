package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.components.NavbarComponent;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.RegisterPage;

import base.BaseTest;
import constants.TestData;

public class CSRFProtectionTest extends BaseTest {

    HomePage homePage;
    RegisterPage registerPage;
    LoginPage loginPage;
    NavbarComponent navbar;
    DashboardPage dashboardPage;

    @BeforeMethod(alwaysRun = true)
    public void init() {
        homePage = new HomePage(driver);
        registerPage = new RegisterPage(driver);
        loginPage = new LoginPage(driver);
        navbar = new NavbarComponent(driver);
        dashboardPage = new DashboardPage(driver);
        homePage.clickLogin();
    }

    // ---------------------------------------------------------
    // Regression: CSRF token is present on page load
    // ---------------------------------------------------------

    @Test
    public void verifyLoginPageIncludesCsrfToken() {
        String token = loginPage.getCsrfTokenValue();
        Assert.assertNotNull(token, "Login page did not include a CSRF token field.");
        Assert.assertFalse(token.trim().isEmpty(), "Login page's CSRF token was empty.");
    }

    // ---------------------------------------------------------
    // Security: tampered/forged CSRF token is rejected
    // ---------------------------------------------------------

    @Test
    public void verifyLoginRejectsTamperedCsrfToken() {
        loginPage.tamperCsrfToken("tampered_invalid_csrf_token_12345");

        // Using VALID credentials deliberately — proves the rejection is
        // caused by the CSRF check itself, not by wrong credentials.
        loginPage.loginAsInvalidUser(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);

        Assert.assertEquals(
                loginPage.getErrorMessage(),
                TestData.CSRF_SESSION_EXPIRED_MESSAGE,
                "Login did not reject a request with a tampered CSRF token."
        );

        Assert.assertTrue(
                driver.getCurrentUrl().contains("auth/login"),
                "User was not kept on the Login page despite a tampered CSRF token."
        );
    }
    
    
    
    
    @Test
    public void verifyResumeDeleteRejectsTamperedCsrfToken() {
        // ---- Setup: register + upload a resume so a delete form exists ----
        homePage.clickSignUp();
        registerPage.waitForRegisterForm();
        registerPage.register(
                "CSRF Delete User",
                "csrfdeluser" + System.currentTimeMillis() + "@gmail.com",
                TestData.VALID_PASSWORD
        );
        loginPage.waitForDashboard();
        navbar.clickHome();
        homePage.clickUploadForm();
        homePage.selectFile(TestData.DASHBOARD_RESUME_PATH);
        homePage.clickAnalyzeBtn();
        homePage.waitForAtsResultPage();

        com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage dashboardPage =
                new com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage(driver);

        navbar.clickDashboard();

        int countBefore = dashboardPage.getResumeHistoryCount();

        dashboardPage.tamperFirstDeleteFormCsrfToken("tampered_invalid_token_12345");
        dashboardPage.clickDeleteResume();
        dashboardPage.confirmDelete();

        // The route raises a raw 403 with no redirect, so the browser stays
        // on the /resume/delete/{id} URL and shows the JSON error body.
        Assert.assertTrue(
                driver.getCurrentUrl().contains("/resume/delete/"),
                "Browser did not stay on the delete endpoint after a tampered CSRF submission."
        );
        Assert.assertTrue(
                driver.findElement(org.openqa.selenium.By.tagName("body")).getText()
                        .contains("Invalid or missing CSRF token"),
                "Expected CSRF rejection message was not shown."
        );

        // Confirm the resume was NOT actually deleted.
        driver.get(driver.getCurrentUrl().replaceAll("/resume/delete/\\d+", "/dashboard"));

        Assert.assertEquals(
                dashboardPage.getResumeHistoryCount(),
                countBefore,
                "Resume was deleted despite a tampered CSRF token."
        );
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}