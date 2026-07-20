package com.resumeanalyzer.ResumeAnalyzerAutomation.utils;

import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
	private Properties prop;
	
	public ConfigReader() throws IOException {
		prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		
	}
	
	public String getProperty(String key) {
		return prop.getProperty(key);
		
		
	}
	
	
	

}
