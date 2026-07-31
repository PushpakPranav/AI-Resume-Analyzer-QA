package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import components.NavbarComponent;

public class LoginPage extends BasePage{
	NavbarComponent navbar;
	ForgotPasswordPage forgotPasswordPage;

	public LoginPage(WebDriver driver) {
		super(driver);
		navbar = new NavbarComponent(driver);
		forgotPasswordPage = new ForgotPasswordPage(driver);
		PageFactory.initElements( driver,this);
		
	}
	
	@FindBy(xpath="//input[@name='email']") WebElement emailInput;
	@FindBy(xpath="//input[@name=\"password\"]") WebElement passwordInput;
	@FindBy(xpath="//button[@type='submit']") WebElement loginBtn;
	@FindBy(xpath="//a[@class='text-primary fw-semibold']") WebElement signUpLink;
	@FindBy(className="fw-bold") WebElement WelcomeText;
	@FindBy(xpath="//p[@class='text-center text-muted small mt-3 mb-0']") WebElement noAccountText;
	@FindBy(xpath="//h4[@class='welcome-text']") WebElement welcomeMessage;
	@FindBy(xpath = "//a[normalize-space()='Forgot password?']") WebElement forgotPasswordLink;
	
	
	public DashboardPage loginWithCredentials(String email,String password) {
		type(emailInput,email);
		type(passwordInput,password);
		click(loginBtn);
		return new DashboardPage(driver);
	}
	
	public void verifyWelcomeText(String Text) {
		checkText(getText(WelcomeText),Text);
		;
	}
	
	public void verifyNoAccountText(String expected)
	{
	    checkText(getText(noAccountText), expected);
	}

	public void clickSignUpLink()
	{
	    click(signUpLink);
	}
	
	public void verifyLoginSuccess(String expectedText) {
		checkText(getText(welcomeMessage),expectedText);
	}
	
	public void clickHomeLink() {
		navbar.clickHome();
	}
	
	public ForgotPasswordPage clickForgotPassword() {
		click(forgotPasswordLink);
		return new ForgotPasswordPage(driver);
	}
	
	
	
	
	
}
