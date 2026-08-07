package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import org.openqa.selenium.WebDriver;

import com.resumeanalyzer.ResumeAnalyzerAutomation.components.NavbarComponent;

public class LogoutPage extends BasePage {

    private final NavbarComponent navbar;

    public LogoutPage(WebDriver driver) {
        super(driver);
        navbar = new NavbarComponent(driver);
    }
    
 // =====================
 // Actions
 // =====================
    
    public LoginPage logout() {
        navbar.clickAvatar();
        navbar.clickLogout();
        return new LoginPage(driver);
    }

 // =====================
 // Validations
 // =====================

    public boolean isLoginDisplayed() {
        return navbar.isLoginDisplayed();
    }

    public boolean isSignUpDisplayed() {
        return navbar.isSignUpDisplayed();
    }
    
    public boolean isLoggedOut() {
        return isLoginDisplayed() && isSignUpDisplayed();
    }
    
}