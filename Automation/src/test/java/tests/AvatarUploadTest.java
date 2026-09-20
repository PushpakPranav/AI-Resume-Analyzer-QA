package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.components.NavbarComponent;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.RegisterPage;

import base.BaseTest;
import constants.TestData;

public class AvatarUploadTest extends BaseTest {

    HomePage homePage;
    RegisterPage registerPage;
    NavbarComponent navbar;

    private String generateUniqueEmail(String prefix) {
        return prefix + System.currentTimeMillis() + "@gmail.com";
    }

    @BeforeMethod(alwaysRun = true)
    public void init() {
        homePage = new HomePage(driver);
        registerPage = new RegisterPage(driver);
        navbar = new NavbarComponent(driver);

        homePage.clickSignUp();
        registerPage.waitForRegisterForm();
        registerPage.register(
                "Avatar Test User",
                generateUniqueEmail("avataruser"),
                TestData.VALID_PASSWORD
        );
        navbar.isUserAvatarDisplayed(); // waits for dashboard to load
        navbar.clickAvatar();
    }

    // ---------------------------------------------------------
    // valid image uploads successfully
    // ---------------------------------------------------------

    @Test
    public void verifyValidAvatarUploadSucceeds() {
        navbar.uploadAvatar(TestData.VALID_AVATAR_IMAGE);

        Assert.assertTrue(
                navbar.isNavAvatarImageDisplayed(),
                "Navbar did not show the uploaded avatar image after a valid upload."
        );
    }

    // ---------------------------------------------------------
    // Rejected: wrong file type
    // ---------------------------------------------------------

    @Test
    public void verifyInvalidFileTypeIsRejected() {
        navbar.uploadAvatar(TestData.DASHBOARD_RESUME_PATH); // a .pdf

        String alertText = navbar.acceptAvatarUploadAlert();

        Assert.assertEquals(
                alertText,
                TestData.AVATAR_TYPE_ERROR_MESSAGE,
                "Uploading a non-image file did not show the expected error."
        );
    }

    // ---------------------------------------------------------
    // Rejected: file too large (> 2 MB)
    // ---------------------------------------------------------

    @Test
    public void verifyOversizedAvatarIsRejected() {
        navbar.uploadAvatar(TestData.OVERSIZED_AVATAR_IMAGE);

        String alertText = navbar.acceptAvatarUploadAlert();

        Assert.assertEquals(
                alertText,
                TestData.AVATAR_SIZE_ERROR_MESSAGE,
                "Uploading an oversized avatar did not show the expected error."
        );
    }
}