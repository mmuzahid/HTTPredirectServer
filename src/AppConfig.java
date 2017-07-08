/*
 * Developer:         MD. MUZAHIDUL ISLAM
 * Email:             CV.MUZAHID@GMAIL.COM
 * Environment:       JDK 1.6
 * Date:              12-SEP-2015
 * */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

/**
 * AppConfig - represents application configuration
 * */
public class AppConfig {
	
	private static String configDir = System.getProperty("user.dir", "");
	private static String appPropertyFileName = "app.properties";
	private static Properties appProperties;
	
	static {
		appProperties = getConfiguration(configDir + File.separator + appPropertyFileName);
	}

	public static String getConfigDir() {
		return configDir;
	}

	public static void setConfigDir(String configDir) {
		AppConfig.configDir = configDir;
	}

	/**
	 * returns application property
	 * */
	public static Properties getConfiguration(String fileName) {
		Properties appProps = new Properties();
		FileInputStream in;
		try {			
			in = new FileInputStream(fileName);
			appProps.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println(fileName + " file is missing");
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
		return appProps;
	}

	public static String getValue(String key) {
		return appProperties.getProperty(key);
	}
	
	public static String getValue(String key, String defaultValue) {
		return appProperties.getProperty(key, defaultValue);
	}
}
