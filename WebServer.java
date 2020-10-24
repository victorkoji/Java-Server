import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.Thread;
import java.nio.file.*;

class WebServer implements Runnable{

	private Socket connectionSocket;

	/** Inicializa a conexão com o socket **/
	public WebServer(Socket connec) {
		this.connectionSocket = connec;
	}

	public static void main(String args[]) throws Exception {
		try {
			String modo = args[1];
			int porta = Integer.parseInt(args[2]);

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
			
				if (requestMessageLine.equals("exit")){
					outToClient.writeBytes("Conexao finalizada . . .");
					break;
				}
				
				System.out.println(requestMessageLine);

				StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);

				if (tokenizedLine.nextToken().equals("GET")) {
					fileName = tokenizedLine.nextToken();

					if(fileName.equals("/") || fileName.equals(""))
						fileName = "public/";
					else if (fileName.startsWith("/") == true)
						fileName = fileName.substring(1);

					try {
						File file = new File(fileName);

						//getParent -> retorna valores, digamos, por cada diretorio
						//Exemplo: [pasta1],[pasta2],[cgi-bin]

						//getPath -> retorna GERAL, pegando tudo depois de localhost
						//Exemplo: /pasta1/pasta2/cgi-bin

						/** Verifica se o getParent é diferente de nulo e se é dentro do cgi-bin, executamos o script **/
						if(file.getParent() != null && file.getParent().equals("cgi-bin"))
							executarProgramaCgiBin(file, outToClient);

						/** Se for um diretório **/
						else if (file.isDirectory()) 
							listarItensDiretorio(file, outToClient);

						/** Se for um arquivo **/
						else
							abrirArquivo(file, outToClient);
						

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

	/**
	 * 1ª Documentação - 24/10/2020, por Victor Koji
	 * 
	 * O que faz: Essa função irá executar um arquivo cgi e retorna a saida para o client
	 * 
	 * Usado em: 
	 * @param file - Recebe o um objeto da classe File
	 * @param outToClient - Recebe o um objeto da classe DataOutputStream
	 * 
	 */
	private void executarProgramaCgiBin(File file, DataOutputStream outToClient) throws IOException {
		PrintStream output = new PrintStream(connectionSocket.getOutputStream());
		String program = file.getPath(); // Me retorna tudo que está depois do localhost
		String query = "";

		/** Verifica se existe o ponto de interrogação, ou seja, possui parâmetros **/
		if(file.getPath().indexOf('?') != -1){
			String[] path = file.getPath().split("\\?");
			program = path[0]; // Antes do ponto de interrogação
			query = path[1]; // Depois do ponto de interrogação
		}

		outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n"); // TROCAR
		outToClient.writeBytes("Content-Type: text/html\r\n"); // TROCAR

		ProcessBuilder pb = new ProcessBuilder(program);
		Map<String, String> env = pb.environment();
		env.put("QUERY_STRING", query);

		Process proc = pb.start();		
		InputStream is = proc.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		
		String line;
		while ( (line = br.readLine()) != null){
			// System.out.println(line); // TROCAR
			output.println(line); // TROCAR
		}

		output.flush(); // TROCAR
		br.close();
	}

	/**
	 * 1ª Documentação - 24/10/2020, por Victor Koji
	 * 
	 * O que faz: Essa função irá abrir um arquivo
	 * 
	 * Usado em: 
	 * @param file - Recebe o um objeto da classe File
	 * @param outToClient - Recebe o um objeto da classe DataOutputStream
	 * 
	 */
	private void abrirArquivo(File file, DataOutputStream outToClient) throws IOException{
		/** Inicializa as variáveis para abrir o arquivo **/
		String fileName = file.getPath();
		int numOfBytes = (int) file.length();
		FileInputStream inFile = new FileInputStream(fileName);
		byte[] fileInBytes = new byte[numOfBytes];
		String contentType = Files.probeContentType(file.toPath());

		inFile.read(fileInBytes);

		outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
		outToClient.writeBytes("Server: FACOMCD-2020/1.0\r\n");
		/** Verifica qual é a extensão do arquivo e coloca o content type de acordo essa extensão. **/
		outToClient.writeBytes("Content-Type: "+contentType+"\r\n");

		/** Adiciona o downlaod do arquivo txt **/
		if (fileName.endsWith(".txt") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".gif") || fileName.endsWith(".png"))
			outToClient.writeBytes("Content-Type: multipart/form-data; boundary=something\r\n");

		/** Adiciona o downlaod do arquivo pdf **/
		if (fileName.endsWith(".pdf"))
			outToClient.writeBytes("Content-Disposition: attachment; filename="+file.getName()+"\r\n");

		outToClient.writeBytes("Content-Length: " + numOfBytes + "\r\n");
		
		outToClient.writeBytes("\r\n");
		outToClient.write(fileInBytes, 0, numOfBytes);
	}

	/**
	 * 1ª Documentação - 19/10/2020, por Victor Koji
	 * 
	 * O que faz: Essa função lista os itens de um diretório.
	 * 
	 * @param file - Recebe o um objeto da classe File
	 * @param outToClient - Recebe o um objeto da classe DataOutputStream
	 */
	private void listarItensDiretorio(File file, DataOutputStream outToClient) throws IOException{
		String[] names = file.list();
		File[] caminhos = file.listFiles();

		/** Monta a header **/
		outToClient.writeBytes(
			"HTTP/1.0 200 Document Follows\r\n" +
			"Content-Type: text/html\r\n\r\n" +
			"<html  xml:lang=\"en\" lang=\"en\">\r\n" +
			"<head>\r\n" +
			"<title>Linux/kernel/ - Linux Cross Reference - Free Electrons</title>\r\n" +
			"</head>\r\n" +
			"<body>\r\n"
		);
		String line = String.format("<table>\n");
		line += String.format("<tr><th>Nome</th><th>Tipo</th></tr>\n");

		/** Percorre os itens que estão dentro do diretório **/
		for (int i = 0; i < names.length; i++) {
			Path path = caminhos[i].toPath();
			String contentType = Files.probeContentType(path);

			/** Se for nulo, é uma pasta. Se não for, é um tipo de arquivo **/
			if(contentType == null)
				contentType = "Pasta";

			/** Cria os links para a pasta ou arquivos **/
			line += String.format("<tr><td><a href=\"/%s/%s\">%s</a></td><td>%s</td></tr>\n", file, names[i], names[i], contentType);
		}

		/** Busca o caminho pai para podemos fazer o botão de voltar. **/
		String pathVoltar = file.getParent();
		if(pathVoltar == null)
			pathVoltar = "./public";

		line += String.format("<tr><td><a href=\"/%s\">Voltar</a></td></tr>\n", pathVoltar);
		line += String.format("</table>\n");
		outToClient.writeBytes(line);
		outToClient.writeBytes("</body>\r\n");
	}
}
