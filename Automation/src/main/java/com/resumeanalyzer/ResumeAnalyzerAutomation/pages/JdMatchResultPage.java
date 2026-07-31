package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class JdMatchResultPage extends BasePage {

    public JdMatchResultPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // Header
    @FindBy(id = "jdMatchResult")
    private WebElement matchResultHeader;

    // Score Card
    @FindBy(id = "match-score-card")
    private WebElement matchScoreCard;

    @FindBy(id = "download-pdf-report-top-btn")
    private WebElement topDownloadReportBtn;

    @FindBy(id = "match-percentage-value")
    private WebElement matchPercentage;

    @FindBy(id = "match-score-circle")
    private WebElement matchScoreCircle;

    @FindBy(id = "match-resume-filename-badge")
    private WebElement resumeFileName;

    @FindBy(id = "match-detected-domain-badge")
    private WebElement detectedDomain;

    @FindBy(id = "match-percentage-progress-bar")
    private WebElement progressBar;

    @FindBy(id = "match-summary-counts")
    private WebElement countSummary;

    @FindBy(id = "match-matched-skills-block")
    private WebElement matchedSkills;

    @FindBy(id = "match-missing-skills-block")
    private WebElement missingSkills;

    @FindBy(id = "ai-feedback-card")
    private WebElement feedbackCard;

    @FindBy(id = "ai-rewriter-card")
    private WebElement aiRewriterCard;

    @FindBy(id = "suggestions-card")
    private WebElement suggestionCard;

    @FindBy(id = "analyze-another-resume-btn")
    private WebElement analyzeAnotherResumeBtn;

    @FindBy(id = "download-full-report-btn")
    private WebElement bottomDownloadBtn;

    // ================= Validation =================

    public boolean isPageLoaded() {
        return isDisplayed(matchResultHeader);
    }

    public boolean isMatchScoreCardDisplayed() {
        return isDisplayed(matchScoreCard);
    }

    public boolean isMatchPercentageDisplayed() {
        return isDisplayed(matchPercentage);
    }

    public String getMatchPercentage() {
        return getText(matchPercentage).trim();
    }

    public String getScoreCircleClass() {
        return getAtribute(matchScoreCircle, "class");
    }

    public String getResumeFileName() {
        return getText(resumeFileName).trim();
    }

    public String getDetectedDomain() {
        return getText(detectedDomain).trim();
    }

    public boolean isProgressBarDisplayed() {
        return isDisplayed(progressBar);
    }

    public String getProgressBarValue() {

        String style = getAtribute(progressBar, "style");

        for (String value : style.split(";")) {

            if (value.trim().startsWith("width")) {
                return value.replace("width:", "").trim();
            }
        }

        return "";
    }

    public String getCountSummary() {
        return getText(countSummary);
    }

    public boolean isMatchedSkillsDisplayed() {
        return isDisplayed(matchedSkills);
    }

    public boolean isMissingSkillsDisplayed() {
        return isDisplayed(missingSkills);
    }

    public boolean isFeedbackDisplayed() {
        return isDisplayed(feedbackCard);
    }

    public boolean isAIRewriterDisplayed() {
        return isDisplayed(aiRewriterCard);
    }

    public boolean isSuggestionDisplayed() {
        return isDisplayed(suggestionCard);
    }

    // ================= Actions =================

    public File clickTopDownloadReport() throws IOException{
    	
        return clickDownloadReport(topDownloadReportBtn);
    }

    public File clickBottomDownloadReport() throws IOException {
    	scrollToElement(bottomDownloadBtn);
    	return clickDownloadReport(bottomDownloadBtn);
    }

    public HomePage clickAnalyzeAnotherResume() {
        click(analyzeAnotherResumeBtn);
        return new HomePage(driver);
    }
    
}