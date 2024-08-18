package ast;

public class Identifier extends AST {
	private String id;
	
	public Identifier(String id, int line) {
		super(AType.IDENTIFIER, line);
		
		this.id = id;
	}
	
	public String getID() {
		return id;
	}
}
