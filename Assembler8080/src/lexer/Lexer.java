package lexer;
import tokens.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import preprocessor.Preprocessor;
/************************************************************
 * This class is responsible for returning a list of tokens *
 * after a complete lexical analysis in the file            *
 * by Eduardo S. Acauan 									*
 ***********************************************************/
public class Lexer {
	private final String  extension = "asm";
	private final String  headerext = "inc";
	private List<Token>   tokens;
	private String        buffer;
	private boolean       error;
	private int 	      pos;
	private int           line;
	private HashMap<String, TokenType> keys;
	private Preprocessor  pproc;
	
	public Lexer(String path) throws IOException {
		String ext = getExtension(path);
		if(!ext.equals(extension) && !ext.equals(headerext)){
			System.out.println("Invalid extension!");
			error = true;
			return;
		}
		tokens = new ArrayList<Token>();
		keys   = new HashMap<>();
		
		pproc = new Preprocessor(path);
		
		pproc.preprocess();
		
		if(pproc.getError()) {
			error = true;
			return;
		}
		
		
		buffer = pproc.getFinalBuffer();
		
		line  = pproc.getLine();
		pos   = 0;
		error = false;
		
		makeKeywords();
	}
	
	public boolean lex() {
		if(error)
			return false;
		while(!endStream()) {
			if(peek() <= ' ')
				skipSpaces();
			
			if(endStream())
				break;
		
			switch(peek()) {
				case ';':
					skipLine();
					continue;
				case '/':
					if(buffer.charAt(pos + 1) == '*') {
						next();
						multiLineSkip();
						break;
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
				case '\'':
					handleChar();
					break;
				case '\"':
					handleString();
					continue;
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
		insert(Token.makeToken(null, TokenType.TK_EOF, 0, line));
		return true;
	}
	
	private void handleChar() {
		next();
		
		char c = peek();
		
		if(pos + 1 >= buffer.length() || !match('\'')) {
			System.out.println("Error(" + line + "): " + "missing \' !");
			error = true;
			return;
		}
		
		insert(Token.makeToken("", TokenType.TK_LITERAL, c, line));
	}
	
	private void handleString() {
		StringBuilder str = new StringBuilder();
		
		do {
			next();
			str.append(peek());
		}while(!endStream() && pos + 1 < buffer.length() && !match('\"'));
		
		if(peek() != '\"') {
			System.out.println("Error(" + line + "): " + "Missing '\"'");
			error = true;
			return;
		}
		
		next();
		
		insert(Token.makeToken(str.toString(), TokenType.TK_STRING, 0, line));
	}
	
	private boolean match(char c) {
		if(endStream() || pos + 1 >= buffer.length() || buffer.charAt(pos + 1) != c)
			return false;
		next();
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
		
		if(!endStream() && peek() == ':') {
			next();
			insert(Token.makeToken(str.toString(), TokenType.TK_LABEL, 0, line));
			return;
		}
		
		insert(Token.makeToken(str.toString(), TokenType.TK_ID, 0, line));
	}
	
	private void number() {
		int value = 0;
		
		if(peek() == '0' && match('x') || match('X')) {
			next();
			
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
		
		while(!endStream() && isDigit()) {
			value = value * 10 + peek() - '0';
			next();
		}
		
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
		}while(!endStream() && pos + 1 < buffer.length() && buffer.charAt(pos + 1) != '/');
		
		pos++;
		
		if(endStream() || peek() != '*' && peek() != '/') {
			System.out.println("Error(" + line + "): missing */ !");
			error = true;
			return;
		}
	}
	
	private void skipSpaces() {
		while(!endStream() && peek() <= ' ') {
			if(peek() == '\n')
				line++;
			next();
		}
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
		keys.put("HL",  TokenType.TK_HL);
		keys.put("PSW", TokenType.TK_PSW);
		keys.put("SP",  TokenType.TK_SP);
		keys.put("JMP", TokenType.TK_JMP);
		keys.put("LXI", TokenType.TK_LXI);
		keys.put("ORG", TokenType.TK_ORG);
		keys.put("BYTE",TokenType.TK_BYTE);
		keys.put("WORD",TokenType.TK_WORD);
		keys.put("INX", TokenType.TK_INX);
		keys.put("STAX",TokenType.TK_STAX);
		keys.put("STA", TokenType.TK_STA);
		keys.put("SHLD",TokenType.TK_SHLD);
		keys.put("INR", TokenType.TK_INR);
		keys.put("DCR", TokenType.TK_DCR);
		keys.put("RLC", TokenType.TK_RLC);
		keys.put("RAL", TokenType.TK_RAL);
		keys.put("STC", TokenType.TK_STC);
		keys.put("DAA", TokenType.TK_DAA);
		keys.put("NOP", TokenType.TK_NOP);
		keys.put("LDAX",TokenType.TK_LDAX);
		keys.put("LHLD",TokenType.TK_LHLD);
		keys.put("LDA", TokenType.TK_LDA);
		keys.put("DAD", TokenType.TK_DAD);
		keys.put("DCX", TokenType.TK_DCX);
		keys.put("HLT", TokenType.TK_HLT);
		keys.put("RRC", TokenType.TK_RRC);
		keys.put("RAR", TokenType.TK_RAR);
		keys.put("CMC", TokenType.TK_CMC);
		keys.put("MOV", TokenType.TK_MOV);
		keys.put("ADD", TokenType.TK_ADD);
		keys.put("ANA", TokenType.TK_ANA);
		keys.put("ORA", TokenType.TK_ORA);
		keys.put("XRA", TokenType.TK_XRA);
		keys.put("ADC", TokenType.TK_ADC);
		keys.put("SUB", TokenType.TK_SUB);
		keys.put("SBB", TokenType.TK_SBB);
		keys.put("CMP", TokenType.TK_CMP);
		keys.put("JZ",  TokenType.TK_JZ);
		keys.put("RNZ", TokenType.TK_RNZ);
		keys.put("RNC", TokenType.TK_RNC);
		keys.put("RPO", TokenType.TK_RPO);
		keys.put("RP",  TokenType.TK_RPLUS);
		keys.put("PUSH",TokenType.TK_PUSH);
		keys.put("POP", TokenType.TK_POP);
		keys.put("JNZ", TokenType.TK_JNZ);
		keys.put("JNC", TokenType.TK_JNC);
		keys.put("JPO", TokenType.TK_JPO);
		keys.put("JP",  TokenType.TK_JP);
		keys.put("OUT", TokenType.TK_OUT);
		keys.put("XTHL",TokenType.TK_XTHL);
		keys.put("DI",  TokenType.TK_DI);
		keys.put("CNZ", TokenType.TK_CNZ);
		keys.put("CNC", TokenType.TK_CNC);
		keys.put("CPO", TokenType.TK_CPO);
		keys.put("CP",  TokenType.TK_CPLUS);
		keys.put("ADI", TokenType.TK_ADI);
		keys.put("SUI", TokenType.TK_SUI);
		keys.put("ANI", TokenType.TK_ANI);
		keys.put("ORI", TokenType.TK_ORI);
		keys.put("RST", TokenType.TK_RST);
		keys.put("RZ",  TokenType.TK_RZ);
		keys.put("RC",  TokenType.TK_RC);
		keys.put("RPE", TokenType.TK_RPE);
		keys.put("RM",  TokenType.TK_RM);
		keys.put("PCHL",TokenType.TK_PCHL);
		keys.put("SPHL",TokenType.TK_SPHL);
		keys.put("JZ",  TokenType.TK_JZ);
		keys.put("JC",  TokenType.TK_JC);
		keys.put("JPE", TokenType.TK_JPE);
		keys.put("JM",  TokenType.TK_JM);
		keys.put("IN",  TokenType.TK_IN);
		keys.put("XCHG",TokenType.TK_XCHG);
		keys.put("EI",  TokenType.TK_EI);
		keys.put("CZ",  TokenType.TK_CZ);
		keys.put("CC",  TokenType.TK_CC);
		keys.put("CPE", TokenType.TK_CPE);
		keys.put("CM",  TokenType.TK_CM);
		keys.put("CALL",TokenType.TK_CALL);
		keys.put("ACI", TokenType.TK_ACI);
		keys.put("SBI", TokenType.TK_SBI);
		keys.put("XRI", TokenType.TK_XRI);
		keys.put("CPI", TokenType.TK_CPI);
		keys.put("NOP", TokenType.TK_NOP);
	}
}
