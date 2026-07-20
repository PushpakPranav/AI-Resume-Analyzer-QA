package factory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverFactory {
	
	private static WebDriver driver;
	
	public static WebDriver initializeBrowser() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		
		driver.manage().window().maximize();
		
		return driver;
		
	}
	
	public static WebDriver getDriver() {
		return driver;
		
	}
	
	public static void quitBrowser() {
		if(driver != null) {
		    driver.quit();
		}
	}

}




