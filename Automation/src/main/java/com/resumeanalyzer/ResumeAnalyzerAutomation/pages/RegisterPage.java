package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;


import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class RegisterPage extends BasePage {
    public RegisterPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

//	  =====================
//	  Elements
//	  =====================
    
    @FindBy(xpath = "//form[@action='/auth/register']")
    private WebElement registerForm;

    @FindBy(xpath = "//form[@action='/auth/register']//input[@name='name']")
    private WebElement nameField;

    @FindBy(xpath = "//form[@action='/auth/register']//input[@name='email']")
    private WebElement emailField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(xpath = "//form[@action='/auth/register']//button[@type='submit']")
    private WebElement btnCreateAccount;

    @FindBy(xpath = "//a[@href='/auth/login']")
    private WebElement lnkLogin;

    @FindBy(xpath = "//div[contains(@class,'alert-danger')]")
    private WebElement lblError;

//	  =====================
//	  Navigation
//	  =====================
    
    public DashboardPage register(String name, String email, String password) {
        fillRegistrationForm(name, email, password);
        submitRegistration();
        return new DashboardPage(driver);
    }
    
//	  =====================
//	  Action
//	  =====================
    
    public void enterName(String name) {
        type(nameField, name);
    }
    
    public void enterEmail(String email) {
        type(emailField, email);
    }
    
    public void enterPassword(String password) {
        type(passwordField, password);
    }
    
    public void clickCreateAccount() {
        click(btnCreateAccount);
    }
    
    public LoginPage clickLoginLink() {
        click(lnkLogin);
        return new LoginPage(driver);
    }
    
    private void fillRegistrationForm(String name,String email,String password){
        enterName(name);
        enterEmail(email);
        enterPassword(password);
    }
    private void submitRegistration() {
        clickCreateAccount();
    }
    
    public void submitWithEmptyName(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        submitRegistration();
    }
    
    public void submitWithEmptyEmail(String name, String password) {
        enterName(name);
        enterPassword(password);
        submitRegistration();
    }

    public void submitWithEmptyPassword(String name, String email) {
        enterName(name);
        enterEmail(email);
        submitRegistration();
    }

    public void submitWithInvalidEmailFormat(String name, String invalidEmail, String password) {
        fillRegistrationForm(name, invalidEmail, password);
        submitRegistration();
    }
    
    
//	  =====================
//	  Getters
//	  =====================
     
    public String getErrorMessage() {
    	return getText(lblError);
    }

    public String registerWithInvalidPassword(String name, String email, String password) {
        fillRegistrationForm(name, email, password);
        submitRegistration();
        return waitForAlert();
    }
    
    private String getFieldValidationMessage(WebElement field) {
        return (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].validationMessage;", field);
    }
    
    public String getNameValidationMessage() {
        return getFieldValidationMessage(nameField);
    }

    public String getEmailValidationMessage() {
        return getFieldValidationMessage(emailField);
    }

    public String getPasswordValidationMessage() {
        return getFieldValidationMessage(passwordField);
    }
    
    public String getName() {
        return getAttribute(nameField, "value");
    }

    public String getEmail() {
        return getAttribute(emailField, "value");
    }

    public String getPassword() {
        return getAttribute(passwordField, "value");
    }
//	  =====================
//	  Validations
//	  =====================
    
    public boolean isRegisterFormDisplayed() {
        return isDisplayed(registerForm);
    }

    public boolean isFieldInvalid(WebElement field) {
        Boolean isValid = (Boolean) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].checkValidity();", field);
        return !isValid;
    }
    
    public boolean isNameFieldInvalid() {
        return isFieldInvalid(nameField);
    }

    public boolean isEmailFieldInvalid() {
        return isFieldInvalid(emailField);
    }

    public boolean isPasswordFieldInvalid() {
        return isFieldInvalid(passwordField);
    }
    
    public boolean isCreateAccountButtonDisplayed() {
        return isDisplayed(btnCreateAccount);
    }

    public boolean isCreateAccountButtonEnabled() {
        return isEnabled(btnCreateAccount);
    }

    public boolean isLoginLinkDisplayed() {
        return isDisplayed(lnkLogin);
    }

    public boolean isNameFieldDisplayed() {
        return isDisplayed(nameField);
    }

    public boolean isEmailFieldDisplayed() {
        return isDisplayed(emailField);
    }

    public boolean isPasswordFieldDisplayed() {
        return isDisplayed(passwordField);
    }
}