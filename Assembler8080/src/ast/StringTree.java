package ast;

public class StringTree extends AST {
	private String id;
	
	public StringTree(String id, int line) {
		super(AType.STRING, line);
		
		this.id= id;
	}
	
	public String getID() {
		return id;
	}
}
