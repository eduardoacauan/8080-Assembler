package parser;
import java.util.List;
import ast.*;

import java.util.ArrayList;
import tokens.*;
/********************************************
 * This class is responsible for            *
 * parsing the buffer of tokens             *
 * provided by the lexical analyser         *
 *                                          *
 * by Eduardo S. Acauan                     *
 *******************************************/
public class Parser {
	private List<AST>   trees;
	private List<Token> tokens;
	private int 		pos;
	private boolean     error;
	
	public Parser(List<Token> tokens) {
		this.tokens = tokens;
		
		trees = new ArrayList<>();
		pos = 0;
		error = false;
	}
	
	public boolean build() {
		while(!endStream()) {
			AST expr = expression();
			next();
			if(expr == null)
				continue;
			trees.add(expr);
		}
		
		if(error)
			return false;
		return true;
	}
	
	public List<AST> getTrees() {
		return trees;
	}
	
	public boolean getError() {
		return error;
	}
	
	private Token peek() {
		return tokens.get(pos);
	}
	
	private boolean match(TokenType type) {
		if(endStream() || tokens.get(pos + 1).getType() != type)
			return false;
		next();
		return true;
	}
	
	private boolean endStream() {
		return pos >= tokens.size() || peek().getType() == TokenType.TK_EOF;
	}
	
	private AST expression() {
		AST left = term();
		
		while(match(TokenType.TK_PLUS) || match(TokenType.TK_MINUS)) {
			TokenType op = peek().getType();
			
			next();
			
			AST right = term();
			
			left = new BinaryExpr(left, right, op, peek().getLine());
		}
		
		return left;
	}
	
	private AST term() {
		AST left = factor();
		
		while(match(TokenType.TK_MUL) || match(TokenType.TK_DIV)) {
			TokenType op = peek().getType();
			
			next();
			
			AST right = factor();
			
			left = new BinaryExpr(left, right, op, peek().getLine());
		}
		
		return left;
	}
	
	private AST factor() {
		if(endStream())
			return null;
		switch(peek().getType()) {
			case TokenType.TK_A:
			case TokenType.TK_B:
			case TokenType.TK_C:
			case TokenType.TK_D:
			case TokenType.TK_E:
			case TokenType.TK_H:
			case TokenType.TK_L:
			case TokenType.TK_M:
			case TokenType.TK_HL:
			case TokenType.TK_SP:
			case TokenType.TK_PSW:
				return new Register(peek().getType(), peek().getLine());
			case TokenType.TK_LITERAL:
				return new Literal(peek().getValue(), peek().getLine());
			case TokenType.TK_LABEL:
				return new Label(peek().getLexeme(), peek().getLine());
			case TokenType.TK_ID:
				return new Identifier(peek().getLexeme(), peek().getLine());
			case TokenType.TK_STRING:
				return new StringTree(peek().getLexeme(), peek().getLine());
			case TokenType.TK_HLT://0 args
			case TokenType.TK_STC:
			case TokenType.TK_RLC:
			case TokenType.TK_RNZ:
			case TokenType.TK_RNC:
			case TokenType.TK_RPO:
			case TokenType.TK_RPLUS:
			case TokenType.TK_RAL:
			case TokenType.TK_DAA:
			case TokenType.TK_NOP:
			case TokenType.TK_RAR:
			case TokenType.TK_DI:
			case TokenType.TK_EI:
			case TokenType.TK_XCHG:
			case TokenType.TK_PCHL:
			case TokenType.TK_XTHL:
			case TokenType.TK_SPHL:
			case TokenType.TK_RST:
			case TokenType.TK_RRC:
			case TokenType.TK_CMC:
			case TokenType.TK_CMA:
			case TokenType.TK_RET:
				return new Mnemonic(null, null, peek().getType(), peek().getLine());
			case TokenType.TK_LP:
				next();
				AST expr = expression();
				if(!match(TokenType.TK_RP)) {
					error("Missing ')' !");
					return null;
				}
				
				if(expr != null && expr.getType() == AType.REGISTER && ((Register)expr).getRG() == TokenType.TK_HL)
					((Register)expr).setRG(TokenType.TK_M);
				return expr;
			case TokenType.TK_MINUS:
				if(match(TokenType.TK_LITERAL)) {
					return new Literal(peek().getValue() * -1, peek().getLine());
				}
			case TokenType.TK_PLUS:
			case TokenType.TK_DIV:
			case TokenType.TK_COMMA:
			case TokenType.TK_MUL:
				error("Token in invalid context!");
				return null;
			//1 arg
			case TokenType.TK_CALL:
			case TokenType.TK_CZ:
			case TokenType.TK_CC:
			case TokenType.TK_CPE:
			case TokenType.TK_CM:
			case TokenType.TK_CNZ:
			case TokenType.TK_CNC:
			case TokenType.TK_CPO:
			case TokenType.TK_CPLUS:
			case TokenType.TK_ADI:
			case TokenType.TK_SUI:
			case TokenType.TK_ANI:
			case TokenType.TK_ORI:
			case TokenType.TK_ACI:
			case TokenType.TK_SBI:
			case TokenType.TK_XRI:
			case TokenType.TK_CPI:
			case TokenType.TK_IN:
			case TokenType.TK_OUT:
			case TokenType.TK_ADD:
			case TokenType.TK_DCR:
			case TokenType.TK_INR:
			case TokenType.TK_PUSH:
			case TokenType.TK_POP:
			case TokenType.TK_STA:
			case TokenType.TK_SHLD:
			case TokenType.TK_STAX:
			case TokenType.TK_INX:
			case TokenType.TK_ORG:
			case TokenType.TK_LDAX:
			case TokenType.TK_DAD:
			case TokenType.TK_DCX:
			case TokenType.TK_JZ:
			case TokenType.TK_JC:
			case TokenType.TK_JPE:
			case TokenType.TK_JM:
			case TokenType.TK_JNZ:
			case TokenType.TK_JNC:
			case TokenType.TK_JPO:
			case TokenType.TK_JP:
			case TokenType.TK_JMP:
			case TokenType.TK_ANA:
			case TokenType.TK_ORA:
			case TokenType.TK_XRA:
			case TokenType.TK_ADC:
			case TokenType.TK_CMP:
			case TokenType.TK_SBB:
			case TokenType.TK_SUB:
				TokenType type = peek().getType();
				next();
				return new Mnemonic(expression(), null, type, peek().getLine());
			case TokenType.TK_BYTE:
				return parseByte();
			case TokenType.TK_WORD:
				return parseWord();
			default:
				return parseMnemonic();
		}
	}
	
	private void next() {
		pos++;
	}
	
	private AST parseByte() {
		ByteDecl decl = new ByteDecl(peek().getLine());
		
		do {
			next();
			AST expr = expression();
			
			decl.getBytes().add(expr);
		}while(!endStream() && match(TokenType.TK_COMMA));
		
		return decl;
	}
	
	private AST parseWord() {
		WordDecl decl = new WordDecl(peek().getLine());
		
		do {
			next();
			AST expr = expression();
			
			decl.getWords().add(expr);
		}while(!endStream() && match(TokenType.TK_COMMA));
		
		return decl;
	}
	
	private AST parseMnemonic() {
		TokenType ins = peek().getType();
		
		next();
		
		if(peek().getType() == TokenType.TK_EOF) {
			error("Missing argument!");
			return null;
		}
		
		AST expr = expression();
		
		if(expr == null || expr.getType() == AType.MNEMONIC) {
			error("Invalid Left Hand Side !");
			return null;
		}
		
		if(!match(TokenType.TK_COMMA)) {
			error("',' expected!");
			return null;
		}
		
		next();
		
		if(peek().getType() == TokenType.TK_EOF) {
			error("Missing argument!");
			return null;
		}
		
		AST expr2 = expression();
		
		if(expr2 == null || expr2.getType()  == AType.MNEMONIC) {
			error("Invalid Right Hand Side !");
			return null;
		}
		
		return new Mnemonic(expr, expr2, ins, peek().getLine());
	}
	
	private void error(String args) {
		System.out.print("Error(" + peek().getLine() + "): " + args);
		System.out.print("\n");
		error = true;
		return;
	}
}
