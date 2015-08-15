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
		String lang = "en";

		String noCacheHeader = "Cache-Control: no-cache, no-store, must-revalidate\r\n" + // HTTP 1.1 client
				"Pragma: no-cache\r\n" + // HTTP 1.0, for client
				"Expires: 0\r\n"; // HTTP 1.0, for client and proxies

		String redirectLocation = "https://" + lang + ".wikipedia.org/wiki/URL_redirection";	
		String responseBody = new String("HTTP/1.1 301 Moved Permanently\r\n"
				+ // 200 OK Moved Permanently\r\n
				"Location: " + redirectLocation + "\r\n"
				+ "Content-Type: text/html; charset=utf-8\r\n" + noCacheHeader
				+ "\r\n server time: " + (new java.util.Date())
				+ "<br \\> your request: <hr \\>"
				+ reqMessage.replace("\n", "<br \\>") + "\r\n");

		//System.out.println(requestBody.toString());
		java.io.OutputStream out = clientSocket.getOutputStream();
		out.write(responseBody.getBytes("UTF-8"));
		out.flush();
		out.close();
		reader.close();
		clientSocket.close();
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
