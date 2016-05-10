/*
* Developer:         MD. MUZAHIDUL ISLAM
* Email:             CV.MUZAHID@GMAIL.COM
* Environment:       JDK 1.6
* Date:              15-AUG-2015
* */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

/**A thread class to process HTTP request*/
public class HttpRequestProcessor implements Runnable {
	
	private static enum RedirectionType {LANG, RING};
	private static RedirectionType redirectionType;
	private static String[] redirectUrls;
	private static int ringLength;
	private static int currRingIndex;
	java.net.Socket clientSocket;
	private static Map<String, String> subDomainForLang = new TreeMap<String, String>();

	private static String defLang = "en";

	static {
		redirectUrls = AppConfig.getValue("redirect.urls").trim().split("\\s+");
		redirectionType = "lang".equalsIgnoreCase(AppConfig.getValue("redirect.type")) 
				? RedirectionType.LANG : RedirectionType.RING;
		ringLength = redirectUrls.length;
		currRingIndex = 0;
		subDomainForLang.put("en", "en");
		subDomainForLang.put("bn", "bn");
		subDomainForLang.put("ar", "ar");
		subDomainForLang.put("fr", "fr");				
	}
	
	public HttpRequestProcessor(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	/**process client socket request as HTTP request*/
	public void process() throws IOException, InterruptedException {
		String reqMessage = getContentAsString(clientSocket.getInputStream());
		HttpRequest request = new HttpRequest(reqMessage);
		
		String noCacheHeader = getNoCacheHeaderString();

		String redirectLocation = getLocation(request);
		
		String responseBody = new String("HTTP/1.1 301 Moved Permanently\r\n"
				+ 
				"Location: " + redirectLocation + "\r\n"
				+ "Content-Type: text/html; charset=utf-8\r\n" + noCacheHeader
				+ "\r\n server time: " + (new java.util.Date())
				+ "<br \\> your request: <hr \\>"
				+ reqMessage.replace("\n", "<br \\>") + "\r\n");

		OutputStream outputStream = clientSocket.getOutputStream();
		outputStream.write(responseBody.getBytes("UTF-8"));
		outputStream.flush();
		outputStream.close();
		clientSocket.close();
	}

	/**return redirect location*/
	public String getLocation(HttpRequest request) {
		String location = "";
		if (redirectionType == RedirectionType.LANG) {
			location = getLocationForLang(request.getAcceptLanguage());
		}
		else if (redirectionType == RedirectionType.RING) {
			location = getLocationForRingModel(request);
		}
		
		if (!location.startsWith("http")) {
			location = "https://" + location;
		}
		return location;
	}
	
	/**return redirect location based on circular/ring URL model
	 * static synchronized modifier to ensure RING redirection
	 * */
	private static synchronized String getLocationForRingModel(HttpRequest request) {
		currRingIndex = currRingIndex % ringLength;
		return redirectUrls[currRingIndex++];
	}

	/**return redirect location based on accept language in HTTP message*/
	private String getLocationForLang(String acceptLangs) {
		for (String lang : acceptLangs.split(",")) {
			lang = lang.split(";")[0];
			if (subDomainForLang.containsKey(lang)){
				return subDomainForLang.get(lang) + "." + redirectUrls[0];	
			}	
		}
		return subDomainForLang.get(defLang) + "." + redirectUrls[0];	
	}

	private String getContentAsString(InputStream inputStream) throws IOException {
		StringBuffer content = new StringBuffer("");
		do {
			content.append((char) inputStream.read());
		} while (inputStream.available() > 0);
		
		return content.toString();
	}
	
	private String getNoCacheHeaderString() {
		String noCacheHeader = "Cache-Control: no-cache, no-store, must-revalidate\r\n" + //for HTTP 1.1 client
				"Pragma: no-cache\r\n" + // for HTTP 1.0 client
				"Expires: 0\r\n"; // HTTP 1.0, for client and proxies
		return noCacheHeader;
	}
	
	/**thread method*/
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
