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

		ProcessBuilder pb = new ProcessBuilder(commandLine);
		Map<String, String> env = pb.environment();
		env.put("PARAM1", "PARAM1_VALUE");
		env.put("PARAM2", "PARAM2_VALUE");
		
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
