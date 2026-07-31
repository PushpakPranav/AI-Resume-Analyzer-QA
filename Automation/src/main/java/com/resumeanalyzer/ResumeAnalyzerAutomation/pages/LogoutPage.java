package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import components.NavbarComponent;

public class LogoutPage extends BasePage {

    NavbarComponent navbar;

    public LogoutPage(WebDriver driver) {
        super(driver);
        navbar = new NavbarComponent(driver);
        PageFactory.initElements(driver, this);
    }

    public void logout() {
        navbar.clickAvatar();
        navbar.clickLogout();
    }

    public boolean isLoginDisplayed() {
        return navbar.isLoginDisplayed();
    }

    public boolean isSignUpDisplayed() {
        return navbar.isSignUpDisplayed();
    }
}