package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ResetPasswordPage extends BasePage {
		LoginPage loginPage;

    public ResetPasswordPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        loginPage = new LoginPage(driver);
    }

    // =====================
    // Elements
    // =====================

    @FindBy(id = "new_password")
    private WebElement newPasswordField;

    @FindBy(id = "confirm_password")
    private WebElement confirmPasswordField;

    @FindBy(id = "reset-password-submit-btn")
    private WebElement submitBtn;

    @FindBy(id = "reset-password-error-box")
    private WebElement errorBox;

    @FindBy(id = "reset-password-invalid-message")
    private WebElement invalidTokenMessage;

    // =====================
    // Actions
    // =====================

    public LoginPage resetPassword(String newPassword) {
        type(newPasswordField, newPassword);
        type(confirmPasswordField, newPassword);
        click(submitBtn);
        loginPage.waitForVisibility(loginPage.getEmailInput());
        return loginPage;
    }

    public String submitWithInvalidPassword(String password, String confirmPassword) {
        type(newPasswordField, password);
        type(confirmPasswordField, confirmPassword);
        click(submitBtn);
        return waitForAlert();
    }

    // =====================
    // Getters
    // =====================

    public String getErrorMessage() {
        return getText(errorBox);
    }

    // =====================
    // Validations
    // =====================

    public boolean isInvalidTokenMessageDisplayed() {
        return isDisplayed(invalidTokenMessage);
    }

    public void waitForResetForm() {
        waitForVisibility(newPasswordField);
    }
}