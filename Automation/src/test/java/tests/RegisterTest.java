package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LogoutPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.RegisterPage;

import base.BaseTest;
import constants.TestData;

public class RegisterTest extends BaseTest {

    RegisterPage registerPage;
    HomePage homePage;
    LogoutPage logoutPage;

    @BeforeMethod(alwaysRun = true)
    public void init() {
        registerPage = new RegisterPage(driver);
        homePage = new HomePage(driver);
        logoutPage = new LogoutPage(driver);

        homePage.clickSignUp();
        registerPage.waitForRegisterForm();
    }
    
    // ---------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------
    
    private String generateUniqueEmail(String prefix) {
        return prefix + System.currentTimeMillis() + "@gmail.com";
    }

    private DashboardPage registerValidUser() {

        String uniqueEmail = generateUniqueEmail("testuser");

        registerPage.register(
                TestData.VALID_NAME,
                uniqueEmail,
                TestData.VALID_PASSWORD
        );

        return new DashboardPage(driver);
    }
    
    
    
    // ---------------------------------------------------------
    // Registration
    // ---------------------------------------------------------

    @Test(groups = {"smoke"})
    public void verifySuccessfulRegistration() {

        DashboardPage dashboardPage = registerValidUser();

        Assert.assertTrue(
                dashboardPage.isUsernameDisplayed(),
                "Username is not displayed after successful registration."
        );

        Assert.assertTrue(
                dashboardPage.getWelcomeMessage().contains(TestData.EXPECTED_WELCOME_MESSAGE),
                "Welcome message is incorrect after successful registration."
        );
    }

    // ---------------------------------------------------------
    // Registration Form
    // ---------------------------------------------------------

    @Test(groups = {"smoke"})
    public void verifyRegisterFormIsDisplayed() {
        Assert.assertTrue(
                registerPage.isRegisterFormDisplayed(),
                "Registration form is not displayed."
        );
    }

    @Test
    public void verifyLoginLinkNavigatesToLoginPage() {

        registerPage.clickLoginLink();

        Assert.assertTrue(
                driver.getCurrentUrl().contains("auth/login"),
                "User is not redirected to Login page."
        );
    }

    // ---------------------------------------------------------
    // Required Field Validations
    // ---------------------------------------------------------

    @Test
    public void verifyEmptyNameShowsError() {

        registerPage.submitWithEmptyName(
        		TestData.EMPTY_NAME_EMAIL,
                TestData.VALID_PASSWORD
        );

        Assert.assertTrue(
                registerPage.isNameFieldInvalid(),
                "Name field validation is not displayed."
        );
    }

    @Test
    public void verifyEmptyEmailShowsError() {

        registerPage.submitWithEmptyEmail(
        		TestData.EMPTY_EMAIL_NAME,
                TestData.VALID_PASSWORD
        );

        Assert.assertTrue(
                registerPage.isEmailFieldInvalid(),
                "Email field validation is not displayed."
        );
    }

    @Test
    public void verifyEmptyPasswordShowsError() {

        registerPage.submitWithEmptyPassword(
        		TestData.EMPTY_PASSWORD_NAME,
                TestData.EMPTY_PASSWORD_EMAIL
        );

        Assert.assertTrue(
                registerPage.isPasswordFieldInvalid(),
                "Password field validation is not displayed."
        );
    }

 // ---------------------------------------------------------
 // Email Validation
 // ---------------------------------------------------------

 @Test
 public void verifyInvalidEmailFormatShowsError() {

     registerPage.submitWithInvalidEmailFormat(
             TestData.EMAIL_VALIDATION_NAME,
             TestData.INVALID_EMAIL,
             TestData.VALID_PASSWORD
     );

     Assert.assertTrue(
             registerPage.isEmailFieldInvalid(),
             "Email format validation is not displayed."
     );
 }

 @Test
 public void verifyEmailWithoutAtSymbolShowsError() {

     registerPage.submitWithInvalidEmailFormat(
             TestData.EMAIL_VALIDATION_NAME,
             TestData.EMAIL_MISSING_AT,
             TestData.VALID_PASSWORD
     );

     Assert.assertTrue(
             registerPage.isEmailFieldInvalid(),
             "Validation is not displayed for email without @ symbol."
     );
 }

 @Test
 public void verifyEmailWithoutUsernameShowsError() {

     registerPage.submitWithInvalidEmailFormat(
             TestData.EMAIL_VALIDATION_NAME,
             TestData.EMAIL_MISSING_USERNAME,
             TestData.VALID_PASSWORD
     );

     Assert.assertTrue(
             registerPage.isEmailFieldInvalid(),
             "Validation is not displayed for email without username."
     );
 }

 @Test
 public void verifyEmailWithoutDomainShowsError() {

     registerPage.submitWithInvalidEmailFormat(
             TestData.EMAIL_VALIDATION_NAME,
             TestData.EMAIL_MISSING_DOMAIN,
             TestData.VALID_PASSWORD
     );

     Assert.assertTrue(
             registerPage.isEmailFieldInvalid(),
             "Validation is not displayed for email without domain."
     );
 }

 @Test
 public void verifyEmailWithoutTLDShowsError() {

     registerPage.submitWithInvalidEmailFormat(
             TestData.EMAIL_VALIDATION_NAME,
             TestData.EMAIL_MISSING_TLD,
             TestData.VALID_PASSWORD
     );

     Assert.assertTrue(
             registerPage.isEmailFieldInvalid(),
             "Validation is not displayed for email without TLD."
     );
 }

 @Test
 public void verifyEmailWithoutDomainNameShowsError() {

     registerPage.submitWithInvalidEmailFormat(
             TestData.EMAIL_VALIDATION_NAME,
             TestData.EMAIL_MISSING_DOMAIN_NAME,
             TestData.VALID_PASSWORD
     );

     Assert.assertTrue(
             registerPage.isEmailFieldInvalid(),
             "Validation is not displayed for email without domain name."
     );
 }

 @Test
 public void verifyEmailWithSpacesShowsError() {

     registerPage.submitWithInvalidEmailFormat(
             TestData.EMAIL_VALIDATION_NAME,
             TestData.EMAIL_WITH_SPACES,
             TestData.VALID_PASSWORD
     );

     Assert.assertTrue(
             registerPage.isEmailFieldInvalid(),
             "Validation is not displayed for email containing spaces."
     );
 }

 @Test
 public void verifyEmailWithMultipleAtSymbolsShowsError() {

     registerPage.submitWithInvalidEmailFormat(
             TestData.EMAIL_VALIDATION_NAME,
             TestData.EMAIL_MULTIPLE_AT,
             TestData.VALID_PASSWORD
     );

     Assert.assertTrue(
             registerPage.isEmailFieldInvalid(),
             "Validation is not displayed for email with multiple @ symbols."
     );
 }
 
 @Test
 public void verifyEmailWithDotInUsernameIsAccepted() {

     registerPage.register(
             TestData.EMAIL_VALIDATION_NAME,
             generateUniqueEmail("test.user"),
             TestData.VALID_PASSWORD
     );

     DashboardPage dashboardPage = new DashboardPage(driver);

     Assert.assertTrue(
             dashboardPage.isUsernameDisplayed(),
             "Email with dot in username was not accepted."
     );
 }
 
 @Test
 public void verifyEmailWithPlusSymbolIsAccepted() {

     registerPage.register(
             TestData.EMAIL_VALIDATION_NAME,
             generateUniqueEmail("testuser+"),
             TestData.VALID_PASSWORD
     );

     DashboardPage dashboardPage = new DashboardPage(driver);

     Assert.assertTrue(
             dashboardPage.isUsernameDisplayed(),
             "Email with plus symbol was not accepted."
     );
 }
 
 @Test
 public void verifyEmailWithConsecutiveDotsShowsError() {
     registerPage.submitWithInvalidEmailFormat(
             TestData.INVALID_EMAIL_NAME,
             "testuser@gmail..com",
             TestData.VALID_PASSWORD
     );

     Assert.assertTrue(
             registerPage.isEmailFieldInvalid(),
             "Email with consecutive dots was accepted."
     );
 }
 
 @Test
 public void verifyEmailWithLeadingSpacesIsAccepted() {

     String email = "  " + generateUniqueEmail("leading");

     registerPage.register(
             TestData.VALID_NAME,
             email,
             TestData.VALID_PASSWORD
     );

     Assert.assertTrue(
             new DashboardPage(driver).isUsernameDisplayed(),
             "Email with leading spaces was not accepted after trimming."
     );
 }
 
 @Test
 public void verifyEmailWithTrailingSpacesIsAccepted() {

     String email = generateUniqueEmail("trailing") + "  ";

     registerPage.register(
             TestData.VALID_NAME,
             email,
             TestData.VALID_PASSWORD
     );

     Assert.assertTrue(
             new DashboardPage(driver).isUsernameDisplayed(),
             "Email with trailing spaces was not accepted after trimming."
     );
 }

    // ---------------------------------------------------------
    // Password Validation
    // ---------------------------------------------------------

 	@Test(groups = {"smoke"})
    public void verifyInvalidPasswordShowsAlert() {

        String alertText = registerPage.registerWithInvalidPassword(
        		TestData.WEAK_PASSWORD_NAME,
                TestData.WEAK_PASSWORD_EMAIL,
                TestData.WEAK_PASSWORD
        );

        Assert.assertTrue(
                alertText.toLowerCase().contains("password"),
                "Password validation alert is not displayed."
        );
    }

    @Test
    public void verifyPasswordMissingUppercaseShowsAlert() {

        String alertText = registerPage.registerWithInvalidPassword(
        		TestData.PASSWORD_RULE_NAME,
    	        generateUniqueEmail("ruleuser1"),
    	        TestData.PASSWORD_NO_UPPERCASE
        );

        Assert.assertTrue(
                alertText.toLowerCase().contains("uppercase"),
                "Uppercase validation message is not displayed."
        );
    }

    @Test
    public void verifyPasswordMissingLowercaseShowsAlert() {

        String alertText = registerPage.registerWithInvalidPassword(
        		TestData.PASSWORD_RULE_NAME,
                generateUniqueEmail("ruleuser2"),
                TestData.PASSWORD_NO_LOWERCASE
        );

        Assert.assertTrue(
                alertText.toLowerCase().contains("lowercase"),
                "Lowercase validation message is not displayed."
        );
    }

    @Test
    public void verifyPasswordMissingNumberShowsAlert() {

        String alertText = registerPage.registerWithInvalidPassword(
        		TestData.PASSWORD_RULE_NAME,
                generateUniqueEmail("ruleuser3"),
                TestData.PASSWORD_NO_NUMBER
        );

        Assert.assertTrue(
                alertText.toLowerCase().contains("number"),
                "Number validation message is not displayed."
        );
    }

    @Test
    public void verifyPasswordMissingSpecialCharShowsAlert() {

        String alertText = registerPage.registerWithInvalidPassword(
        		TestData.PASSWORD_RULE_NAME,
                generateUniqueEmail("ruleuser4"),
                TestData.PASSWORD_NO_SPECIAL_CHAR
        );

        Assert.assertTrue(
                alertText.toLowerCase().contains("special"),
                "Special character validation message is not displayed."
        );
    }

    @Test
    public void verifyPasswordTooShortShowsAlert() {

        String alertText = registerPage.registerWithInvalidPassword(
        		TestData.PASSWORD_RULE_NAME,
                generateUniqueEmail("ruleuser5"),
                TestData.PASSWORD_TOO_SHORT
        );

        Assert.assertTrue(
                alertText.toLowerCase().contains("8 characters"),
                "Minimum password length validation message is not displayed."
        );
    }
    
    @Test
    public void verifyPasswordWithExactlyEightCharactersIsAccepted() {

        String email = generateUniqueEmail("eightchar");

        registerPage.register(
                TestData.PASSWORD_RULE_NAME,
                email,
                TestData.PASSWORD_EXACTLY_EIGHT_CHARS
        );

        DashboardPage dashboardPage = new DashboardPage(driver);

        Assert.assertTrue(
                dashboardPage.isUsernameDisplayed(),
                "Password with exactly 8 characters was not accepted."
        );
    }
    
    @Test
    public void verifyPasswordWithLeadingSpacesShowsAlert() {
        String alertText = registerPage.registerWithInvalidPassword(
                TestData.PASSWORD_RULE_NAME,
                generateUniqueEmail("spacepass"),
                " Test@123"
        );

        Assert.assertTrue(
                alertText.toLowerCase().contains("password"),
                "Password with leading spaces was accepted."
        );
    }
    
    @Test
    public void verifyPasswordWithTrailingSpacesShowsAlert() {
        String alertText = registerPage.registerWithInvalidPassword(
                TestData.PASSWORD_RULE_NAME,
                generateUniqueEmail("spacepass"),
                "Test@123 "
        );

        Assert.assertTrue(
                alertText.toLowerCase().contains("password"),
                "Password with trailing spaces was accepted."
        );
    }

    // ---------------------------------------------------------
    // Duplicate Registration
    // ---------------------------------------------------------

    @Test(groups = {"smoke"})
    public void verifyDuplicateEmailShowsError() {

        String duplicateEmail = generateUniqueEmail("duplicateuser");

        registerPage.register(
        		TestData.FIRST_USER_NAME,
                duplicateEmail,
                TestData.VALID_PASSWORD
        );

        logoutPage.logout();

        homePage.clickSignUp();

        registerPage.register(
        		TestData.SECOND_USER_NAME,
                duplicateEmail,
                TestData.VALID_PASSWORD
        );

        Assert.assertTrue(
                registerPage.isRegisterFormDisplayed(),
                "Registration form is not displayed after duplicate registration."
        );

        Assert.assertTrue(
                registerPage.getErrorMessage().toLowerCase().contains("already"),
                "Duplicate email validation message is not displayed."
        );
    }
}
