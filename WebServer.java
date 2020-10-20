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

	public static void main(String args[]) throws Exception {
		try {
			String modo = args[0];
			int porta = Integer.parseInt(args[1]);

			// Listen(escuta) a porta 8080 => Aceita a conexão na porta passada abaixo
			ServerSocket listenSocket = new ServerSocket(porta);
			System.err.println("Servidor rodando...");
			System.err.println("Porta: " + porta);

			while(true){
				switch (modo) {
					case "-f": // Cria Processos
						System.out.print("Não implementado para Java!");
						
						break;
					case "-t": // Cria Threads

						// Cria-se um objeto socket
						WebServer myServer = new WebServer(listenSocket.accept()); 
						
						// Cria-se Threads para realizar novas conexões para cada Client
						Thread thread = new Thread(myServer);

						// Inicia usando o método 'public void run()'
						thread.start();
						break;
				
					default:
						break;
				}
			}
		} catch (IOException e) {
			System.err.println("Server Connection error : " + e.getMessage());
		}
	}

	public void run() {
		String requestMessageLine = null;
		String fileName = null;
		String capitalizedSentence = null;
		BufferedReader inFromClient = null;
		DataOutputStream outToClient = null;

		try {
			while(true){
				// Entrada da informação Client -> Server
				inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

				// Saída da informação Server -> Client
				outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			
				requestMessageLine = inFromClient.readLine();
				
				// if (requestMessageLine == null)
				// 	break;
				
				if (requestMessageLine.equals("exit")){
					outToClient.writeBytes("Conexao finalizada . . .");
					break;
				}
				
				System.out.println(requestMessageLine);

				/** Envia para o cliente uma resposta **/
				// capitalizedSentence = requestMessageLine.toUpperCase() + '\n';
				// outToClient.writeBytes(capitalizedSentence);
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
							"<html  xml:lang=\"en\" lang=\"en\">\r\n" +
							"<head>\r\n" +
							"<title>Linux/kernel/ - Linux Cross Reference - Free Electrons</title>\r\n" +
							"</head>\r\n" +
							"<body>\r\n");

							for (int i = 0; i < names.length; i++) {
								String line = String.format("<td><a href=\"%s\" target='_blank'>%s</a></td>\n", names[i], names[i]);
								outToClient.writeBytes(line);
							}
							outToClient.writeBytes("</body>\r\n");
							return;
						}
						
						FileInputStream inFile = new FileInputStream(fileName);
						byte[] fileInBytes = new byte[numOfBytes];

						inFile.read(fileInBytes);
						
						outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
						outToClient.writeBytes("Server: FACOMCD-2020/1.0\r\n");
						
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
						outToClient.writeBytes("Server: FACOMCD-2020/1.0\r\n");
						outToClient.writeBytes("Content-Type: text/plain\r\n");
						outToClient.writeBytes("Nao pode encontrar essa url\r\n");
					}
				}
				else
					System.out.println("Bad Request Message");
			}
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}
}
