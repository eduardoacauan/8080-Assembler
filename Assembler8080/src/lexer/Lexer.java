package lexer;
import tokens.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

public class Lexer {
	private List<Token>   tokens;
	private String        buffer;
	private boolean 	  error;
	private int 		  pos;
	private int 		  line;
	
	public Lexer(String path) throws IOException {
		if(getExtension(path) == null) {
			System.out.println("Invalid extension!");
			return;
		}
		tokens = new ArrayList<Token>();
		
		File file = new File(path);
		
		buffer = new String(Files.readAllBytes(file.toPath()), StandardCharsets.US_ASCII);
		
		System.out.println(buffer);
	}
	
	public boolean getError() {
		return error;
	}
	
	public List<Token> getTokens(){
		return tokens;
	}
	
	private boolean isAlpha() {
		return (char)(buffer.charAt(pos) & 0xDF) >= 'A' && (char)(buffer.charAt(pos) & 0xDF) <= 'Z'
				|| buffer.charAt(pos) == '_';
	}
	
	private boolean isDigit() {
		return buffer.charAt(pos) >= '0' && buffer.charAt(pos) <= '9';
	}
	
	private String getExtension(String path) {
		if(path.lastIndexOf(".") != -1 && path.lastIndexOf(".") != 0)
			return path.substring(path.lastIndexOf(".") + 1);
		return null;
	}
}
