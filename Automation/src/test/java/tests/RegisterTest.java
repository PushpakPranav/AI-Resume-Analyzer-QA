package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LogoutPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.RegisterPage;

import base.BaseTest;

public class RegisterTest extends BaseTest {

    RegisterPage registerPage;
    HomePage homePage;
    LogoutPage logoutPage;

    @BeforeMethod
    public void init() {
        registerPage = new RegisterPage(driver);
        homePage = new HomePage(driver);
        logoutPage = new LogoutPage(driver);
        homePage.clickSignUP(); 
    }

    @Test
    public void verifySuccessfulRegistration() {
        String uniqueEmail = "testuser" + System.currentTimeMillis() + "@gmail.com";
        registerPage.register("Test User", uniqueEmail, "Test@123");

        DashboardPage dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.isUsernameDisplayed());
        Assert.assertEquals(dashboardPage.verifyWelcomeMessage(),"Welcome, Test User!");
    }

    @Test
    public void verifyEmptyNameShowsError() {
        registerPage.submitWithEmptyName("emptyname@gmail.com", "Test@123");
        Assert.assertTrue(registerPage.isFieldInvalid(registerPage.txtName));
    }

    @Test
    public void verifyEmptyEmailShowsError() {
        registerPage.submitWithEmptyEmail("Empty Email User", "Test@123");
        Assert.assertTrue(registerPage.isFieldInvalid(registerPage.txtEmail));
    }

    @Test
    public void verifyEmptyPasswordShowsError() {
        registerPage.submitWithEmptyPassword("Empty Password User", "emptypass@gmail.com");
        Assert.assertTrue(registerPage.isFieldInvalid(registerPage.txtPassword));
    }

    @Test
    public void verifyInvalidEmailFormatShowsError() {
        registerPage.submitWithInvalidEmailFormat("Invalid Email User", "not-an-email", "Test@123");
        Assert.assertTrue(registerPage.isFieldInvalid(registerPage.txtEmail));
    }

    @Test
    public void verifyInvalidPasswordShowsAlert() {
        String alertText = registerPage.invalidPassword("Weak Pass User", "weakpass@gmail.com", "123");
        Assert.assertTrue(alertText.toLowerCase().contains("password"));
    }

    @Test
    public void verifyRegisterFormIsDisplayed() {
        Assert.assertTrue(registerPage.isRegisterFormDisplayed());
    }

    @Test
    public void verifyLoginLinkNavigatesToLoginPage() {
        registerPage.clickLoginLink();
        Assert.assertTrue(driver.getCurrentUrl().contains("auth/login"));
    }
    
    
    
    @Test
    public void verifyDuplicateEmailShowsError() {
        String duplicateEmail = "duplicateuser" + System.currentTimeMillis() + "@gmail.com";

        // First registration - should succeed
        registerPage.register("First User", duplicateEmail, "Test@123");
        
        logoutPage.logout();
        // Navigate back to register page and try same email again
        homePage.clickSignUP();
        registerPage.register("Second User", duplicateEmail, "Test@123");

        Assert.assertTrue(registerPage.isRegisterFormDisplayed());
        Assert.assertTrue(registerPage.getErrorMessage().toLowerCase().contains("already"));
    }

    @Test
    public void verifyPasswordMissingUppercaseShowsAlert() {
        String alertText = registerPage.invalidPassword("Rule User", "ruleuser1" + System.currentTimeMillis() + "@gmail.com", "test@123");
        Assert.assertTrue(alertText.toLowerCase().contains("uppercase"));
    }

    @Test
    public void verifyPasswordMissingLowercaseShowsAlert() {
        String alertText = registerPage.invalidPassword("Rule User", "ruleuser2" + System.currentTimeMillis() + "@gmail.com", "TEST@123");
        Assert.assertTrue(alertText.toLowerCase().contains("lowercase"));
    }

    @Test
    public void verifyPasswordMissingNumberShowsAlert() {
        String alertText = registerPage.invalidPassword("Rule User", "ruleuser3" + System.currentTimeMillis() + "@gmail.com", "Test@Pass");
        Assert.assertTrue(alertText.toLowerCase().contains("number"));
    }

    @Test
    public void verifyPasswordMissingSpecialCharShowsAlert() {
        String alertText = registerPage.invalidPassword("Rule User", "ruleuser4" + System.currentTimeMillis() + "@gmail.com", "Test1234");
        Assert.assertTrue(alertText.toLowerCase().contains("special"));
    }

    @Test
    public void verifyPasswordTooShortShowsAlert() {
        String alertText = registerPage.invalidPassword("Rule User", "ruleuser5" + System.currentTimeMillis() + "@gmail.com", "Te@1");
        Assert.assertTrue(alertText.toLowerCase().contains("8 characters"));
    }
}
    

