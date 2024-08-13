package parser;

public class Literal extends AST {
	private int value;
	
	public Literal(int value, int line) {
		super(AType.LITERAL, line);
		
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}