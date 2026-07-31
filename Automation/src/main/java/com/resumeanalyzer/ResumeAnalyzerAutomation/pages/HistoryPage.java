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
	
	//Elements
	@FindBy(id="history-title") WebElement historyPageHeader;
	@FindBy(id="resume-filename") WebElement resumeFileName;
	@FindBy(id="dashboard-btn") WebElement dashboardPageBtn;
	@FindBy(id="analysis-list") WebElement analysisList;
	
	@FindBy(xpath="//span[starts-with(@id,'match-percentage')]") List<WebElement> matchPercentage;
	@FindBy(xpath="//div[starts-with(@id,'analysis-date')]") List <WebElement> analysisDate;
	@FindBy(xpath="//div[starts-with(@id,'matched-skills')]") List <WebElement> matchedSkillContainer;
	@FindBy(xpath="//a[starts-with(@id,'download-report')]") List <WebElement> downloadReportBtn;
	@FindBy(xpath="//div[starts-with(@id,'progress-bar')]") List <WebElement> progressBar;
	@FindBy(xpath="//div[starts-with(@id,'analysis-card')]")
	List<WebElement> analysisCards;
	
	
	public boolean isHistoryHeadingDisplayed() {
		
		return isDisplayed(historyPageHeader);
	}
	
	public String getResumeFileName() {
		return getText(resumeFileName);
	}
	public DashboardPage clickDashboardBtn() {
		click(dashboardPageBtn);
		return new DashboardPage(driver);
	}
	
	public boolean isAnalysisListDisplayed() {
		return isDisplayed(analysisList);
	}
	
	
	public String getFirstAnalysisMatchPercentage() {
		return getText(matchPercentage.get(0));
	}
	
	public String getFirstAnalysisDate() {
		return getText(analysisDate.get(0));
		
	}
	
	public List<String> getFirstAnalysisMatchedSkills() {
		List<WebElement> skills =
				matchedSkillContainer.get(0).findElements(By.tagName("span"));

	    List<String> skillNames = new ArrayList<>();

	    for (WebElement skill : skills) {
	        skillNames.add(skill.getText());
	    }

	    return skillNames;
	}
	
	public File clickFirstAnalysisDownloadReport() throws IOException {
		return clickDownloadReport(downloadReportBtn.get(0));
	}
	
	
	public boolean isFirstAnalysisProgressBarDisplayed() {
		return isDisplayed(progressBar.get(0));
	}
	
	public int getAnalysisCount() {
	    return analysisCards.size();
	}
	
	
	
	
	

}
