package ast;
import tokens.TokenType;

public class Register extends AST {
	private TokenType rg;
	
	public Register(TokenType rg, int line) {
		super(AType.REGISTER, line);
		
		this.rg = rg;
	}
	
	public TokenType getRG() {
		return rg;
	}
	
	public void setRG(TokenType type) {
		rg = type;
	}
}
