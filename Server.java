import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {
        /*
         * cria um socket "servidor" associado a porta 8000 já aguardando conexões
         */
        ServerSocket servidor = new ServerSocket(8000);
        // aceita a primeita conexao que vier
        Socket socket = servidor.accept();
        // verifica se esta conectado
        if (socket.isConnected()) {
            // imprime na tela o IP do cliente
            System.out.println("O computador " + socket.getInetAddress() + " se conectou ao servidor.");
        }

        // cria um BufferedReader a partir do InputStream do cliente
        BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Requisição: ");
        // Lê a primeira linha
        String linha = buffer.readLine();
        // Enquanto a linha não for vazia
        while (!linha.isEmpty()) {
            // imprime a linha
            System.out.println(linha);
            // lê a proxima linha
            linha = buffer.readLine();
        }
    }
}