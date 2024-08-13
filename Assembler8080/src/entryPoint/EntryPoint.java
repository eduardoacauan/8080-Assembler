package entryPoint;
import java.io.IOException;

import lexer.*;

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
			
		}
		catch(IOException e) {
			System.out.println("Error opening file " + args[0]);
			return;
		}
	}
}
