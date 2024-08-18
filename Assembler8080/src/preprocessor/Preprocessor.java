package preprocessor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

public class Preprocessor {
	private String 		  buffer;
	private int    		  pos;
	private StringBuilder finalBuffer;
	private StringBuilder joinedFiles;
	private int 		  line;
	private final String  inc = "include";
	private final String  def = "define";
	private boolean 	  error;
	
	public Preprocessor(String path) throws IOException {
		this.pos    = 0;
		finalBuffer = new StringBuilder();
		joinedFiles = new StringBuilder();
		
		File file = new File(path);
		
		buffer = new String(Files.readAllBytes(file.toPath()), StandardCharsets.US_ASCII);
		
		pos  = 0;
		line = 1;
		error = false;
	}
	
	public void preprocess() {
		include();
	}
	
	public String getFinalBuffer() {
		return finalBuffer.toString();
	}
	
	private void include() {
		while(!endStream()) {
			skipSpaces();
			
			if(peek() != '%') {
				skipLine();
				continue;
			}
			
			next();
			
			
			String key = captureString();
			
			if(!key.equals(inc)) {
				System.out.print("Error(" + line + "): ");
				System.out.println("Invalid preprocessor command!");
				error = true;
				continue;
			}
			
			next();
			
			_skipSpaces();
			
			if(peek() != '\"') {
				System.out.print("Error(" + line + "): ");
				System.out.println("String expected at preprocessor include!");
				error = true;
				continue;
			}
			
			next();
			
			String path = captureALL();
			
			if(error)
				return;
			
			next();
			
			try {
				joinFiles(path);
			}
			catch(IOException e) {
				error = true;
				System.out.println("Error: unable to open file \"" + e.getMessage() + "\"");
				return;
			}
		}
		
		if(error)
			return;
		
		finalBuffer = joinedFiles;
	}
	
	private boolean joinFiles(String path) throws IOException {
		File file = new File(path);
		
		String tmp = new String(Files.readAllBytes(file.toPath()), StandardCharsets.US_ASCII);
		
		joinedFiles.append(tmp);
		joinedFiles.append('\n');
		
		return true;
	}
	
	private void skipSpaces() {
		while(!endStream() && peek() <= ' ') {
			joinedFiles.append(peek());
			next();
		}
	}
	
	private void _skipSpaces() {
		while(!endStream() && !endLine() && peek() <= ' ') {
			next();
		}
	}
	
	private void skipLine() {
		do {
			if(peek() == '\n')
				line++;
			joinedFiles.append(peek());
			next();
		}while(!endStream() && peek() != '\n');
	}
	
	private boolean endStream() {
		return pos >= buffer.length();
	}
	
	private boolean endLine() {
		return !endStream() && buffer.charAt(pos) == '\n';
	}
	
	private char peek() {
		return buffer.charAt(pos);
	}
	
	private void next() {
		pos++;
	}
	
	private String captureString() {
		StringBuilder str = new StringBuilder();
		
		do {
			str.append(peek());
			next();
		}while(!endStream() && isAlpha());
		
		return str.toString();
	}
	
	private String captureALL() {
		StringBuilder str = new StringBuilder();
		
		do {
			str.append(peek());
			next();
		}while(!endLine() && peek() != '\"');
		
		if(endStream() || peek() != '\"') {
			System.out.println("Error(" + line + "): " + "Missing '\"'");
			error = true;
		}
		
		return str.toString();
	}
	
	private boolean isAlpha() {
		return (char)(peek() & 0xDF) >= 'A' && (char)(peek() & 0xDF) <= 'Z';
	}
	
	public int getLine() {
		return line;
	}
	
	public boolean getError() {
		return error;
	}
}
