package factory;
import java.io.File;
import java.util.HashMap;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverFactory {
	
	private static WebDriver driver;
	
	public static WebDriver initializeBrowser() {
		WebDriverManager.chromedriver().setup();
		
		String downloadPath = new File(System.getProperty("user.dir"), "Downloads")
		        .getAbsolutePath();
		
		HashMap<String,Object> prefs = new HashMap<>();
		
		prefs.put("download.default_directory", downloadPath);
		prefs.put("download.prompt_for_download", false);
		prefs.put("plugins.always_open_pdf_externally", true);
		
		ChromeOptions option = new ChromeOptions();		
		option.setExperimentalOption("prefs", prefs);
		
		driver = new ChromeDriver(option);
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




