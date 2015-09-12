/*
* Developer:         MD. MUZAHIDUL ISLAM
* Email:             CV.MUZAHID@GMAIL.COM
* Environment:       JDK 1.6
* Date:              15-AUG-2015
* */
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

/**A thread class to process HTTP request*/
public class HttpRequestProcessor implements Runnable {
	java.net.Socket clientSocket = null;

	public HttpRequestProcessor(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	/**process client socket request as HTTP request*/
	public void process() throws IOException, InterruptedException {
		StringBuffer requestBody = new StringBuffer("");
		java.io.InputStream input = clientSocket.getInputStream();
		java.io.InputStreamReader reader = new java.io.InputStreamReader(input);
		do {
			requestBody.append((char) input.read());
		} while (input.available() > 0);
		
		String reqMessage = requestBody.toString();
		HttpRequest request = new HttpRequest(reqMessage);
		String lang = request.getAcceptLanguage();

		String noCacheHeader = "Cache-Control: no-cache, no-store, must-revalidate\r\n" + //for HTTP 1.1 client
				"Pragma: no-cache\r\n" + // for HTTP 1.0 client
				"Expires: 0\r\n"; // HTTP 1.0, for client and proxies

		String redirectLocation = getLocationForLang(lang);
		
		String responseBody = new String("HTTP/1.1 301 Moved Permanently\r\n"
				+ 
				"Location: " + redirectLocation + "\r\n"
				+ "Content-Type: text/html; charset=utf-8\r\n" + noCacheHeader
				+ "\r\n server time: " + (new java.util.Date())
				+ "<br \\> your request: <hr \\>"
				+ reqMessage.replace("\n", "<br \\>") + "\r\n");

		java.io.OutputStream out = clientSocket.getOutputStream();
		out.write(responseBody.getBytes("UTF-8"));
		out.flush();
		out.close();
		reader.close();
		clientSocket.close();
	}

	/**override this method as your requirement*/
	public String getLocationForLang(String acceptLangs) {
		Map<String, String> langToPath = new TreeMap<String, String>();
		langToPath.put("en", "en");
		langToPath.put("bn", "bn");
		langToPath.put("ar", "ar");
		langToPath.put("fr", "fr");
		String defLang = "en";
		String selectedLang = defLang;
		
		String langs[] = acceptLangs.split(",");
		for (String lang : langs) {
			lang = lang.split(";")[0];
			if (langToPath.containsKey(lang)){
				selectedLang = lang;
				break;
			}	
		}

		String[] urls = AppConfig.globalConfig.get("redirect.urls").trim().split("\\s+");
		return "https://" + langToPath.get(selectedLang) + "." + urls[0];	
	}

	@Override
	public void run() {
		try {
			process();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}

	/**create new thread for each socket request*/
	public static void processClient(Socket clientSocket) {
		new Thread(new HttpRequestProcessor(clientSocket)).start();
	}

}
