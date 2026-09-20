package base;
import java.io.IOException;
import org.testng.annotations.Listeners;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.resumeanalyzer.ResumeAnalyzerAutomation.utils.ConfigReader;

import factory.DriverFactory;


@Listeners(listeners.ScreenshotListener.class)
public class BaseTest {
	protected WebDriver driver;
	protected ConfigReader config;
	
	
	@BeforeMethod(alwaysRun = true)
    public void setup() throws IOException {
		
		config = new ConfigReader();
		
		DriverFactory.initializeBrowser();
		
		driver = DriverFactory.getDriver();
		
		driver.get(config.getProperty("url"));
    }
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitBrowser();
    }
}
