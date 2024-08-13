package parser;
import java.util.List;
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
		switch(peek().getType()) {
			case TokenType.TK_A:
			case TokenType.TK_B:
			case TokenType.TK_C:
			case TokenType.TK_D:
			case TokenType.TK_E:
			case TokenType.TK_H:
			case TokenType.TK_L:
			case TokenType.TK_M:
				return new Register(peek().getType(), peek().getLine());
			case TokenType.TK_LITERAL:
				return new Literal(peek().getValue(), peek().getLine());
			case TokenType.TK_LABEL:
				return new Label(peek().getLexeme(), peek().getLine());
			case TokenType.TK_ID:
				return new Identifier(peek().getLexeme(), peek().getLine());
			case TokenType.TK_RET:
				return new Mnemonic(null, null, peek().getType(), peek().getLine());
			case TokenType.TK_LP:
				next();
				AST expr = expression();
				if(!match(TokenType.TK_RP)) {
					error("Missing ')' !");
					return null;
				}
				
				return expr;
			case TokenType.TK_MINUS:
				if(match(TokenType.TK_LITERAL)) {
					return new Literal(peek().getValue() * -1, peek().getLine());
				}
			case TokenType.TK_PLUS:
			case TokenType.TK_DIV:
			case TokenType.TK_COMMA:
				error("Token in invalid context!");
				return null;
			default:
				return parseMnemonic();
		}
	}
	
	private void next() {
		pos++;
	}
	
	private AST parseMnemonic() {
		TokenType ins = peek().getType();
		
		next();
		
		AST expr = expression();
		
		if(expr == null) {
			error("Expression expected!");
			return null;
		}
		
		if(!match(TokenType.TK_COMMA)) {
			error("',' expected!");
			return null;
		}
		
		next();
		
		AST expr2 = expression();
		
		if(expr2 == null) {
			error("Expression expected!");
			return null;
		}
		
		return new Mnemonic(expr, expr2, ins, peek().getLine());
	}
	
	private void error(String... args) {
		System.out.print("Error(" + peek().getLine() + "): ");
		for(var c : args) {
			System.out.print(c);
		}
		error = true;
		return;
	}
}
