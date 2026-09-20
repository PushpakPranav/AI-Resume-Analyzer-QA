package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.ForgotPasswordPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LogoutPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.RegisterPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.ResetPasswordPage;

import base.BaseTest;
import constants.TestData;

public class ResetPasswordTest extends BaseTest {

    HomePage homePage;
    LoginPage loginPage;
    RegisterPage registerPage;
    LogoutPage logoutPage;

    private String generateUniqueEmail(String prefix) {
        return prefix + System.currentTimeMillis() + "@gmail.com";
    }

    @BeforeMethod(alwaysRun = true)
    public void init() {
        homePage = new HomePage(driver);
        loginPage = new LoginPage(driver);
        registerPage = new RegisterPage(driver);
        logoutPage = new LogoutPage(driver);
    }

    // Helper: registers a throwaway user, then walks them through
    // "forgot password" up to the dev-mode reset link click.
    
    private ResetPasswordPage startResetFlowForNewUser(String emailPrefix) {
        String email = generateUniqueEmail(emailPrefix);
        homePage.clickSignUp();
        registerPage.waitForRegisterForm();
        registerPage.register("Reset Flow User", email, TestData.VALID_PASSWORD);
        loginPage.waitForDashboard();
        logoutPage.logout();

        homePage.clickLogin();
        ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPassword();
        forgotPasswordPage.waitForForgotPasswordPage();
        forgotPasswordPage.requestPasswordReset(email);

        Assert.assertTrue(
                forgotPasswordPage.isSuccessMessageDisplayed(),
                "Forgot-password form did not show a success message."
        );

        ResetPasswordPage resetPasswordPage = forgotPasswordPage.clickDevModeResetLink();
        resetPasswordPage.waitForResetForm();
        return resetPasswordPage;
    }

    // ---------------------------------------------------------
    // Invalid / garbage token
    // ---------------------------------------------------------

    @Test
    public void verifyInvalidResetTokenShowsErrorMessage() {
        String currentUrl = driver.getCurrentUrl();
        String baseUrl = currentUrl.endsWith("/") ? currentUrl : currentUrl + "/";
        driver.get(baseUrl + "auth/reset-password/garbage-invalid-token-12345");

        ResetPasswordPage resetPasswordPage = new ResetPasswordPage(driver);
        Assert.assertTrue(
                resetPasswordPage.isInvalidTokenMessageDisplayed(),
                "Invalid reset token did not show the expected error message."
        );
    }

    // ---------------------------------------------------------
    // Full flow: request -> reset -> old password fails, new works
    // ---------------------------------------------------------

    @Test
    public void verifyFullResetFlowSucceedsAndAllowsLoginWithNewPassword() {
        String email = generateUniqueEmail("resetflow");
        homePage.clickSignUp();
        registerPage.waitForRegisterForm();
        registerPage.register("Reset Flow User", email, TestData.VALID_PASSWORD);
        
        loginPage.waitForDashboard();
        logoutPage.logout();
        
        homePage.clickLogin();
        ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPassword();
        forgotPasswordPage.waitForForgotPasswordPage();
        forgotPasswordPage.requestPasswordReset(email);

        ResetPasswordPage resetPasswordPage = forgotPasswordPage.clickDevModeResetLink();
        resetPasswordPage.waitForResetForm();

        LoginPage loginAfterReset = resetPasswordPage.resetPassword(TestData.NEW_PASSWORD_AFTER_RESET);

        Assert.assertTrue(
                loginAfterReset.isEmailFieldDisplayed() && loginAfterReset.isPasswordFieldDisplayed(),
                "Login form was not rendered after a successful password reset."
        );

        Assert.assertTrue(
                driver.findElement(org.openqa.selenium.By.tagName("body")).getText()
                        .contains("Password reset successful"),
                "Success message was not shown after a successful password reset."
        );

        // Old password must no longer work.
        loginAfterReset.loginAsInvalidUser(email, TestData.VALID_PASSWORD);
        Assert.assertEquals(
                loginAfterReset.getErrorMessage(),
                TestData.INVALID_CREDENTIALS_MESSAGE,
                "Old password still worked after a password reset."
        );

        // New password must work.
        loginAfterReset.loginAsValidUser(email, TestData.NEW_PASSWORD_AFTER_RESET);
        loginAfterReset.waitForDashboard();
        Assert.assertTrue(
                driver.getCurrentUrl().contains("/dashboard"),
                "Login with the NEW password after reset did not succeed."
        );
    }

    // ---------------------------------------------------------
    // Client-side validation: weak new password
    // ---------------------------------------------------------

    @Test
    public void verifyWeakNewPasswordShowsAlert() {
        ResetPasswordPage resetPasswordPage = startResetFlowForNewUser("resetweak");

        String alertText = resetPasswordPage.submitWithInvalidPassword("weak", "weak");

        Assert.assertTrue(
                alertText.toLowerCase().contains("password"),
                "Weak new password did not trigger the expected client-side alert."
        );
    }

    // ---------------------------------------------------------
    // Client-side validation: mismatched confirm password
    // ---------------------------------------------------------

    @Test
    public void verifyMismatchedPasswordsShowsAlert() {
        ResetPasswordPage resetPasswordPage = startResetFlowForNewUser("resetmismatch");

        String alertText = resetPasswordPage.submitWithInvalidPassword("Valid@123", "Different@123");

        Assert.assertEquals(
                alertText,
                "Passwords do not match",
                "Mismatched passwords did not trigger the expected client-side alert."
        );
    }
}