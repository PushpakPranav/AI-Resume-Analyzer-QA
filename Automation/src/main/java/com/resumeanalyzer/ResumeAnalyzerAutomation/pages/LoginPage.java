package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.JavascriptExecutor;

import com.resumeanalyzer.ResumeAnalyzerAutomation.components.NavbarComponent;


public class LoginPage extends BasePage{
	private final NavbarComponent navbar;

	public LoginPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
		navbar = new NavbarComponent(driver);
		
	}
	
//	  =====================
//	  Elements
//	  =====================
	
	@FindBy(id="email")
	private WebElement emailInput;
	
	@FindBy(id="password")
	private WebElement passwordInput;
	
	@FindBy(id="login-submit-btn")
	private WebElement loginBtn;
	
	@FindBy(id="login-signup-link")
	private WebElement signUpLink;
	
	@FindBy(id="login-error-box")
	private WebElement loginErrorBox;
	
	@FindBy(className="fw-bold")
	private WebElement welcomeText;
	
	@FindBy(xpath="//p[@class='text-center text-muted small mt-3 mb-0']")
	private WebElement noAccountText;
	
	@FindBy(xpath="//h4[@class='welcome-text']")
	private WebElement welcomeMessage;
	
	@FindBy(id="login-forgot-password-link")
	private WebElement forgotPasswordLink;
	
	@FindBy(css = "#login-form input[name='csrf_token']")
	private WebElement csrfTokenInput;
	
//	  =====================
//	  Navigation
//	  =====================
	
	public void clickHomeLink() {
		navbar.clickHome();
	}
	
	public void clickSignUpLink() {
	    click(signUpLink);
	}
	
	public ForgotPasswordPage clickForgotPassword() {
		click(forgotPasswordLink);
		return new ForgotPasswordPage(driver);
	}
	
//	  =====================
//	  Actions
//	  =====================
	private void performLogin(String email, String password) {
	    type(emailInput, email);
	    type(passwordInput, password);
	    click(loginBtn);
	}
	
	public DashboardPage loginAsValidUser(String email, String password) {
	    performLogin(email, password);
	    return new DashboardPage(driver);
	}

	public LoginPage loginAsInvalidUser(String email, String password) {
	    performLogin(email, password);
	    return this;
	}
	
	public DashboardPage loginWithTrailingSpaceInEmail(String email,String password) {
		performLogin(email,password);
		return new DashboardPage(driver);
	}
	
	public DashboardPage loginWithLeadingSpaceInEmail(String email,String password) {
		performLogin(email,password);
		return new DashboardPage(driver);
	}
	
	public void waitForDashboard() {
		waitForVisibility(welcomeMessage);
		
	}
	
	public void tamperCsrfToken(String fakeToken) {
	    ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", csrfTokenInput, fakeToken);
	}
	
//	  =====================
//	  Getters
//	  =====================
	
	public String getWelcomeText() {
		return getText(welcomeText);
	}
	
	public String getNoAccountText(){
	    return getText(noAccountText);
	}

	public String getWelcomeMessage() {
		return getText(welcomeMessage);
	}
	
	public String getErrorMessage() {
	    return getText(loginErrorBox);
	}
	
	public String getCsrfTokenValue() {
	    return getAttribute(csrfTokenInput, "value");
	}
	
	public WebElement getEmailInput() {
		return emailInput;
	}
	
//	  =====================
//	  Validations
//	  =====================
	
	public boolean isLoginButtonDisplayed() {
	    return isDisplayed(loginBtn);
	}
	
	public boolean isLoginButtonEnabled() {
	    return isEnabled(loginBtn);
	}

	public boolean isForgotPasswordLinkDisplayed() {
	    return isDisplayed(forgotPasswordLink);
	}

	public boolean isEmailFieldDisplayed() {
	    return isDisplayed(emailInput);
	}
	public boolean isPasswordFieldDisplayed() {
	    return isDisplayed(passwordInput);
	}
	public boolean isSignUpLinkDisplayed() {
	    return isDisplayed(signUpLink);
	}
	
	public boolean isErrorMessageDisplayed() {
	    return isDisplayed(loginErrorBox);
	}
	

	
}
