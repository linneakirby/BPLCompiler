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
	private Token cachedToken;

	public BPLParser(String filename) throws BPLException{
		scanner = new BPLScanner(filename);
		getCurrentToken();
		program = createParseTree();
	}

	public Token currentToken(){
		return currentToken;
	}

	private void getCurrentToken(){
		try{
			if(cachedToken != null){
				currentToken = cachedToken;
				cachedToken = null;
			}
			else{
				scanner.getNextToken();
				currentToken = scanner.nextToken();
			}
		}
		catch (Exception e){
			System.out.println(e.toString());
		}
	}

	private void ungetCurrentToken(){
		cachedToken = currentToken;
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

	private ParseTreeNode createParseTree() throws BPLException{
		ParseTreeNode program = new ParseTreeNode(currentToken, 1, "program");
		program.setChild(0, statement());
		return program;
	}

	private ParseTreeNode expressionStatement() throws BPLException{
		ParseTreeNode es = new ParseTreeNode(currentToken, 1, "expression statement");	
		if(checkCurrentToken(Token.T_SEMICOLON)){
			es.setChild(0, empty());
		}
		else{
			es.setChild(0, expression());
			getCurrentToken();
		}
		if(!checkCurrentToken(Token.T_SEMICOLON)){
			throw new BPLException(currentToken.lineNumber, "Missing ;");
		}
		getCurrentToken();
		return es;
	}

	private ParseTreeNode typeSpecifier() throws BPLException{
		if(!checkCurrentToken(Token.T_INT) || checkCurrentToken(Token.T_VOID) || checkCurrentToken(Token.T_STRING)){
			throw new BPLException(currentToken.lineNumber, "Missing 'int' or 'void' or 'string'");
		}
		return new ParseTreeNode(currentToken, 0, currentToken.tokenString);
	}

	private ParseTreeNode varDec() throws BPLException{
		ParseTreeNode vd = new ParseTreeNode(currentToken, 3, "var dec");
		vd.setChild(0, typeSpecifier());
		getCurrentToken();
		if(checkCurrentToken(Token.T_STAR)){
			vd.setChild(1, star());
			vd.setChild(2, id());
			return vd;
		}
		else{
			vd.setChild(1, id());
			getCurrentToken();
			if(checkCurrentToken(Token.T_SEMICOLON)){
				return vd;
			}
			else if(!checkCurrentToken(Token.T_LBRACKET)){
				throw new BPLException(currentToken.lineNumber, "Missing '['");
			}
			getCurrentToken();
			vd.setChild(2, num());
			getCurrentToken();
			if(!checkCurrentToken(Token.T_RBRACKET)){
				throw new BPLException(currentToken.lineNumber, "Missing ']'");
			}
			getCurrentToken();
			if(!checkCurrentToken(Token.T_SEMICOLON)){
				throw new BPLException(currentToken.lineNumber, "Missing ';'");
			}
			getCurrentToken();
		}
		return vd;
	}

	private ParseTreeNode localDecs() throws BPLException{
		ParseTreeNode ld = new ParseTreeNode(currentToken, 2, "local decs");
		if(!checkCurrentToken(Token.T_INT) || checkCurrentToken(Token.T_VOID) || checkCurrentToken(Token.T_STRING)){
			ld.setChild(0, empty());
		}
		else{
			ld.setChild(0, varDec());
			ld.setChild(1, localDecs());
		}
		return ld;
	}

	private ParseTreeNode compoundStatement() throws BPLException{
		if(!checkCurrentToken(Token.T_LCURLY)){
			throw new BPLException(currentToken.lineNumber, "Missing {");
		}
		ParseTreeNode cs = new ParseTreeNode(currentToken, 2, "compound statement");
		getCurrentToken();
		cs.setChild(0, localDecs());
		cs.setChild(1, statementList());
		if(!checkCurrentToken(Token.T_RCURLY)){
			throw new BPLException(currentToken.lineNumber, "Missing }");
		}
		return cs;
	}

	private ParseTreeNode ifStatement() throws BPLException{
		if(!checkCurrentToken(Token.T_IF)){
			throw new BPLException(currentToken.lineNumber, "Missing if");
		}
		ParseTreeNode i = new ParseTreeNode(currentToken, 3, "if statement");
		getCurrentToken();
		if(!checkCurrentToken(Token.T_LPAREN)){
			throw new BPLException(currentToken.lineNumber, "Missing (");
		}
		getCurrentToken();
		i.setChild(0, expression());
		getCurrentToken();
		if(!checkCurrentToken(Token.T_RPAREN)){
			throw new BPLException(currentToken.lineNumber, "Missing )");
		}
		getCurrentToken();
		i.setChild(1, statement());
		if(checkCurrentToken(Token.T_ELSE)){
			i.setChild(2, statement());
		}
		return i;
	}

	private ParseTreeNode whileStatement() throws BPLException{
		if(!checkCurrentToken(Token.T_WHILE)){
			throw new BPLException(currentToken.lineNumber, "Missing while");
		}
		ParseTreeNode w = new ParseTreeNode(currentToken, 2, "while statement");
		getCurrentToken();
		if(!checkCurrentToken(Token.T_LPAREN)){
			throw new BPLException(currentToken.lineNumber, "Missing (");
		}
		getCurrentToken();
		w.setChild(0, expression());
		getCurrentToken();
		if(!checkCurrentToken(Token.T_RPAREN)){
			throw new BPLException(currentToken.lineNumber, "Missing )");
		}
		getCurrentToken();
		w.setChild(1, statement());
		return w;
	}

	private ParseTreeNode returnStatement() throws BPLException{
		if(!checkCurrentToken(Token.T_RETURN)){
			throw new BPLException(currentToken.lineNumber, "Missing return");
		}
		ParseTreeNode ret = new ParseTreeNode(currentToken, 1, "return statement");
		getCurrentToken();
		if(checkCurrentToken(Token.T_SEMICOLON)){
			return ret;
		}
		ret.setChild(0, expression());
		return ret;
	}

	private ParseTreeNode writeStatement() throws BPLException{
		if(!checkCurrentToken(Token.T_WRITE) && !checkCurrentToken(Token.T_WRITELN)){
			throw new BPLException(currentToken.lineNumber, "Missing 'write' or 'writeln'");
		}
		ParseTreeNode w;
		if(checkCurrentToken(Token.T_WRITE)){
			w = new ParseTreeNode(currentToken, 1, "write statement");
		}
		else{
			w = new ParseTreeNode(currentToken, 1, "writeln statement");
		}
		getCurrentToken();
		if(!checkCurrentToken(Token.T_LPAREN)){
			throw new BPLException(currentToken.lineNumber, "Missing '('");
		}
		getCurrentToken();
		if(checkCurrentToken(Token.T_RPAREN)){
			getCurrentToken();
			if(!checkCurrentToken(Token.T_SEMICOLON)){
				throw new BPLException(currentToken.lineNumber, "Missing ';'");
			}
			return w;
		}
		w.setChild(0, expression());
		getCurrentToken();
		if(!checkCurrentToken(Token.T_RPAREN)){
			throw new BPLException(currentToken.lineNumber, "Missing ')'");
		}
		getCurrentToken();
		if(!checkCurrentToken(Token.T_SEMICOLON)){
			throw new BPLException(currentToken.lineNumber, "Missing ';'");
		}
		return w;
	}

	private ParseTreeNode statementList() throws BPLException{
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

	private ParseTreeNode statement() throws BPLException{
		ParseTreeNode s = new ParseTreeNode(currentToken, 1, "statement");
		if(checkCurrentToken(Token.T_LCURLY)){
			s.setChild(0, compoundStatement());
		}
		else if(checkCurrentToken(Token.T_IF)){
			s.setChild(0, ifStatement());
		}
		else if(checkCurrentToken(Token.T_WHILE)){
			s.setChild(0, whileStatement());
		}
		else if(checkCurrentToken(Token.T_RETURN)){
			s.setChild(0, returnStatement());
		}
		else if(checkCurrentToken(Token.T_WRITE) || checkCurrentToken(Token.T_WRITELN)){
			s.setChild(0, writeStatement());
		}
		else{
			s.setChild(0, expressionStatement());
		}
		return s;
	}

	private ParseTreeNode expression() throws BPLException{
		ParseTreeNode e = new ParseTreeNode(currentToken, 1, "expression");
		e.setChild(0, id());
		return e;
	}

	private ParseTreeNode id() throws BPLException{
		if(!checkCurrentToken(Token.T_ID)){
			throw new BPLException(currentToken.lineNumber, "Missing '<id>'");
		}
		return new ParseTreeNode(currentToken, 0, currentToken.tokenString);
	}

	private ParseTreeNode num() throws BPLException{
		if(!checkCurrentToken(Token.T_NUM)){
			throw new BPLException(currentToken.lineNumber, "Missing '<num>'");
		}
		return new ParseTreeNode(currentToken, 0, currentToken.tokenString);
	}

	private ParseTreeNode star() throws BPLException{
		if(!checkCurrentToken(Token.T_STAR)){
			throw new BPLException(currentToken.lineNumber, "Missing '*'");
		}
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
		catch(BPLException b){
			System.out.println(b.getMessage());
		}
		
	}
}