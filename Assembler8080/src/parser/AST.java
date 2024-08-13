package parser;

public abstract class AST {
	protected AType type;
	protected int   line;
	
	public AST(AType type, int line) {
		this.type = type;
		this.line = line;
	}
	
	public AType getType() {
		return type;
	}
	
	public int getLine() {
		return line;
	}
}