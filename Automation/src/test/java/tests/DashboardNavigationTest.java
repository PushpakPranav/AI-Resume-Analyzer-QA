package tests;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HistoryPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;

import base.BaseTest;
import components.NavbarComponent;

public class DashboardNavigationTest extends BaseTest {

	LoginPage loginpage;
	HomePage homepage;
	DashboardPage dashboardpage;
	NavbarComponent navbar;

	String email = "test11@gmail.com";
	String password = "MyStr0ng@Pass!";
	String resumePath;

	@BeforeMethod
	public void init() {
		homepage = new HomePage(driver);
		loginpage = new LoginPage(driver);
		dashboardpage = new DashboardPage(driver);
		navbar = new NavbarComponent(driver);

		resumePath = System.getProperty("user.dir")
				+ "/src/test/resources/TestData/Pushpak_Pranav_QA_Resume.docx";

		homepage.clickLogin();
		loginpage.loginWithCredentials(email, password);
		if (!dashboardpage.isResumePresent()) {

	        navbar.clickHome();

	        homepage.clickUploadForm();
	        homepage.selectFile(resumePath);
	        homepage.clickAnalyzeBtn();

	        navbar.clickDashboard();
		}
	}

	

	

	@Test
	public void verifyClickHomeNavigatesToHomePage() {
		dashboardpage.clickHome();
		Assert.assertTrue(
				homepage.isUploadFormDisplayed(),
				"Upload form is not displayed after navigating Home from the dashboard.");
	}
	@Test
	public void verifyClickHistoryBtnNavigatesToHistoryPage() {

	    HistoryPage historyPage = dashboardpage.clickFirstHistoryButton();
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	    wait.until(ExpectedConditions.urlContains("/analysis/history/"));
	    Assert.assertTrue(
	            driver.getCurrentUrl().contains("/analysis/history"),
	            "Navigation to History page failed."
	    );

	    Assert.assertTrue(
	            historyPage.isHistoryHeadingDisplayed(),
	            "History page is not loaded properly."
	    );
	}
	

}
