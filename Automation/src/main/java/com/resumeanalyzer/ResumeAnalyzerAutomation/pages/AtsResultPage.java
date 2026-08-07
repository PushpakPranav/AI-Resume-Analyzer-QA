package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

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

	@FindBy(id = "ats-score-progress")
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
	
//	  =====================
//	  Methods
//	  =====================
	
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
		return getText(resumeFileName);
	}
	
	public String getDetectedDomain() {
		return getText(detectedDomain);
	}
	
	public int getAtsScoreValue() {
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
	    return getAttribute(atsScoreProgressBar, "aria-valuenow");
	}
	
	public int getAtsMatchedCount() {
	    return Integer.parseInt(getText(atsMatchedCount));
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
		return isDisplayed(mainNavbar);
	}
	
	public boolean isDetectedDomainDisplayed() {
		return isDisplayed(detectedDomain);	
	}
	
	public boolean isAtsScoreValueDisplayed() {
		return isDisplayed(atsScoreValue);
	}
	
	public boolean isAtsGradeValueDisplayed() {
		return isDisplayed(atsGradeValue);
	}
	
	public boolean isAtsMatchedCountDisplayed() {
		return isDisplayed(atsMatchedCount);
	}
	
	public boolean isAtsMatchedSkillsBlockDisplayed() {
		return isDisplayed(atsMatchedSkillsBlock);
	}
	
	public boolean isAtsMissingCountDisplayed() {
		return isDisplayed(atsMissingCount);
	}
	
	public boolean isAtsMissingSkillsBlockDisplayed() {
		return isDisplayed(atsMissingSkillsBlock);
	}
	
	public boolean isAtsScoreProgressBarDisplayed() {
		return isDisplayed(atsScoreProgressBar);
	}
	public boolean isAiSummaryDisplayed() {
	    return isDisplayed(aiSummaryText);
	}
	
	public boolean isAiSummaryBlockDisplayed() {
		return isDisplayed(aiSummaryBlock);
	}
	
	public boolean isJDMatchFormDisplayed() {
		return isDisplayed(jdFormBlock);
	}
	
	public boolean isJDTextAreaEnabled() {
		return isEnabled(jdTextArea);
	}
	
	public boolean isJdAnalyzeBtnEnabled() {
		return isEnabled(jdAnalyzeBtn);
	}
	
	public boolean isJdAnalyzeBtnDisplayed() {
		return isDisplayed(jdAnalyzeBtn);
	}
	
	public boolean isUploadAnotherBtnDisplayed() {
		return isDisplayed(uploadAnotherBtn);
	}	

}
