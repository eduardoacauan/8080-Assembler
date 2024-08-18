package ast;
import tokens.TokenType;

public class Mnemonic extends AST {
	private AST 	  arg1;
	private AST 	  arg2;
	private TokenType ins;
	
	public Mnemonic(AST arg1, AST arg2, TokenType ins, int line) {
		super(AType.MNEMONIC, line);
		
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.ins  = ins;
	}
	
	public AST getArg1() {
		return arg1;
	}

	public AST getArg2() {
		return arg2;
	}
	
	public TokenType getIns() {
		return ins;
	}
}
