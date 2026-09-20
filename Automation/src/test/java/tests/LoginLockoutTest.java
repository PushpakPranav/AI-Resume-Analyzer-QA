package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LogoutPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.RegisterPage;

import base.BaseTest;
import constants.TestData;

public class LoginLockoutTest extends BaseTest {

    HomePage homePage;
    LoginPage loginPage;

    private String generateUniqueEmail(String prefix) {
        return prefix + System.currentTimeMillis() + "@gmail.com";
    }

    @BeforeMethod(alwaysRun = true)
    public void init() {
        homePage = new HomePage(driver);
        loginPage = new LoginPage(driver);
        homePage.clickLogin();
    }

    // ---------------------------------------------------------
    // Lockout triggers after MAX_LOGIN_ATTEMPTS failures
    // ---------------------------------------------------------

    @Test
    public void verifyAccountLocksAfterMaxFailedAttempts() {
        String email = generateUniqueEmail("lockouttest");

        for (int i = 1; i <= TestData.MAX_LOGIN_ATTEMPTS; i++) {
            loginPage.loginAsInvalidUser(email, "WrongPass@123");
            String msg = loginPage.getErrorMessage();
            Assert.assertEquals(
                    msg,
                    TestData.INVALID_CREDENTIALS_MESSAGE,
                    "Attempt " + i + " did not show the standard invalid-credentials message."
            );
        }

        loginPage.loginAsInvalidUser(email, "WrongPass@123");
        String finalMsg = loginPage.getErrorMessage();

        Assert.assertEquals(
                finalMsg,
                TestData.LOCKOUT_ERROR_MESSAGE,
                "Account was not locked out after " + TestData.MAX_LOGIN_ATTEMPTS + " failed attempts."
        );
    }

    // ---------------------------------------------------------
    // Lockout blocks even the CORRECT password once triggered
    // ---------------------------------------------------------

    @Test
    public void verifyLockoutBlocksCorrectPasswordToo() {
        String name = "Lockout Real User";
        String email = generateUniqueEmail("lockoutreal");
        String correctPassword = TestData.VALID_PASSWORD;

        // Register a real account with a known password.
        RegisterPage registerPage = new RegisterPage(driver);
        homePage.clickSignUp();
        registerPage.waitForRegisterForm();
        registerPage.register(name, email, correctPassword);

        LogoutPage logoutPage = new LogoutPage(driver);
        logoutPage.logout();

        homePage.clickLogin();

        // Fail MAX_LOGIN_ATTEMPTS times with a WRONG password.
        for (int i = 1; i <= TestData.MAX_LOGIN_ATTEMPTS; i++) {
            loginPage.loginAsInvalidUser(email, "WrongPass@123");
        }

        // Even the CORRECT password should now be rejected — lockout is
        // account-level, not tied to which password was tried.
        loginPage.loginAsInvalidUser(email, correctPassword);

        Assert.assertEquals(
                loginPage.getErrorMessage(),
                TestData.LOCKOUT_ERROR_MESSAGE,
                "Lockout did not block a login attempt using the CORRECT password."
        );

        Assert.assertTrue(
                driver.getCurrentUrl().contains("auth/login"),
                "User was not kept on the Login page despite being locked out."
        );
    }
}