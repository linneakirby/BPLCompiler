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

	private ParseTreeNode createParseTree(){
		ParseTreeNode program = new ParseTreeNode(currentToken, 1, "program");
		program.setChild(0, statement());
		return program;
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

	private ParseTreeNode statement(){
		ParseTreeNode s = new ParseTreeNode(currentToken, 1, "statement");
		s.setChild(0, expressionStatement());
		return s;
	}

	private ParseTreeNode expressionStatement(){
		ParseTreeNode es = new ParseTreeNode(currentToken, 1, "expression statement");
		es.setChild(0, expression());
		getCurrentToken();
		if(!checkCurrentToken(Token.T_SEMICOLON)){
			//TODO
		}
		return es;
	}

	private ParseTreeNode expression(){
		ParseTreeNode e = new ParseTreeNode(currentToken, 1, "expression");
		e.setChild(0, id());
		return e;
	}

	private ParseTreeNode id(){
		ParseTreeNode i = new ParseTreeNode(currentToken, 0, currentToken.tokenString);
		return i;
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