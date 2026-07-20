package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import components.NavbarComponent;

public class DashboardPage extends BasePage{
	NavbarComponent navbar;
	public DashboardPage(WebDriver driver) {
		super(driver);
		navbar = new NavbarComponent(driver);
		PageFactory.initElements( driver, this);
	}
	
	
	//Elements
	
	@FindBy(id="upload-new-resume-btn") WebElement uploadNewResumeBtn;
	@FindBy(xpath="//h6[contains(normalize-space(),'Resume History')]") WebElement resumeHistoryHeading;
	@FindBy(id="resume-history-table") WebElement resumeHistoryTable;
	@FindBy(id="scoreChart") WebElement scoreHistoryHeading;
	@FindBy(id="welcome-text") WebElement welcomeMessage;
	
	
	public void verifyWelcomeMessage(String expectedMessage) {
		checkText(getText(welcomeMessage),expectedMessage);
		
		
	}
	
	public boolean isUploadBtnDisplayed() {
		return isDisplayed(uploadNewResumeBtn);
	}
	
	public boolean isResumeHistoryHeadingDisplayed() {
		return isDisplayed(resumeHistoryHeading);
		
	}
	
	public boolean isResumeHistoryTableDisplayed() {
		return isDisplayed(resumeHistoryTable);
		
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
	 
	
	
	
	
}
