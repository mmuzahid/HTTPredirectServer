/*
* Developer:         MD. MUZAHIDUL ISLAM
* Email:             CV.MUZAHID@GMAIL.COM
* Environment:       JDK 1.6
* Date:              15-AUG-2015
* */
/**this class act as HTTP server*/
public class HttpRedirectServer {

	public static void main(String args[]) throws java.io.IOException, InterruptedException {
		int port  = AppConfig.globalConfig.get("application.port");
		java.net.ServerSocket server = new java.net.ServerSocket(port);
		java.net.Socket clientSocket = null;
		System.out.println("server started at 7070....");
		while (true) {	
			clientSocket = server.accept();
			HttpRequestProcessor.processClient(clientSocket);
		}
	}

}
