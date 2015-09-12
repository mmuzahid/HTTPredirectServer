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
	public static Map<String, String> globalConfig;
	
	static {
		globalConfig = getConfiguration(configDir + File.separator + globalConfigFileName );
	}

	public static String getConfigDir() {
		return configDir;
	}

	public static void setConfigDir(String configDir) {
		AppConfig.configDir = configDir;
	}

	public static Map<String, String> getConfiguration(String fileName) {
		Map<String, String> configMap = new HashMap<String, String>();
		File file = new File(fileName);
		Scanner fileScanner = null;
		String[] lineValues = null;
		
		try {
			fileScanner = new Scanner(file);
			
			while(fileScanner.hasNextLine()) {
				String line = fileScanner.nextLine();
				if(line.startsWith(configCommentPrefix)) {
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

}
