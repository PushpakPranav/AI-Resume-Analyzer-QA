package tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.AtsResultPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.JdMatchResultPage;

import base.BaseTest;

public class JdMatchResultTest extends BaseTest {

    private HomePage homePage;
    private AtsResultPage atsResultPage;
    private JdMatchResultPage jdPage;

    private String jdText;
    private String resumePath;
    private String expectedDomain;
    private String resumeName;

    @BeforeClass
    public void loadData() throws IOException {

        jdText = Files.readString(Paths.get(
                System.getProperty("user.dir"),
                "src",
                "test",
                "resources",
                "TestData",
                "JD.txt"));

        resumePath = System.getProperty("user.dir")
                + "/src/test/resources/TestData/Pushpak_Pranav_QA_Resume.docx";

        expectedDomain = "Software Testing/QA";
        resumeName = "Pushpak_Pranav_QA_Resume.docx";
    }

    @BeforeMethod
    public void setupTest() {

        homePage = new HomePage(driver);
        atsResultPage = new AtsResultPage(driver);

        homePage.clickUploadForm();
        homePage.selectFile(resumePath);
        homePage.clickAnalyzeBtn();

        jdPage = atsResultPage.enterJdAndAnalyze(jdText);
    }

    @Test
    public void verifyResultPageLoaded() {

        Assert.assertTrue(
                jdPage.isPageLoaded(),
                "JD Match Result page is not loaded.");
    }

    @Test
    public void verifyMatchScoreCardDisplayed() {

        Assert.assertTrue(
                jdPage.isMatchScoreCardDisplayed(),
                "Match Score Card is not displayed.");
    }

    @Test
    public void verifyMatchPercentageDisplayed() {

        Assert.assertTrue(
                jdPage.isMatchPercentageDisplayed(),
                "Match Percentage is not displayed.");
    }

    @Test
    public void verifyMatchPercentageValue() {

        String percentage = jdPage.getMatchPercentage();

        Assert.assertTrue(
                percentage.matches("\\d+(\\.\\d+)?%"),
                "Invalid Match Percentage : " + percentage);
    }

    @Test
    public void verifyMatchPercentageRange() {

        String percentage = jdPage.getMatchPercentage()
                .replace("%", "")
                .trim();

        double value = Double.parseDouble(percentage);

        Assert.assertTrue(
                value >= 0 && value <= 100,
                "Match percentage should be between 0 and 100.");
    }

    @Test
    public void verifyScoreCircleColorClass() {

        String cls = jdPage.getScoreCircleClass();

        Assert.assertTrue(
                cls.contains("excellent")
                        || cls.contains("good")
                        || cls.contains("average")
                        || cls.contains("poor"),
                "Unexpected score circle class : " + cls);
    }

    @Test
    public void verifyScoreCircleClassNotBlank() {

        Assert.assertFalse(
                jdPage.getScoreCircleClass().isBlank(),
                "Score circle class is blank.");
    }

    @Test
    public void verifyResumeFileName() {

        Assert.assertEquals(
                jdPage.getResumeFileName(),
                resumeName,
                "Resume filename is incorrect.");
    }

    @Test
    public void verifyResumeFileNameNotBlank() {

        Assert.assertFalse(
                jdPage.getResumeFileName().isBlank(),
                "Resume filename is blank.");
    }

    @Test
    public void verifyDetectedDomain() {

        Assert.assertTrue(
                jdPage.getDetectedDomain().contains(expectedDomain),
                "Incorrect detected domain.");
    }

    @Test
    public void verifyDetectedDomainNotBlank() {

        Assert.assertFalse(
                jdPage.getDetectedDomain().isBlank(),
                "Detected domain is blank.");
    }

    @Test
    public void verifyProgressBarDisplayed() {

        Assert.assertTrue(
                jdPage.isProgressBarDisplayed(),
                "Progress bar is not displayed.");
    }

    @Test
    public void verifyProgressBarValueFormat() {

        String progress = jdPage.getProgressBarValue();

        Assert.assertTrue(
                progress.matches("\\d+(\\.\\d+)?%"),
                "Invalid progress bar value : " + progress);
    }

    @Test
    public void verifyProgressBarValue() {

        String progress = jdPage.getProgressBarValue().replace(" ", "");
        String percentage = jdPage.getMatchPercentage().replace(" ", "");

        Assert.assertEquals(
                progress,
                percentage,
                "Progress bar percentage mismatch.");
    }
    @Test
    public void verifyCountSummary() {

        String summary = jdPage.getCountSummary();

        Assert.assertTrue(
                summary.matches("(?i).*\\d+.*matched.*\\d+.*missing.*"),
                "Invalid count summary : " + summary);
    }

    @Test
    public void verifyMatchedSkillsBlock() {

        Assert.assertTrue(
                jdPage.isMatchedSkillsDisplayed(),
                "Matched Skills block is not displayed.");
    }

    @Test
    public void verifyMissingSkillsBlock() {

        Assert.assertTrue(
                jdPage.isMissingSkillsDisplayed(),
                "Missing Skills block is not displayed.");
    }

    @Test
    public void verifyFeedbackCard() {

        Assert.assertTrue(
                jdPage.isFeedbackDisplayed(),
                "AI Feedback card is not displayed.");
    }

    @Test
    public void verifyAIRewriterCard() {

        Assert.assertTrue(
                jdPage.isAIRewriterDisplayed(),
                "AI Rewriter card is not displayed.");
    }

    @Test
    public void verifySuggestionCard() {

        Assert.assertTrue(
                jdPage.isSuggestionDisplayed(),
                "Suggestion card is not displayed.");
    }

    @Test
    public void verifyAnalyzeAnotherResumeButton() {

        HomePage page = jdPage.clickAnalyzeAnotherResume();

        Assert.assertTrue(
                page.isUploadFormDisplayed(),
                "Home page was not loaded after clicking Analyze Another Resume.");
    }

    @Test
    public void verifyTopDownloadButton() {

        jdPage.clickTopDownloadReport();
        //To Do

    }

    @Test
    public void verifyBottomDownloadButton(){
        jdPage.clickBottomDownloadReport();
        //To Do
        
    };
   
    

}