/*
 * Developer:         MD. MUZAHIDUL ISLAM
 * Email:             CV.MUZAHID@GMAIL.COM
 * Environment:       JDK 1.6
 * Date:              15-AUG-2015
 * */

import java.util.HashMap;
import java.util.Map;

/**
 * Object represents an HTTP request
 * */
public class HttpRequest {

	private static final String CRLF = System.getProperty("line.separator");// "\r\n";
	// private final String SP = " ";
	private String method;
	private String url;
	private String httpVersion;
	private Map<String, String> header = new HashMap<String, String>();
	private String body;

	public HttpRequest(String message) {
		try {
			prepareRequestObject(message);
		} catch (Exception e) {
			System.out.println("HttpRequest create Exception: "	+ e.getMessage());
		}
	}

	public String getMethod() {
		return method;
	}

	private void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	private void setUrl(String url) {
		this.url = url;
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	private void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getAcceptLanguage() {
		return header.get("Accept-Language");
	}

	/**
	 * Prepare HTTP Request Object by parsing string content
	 * */
	private void prepareRequestObject(String message) {
		int startPos = 0;
		int endPos = message.indexOf(CRLF);

		String[] reqLine = message.substring(startPos, endPos)
				.split("\\s+");
		this.setMethod(reqLine[0]);
		this.setUrl(reqLine[1]);
		this.setHttpVersion(reqLine[2]);
		do {
			startPos = endPos + CRLF.length();
			endPos = message.indexOf(CRLF, startPos);
			String line = message.substring(startPos, endPos);
			if (line.isEmpty()) {
				this.setBody(message.substring(endPos + CRLF.length()));
				break;
			} else {
				header.put(line.split(":")[0].trim(), line.split(":")[1].trim());
			}
		} while (true);
	}
	
}
