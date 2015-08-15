/*
* Developer:         MD. MUZAHIDUL ISLAM
* Email:             CV.MUZAHID@GMAIL.COM
* Environment:       JDK 1.6
* Date:              15-AUG-2015
* */
/**this class act as HTTP server*/
public class HttpRedirectServer {

	public static void main(String args[]) throws java.io.IOException, InterruptedException {
		java.net.ServerSocket server = new java.net.ServerSocket(7070);
		java.net.Socket clientSocket = null;
		System.out.println("server started at 7070....");
		while (true) {	
			clientSocket = server.accept();
		}
	}

}
