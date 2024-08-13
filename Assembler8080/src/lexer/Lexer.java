package lexer;
import tokens.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
/************************************************************
 * This class is responsible for returning a list of tokens *
 * after a complete lexical analysis in the file            *
 * by Eduardo S. Acauan 									*
 ***********************************************************/
public class Lexer {
	private final String  extension = "asm";
	private List<Token>   tokens;
	private String        buffer;
	private boolean       error;
	private int 	      pos;
	private int           line;
	private HashMap<String, TokenType> keys;
	
	public Lexer(String path) throws IOException {
		String ext = getExtension(path);
		if(!ext.equals(extension)) {
			System.out.println("Invalid extension!");
			error = true;
			return;
		}
		tokens = new ArrayList<Token>();
		keys   = new HashMap<>();
		
		File file = new File(path);
		
		buffer = new String(Files.readAllBytes(file.toPath()), StandardCharsets.US_ASCII);
		
		line  = 1;
		pos   = 0;
		error = false;
		
		makeKeywords();
	}
	
	public boolean lex() {
		while(!endStream()) {
			if(peek() <= ' ')
				skipSpaces();
		
			switch(peek()) {
				case ';':
					skipLine();
					continue;
				case '/':
					if(buffer.charAt(pos + 1) == '*') {
						multiLineSkip();
						continue;
					}
					if(buffer.charAt(pos + 1) == '/') {
						skipLine();
						continue;
					}
					
					insert(Token.makeToken(null, TokenType.TK_DIV, 0, line));
					break;
				case '(':
					insert(Token.makeToken(null, TokenType.TK_LP, 0, line));
					break;
				case ')':
					insert(Token.makeToken(null, TokenType.TK_RP, 0, line));
					break;
				case '+':
					insert(Token.makeToken(null, TokenType.TK_PLUS, 0, line));
					break;
				case '-':
					insert(Token.makeToken(null, TokenType.TK_MINUS, 0, line));
					break;
				case '*':
					insert(Token.makeToken(null, TokenType.TK_MUL, 0, line));
					break;
				case ',':
					insert(Token.makeToken(null, TokenType.TK_COMMA, 0, line));
					break;
				default:
					if(isAlpha()) {
						identifier();
						continue;
					}
					
					if(isDigit()) {
						number();
						continue;
					}
				System.out.println("Error(" + line + "): invalid token: " + peek());
				error = true;
				break;
			}
			
			next();
		}
		
		if(error)
			return false;
		
		return true;
	}
	
	private void identifier() {
		StringBuilder str = new StringBuilder();
		
		do {
			str.append(peek());
			next();
		}while(!endStream() && (isAlpha() || isDigit()));
		
		if(keys.containsKey(str.toString().toUpperCase())) {
			insert(Token.makeToken(str.toString(), keys.get(str.toString().toUpperCase()), 0, line));
			return;
		}
		
		if(peek() == ':') {
			next();
			insert(Token.makeToken(str.toString(), TokenType.TK_LABEL, 0, line));
			return;
		}
		
		insert(Token.makeToken(str.toString(), TokenType.TK_ID, 0, line));
	}
	
	private void number() {
		int value = 0;
		
		if(peek() == '0' && (char)(buffer.charAt(pos + 1) & 0xDF) == 'X') {
			pos += 2;
			
			while(!endStream() && (isAlpha() || isDigit())) {
				if(isDigit()) {
					value = value << 4 | (peek() - '0') & 0xF;
				}
				else if(isHex()) {
					value = value << 4 | (peek() - 'A' + 10) & 0xF;
				}
				else {
					System.out.println("Error(" + line + "): invalid hex number " + peek());
					error = true;
				}
				next();
			}
			
			if(error)
				return;
			
			insert(Token.makeToken(null, TokenType.TK_LITERAL, value, line));
			return;
		}
		
		do {
			value = value * 10 + peek() - '0';
			next();
		}while(!endStream() && isDigit());
		
		insert(Token.makeToken(null, TokenType.TK_LITERAL, value, line));
	}
	
	public boolean getError() {
		return error;
	}
	
	public List<Token> getTokens(){
		return tokens;
	}
	
	private boolean isAlpha() {
		return (char)(peek() & 0xDF) >= 'A' && (char)(peek() & 0xDF) <= 'Z'
				|| buffer.charAt(pos) == '_';
	}
	
	private boolean isDigit() {
		return peek() >= '0' && peek() <= '9';
	}
	
	private String getExtension(String path) {
		if(path.lastIndexOf(".") != -1 && path.lastIndexOf(".") != 0)
			return path.substring(path.lastIndexOf(".") + 1);
		return null;
	}
	
	private boolean isHex() {
		return (char)(peek() & 0xDF) >= 'A' && (char)(peek() & 0xDF) <= 'F';
	}
	
	private void multiLineSkip() {
		do {
			if(peek() == '\n')
				line++;
			next();
		}while(pos < buffer.length() && peek() != '*' || buffer.charAt(pos + 1) != '/');
		
		pos += 2;
	}
	
	private void skipSpaces() {
		do {
			if(peek() == '\n')
				line++;
			next();
		}while(!endStream() && peek() <= ' ');
	}
	
	private void skipLine() {
		do {
			if(peek() == '\n')
				line++;
			next();
		}while(!endStream() && peek() != '\n');
	}
	
	private void next() {
		pos++;
	}
	
	private boolean endStream() {
		return pos >= buffer.length();
	}
	
	private char peek() {
		return buffer.charAt(pos);
	}
	
	private void insert(Token token) {
		tokens.add(token);
	}
	
	private void makeKeywords() {
		keys.put("MVI", TokenType.TK_MVI);
		keys.put("RET", TokenType.TK_RET);
		keys.put("A",   TokenType.TK_A);
		keys.put("B",   TokenType.TK_B);
		keys.put("C",   TokenType.TK_C);
		keys.put("D",   TokenType.TK_D);
		keys.put("E",   TokenType.TK_E);
		keys.put("H",   TokenType.TK_H);
		keys.put("L",   TokenType.TK_L);
		keys.put("M",   TokenType.TK_M);
	}
}
