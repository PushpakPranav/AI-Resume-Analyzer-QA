package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import components.NavbarComponent;


public class HomePage extends BasePage{
		NavbarComponent navbar;
		public HomePage(WebDriver driver) {
			super(driver);
			navbar = new NavbarComponent(driver);
			PageFactory.initElements(driver,this);
			
		}
		
	    // Elements
	    @FindBy(id="submit-btn") WebElement analyzeBtn;
	    @FindBy(id="upload-form") WebElement uploadForm;
	    @FindBy(id="file-input") WebElement fileInput;
	    @FindBy(css=".mb-1.fw-semibold") WebElement formText;
	    @FindBy(id="file-name") WebElement filename;
	    @FindBy(xpath="//p[@class='text-muted small mb-0']") WebElement supportedFileTypeText;
	    @FindBy(xpath="//i[@class='bi bi-info-circle me-2 text-teal']") WebElement howItWorksSection;
	    
	    @FindBy(css = ".card .d-flex.gap-3 strong")
	    List<WebElement> stepTitles;
	    
	    
	    

	    // Methods

	    public void clickLogin() {
	        navbar.clickLogin();
	    }
	    
	    public void clickSignUP() {
	        navbar.clickSignUp();
	    }
	    
	    public void clickAnalyzeBtn() {
	    	scrollToElement(analyzeBtn);
	        click(analyzeBtn);
	    }
	    
	    public boolean isAnalyzeBtnDisplayed() {
			return isDisplayed(analyzeBtn);
	    	
	    }
	    
	    public boolean isUploadFormDisplayed() {
	        return isDisplayed(uploadForm);
	    }
	    
	    
	    public String getUploadInstructionText() {
	        return getText(formText);
	    }
	    
	    public boolean isAnalyzeBtnEnabled() {
	    	return analyzeBtn.isEnabled();
	    }
	    
	    public void clickUploadForm() {
	    	click(uploadForm);
	    }
	    
	    public String getSelectedFileName() {
	    	return getText(filename).replace("✅", "").trim();
	    }
	    
	    public void selectFile(String filepath) {
	    	fileInput.sendKeys(filepath);
	    }
	    
	    public String getSupportedFileTypeText() {
	    	return getText(supportedFileTypeText);
	    }
	    
	    
	    public boolean howItWorksSectionDisplayed() {
	    	scrollToElement(howItWorksSection);
	    	return isDisplayed(howItWorksSection);
	    }

	    public boolean areAllFourStepsDisplayed() {

	        List<String> expected = Arrays.asList(
	                "Upload Resume",
	                "Auto Domain Detection",
	                "ATS Score + Skill Gap",
	                "Paste JD → Match %"
	        );

	        if (stepTitles.size() != expected.size()) {
	            return false;
	        }

	        for (int i = 0; i < stepTitles.size(); i++) {
	            if (!getText(stepTitles.get(i)).trim().equals(expected.get(i))) {
	                return false;
	            }
	        }

	        return true;
	    }
	    
	    public boolean isLoginDisplayed() {
	        return navbar.isLoginDisplayed();
	    }

	    public boolean isSignUpDisplayed() {
	        return navbar.isSignUpDisplayed();
	    }
	   
	    
	   public boolean isDashboardDisplayed() {
		   return navbar.isDashboardDisplayed();
	   }
	    
	    
	    
	    
	    
	    
	    
}
