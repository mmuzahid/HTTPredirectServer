/*
* Developer:         MD. MUZAHIDUL ISLAM
* Email:             CV.MUZAHID@GMAIL.COM
* Environment:       JDK 1.6
* Date:              15-AUG-2015
* */
/**
 * Entry point of HTTP server
 * */
public class HttpRedirectServer {

	public static void main(String args[]) throws java.io.IOException, InterruptedException {
		int port  = Integer.parseInt(AppConfig.getValue("application.port", "8080"));
		java.net.ServerSocket server = new java.net.ServerSocket(port);
		java.net.Socket clientSocket = null;
		System.out.println("server listening at the port: " + port);
		while (true) {	
			clientSocket = server.accept();
			HttpRequestProcessor.processClient(clientSocket);
		}
	}

}
