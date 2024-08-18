package assembler;
import lexer.*;
import parser.*;
import generator.*;

import java.io.IOException;

public class Assembler {
	private Lexer   lex;
	private Parser  parser;
	private CodeGen gen;
	private String  path;
	
	public Assembler(String path) {
		this.path = path;
	}
	
	public void assembly() throws IOException {
		lex = new Lexer(path);
		
		if(!lex.lex())
			return;
		
		parser = new Parser(lex.getTokens());
		
		if(!parser.build())
			return;
		
		gen = new CodeGen(parser.getTrees());
		
		gen.generate(path);
		
		if(gen.getError())
			return;
		
		gen.WriteToFile(path);
	}
}