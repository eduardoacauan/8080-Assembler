package ast;

public class Label extends AST {
	private String id;
	
	public Label(String id, int line) {
		super(AType.LABEL, line);
		
		this.id = id;
	}
	
	public String getID() {
		return id;
	}
}
