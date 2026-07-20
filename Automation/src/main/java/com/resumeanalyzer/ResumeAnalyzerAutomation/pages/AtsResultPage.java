package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class AtsResultPage extends BasePage{
	public AtsResultPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver,this);
	}
	
	//Elements
	@FindBy(id="main-navbar") WebElement mainNavbar;
	@FindBy(id = "resume-filename-badge") WebElement resumeFileName;
	@FindBy(id = "detected-domain-badge") WebElement detectedDomain;
	@FindBy(id = "ats-score-value") WebElement atsScoreValue;
	@FindBy(id = "ats-grade-value") WebElement atsGradeValue;
	@FindBy(id = "ats-matched-count") WebElement atsMatchedCount;
	@FindBy(id = "ats-matched-skills-block") WebElement atsMatchedSkillsBlock;
	@FindBy(id = "ats-missing-count") WebElement atsMissingCount;
	@FindBy(id ="ats-missing-skills-block") WebElement atsMissingSkillsBlock;
	@FindBy(id = "ats-score-progress") WebElement atsScoreProgressBar;
	@FindBy(id="ai-summary-block") WebElement aiSummaryBlock;
	@FindBy(id="ai-summary-text") WebElement aiSummaryText;
	
	@FindBy(id = "jd-match-form-card") WebElement jdFormBlock;
	@FindBy(id ="jd-textarea") WebElement jdTextArea;
	@FindBy(id="jd-btn") WebElement jdAnalyzeBtn;
	
	@FindBy(id="upload-another-link") WebElement uploadAnotherBtn;
	
	
	
	
	
	
	
	
	
	
	//Methods 
	
	public boolean isMainNavbarDisplayed() {
		return isDisplayed(mainNavbar);
	}
	
	public String getResumeFileName() {
		return getText(resumeFileName);
	}
	
	public boolean isDetectedDomainDisplayed() {
		return isDisplayed(detectedDomain);	
	}
	
	public String getDetectedDomain() {
		return getText(detectedDomain);
	}
	
	public boolean isAtsScoreValueDisplayed() {
		return isDisplayed(atsScoreValue);
	}
	public String getAtsScoreValue() {
		return getText(atsScoreValue);
	}
	
	public boolean isAtsGradeValueDisplayed() {
		return isDisplayed(atsGradeValue);
	}
	public String getAtsGradeValue() {
		return getText(atsGradeValue);
	}
	public boolean isAtsMatchedCountDisplayed() {
		return isDisplayed(atsMatchedCount);
	}
	
	public boolean isAtsMatchedSkillsblockDisplayed() {
		return isDisplayed(atsMatchedSkillsBlock);
	}
	
	public String getAtsMatchedCount() {
		return getText(atsMatchedCount);
	}
	public boolean isAtsMissingCountDisplayed() {
		return isDisplayed(atsMissingCount);
	}
	public String getAtsMissingCount() {
		return getText(atsMissingCount);
	}
	public boolean isAtsMissingSkillsBlockDisplayed() {
		return isDisplayed(atsMissingSkillsBlock);
	}
	
	public boolean isAtsScoreProgressBarDisplayed() {
		return isDisplayed(atsScoreProgressBar);
	}
	
	public boolean isAiSummaryBlockDisplayed() {
		return isDisplayed(aiSummaryBlock);
	}
	
	public boolean isAiSummaryTextDisplayed() {
		return isDisplayed(aiSummaryText);
	}
	public String getAiSummaryText() {
		return getText(aiSummaryText);
	}
	
	public boolean isJDMatchFormDisplayed() {
		return isDisplayed(jdFormBlock);
	}
	public void enterJdText(String jdText) {
		type(jdTextArea,jdText);
	}
	public boolean isJDTextAreaEnabled() {
		return isEnabled(jdTextArea);
	}
	
	public String getJdTextAreaPlaceholder() {
		return getAtribute(jdTextArea,"placeholder");
	}
	public boolean isJdAnalyzeBtnEnabled() {
		return isEnabled(jdAnalyzeBtn);
	}
	
	public void clickJdAnalyzeBtn() {
		click(jdAnalyzeBtn);
	}
	public boolean isJdAnalyzeBtnDisplayed() {
		return isDisplayed(jdAnalyzeBtn);
	}
	
	public boolean isUploadAnotherBtnDisplayed() {
		return isDisplayed(uploadAnotherBtn);
	}
	
	public void clickUploadAnotherBtn() {
		click(uploadAnotherBtn);
	}

	public String getJDText() {
		
		return getAtribute(jdTextArea,"value");
	}

	
	
	

	
	
	
}
