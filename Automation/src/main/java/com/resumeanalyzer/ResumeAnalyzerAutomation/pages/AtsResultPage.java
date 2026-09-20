package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.By;

public class AtsResultPage extends BasePage{
	public AtsResultPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
//	  =====================
//	  Elements
//	  =====================
	
	@FindBy(id="main-navbar")
	private WebElement mainNavbar;
	
	@FindBy(id = "resume-filename-badge")
	private WebElement resumeFileName;

	@FindBy(id = "detected-domain-badge")
	private WebElement detectedDomain;

	@FindBy(id = "ats-score-value")
	private WebElement atsScoreValue;

	@FindBy(id = "ats-grade-value")
	private WebElement atsGradeValue;

	@FindBy(id = "ats-matched-count")
	private WebElement atsMatchedCount;

	@FindBy(id = "ats-matched-skills-block")
	private WebElement atsMatchedSkillsBlock;

	@FindBy(id = "ats-missing-count")
	private WebElement atsMissingCount;

	@FindBy(id ="ats-missing-skills-block")
	private WebElement atsMissingSkillsBlock;

	@FindBy(id = "ats-score-progress-bar")
	private WebElement atsScoreProgressBar;

	@FindBy(id="ai-summary-block")
	private WebElement aiSummaryBlock;

	@FindBy(id="ai-summary-text")
	private WebElement aiSummaryText;
	
	@FindBy(id = "jd-match-form-card")
	private WebElement jdFormBlock;
	
	@FindBy(id ="jd-textarea")
	private WebElement jdTextArea;
	
	@FindBy(id="jd-btn")
	private WebElement jdAnalyzeBtn;
	
	@FindBy(id="upload-another-link")
	private WebElement uploadAnotherBtn;
	
	@FindBy(id="jd-error-alert") 
	private WebElement jdErrorAlert;
	
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
//	  Navigations
//	  =====================
	public HomePage clickUploadAnotherBtn(){
	    click(uploadAnotherBtn);
	    return new HomePage(driver);
	}
	
	public JdMatchResultPage enterJdAndAnalyze(String text) {
		enterJdText(text);
		clickJdAnalyzeBtn();
		return new JdMatchResultPage(driver);
	}
	
//	  =====================
//	  Actions
//	  =====================
	
	public void enterJdText(String jdText) {
		jdTextArea.clear();
		type(jdTextArea, jdText);
	}
	
	public void clickJdAnalyzeBtn() {
		scrollToElement(jdAnalyzeBtn);
		click(jdAnalyzeBtn);
	}
	
//	  =====================
//	  Getters
//	  =====================
	
	public String getResumeFileName() {
		return getText(resumeFileName).trim();
	}
	
	public String getDetectedDomain() {
		return getText(detectedDomain);
	}
	
	public double getAtsScoreValue() {
		return getPercentage(atsScoreValue);
	}
	
	public String getAtsGradeValue() {
		return getText(atsGradeValue);
	}
	
	public String getAiSummaryText() {
		return getText(aiSummaryText);
	}
	
	public int getAtsMissingCount() {
		return Integer.parseInt(getText(atsMissingCount));
	}
	
	public String getJdTextAreaPlaceholder() {
		return getAttribute(jdTextArea, "placeholder");
	}
	
	public String getJdText() {
		return getAttribute(jdTextArea, "value");
	}
	
	public String getProgressBarValue() {
	    String style = getAttribute(atsScoreProgressBar, "style");
	    return style.replaceAll(".*width:\\s*([0-9.]+)%;.*", "$1");
	}
	
	public int getAtsMatchedCount() {
	    return Integer.parseInt(getText(atsMatchedCount));
	}
	
	public String getJdErrorMessage() {
	    return getText(jdErrorAlert);
	}

	public String getJdValidationMessage() {
	    return getAttribute(jdTextArea, "validationMessage");
	}
	
	
	
//	  =====================
//	  Validations
//	  =====================
	
	public boolean isResultPageLoaded() {
	    return isDisplayed(mainNavbar)
	            && isDisplayed(atsScoreValue)
	            && isDisplayed(detectedDomain)
	            && isDisplayed(aiSummaryText);
	}
	
	public boolean isMainNavbarDisplayed() {
	    return isVisible("main-navbar");
	}
	
	public boolean isDetectedDomainDisplayed() {
	    return isVisible("detected-domain-badge");
	}

	public boolean isAtsScoreValueDisplayed() {
	    return isVisible("ats-score-value");
	}

	public boolean isAtsGradeValueDisplayed() {
	    return isVisible("ats-grade-value");
	}
	
	public boolean isAtsMatchedCountDisplayed() {
	    return isVisible("ats-matched-count");
	}
	
	public boolean isAtsMatchedSkillsBlockDisplayed() {
	    return isVisible("ats-matched-skills-block");
	}
	
	public boolean isAtsMissingCountDisplayed() {
	    return isVisible("ats-missing-count");
	}
	
	public boolean isAtsMissingSkillsBlockDisplayed() {
	    return isVisible("ats-missing-skills-block");
	}
	
	public boolean isAtsScoreProgressBarDisplayed() {
	    return isVisible("ats-score-progress-bar");
	}
	
	public boolean isAiSummaryDisplayed() {
	    return isVisible("ai-summary-text");
	}
	
	public boolean isAiSummaryBlockDisplayed() {
	    return isVisible("ai-summary-block");
	}
	
	public boolean isJDMatchFormDisplayed() {
	    return isVisible("jd-match-form-card");
	}
	
	public boolean isJDTextAreaEnabled() {
		return isEnabled(jdTextArea);
	}
	
	public boolean isJdAnalyzeBtnEnabled() {
		return isEnabled(jdAnalyzeBtn);
	}
	
	public boolean isJdAnalyzeBtnDisplayed() {
	    return isVisible("jd-btn");
	}
	
	public boolean isUploadAnotherBtnDisplayed() {
	    return isVisible("upload-another-link");
	}

	public void waitForJdPage() {
		waitForURLContains("analysis/match",30);
		
	}	
	
	public boolean isJdErrorDisplayed() {
	    return isDisplayedSafely(By.id("jd-error-alert"), 10);
	}
	

}
