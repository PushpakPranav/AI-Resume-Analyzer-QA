package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.AtsResultPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;

import base.BaseTest;
import constants.TestData;

public class FileUploadValidationTest extends BaseTest {

    HomePage homePage;

    @BeforeMethod(alwaysRun = true)
    public void init() {
        homePage = new HomePage(driver);
    }

    // ---------------------------------------------------------
    // Server-side rejection: wrong file type
    // (client-side JS disables the button for this, so we bypass
    // it with a forced form.submit() to test the server check)
    // ---------------------------------------------------------

    @Test
    public void verifyServerRejectsUnsupportedFileType() {
        homePage.clickUploadForm();
        homePage.selectFileBypassingClientValidation(TestData.JD_File); // TestJD.txt — wrong extension
        homePage.forceSubmitUploadForm();

        Assert.assertTrue(
                homePage.isUploadErrorDisplayed(),
                "No error was shown for an unsupported file type."
        );
        Assert.assertEquals(
                homePage.getUploadError(),
                TestData.UPLOAD_WRONG_TYPE_ERROR,
                "Unexpected error message for an unsupported file type."
        );
    }

    // ---------------------------------------------------------
    // Server-side rejection: oversized file (> 5 MB)
    // ---------------------------------------------------------

    @Test
    public void verifyServerRejectsOversizedFile() {
        homePage.clickUploadForm();
        homePage.selectFileBypassingClientValidation(TestData.OVERSIZED_RESUME);
        homePage.forceSubmitUploadForm();

        Assert.assertTrue(
                homePage.isUploadErrorDisplayed(),
                "No error was shown for an oversized file."
        );
        Assert.assertEquals(
                homePage.getUploadError(),
                TestData.UPLOAD_OVERSIZED_ERROR,
                "Unexpected error message for an oversized file."
        );
    }

    // ---------------------------------------------------------
    // Empty file (0 bytes) — client JS doesn't catch this,
    // so a normal submit already reaches the server.
    // ---------------------------------------------------------

    @Test
    public void verifyEmptyFileIsRejected() {
        homePage.clickUploadForm();
        homePage.selectFile(TestData.EMPTY_FILE_RESUME);
        homePage.clickAnalyzeBtn();

        Assert.assertTrue(
                homePage.isUploadErrorDisplayed(),
                "No error was shown for an empty file."
        );
        Assert.assertEquals(
                homePage.getUploadError(),
                TestData.UPLOAD_EMPTY_FILE_ERROR,
                "Unexpected error message for an empty file."
        );
    }

    // ---------------------------------------------------------
    // Whitespace-only Job Description
    // ---------------------------------------------------------

    @Test
    public void verifyWhitespaceOnlyJdIsRejected() {
        homePage.clickUploadForm();
        homePage.selectFile(TestData.DASHBOARD_RESUME_PATH);
        homePage.clickAnalyzeBtn();
        homePage.waitForAtsResultPage();

        AtsResultPage atsResultPage = new AtsResultPage(driver);
        atsResultPage.enterJdText("      "); // spaces only
        atsResultPage.clickJdAnalyzeBtn();

        String alertText = atsResultPage.waitForAlert();

        Assert.assertEquals(
                alertText,
                TestData.JD_WHITESPACE_ERROR,
                "Unexpected alert message for a whitespace-only job description."
        );
    }

    // ---------------------------------------------------------
    // Completely empty Job Description (native browser validation)
    // ---------------------------------------------------------

    @Test
    public void verifyEmptyJdBlockedByBrowserValidation() {
        homePage.clickUploadForm();
        homePage.selectFile(TestData.DASHBOARD_RESUME_PATH);
        homePage.clickAnalyzeBtn();
        homePage.waitForAtsResultPage();

        AtsResultPage atsResultPage = new AtsResultPage(driver);
        atsResultPage.enterJdText("");
        atsResultPage.clickJdAnalyzeBtn();

        Assert.assertFalse(
                atsResultPage.getJdValidationMessage().isBlank(),
                "Browser did not block submission of a completely empty job description."
        );
    }
}