package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.resumeanalyzer.ResumeAnalyzerAutomation.components.NavbarComponent;

public class DashboardPage extends BasePage{
	private final NavbarComponent navbar;
	public DashboardPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements( driver, this);
		navbar = new NavbarComponent(driver);
	}
	
	
//	  =====================
//	  Elements
//	  =====================
	
	@FindBy(xpath="//h6[contains(normalize-space(),'Resume History')]")
	private WebElement resumeHistoryHeading;
	
	@FindBy(id="resume-history-table")
	private WebElement resumeHistoryTable;
	
	@FindBy(id="scoreChart")
	private WebElement scoreHistoryHeading;
	
	@FindBy(id="welcome-text")
	private WebElement welcomeMessage;
	
	@FindBy(xpath="//table[@id='resume-history-table']//tbody/tr")
	private List<WebElement> resumeRows;

	@FindBy(xpath="//table[@id='resume-history-table']//tbody/tr[1]/td[1]")
	private WebElement resumeFileName;

	@FindBy(xpath="//table[@id='resume-history-table']//tbody/tr[1]/td[3]")
	private WebElement atsScore;

	@FindBy(xpath="//table[@id='resume-history-table']//tbody/tr[1]/td[5]")
	private WebElement uploadDate;

	@FindBy(xpath="//table[@id='resume-history-table']//tbody/tr[1]/td[4]")
	private WebElement resumeStatus;

	@FindBy(xpath="//button[contains(@id,'resume-delete-btn-')]")
	private WebElement deleteResumeBtn;
	
	@FindBy(xpath = "//div[contains(text(),'Resume deleted successfully.')]")
	private WebElement deleteSuccessMessage;

	@FindBy(id = "upload-first-resume-btn")
	private WebElement uploadResumeBtn;

	@FindBy(id = "stat-total-resumes-value")
	private WebElement totalResumeCount;

	@FindBy(id = "stat-avg-score-value")
	private WebElement avgScore;

	@FindBy(id = "stat-best-score-value")
	private WebElement bestScore;

	@FindBy(id = "stat-domains-tried-value")
	private WebElement domainsTried;

	@FindBy(id = "domains-detected-card")
	private WebElement domainsDetectedCard;

	
	@FindBy(xpath = "//a[starts-with(@id ,'resume-history-btn')]")
	private List<WebElement> historyBtn;
	
	
	
	private final By resumeHistoryTableLocator = By.id("resume-history-table");
	
	
//	  =====================
//	  Navigation
//	  =====================
	
	public HistoryPage clickFirstHistoryButton() {
		scrollToElement(historyBtn.get(0));
		click(historyBtn.get(0));
		return new HistoryPage(driver);
	}
	
//	  =====================
//	  Action
//	  =====================
	
	public void clickHome() {
		navbar.clickHome();
	}
	
	public void clickDeleteResume() {
		scrollToElement(deleteResumeBtn);
	    click(deleteResumeBtn);
	}
	
	public void confirmDelete() {
	    driver.switchTo().alert().accept();
	}
	
	public void cancelDelete() {
	    driver.switchTo().alert().dismiss();
	}
	
//	  =====================
//	  Getters
//	  =====================
	
	public String getWelcomeMessage() {
		return getText(welcomeMessage);	
	}
	
	public String getResumeFileName() {
	    return getText(resumeFileName);
	}
	
	public int getResumeHistoryCount() {
		return resumeRows == null ? 0 : resumeRows.size();
	}
	
	public String getNoResumeMessage() {
	    return getText(uploadResumeBtn);
	}
	
	public String getTotalResumeCount() {
	    return getText(totalResumeCount);
	}
	public String getAverageScore() {
	    return getText(avgScore);
	}
	
	public String getBestScore() {
	    return getText(bestScore);
	}
	
	public String getDomainsTried() {
	    return getText(domainsTried);
	}
	
//	  =====================
//	  Validations
//	  =====================
	
	public boolean isUploadBtnDisplayed() {
		return isDisplayed(uploadResumeBtn);
	}
	
	public boolean isScoreHistoryDisplayed() {
	    return isDisplayed(scoreHistoryHeading);
	}
	
	public boolean isResumeHistoryHeadingDisplayed() {
		return isDisplayed(resumeHistoryHeading);	
	}
	
	public boolean isResumeHistoryTableDisplayed() {
	    return isDisplayedSafely(resumeHistoryTableLocator, 10);
	}
	
	public boolean isLoginDisplayed() {
		return navbar.isLoginDisplayed();
	}

	public boolean isSignUpDisplayed() {
		return navbar.isSignUpDisplayed();	
	}

	public boolean isUsernameDisplayed() {
		return isDisplayed(welcomeMessage);	
	}

	public boolean isUserAvatarDisplayed() {
		return navbar.isUserAvatarDisplayed();
	}
	 
	public boolean hasResumeHistory() {
	    return !resumeRows.isEmpty();
	}
	
	public boolean isUploadDateDisplayed() {
	    return isDisplayed(uploadDate);
	}
	public boolean isATSScoreDisplayed() {
	    return isDisplayed(atsScore);
	}
	public boolean isResumeStatusDisplayed() {
	    return isDisplayed(resumeStatus);
	}
	
	public boolean isDeleteSuccessMessageDisplayed() {
	    return isDisplayed(deleteSuccessMessage);
	}
	
	public boolean isDomainsDetectedCardDisplayed() {
	    return isDisplayed(domainsDetectedCard);
	}
	
	public boolean isDeleteResumeButtonDisplayed() {
	    return isDisplayed(deleteResumeBtn);
	}
	
	public boolean isHistoryButtonDisplayed() {
	    return !historyBtn.isEmpty() && historyBtn.get(0).isDisplayed();
	}
	
}
