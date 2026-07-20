package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

public class BasePage {
	
	protected WebDriver driver;
	
	public BasePage(WebDriver driver) {
		this.driver = driver;
		
	}
	public void click(WebElement ele) {
		ele.click();
	}
	
	public void type(WebElement ele,String txt) {
		ele.clear();
		ele.sendKeys(txt);
	}
	
	public String getText(WebElement ele) {
		return ele.getText();
	}
	
	public void checkText(String actual,String expected ) {
		Assert.assertEquals(actual,expected);
	}
	
	public boolean isDisplayed(WebElement ele) {
		return ele.isDisplayed();
		
	}
	public void scrollToElement(WebElement ele) {
	Actions action = new Actions(driver);
	action.moveToElement(ele).perform();

	}
	public String getAtribute(WebElement ele, String string) {
		return ele.getAttribute(string);
	}
	
	public boolean isEnabled(WebElement ele) {
		return ele.isEnabled();
		
	}
	
	
}
