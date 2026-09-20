package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.components.NavbarComponent;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;

import base.BaseTest;
import constants.TestData;

public class DashboardDeleteResumeTest extends BaseTest {

	LoginPage loginPage;
	HomePage homePage;
	DashboardPage dashboardPage;
	NavbarComponent navbar;

	private void createFreshResume() {

        navbar.clickHome();

        homePage.clickUploadForm();
        homePage.selectFile(TestData.DASHBOARD_RESUME_PATH);
        homePage.clickAnalyzeBtn();
        homePage.waitForAtsResultPage();

        navbar.clickDashboard();
        loginPage.waitForDashboard();
    }
	
	
	@BeforeMethod(alwaysRun = true)
	public void init() {

	    homePage = new HomePage(driver);
	    loginPage = new LoginPage(driver);
	    dashboardPage = new DashboardPage(driver);
	    navbar = new NavbarComponent(driver);

	    homePage.clickLogin();

	    loginPage.loginAsValidUser(
	            TestData.DASHBOARD_DELETE_USER_EMAIL,
	            TestData.DASHBOARD_DELETE_USER_PASSWORD
	    );

	    loginPage.waitForDashboard();
	    
	}


	// ---------------------------------------------------------
	// Delete button
	// ---------------------------------------------------------

	@Test
	public void verifyDeleteResumeButtonDisplayed() {
		createFreshResume();
		Assert.assertTrue(
				dashboardPage.isDeleteResumeButtonDisplayed(),
				"Delete resume button is not displayed."
				);
	}


	// ---------------------------------------------------------
	// Delete confirmation
	// ---------------------------------------------------------

	@Test
	public void verifyDeleteResume() {
		createFreshResume();
		Assert.assertTrue(
				dashboardPage.hasResumeHistory(),
				"Resume history should contain a resume before deleting."
				);

		dashboardPage.clickDeleteResume();
		dashboardPage.confirmDelete();
		dashboardPage.waitForDashboardWithDeletedParam();

		Assert.assertTrue(
				dashboardPage.isResumeDeletionMessageDisplayed(),
				"Resume was not deleted successfully."
				);
	}

	@Test
	public void verifyDeleteSuccessMessageDisplayed() {
		createFreshResume();
	dashboardPage.clickDeleteResume();
	dashboardPage.confirmDelete();

	dashboardPage.waitForDashboardWithDeletedParam();

	Assert.assertTrue(
	        dashboardPage.isDeleteSuccessMessageDisplayed(),
	        "Delete success message is not displayed."
	);
	}

	// ---------------------------------------------------------
	// Cancel deletion
	// ---------------------------------------------------------

	@Test
	public void verifyCancelDeleteResume() {
		createFreshResume();
		int before = dashboardPage.getResumeHistoryCount();

		dashboardPage.clickDeleteResume();
		dashboardPage.cancelDelete();

		Assert.assertEquals(
				dashboardPage.getResumeHistoryCount(),
				before,
				"Resume should not be deleted after cancelling."
				);
	}


	// ---------------------------------------------------------
	// State after deletion
	// ---------------------------------------------------------

	@Test
    public void verifyNoResumeMessageDisplayed() {
        dashboardPage.deleteAllExistingResume();
        Assert.assertEquals(
                dashboardPage.getNoResumeMessage(),
                "Upload Your First Resume",
                "'No resume' message is not displayed correctly after deletion."
        );
    }


	@Test
    public void verifyUploadButtonDisplayedWhenNoResumeExists() {
		dashboardPage.deleteAllExistingResume();

        Assert.assertTrue(
                dashboardPage.isUploadBtnDisplayed(),
                "Upload button is not displayed after resume is deleted."
        );
    }

	@Test
    public void verifyDashboardStatisticsResetAfterDelete() {

		dashboardPage.deleteAllExistingResume();

        Assert.assertEquals(
                dashboardPage.getTotalResumeCount(),
                "0",
                "Total resume count is not reset after deletion."
        );

        Assert.assertEquals(
                dashboardPage.getAverageScore(),
                "0%",
                "Average score is not reset after deletion."
        );

        Assert.assertEquals(
                dashboardPage.getBestScore(),
                "0%",
                "Best score is not reset after deletion."
        );

        Assert.assertEquals(
                dashboardPage.getDomainsTried(),
                "0",
                "Domains tried count is not reset after deletion."
        );
    }
}