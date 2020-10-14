import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.Thread;

class WebServer implements Runnable{
	// Client Connection via Socket Class
	private Socket connectionSocket;

	public WebServer(Socket connec) {
		this.connectionSocket = connec;
	}

	public static void main(String argv[]) throws Exception {
		while(true){
			ServerSocket listenSocket = new ServerSocket(6789);
			WebServer myServer = new WebServer(listenSocket.accept());
			
			// create dedicated thread to manage the client connection
			Thread thread = new Thread(myServer);
			thread.start();
		}
	}

	public void run() {
		String requestMessageLine = null;
		String fileName = null;
		String capitalizedSentence = null;
		BufferedReader inFromClient = null;
		DataOutputStream outToClient = null;

		try {
			inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		
			requestMessageLine = inFromClient.readLine();
			
			// if (requestMessageLine == null)
			// 	break;
			
			// if (requestMessageLine.equals("exit"))
			// 	break;
			
			System.out.println(requestMessageLine);

			/** Envia para o cliente uma resposta **/
			capitalizedSentence = requestMessageLine.toUpperCase() + '\n';
			outToClient.writeBytes(capitalizedSentence);
			/** Envia para o cliente uma resposta **/

			StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);

			if (tokenizedLine.nextToken().equals("GET")) {
				fileName = tokenizedLine.nextToken();
				if (fileName.startsWith("/") == true)
					fileName = fileName.substring(1);
				try {
					File file = new File(fileName);
					int numOfBytes = (int)file.length();

					if (file.isDirectory()) {
						String[] names = file.list();
						outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
						outToClient.writeBytes("Content-Type: text/html\r\n\r\n");
						outToClient.writeBytes(
						"<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\r\n" +
						"<head>\r\n" +
						"<title>Linux/kernel/ - Linux Cross Reference - Free Electrons</title>\r\n" +
						"</head>\r\n" +
						"<body>\r\n");

						
						for (int i = 0; i < names.length; i++) {
							String line = String.format("<td><a href=\"%s\">%s</a></td>\n", names[i], names[i]);
							outToClient.writeBytes(line);
						}
						outToClient.writeBytes("</body>\r\n");
						return;
					}
					
					FileInputStream inFile = new FileInputStream(fileName);
					byte[] fileInBytes = new byte[numOfBytes];

					inFile.read(fileInBytes);
					
					outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");

					if (fileName.endsWith(".jpg"))
						outToClient.writeBytes("Content-Type: image/jpeg\r\n");
				
					if (fileName.endsWith(".gif"))
						outToClient.writeBytes("Content-Type: image/gif\r\n");

					if (fileName.endsWith(".txt"))
						outToClient.writeBytes("Content-Type: text/plain\r\n");

					outToClient.writeBytes("Content-Length: " + numOfBytes + "\r\n");
					
					outToClient.writeBytes("\r\n");
					outToClient.write(fileInBytes, 0, numOfBytes);

					connectionSocket.close();
				}
				catch (IOException e) {
					outToClient.writeBytes("HTTP/1.1 404 File not found\r\n");
				}
				
				
			}
			else
				System.out.println("Bad Request Message");
		} catch (Exception e) {
			//TODO: handle exception
		}
	}
}
