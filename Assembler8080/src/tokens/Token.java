package tokens;

public class Token {
	private TokenType type;
	private String    lexeme;
	private int 	  value;
	private int 	  line;
	
	public Token(String lexeme, TokenType type, int value, int line){
		this.lexeme = lexeme;
		this.value  = value;
		this.type   = type;
		this.line   = line;
	}
	
	public Token(TokenType type, int value, int line){
		this.value  = value;
		this.type   = type;
		this.line   = line;
	}
	
	public TokenType getType() {
		return type;
	}
	
	public String getLexeme() {
		return lexeme;
	}
	
	public int getValue() {
		return value;
	}
}
