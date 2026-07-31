package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import components.NavbarComponent;

public class DashboardPage extends BasePage{
	NavbarComponent navbar;
	HistoryPage historyPage;
	public DashboardPage(WebDriver driver) {
		super(driver);
		navbar = new NavbarComponent(driver);
		historyPage = new HistoryPage(driver);
		PageFactory.initElements( driver, this);
	}
	
	
	//Elements
	
	@FindBy(xpath="//h6[contains(normalize-space(),'Resume History')]") WebElement resumeHistoryHeading;
	@FindBy(id="resume-history-table") WebElement resumeHistoryTable;
	@FindBy(id="scoreChart") WebElement scoreHistoryHeading;
	@FindBy(id="welcome-text") WebElement welcomeMessage;
	@FindBy(xpath="//table[@id='resume-history-table']//tbody/tr")
	List<WebElement> resumeRows;

	@FindBy(xpath="//table[@id='resume-history-table']//tbody/tr[1]/td[1]")
	WebElement resumeFileName;

	@FindBy(xpath="//table[@id='resume-history-table']//tbody/tr[1]/td[3]")
	WebElement atsScore;

	@FindBy(xpath="//table[@id='resume-history-table']//tbody/tr[1]/td[5]")
	WebElement uploadDate;

	@FindBy(xpath="//table[@id='resume-history-table']//tbody/tr[1]/td[4]")
	WebElement resumeStatus;

	@FindBy(xpath="//button[contains(@id,'resume-delete-btn-')]")
	WebElement deleteResumeBtn;
	@FindBy(xpath = "//div[contains(text(),'Resume deleted successfully.')]")
	WebElement deleteSuccessMessage;

	@FindBy(id = "upload-first-resume-btn")
	WebElement uploadResumeBtn;

	@FindBy(id = "stat-total-resumes-value")
	WebElement totalResumeCount;

	@FindBy(id = "stat-avg-score-value")
	WebElement avgScore;

	@FindBy(id = "stat-best-score-value")
	WebElement bestScore;

	@FindBy(id = "stat-domains-tried-value")
	WebElement domainsTried;

	@FindBy(id = "domains-detected-card")
	WebElement domainsDetectedCard;

	
	@FindBy(xpath = "//a[starts-with(@id ,'resume-history-btn')]")
	List<WebElement> historyBtn;
	
	
	
	private final By resumeHistoryTableLocator = By.id("resume-history-table");
	
	
	
	
	public String verifyWelcomeMessage() {
		return getText(welcomeMessage);
		
		
	}
	
	public boolean isUploadBtnDisplayed() {
		return isDisplayed(uploadResumeBtn);
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
	
	public void clickHome() {
		navbar.clickHome();
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
	 
	public boolean isResumePresent() {
	    return resumeRows.size() > 0;
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
	public String getResumeFileName() {
	    return getText(resumeFileName);
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
	public int getResumeHistoryCount() {
	    return resumeRows.size();
	}
	public boolean isDeleteSuccessMessageDisplayed() {
	    return isDisplayed(deleteSuccessMessage);
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
	public boolean isDomainsDetectedCardDisplayed() {
	    return isDisplayed(domainsDetectedCard);
	}
	public HistoryPage clickFirstHistoryButton() {
		scrollToElement(historyBtn.get(0));
		click(historyBtn.get(0));
		return new HistoryPage(driver);
	}
	
	
	
}
