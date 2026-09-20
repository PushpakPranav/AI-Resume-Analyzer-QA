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
    
    @FindBy(id = "register-form")
    private WebElement registerForm;

    @FindBy(id = "name")
    private WebElement nameField;

    @FindBy(id = "email")
    private WebElement emailField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "register-submit-btn")
    private WebElement btnCreateAccount;

    @FindBy(id = "loginLink")
    private WebElement lnkLogin;

    @FindBy(id = "register-error-box")
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
    
    public String getNameValidationMessage() {
        return getAttribute(nameField, "validationMessage");
    }

    public String getEmailValidationMessage() {
        return getAttribute(emailField, "validationMessage");
    }

    public String getPasswordValidationMessage() {
        return getAttribute(passwordField, "validationMessage");
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

    public void waitForRegisterForm() {
        waitForVisibility(registerForm);
    }
}