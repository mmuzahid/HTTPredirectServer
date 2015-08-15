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
	public void process() {
		//TODO
	}

	@Override
	public void run() {
		process();
	}

	/**create new thread for each socket request*/
	public static void processClient(Socket clientSocket) {
		new Thread(new HttpRequestProcessor(clientSocket)).start();
	}

}
