package generator;
import parser.AST;
import java.util.ArrayList;
import java.util.List;
/**********************************
 * This class is responsible for  *
 * generating the machine code    *
 * and returning it               *
 **********************************/
public class CodeGen {
	private List<AST>  trees;
	private List<Byte> program;
	private int 	   pc;
	private int 	   origin;
	
	public CodeGen(List<AST> trees) {
		this.trees = trees;
	}
	
	public List<Byte> getProgram(){
		return program;
	}
}
