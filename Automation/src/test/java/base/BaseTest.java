package base;
import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.resumeanalyzer.ResumeAnalyzerAutomation.utils.ConfigReader;

import factory.DriverFactory;

public class BaseTest {
	protected WebDriver driver;
	protected ConfigReader config;
	
	
	@BeforeMethod
    public void setup() throws Exception {
		config = new ConfigReader();
		DriverFactory.initializeBrowser();
		driver = DriverFactory.getDriver();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.get(config.getProperty("url"));
    }
    @AfterMethod
    public void tearDown() {
        DriverFactory.quitBrowser();
    }
}
