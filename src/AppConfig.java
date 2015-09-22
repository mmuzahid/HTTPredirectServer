/*
 * Developer:         MD. MUZAHIDUL ISLAM
 * Email:             CV.MUZAHID@GMAIL.COM
 * Environment:       JDK 1.6
 * Date:              12-SEP-2015
 * */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * AppConfig - represents application configuration
 * */
public class AppConfig {
	
	private static String configDir = System.getProperty("user.dir", "");
	private static String globalConfigFileName = "global.config";
	private static String configCommentPrefix = "#";
	private static String configKeyValueSeparator = "=";
	private static Map<String, String> globalConfig;
	
	static {
		globalConfig = getConfiguration(configDir + File.separator + globalConfigFileName );
	}

	public static String getConfigDir() {
		return configDir;
	}

	public static void setConfigDir(String configDir) {
		AppConfig.configDir = configDir;
	}

	/**
	 * parse file and return configuration Map
	 * */
	public static Map<String, String> getConfiguration(String fileName) {
		Map<String, String> configMap = new HashMap<String, String>();
		File file = new File(fileName);
		Scanner fileScanner = null;
		String[] lineValues = null;
		
		try {
			fileScanner = new Scanner(file);
			
			while(fileScanner.hasNextLine()) {
				String line = fileScanner.nextLine().trim();
				
				if(line.startsWith(configCommentPrefix) || line.isEmpty()) {
					continue;
				}
				else if(!line.contains(configKeyValueSeparator)) {
					break;
				}
				
				lineValues = line.split(configKeyValueSeparator);
				configMap.put(lineValues[0].trim(), lineValues[1].trim());
			}
		} catch (FileNotFoundException e) {
			System.out.println(fileName + " file missing");
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
		finally {
			if (fileScanner != null) fileScanner.close();
		}
		
		return configMap;
	}

	public static String getValue(String key) {
		return globalConfig.get(key);
	}
	
	public static String getValue(String key, String defaultValue) {
		return globalConfig.containsKey(key) ? globalConfig.get(key) : defaultValue;
	}
}
