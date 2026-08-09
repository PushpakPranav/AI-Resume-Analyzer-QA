package com.resumeanalyzer.ResumeAnalyzerAutomation.components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.BasePage;

public class FooterComponent extends BasePage{

	public FooterComponent(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
//	  =====================
//	  Elements
//	  =====================
	@FindBy(id="site-footer") WebElement footerMessage;
 	
//	  =====================
//	  Getters
//	  =====================
	
	public String getFooterMessage() {
		return getText(footerMessage);
	}
	
//	  =====================
//	  Validations
//	  =====================
	
	public boolean footerMessageDisplayed() {
		return isDisplayed(footerMessage);
	}
	
}
