package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class JdMatchResultPage extends BasePage {

    public JdMatchResultPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }
    
//	  =====================
//	  Elements
//	  =====================

    @FindBy(id = "jdMatchResult")
    private WebElement matchResultHeader;

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
    
//	  =====================
//	  Methods
//	  =====================
    
    private boolean isVisible(String id) {
        boolean result = isDisplayedSafely(By.id(id), 30);
        if (!result) {
            checkForGroqFailure();
        }
        return result;
    }

//	  =====================
//	  Navigation
//	  =====================
    
    public HomePage clickAnalyzeAnotherResume() {
    	    click(analyzeAnotherResumeBtn);

    	    return new HomePage(driver);
    }
    
//	  =====================
//	  Actions
//	  =====================
    
    public File clickTopDownloadReport() throws IOException {
        return clickDownloadReport(topDownloadReportBtn);
    }

    public File clickBottomDownloadReport() throws IOException {
    	scrollToElement(bottomDownloadBtn);
    	return clickDownloadReport(bottomDownloadBtn);
    }
    
    public void waitForPageToLoad() {
        waitForVisibility(matchResultHeader,30);
    }
    
//	  =====================
//	  Getters
//	  =====================
    
    public String getMatchPercentage() {
        return getText(matchPercentage).trim();
    }

    public String getScoreCircleClass() {
        return getAttribute(matchScoreCircle, "class");
    }
    
    public String getResumeFileName() {
        return getText(resumeFileName).trim();
    }

    public String getDetectedDomain() {
        return getText(detectedDomain).trim();
    }
    
    public String getCountSummary() {
        return getText(countSummary);
    }
    
    public String getProgressBarValue() {
        String style = getAttribute(progressBar, "style");

        for (String part : style.split(";")) {
            part = part.trim();

            if (part.startsWith("width:")) {
                return part.replace("width:", "").trim();
            }
        }
        return "";
    }
    
//	  =====================
//	  Validations
//	  =====================

    public boolean isPageLoaded() {
        return isVisible("jdMatchResult");
    }

    public boolean isMatchScoreCardDisplayed() {
        return isVisible("match-score-card");
    }

    public boolean isMatchPercentageDisplayed() {
        return isVisible("match-percentage-value");
    }

    public boolean isProgressBarDisplayed() {
    	return isVisible("match-percentage-progress-bar");
    }

    public boolean isMatchedSkillsDisplayed() {
        return isVisible("match-matched-skills-block");
    }

    public boolean isMissingSkillsDisplayed() {
        return isVisible("match-missing-skills-block");
    }

    public boolean isFeedbackCardDisplayed() {
        return isVisible("ai-feedback-card");
    }

    public boolean isAIRewriterDisplayed() {
        return isVisible("ai-rewriter-card");
    }

    public boolean isSuggestionsCardDisplayed() {
        return isVisible("suggestions-card");
    }
    
    public boolean isDownloadButtonsDisplayed() {
    	return isTopDownloadButtonDisplayed()
    		    && isBottomDownloadButtonDisplayed();
    }
    
    public boolean isTopDownloadButtonDisplayed() {
        return isVisible("download-pdf-report-top-btn");
    }

    public boolean isBottomDownloadButtonDisplayed() {
        return isVisible("download-full-report-btn");
    }

    public boolean isAnalyzeAnotherResumeButtonDisplayed() {
        return isVisible("analyze-another-resume-btn");
    }
}