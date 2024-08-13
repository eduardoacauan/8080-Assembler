package entryPoint;
import java.io.IOException;

import lexer.*;
import parser.*;

public class EntryPoint {
	public static void main(String[] args) {
		
		if(args.length == 0) {
			System.out.println("File expected!");
			return;
		}
		
		try {
			Lexer lexer = new Lexer(args[0]);
			
			if(lexer.getError())
				return;
			
			lexer.lex();
			
			if(lexer.getError())
				return;
			
			Parser parser = new Parser(lexer.getTokens());
			
			parser.build();
			
		}
		catch(IOException e) {
			System.out.println("Error opening file " + args[0]);
			return;
		}
	}
}
