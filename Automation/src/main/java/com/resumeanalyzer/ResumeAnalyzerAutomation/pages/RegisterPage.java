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

    // Elements
    @FindBy(xpath = "//form[@action='/auth/register']")
    public WebElement registerForm;

    @FindBy(xpath = "//form[@action='/auth/register']//input[@name='name']")
    public WebElement txtName;

    @FindBy(xpath = "//form[@action='/auth/register']//input[@name='email']")
    public WebElement txtEmail;

    @FindBy(id = "password")
    public WebElement txtPassword;

    @FindBy(xpath = "//form[@action='/auth/register']//button[@type='submit']")
    public WebElement btnCreateAccount;

    @FindBy(xpath = "//a[@href='/auth/login']")
    public WebElement lnkLogin;

    @FindBy(xpath = "//div[contains(@class,'alert-danger')]")
    WebElement lblError;

    // Basic Actions
    public void enterName(String name) {
        type(txtName, name);
    }
    public void enterEmail(String email) {
        type(txtEmail, email);
    }
    public void enterPassword(String password) {
        type(txtPassword, password);
    }
    public void clickCreateAccount() {
        click(btnCreateAccount);
    }
    public void clickLoginLink() {
        click(lnkLogin);
    }
    public String getErrorMessage() {
        return lblError.getText();
    }
    public boolean isRegisterFormDisplayed() {
        return registerForm.isDisplayed();
    }

    // Password validation flow (via alert)
    public String invalidPassword(String name, String email, String password) {
        type(txtName, name);
        type(txtEmail, email);
        type(txtPassword, password);
        clickCreateAccount();
        return waitForAlert();
    }

    // Complete Registration
    public void register(String name, String email, String password) {
        enterName(name);
        enterEmail(email);
        enterPassword(password);
        clickCreateAccount();
    }

    // Partial submissions for empty-field tests
    public void submitWithEmptyName(String email, String password) {
        type(txtEmail, email);
        type(txtPassword, password);
        clickCreateAccount();
    }
    public void submitWithEmptyEmail(String name, String password) {
        type(txtName, name);
        type(txtPassword, password);
        clickCreateAccount();
    }
    public void submitWithInvalidEmailFormat(String name, String invalidEmail, String password) {
        type(txtName, name);
        type(txtEmail, invalidEmail);
        type(txtPassword, password);
        clickCreateAccount();
    }
    public void submitWithEmptyPassword(String name, String email) {
        type(txtName, name);
        type(txtEmail, email);
        clickCreateAccount();
    }

    // Native HTML5 validation helpers
    public String getFieldValidationMessage(WebElement field) {
        return (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].validationMessage;", field);
    }
    public boolean isFieldInvalid(WebElement field) {
        Boolean isValid = (Boolean) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].checkValidity();", field);
        return !isValid;
    }
}