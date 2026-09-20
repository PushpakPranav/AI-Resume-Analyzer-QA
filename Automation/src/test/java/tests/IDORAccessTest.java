package tests;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.components.NavbarComponent;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.AtsResultPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HistoryPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LogoutPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.RegisterPage;

import base.BaseTest;
import constants.TestData;

public class IDORAccessTest extends BaseTest {

    HomePage homePage;
    LoginPage loginPage;
    RegisterPage registerPage;
    AtsResultPage atsResultPage;
    LogoutPage logoutPage;
    NavbarComponent navbar;

    String userAHistoryUrl;
    String userAReportUrl;

    private String generateUniqueEmail(String prefix) {
        return prefix + System.currentTimeMillis() + "@gmail.com";
    }

    @BeforeMethod(alwaysRun = true)
    public void init() throws IOException {
        homePage = new HomePage(driver);
        loginPage = new LoginPage(driver);
        registerPage = new RegisterPage(driver);
        atsResultPage = new AtsResultPage(driver);
        logoutPage = new LogoutPage(driver);
        navbar = new NavbarComponent(driver);

        // ---- Setup: User A registers, uploads a resume, runs a JD match ----
        homePage.clickSignUp();
        registerPage.waitForRegisterForm();
        registerPage.register(
                "IDOR User A",
                generateUniqueEmail("idorusera"),
                TestData.VALID_PASSWORD
        );

        DashboardPage dashboardPage = new DashboardPage(driver);
        loginPage.waitForDashboard();
        navbar.clickHome();
        homePage.clickUploadForm();
        homePage.selectFile(TestData.DASHBOARD_RESUME_PATH);
        homePage.clickAnalyzeBtn(); 
        homePage.waitForAtsResultPage();
        String jdText = java.nio.file.Files.readString(java.nio.file.Paths.get(TestData.JD_File));
        com.resumeanalyzer.ResumeAnalyzerAutomation.pages.JdMatchResultPage jdMatchResultPage =
                atsResultPage.enterJdAndAnalyze(jdText);
        jdMatchResultPage.waitForPageToLoad();

        navbar.clickDashboard();

        HistoryPage historyPage = dashboardPage.clickFirstHistoryButton();
        historyPage.waitForHistoryPage();

        // Capture User A's private URLs before switching users
        userAHistoryUrl = driver.getCurrentUrl();
        userAReportUrl = historyPage.getFirstDownloadReportUrl();

        // ---- Switch to User B ----
        logoutPage.logout();

        homePage.clickSignUp();
        registerPage.waitForRegisterForm();
        registerPage.register(
                "IDOR User B",
                generateUniqueEmail("idoruserb"),
                TestData.VALID_PASSWORD
        );
    }

    // ---------------------------------------------------------
    // IDOR — Analysis History
    // ---------------------------------------------------------

    @Test
    public void verifyUserCannotAccessAnotherUsersAnalysisHistory() {
        driver.get(userAHistoryUrl);

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/dashboard"),
                "User B was not redirected away from User A's analysis history. Current URL: " + driver.getCurrentUrl()
        );

        Assert.assertFalse(
                driver.getPageSource().contains("history-title"),
                "User B's browser rendered the actual History page content for User A's resume."
        );
    }

    // ---------------------------------------------------------
    // IDOR — Analysis Report (PDF)
    // ---------------------------------------------------------

    @Test
    public void verifyUserCannotDownloadAnotherUsersReport() {
        driver.get(userAReportUrl);

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/dashboard"),
                "User B was not redirected away when trying to download User A's report. Current URL: " + driver.getCurrentUrl()
        );
    }
}