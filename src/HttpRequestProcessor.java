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
import java.util.HashMap;
import java.util.Map;

/**A thread class to process HTTP request*/
public class HttpRequestProcessor implements Runnable{	
	private static final RedirectionType redirectionType;
	private static final String[] redirectUrls;
	private static final int ringLength;
	private static int currRingIndex;
	private static final Map<String, String> subDomainForLang;
	private static final String defLang = "en";
	private static final LocationResolver<HttpRequest> locationResolver;
	private static final String noCacheHeader;

	private final Socket clientSocket;

	static {
		redirectUrls = getUrls();
		redirectionType = "lang".equalsIgnoreCase(AppConfig.getValue("redirect.type")) 
				? RedirectionType.LANG : RedirectionType.RING;
		ringLength = redirectUrls.length;
		subDomainForLang = new HashMap<String, String>();
		subDomainForLang.put("en", "en");
		subDomainForLang.put("bn", "bn");
		subDomainForLang.put("ar", "ar");
		subDomainForLang.put("fr", "fr");
		locationResolver = getLocationResolver();
		noCacheHeader = getNoCacheHeaderString();
	}

	public HttpRequestProcessor(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	/**create new thread for each socket request*/
	public static void processClient(Socket clientSocket) {
		new Thread(new HttpRequestProcessor(clientSocket)).start();
	}
	
	/**thread run*/
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
	
	/**process client socket request as HTTP request*/
	public void process() throws IOException, InterruptedException {
		String reqMessage = getContentAsString(clientSocket.getInputStream());
		HttpRequest request = new HttpRequest(reqMessage);		
		String redirectLocation = setHttpProtocol(locationResolver.getLocation(request));
		
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
	
	private static String[] getUrls() {
		return AppConfig.getValue("redirect.urls").trim().split("\\s+");
	}

	/**returns <code>LocationResolver</code> object based on <code>redirectionType</code>*/
	private static LocationResolver<HttpRequest> getLocationResolver() {
		if (redirectionType == RedirectionType.LANG) {
			return new LocationResolver<HttpRequest>() {
				@Override
				public String getLocation(HttpRequest request) {
					for (String lang : request.getAcceptLanguage().split(",")) {
						lang = lang.split(";")[0];
						if (subDomainForLang.containsKey(lang)) {
							return subDomainForLang.get(lang) + "."	+ redirectUrls[0];
						}
					}
					return subDomainForLang.get(defLang) + "." + redirectUrls[0];
				}
			};
		} else if (redirectionType == RedirectionType.RING) {
			return new LocationResolver<HttpRequest>() {
				@Override
				public synchronized String getLocation(HttpRequest request) {
					return redirectUrls[currRingIndex++ % ringLength];
				}
			};
		}
		throw new NullPointerException("Invalid Redirection Type");
	}

	private static String setHttpProtocol(String url) {
		if (!url.startsWith("http")) {
			url = "http://" + url;
		}
		return url;
	}

	private static String getNoCacheHeaderString() {
		String noCacheHeader = "Cache-Control: no-cache, no-store, must-revalidate\r\n" + //for HTTP 1.1 client
				"Pragma: no-cache\r\n" + // for HTTP 1.0 client
				"Expires: 0\r\n"; // HTTP 1.0, for client and proxies
		return noCacheHeader;
	}

	/**returns String from input stream */
	private String getContentAsString(InputStream inputStream) throws IOException {
		StringBuffer content = new StringBuffer("");
		do {
			content.append((char) inputStream.read());
		} while (inputStream.available() > 0);
		
		return content.toString();
	}
	
}
