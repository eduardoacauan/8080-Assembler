package entryPoint;
import java.io.IOException;

import assembler.*;

public class EntryPoint {
	public static void main(String[] args) {
		
		if(args.length == 0) {
			System.out.println("File expected!");
			return;
		}
		
		try {		
			Assembler asm = new Assembler(args[0]);
			
			asm.assembly();
		}
		catch(IOException e) {
			System.out.println("Error opening file " + args[0]);
			return;
		}
	}
}
