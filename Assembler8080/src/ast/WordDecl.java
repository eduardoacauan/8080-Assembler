package ast;
import java.util.List;
import java.util.ArrayList;

public class WordDecl extends AST {
	private List<AST> words;
	
	public WordDecl(int line) {
		super(AType.WORDDECLARATION, line);
		
		words = new ArrayList<>();
	}
	
	public List<AST> getWords() {
		return words;
	}
}