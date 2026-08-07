package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HistoryPage extends BasePage{

	public HistoryPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
//	  =====================
//	  Elements
//	  =====================
	
	@FindBy(id="history-title")
	private WebElement historyPageHeader;
	
	@FindBy(id="resume-filename")
	private WebElement resumeFileName;
	
	@FindBy(id="dashboard-btn")
	private WebElement dashboardPageBtn;
	
	@FindBy(id="analysis-list")
	private WebElement analysisList;
	
	@FindBy(xpath="//span[starts-with(@id,'match-percentage')]")
	private List<WebElement> matchPercentage;
	
	@FindBy(xpath="//div[starts-with(@id,'analysis-date')]")
	private List<WebElement> analysisDate;
	
	@FindBy(xpath="//div[starts-with(@id,'matched-skills')]")
	private List<WebElement> matchedSkillContainer;
	
	@FindBy(xpath="//a[starts-with(@id,'download-report')]")
	private List<WebElement> downloadReportBtn;
	
	@FindBy(xpath="//div[starts-with(@id,'progress-bar')]")
	private List<WebElement> progressBar;
	
	@FindBy(xpath="//div[starts-with(@id,'analysis-card')]")
	private List<WebElement> analysisCards;
	
//	  =====================
//	  Navigation
//	  =====================
	
	public DashboardPage clickDashboardBtn() {
		click(dashboardPageBtn);
		return new DashboardPage(driver);
	}
	
//	  =====================
//	  Action
//	  =====================
	
	public File clickFirstAnalysisDownloadReport() throws IOException {
	    if (downloadReportBtn.isEmpty()) {
	        throw new IllegalStateException("No download report button found.");
	    }

	    return clickDownloadReport(getFirstElement(downloadReportBtn));
	}
	
//	  =====================
//	  Getters
//	  =====================
	
	public int getAnalysisCount() {
	    return analysisCards.size();
	}
	
	public String getResumeFileName() {
		return getText(resumeFileName);
	}
	
	private WebElement getFirstElement(List<WebElement> elements) {
	    if (elements.isEmpty()) {
	        throw new IllegalStateException("Element not found.");
	    }
	    return elements.get(0);
	}

	private String getFirstElementText(List<WebElement> elements) {
	    return getText(getFirstElement(elements));
	}
	
	public String getFirstAnalysisMatchPercentage() {
	    return getFirstElementText(matchPercentage);
	}

	public String getFirstAnalysisDate() {
	    return getFirstElementText(analysisDate);
	}
	
	
	
	public int getDownloadReportButtonCount() {
	    return downloadReportBtn.size();
	}
	
	private List<WebElement> getMatchedSkillsElements() {

	    if (matchedSkillContainer.isEmpty()) {
	        return new ArrayList<>();
	    }

	    return matchedSkillContainer.get(0)
	            .findElements(By.tagName("span"));
	}
	
	public List<String> getFirstAnalysisMatchedSkills() {

	    List<String> matchedSkills = new ArrayList<>();

	    for (WebElement skill : getMatchedSkillsElements()) {
	        matchedSkills.add(skill.getText().trim());
	    }

	    return matchedSkills;
	}
	
//	  =====================
//	  Validations
//	  =====================
	
	public boolean isPageLoaded() {
	    return isDisplayed(historyPageHeader)
	            && isDisplayed(resumeFileName)
	            && isDisplayed(analysisList)
	            && isDisplayed(dashboardPageBtn);
	}
	
	public boolean isHistoryHeadingDisplayed() {
		return isDisplayed(historyPageHeader);
	}
	
	public boolean isAnalysisCardDisplayed() {
		return isDisplayed(getFirstElement(analysisCards));
	}
	
	public boolean isAnalysisListDisplayed() {
		return isDisplayed(analysisList);
	}
	
	public boolean hasAnalysis() {
	    return getAnalysisCount() > 0;
	}
	
	public boolean isFirstAnalysisProgressBarDisplayed() {
		return isDisplayed(getFirstElement(progressBar));
	}
	
	public boolean isResumeFileNameDisplayed() {
	    return isDisplayed(resumeFileName);
	}

	public boolean isDashboardButtonDisplayed() {
	    return isDisplayed(dashboardPageBtn);
	}
	
	public boolean isDownloadReportButtonDisplayed() {
	    try {
	        return isDisplayed(getFirstElement(downloadReportBtn));
	    } catch (IllegalStateException e) {
	        return false;
	    }
	}
	
	public boolean hasDownloadReport() {
	    return getDownloadReportButtonCount() > 0;
	}
	
}