package ast;
import java.util.List;
import java.util.ArrayList;

public class ByteDecl extends AST {
	private List<AST> bytes;
	
	public ByteDecl(int line) {
		super(AType.BYTEDECLARATION, line);
		
		bytes = new ArrayList<>();
	}
	
	public List<AST> getBytes() {
		return bytes;
	}
}
