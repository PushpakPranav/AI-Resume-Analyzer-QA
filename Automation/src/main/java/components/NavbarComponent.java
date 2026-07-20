package components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.BasePage;

public class NavbarComponent extends BasePage{

	public NavbarComponent(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);	
		}
	
	@FindBy(linkText="Login") WebElement navLoginBtn;
    @FindBy(linkText="Sign Up") WebElement navSignUpBtn;
    @FindBy(id="nav-home-link") WebElement homeBtn;
    @FindBy(id="nav-dashboard-link") WebElement dashboardBtn;
    @FindBy(id="user-avatar-dropdown-toggle") WebElement userAvatarDropdown;
    @FindBy(id="dropdown-my-resumes-link") WebElement myResumesLink;
    @FindBy(id="dropdown-logout-link") WebElement logoutLink;
    @FindBy(id="dark-mode-toggle-btn") WebElement darkModeToggleBtn;
    @FindBy(id="nav-brand-logo") WebElement brandLogo;
    
    
    
    public void clickLogin() {
    	click(navLoginBtn);
    }
    
    public void clickSignUp() {
    	click(navSignUpBtn);
    }
    public void clickDashboard() {
    	click(dashboardBtn);
    }
    public void clickHome() {
    	click(homeBtn);
    }
    public void clickAvatar() {
    	click(userAvatarDropdown);
    }
    public void clickMyResumes() {
    	click(myResumesLink);
    }
    public void clickLogout() {
    	click(logoutLink);
    }
    public void clickDarkModeToggle() {
    	click(darkModeToggleBtn);
    }
    public void clickBrandLogo() {
    	click(brandLogo);
    }
    public boolean isLoginDisplayed() {
    	return isDisplayed(navLoginBtn);
    }
    public boolean isSignUpDisplayed() {
    	return isDisplayed(navSignUpBtn);
    }
    public boolean isDashboardDisplayed() {
    	return isDisplayed(dashboardBtn);
    }
    public boolean isHomeDisplayed() {
    	return isDisplayed(homeBtn);
    }
    public boolean isUserAvatarDisplayed() {
    	return isDisplayed(userAvatarDropdown);
    }
    public boolean isMyResumesDisplayed() {
    	return isDisplayed(myResumesLink);
    }
    public boolean isLogoutDisplayed() {
    	return isDisplayed(logoutLink);
    }
    public boolean isDarkModeToggleDisplayed() {
    	return isDisplayed(darkModeToggleBtn);
    }
    public boolean isBrandLogoDisplayed() {
    	return isDisplayed(brandLogo);
    }
}



