package ast;
import tokens.TokenType;

public class BinaryExpr extends AST {
	private AST 	  left;
	private AST 	  right;
	private TokenType op;
	
	public BinaryExpr(AST left, AST right, TokenType op, int line) {
		super(AType.BINARYEXPR, line);
		
		this.left = left;
		this.right = right;
		this.op = op;
	}
	
	public AST getLeft() {
		return left;
	}
	
	public AST getRight() {
		return right;
	}
	
	public TokenType getOP() {
		return op;
	}
}
