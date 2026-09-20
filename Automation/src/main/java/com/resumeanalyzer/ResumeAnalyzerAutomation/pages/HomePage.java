package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.resumeanalyzer.ResumeAnalyzerAutomation.components.NavbarComponent;


public class HomePage extends BasePage{
		private final NavbarComponent navbar;
		private final LoginPage loginPage;
		private static final List<String> EXPECTED_STEPS = List.of(
                "Upload Resume",
                "Auto Domain Detection",
                "ATS Score + Skill Gap",
                "Paste JD → Match %"
        );
		public HomePage(WebDriver driver) {
			super(driver);
			PageFactory.initElements(driver, this);
			navbar = new NavbarComponent(driver);
			loginPage = new LoginPage(driver);
			
		}
		
//		  =====================
//		  Elements
//		  =====================
		
	    @FindBy(id="submit-btn")
	    private WebElement analyzeBtn;
	    
	    @FindBy(id="upload-form")
	    private WebElement uploadForm;
	    
	    @FindBy(id="file-input")
	    private WebElement fileInput;
	    
	    @FindBy(css=".mb-1.fw-semibold")
	    private WebElement uploadInstructionText;
	    
	    @FindBy(id="file-name")
	    private WebElement selectedFileName;
	    
	    @FindBy(xpath="//p[@class='text-muted small mb-0']")
	    private WebElement supportedFileTypeText;
	    
	    @FindBy(xpath="//i[@class='bi bi-info-circle me-2 text-teal']")
	    private WebElement howItWorksSection;
	    
	    @FindBy(css = ".card .d-flex.gap-3 strong")
	    private List<WebElement> stepTitles;
	    
	    @FindBy(id="upload-error") 
	    private WebElement uploadError;
	    
//	  =====================
//	  Methods
//	  =====================
	    
//	  =====================
//	  Navigation
//	  =====================
	    
	    public LoginPage clickLogin() {
	        navbar.clickLogin();
	        return new LoginPage(driver);
	    }
	    
	    public void clickSignUp() {
	        navbar.clickSignUp();
	    }
	    
	    public void loginAndGoHome(String email, String password){
	    	clickLogin();
			loginPage.loginAsValidUser(email, password);
			loginPage.waitForDashboard();
			navbar.clickHome();
		}
	    
	 // =====================
	 // Actions
	 // =====================
	    
	    public void clickUploadForm() {
	    	click(uploadForm);
	    }
	    
	    public void clickAnalyzeBtn() {
	    	scrollToElement(analyzeBtn);
	        click(analyzeBtn);
	    }
	    
	    public void selectFile(String filepath) {
	    	fileInput.sendKeys(filepath);
	    }	
	    
	    public void forceSubmitUploadForm() {
	        ((JavascriptExecutor) driver).executeScript(
	                "document.getElementById('upload-form').submit();"
	        );
	    }
	    
	    public void selectFileBypassingClientValidation(String filepath) {
	        ((JavascriptExecutor) driver).executeScript(
	                "var oldInput = document.getElementById('file-input');" +
	                "var newInput = oldInput.cloneNode(true);" +
	                "oldInput.parentNode.replaceChild(newInput, oldInput);"
	        );
	        fileInput.sendKeys(filepath);
	    }
	    
	 // =====================
	 // Getters
	 // =====================
	    
	    public String getSelectedFileName() {
	    	return getText(selectedFileName).replace("✅", "").trim();
	    }
	    
	    public String getUploadInstructionText() {
	        return getText(uploadInstructionText);
	    }
	    
	    public String getSupportedFileTypeText() {
	    	return getText(supportedFileTypeText);
	    }
	    
	    public String getUploadError() {
	        return getText(uploadError);
	    }
	    
	 // =====================
	 // Validations
	 // =====================
	    
	    public boolean waitForAtsResultPage() {
	    	return waitForURLContains("resume/upload", 30);
		}
	    
	    public boolean isUploadFormDisplayed() {
	        return isDisplayed(uploadForm);
	    }
	    
	    public boolean isAnalyzeBtnDisplayed() {
			return isDisplayed(analyzeBtn);	
	    }
	    
	    public boolean isAnalyzeBtnEnabled() {
	    	return isEnabled(analyzeBtn);
	    }
	    
	    public boolean isHowItWorksSectionDisplayed() {
	    	scrollToElement(howItWorksSection);
	    	return isDisplayed(howItWorksSection);
	    }
	    
	    public boolean areAllFourStepsDisplayed() {

	        if (stepTitles.size() != EXPECTED_STEPS.size()) {
	            return false;
	        }

	        for (int i = 0; i < EXPECTED_STEPS.size(); i++) {
	            String actual = getText(stepTitles.get(i)).trim();
	            if (!EXPECTED_STEPS.get(i).equals(actual)) {
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
	    	return waitForURLContains("/dashboard",10);
	   }
	    
	   public boolean isUploadErrorDisplayed() {
	       return isDisplayedSafely(By.id("upload-error"), 10);
	   }
}
