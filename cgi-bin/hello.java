import java.net.UnknownHostException;
import java.util.*;
import java.io.*;
import java.net.Socket;

public class hello{
    public static void main(String [] args){
        String type = "Content-Type: text/html\n\n";
        String output = "<html>" + "<p>Hello CGI</p>" + "</html>";

        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String [] data = {in.readLine()};

            System.out.println(type);
            System.out.println(output + data[0]);
        }
        catch(IOException ioe){
            System.out.println("<p>IOException reading POST data:" + ioe + "</p>");
        }
    }
}
