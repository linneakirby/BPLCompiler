package Compiler;

import java.util.Scanner;
import java.io.File;
import java.lang.Exception;
import java.lang.StringBuilder;
import java.lang.String;

public class BPLParser{
	private BPLScanner scanner;
	private ParseTreeNode program;
	private Token currentToken;

	public BPLParser(String filename){
		scanner = new BPLScanner(filename);
		getCurrentToken();
		program = createParseTree();
	}

	public Token currentToken(){
		return currentToken;
	}

	private void getCurrentToken(){
		try{
			scanner.getNextToken();
			currentToken = scanner.nextToken();
		}
		catch (Exception e){
			System.out.println(e.toString());
		}
	}

	private boolean checkCurrentToken(int check){
		if(currentToken.type == check){
			return true;
		}
		return false;
	}

	public ParseTreeNode getParseTree(){
		return program;
	}

	public String toString(){
		return program.toString();
	}

	private ParseTreeNode createParseTree(){
		ParseTreeNode program = new ParseTreeNode(currentToken, 1, "program");
		program.setChild(0, statement());
		return program;
	}

	private ParseTreeNode expressionStatement(){
		ParseTreeNode es = new ParseTreeNode(currentToken, 1, "expression statement");	
		if(checkCurrentToken(Token.T_SEMICOLON)){
			es.setChild(0, empty());
		}
		else{
			es.setChild(0, expression());
			getCurrentToken();
		}
		if(!checkCurrentToken(Token.T_SEMICOLON)){
			//TODO
		}
		getCurrentToken();
		return es;
	}

	private ParseTreeNode compoundStatement(){
		if(!checkCurrentToken(Token.T_LCURLY)){
			//TODO
		}
		ParseTreeNode cs = new ParseTreeNode(currentToken, 2, "compound statement");
		getCurrentToken();
		//TODO: cs.setChild(0, localDecs());
		cs.setChild(1, statementList());
		if(!checkCurrentToken(Token.T_RCURLY)){
			//TODO
		}
		return cs;
	}

	private ParseTreeNode ifStatement(){
		if(!checkCurrentToken(Token.T_IF)){
			//TODO
		}
		ParseTreeNode i = new ParseTreeNode(currentToken, 3, "if statement");
		getCurrentToken();
		if(!checkCurrentToken(Token.T_LPAREN)){
			//TODO
		}
		getCurrentToken();
		i.setChild(0, expression());
		getCurrentToken();
		if(!checkCurrentToken(Token.T_RPAREN)){
			//TODO
		}
		getCurrentToken();
		i.setChild(1, statement());
		getCurrentToken(); //NOTE: THIS MIGHT CAUSE PROBLEMS
		if(checkCurrentToken(Token.T_ELSE)){
			i.setChild(2, statement());
		}
		return i;
	}

	private ParseTreeNode statementList(){
		ParseTreeNode sl = new ParseTreeNode(currentToken, 2, "statement list");
		if(checkCurrentToken(Token.T_RCURLY)){
			sl.setChild(0, empty());
		}
		else{
			sl.setChild(0, statement());
			sl.setChild(1, statementList());
		}
		return sl;
	}

	private ParseTreeNode statement(){
		ParseTreeNode s = new ParseTreeNode(currentToken, 1, "statement");
		if(checkCurrentToken(Token.T_LCURLY)){
			s.setChild(0, compoundStatement());
		}
		else if(checkCurrentToken(Token.T_IF)){
			s.setChild(0, ifStatement());
		}
		else{
			s.setChild(0, expressionStatement());
		}
		return s;
	}

	private ParseTreeNode expression(){
		ParseTreeNode e = new ParseTreeNode(currentToken, 1, "expression");
		e.setChild(0, id());
		return e;
	}

	private ParseTreeNode id(){
		return new ParseTreeNode(currentToken, 0, currentToken.tokenString);
	}

	private ParseTreeNode empty(){
		return new ParseTreeNode(currentToken, 0, "empty");
	}

	public static void main(String[ ] args) {
		String filename ;
		BPLParser myParser;
		try{
			filename = args[0] ;
			myParser = new BPLParser(filename);
			System.out.println(myParser.toString());
			/*while (myScanner.nextToken().type != Token.T_EOF) { 
				try {
					myScanner.getNextToken();
					myScanner.printToken();
				}
				catch (Exception e) {
				System.out.println(e);
				}
			}*/
		}
		catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Please enter a filename! "+e.getMessage());
		}
		
	}
}