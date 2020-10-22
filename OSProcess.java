import java.io.*;
import java.util.*;

public class OSProcess {
   public static void main(String[] args) throws IOException {

	while (true) {
		String commandLine;
		BufferedReader console = new BufferedReader (new InputStreamReader(System.in));

		System.out.print("jsh> ");
		commandLine = console.readLine();

		if (commandLine.equals(""))
			continue;
		if (commandLine.equals("exit"))
			break;

		ProcessBuilder pb = new ProcessBuilder("perl", "printenv.pl");
		Map<String, String> env = pb.environment();
		env.put("QUERY_STRING", "{<var>=<val>}*{<var>=<val>}");

		Process proc = pb.start();		
		// obtain the input stream
		InputStream is = proc.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		// read what is returned by the command
		String line;
		while ( (line = br.readLine()) != null)
			System.out.println(line);
    
		br.close();
	}
   }
}
