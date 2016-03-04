package Compiler;

import java.util.Scanner;
import java.util.LinkedList;
import java.io.File;
import java.lang.Exception;
import java.lang.StringBuilder;
import java.lang.String;

public class BPLParser{
	private BPLScanner scanner;
	private ParseTreeNode program;
	private Token currentToken;
	private LinkedList<Token> cachedTokens;

	public boolean debug = true;

	public BPLParser(String filename) throws BPLException{
		scanner = new BPLScanner(filename);
		cachedTokens = new LinkedList<Token>();
		getCurrentToken();
		program = createParseTree();
	}

	public Token currentToken(){
		return currentToken;
	}

	private void getCurrentToken(){
		try{
			if(cachedTokens.size() != 0){
				currentToken = cachedTokens.poll();
			}
			else{
				scanner.getNextToken();
				currentToken = scanner.nextToken();
			}
			if(debug){
				System.out.println(currentToken().tokenString);
			}
		}
		catch (Exception e){
			System.out.println(e.toString());
		}
	}

	private void getCurrentTokenWhileCached(){
		try{
			scanner.getNextToken();
			currentToken = scanner.nextToken();
		}
		catch (Exception e){
			System.out.println(e.toString());
		}
	}

	private void ungetCurrentToken(){
		cachedTokens.add(currentToken);
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
		if(debug){
			System.out.println("PROGRAM");
		}
		ParseTreeNode program = new ParseTreeNode(currentToken, 1, "program");
		program.setChild(0, declarationList());
		return program;
	}

	private ParseTreeNode declarationList() throws BPLException{
		if(debug){
			System.out.println("DECLARATION LIST");
		}
		ParseTreeNode dl = new ParseTreeNode(currentToken, 2, "declaration list");
		dl.setChild(0, declaration());
		if(checkCurrentToken(Token.T_EOF)){
			return dl;
		}
		dl.setChild(1, declarationList());
		return dl;
	}

	private ParseTreeNode declaration() throws BPLException{
		if(debug){
			System.out.println("DECLARATION");
		}
		ParseTreeNode d = new ParseTreeNode(currentToken, 1, "declaration");
		if(!checkCurrentToken(Token.T_INT) && !checkCurrentToken(Token.T_VOID) && !checkCurrentToken(Token.T_STRING)){
			throw new BPLException(currentToken.lineNumber, "Missing 'int' or 'void' or 'string'");
		}
		ungetCurrentToken();
		getCurrentTokenWhileCached();
		if(!checkCurrentToken(Token.T_ID)){
			throw new BPLException(currentToken.lineNumber, "Missing '<id>'");
		}
		ungetCurrentToken();
		getCurrentTokenWhileCached();
		if(checkCurrentToken(Token.T_LPAREN)){
			ungetCurrentToken();
			getCurrentToken();
			d.setChild(0, funDec());
		}
		else{
			ungetCurrentToken();
			getCurrentToken();
			d.setChild(0, varDec());
		}
		return d;
	}

	private ParseTreeNode varDec() throws BPLException{
		if(debug){
			System.out.println("VAR DEC");
		}
		ParseTreeNode vd = new ParseTreeNode(currentToken, 5, "var dec");
		vd.setChild(0, typeSpecifier());
		getCurrentToken();
		if(checkCurrentToken(Token.T_STAR)){
			vd.setChild(1, star());
			getCurrentToken();
			vd.setChild(2, id());
			getCurrentToken();
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
			vd.setChild(2, new ParseTreeNode(currentToken, 0, currentToken.tokenString));
			getCurrentToken();
			vd.setChild(3, num());
			getCurrentToken();
			if(!checkCurrentToken(Token.T_RBRACKET)){
				throw new BPLException(currentToken.lineNumber, "Missing ']'");
			}
			vd.setChild(4, new ParseTreeNode(currentToken, 0, currentToken.tokenString));
			getCurrentToken();
			if(!checkCurrentToken(Token.T_SEMICOLON)){
				throw new BPLException(currentToken.lineNumber, "Missing ';'");
			}
			getCurrentToken();
		}
		return vd;
	}

	private ParseTreeNode typeSpecifier() throws BPLException{
		if(debug){
			System.out.println("TYPE SPECIFIER");
		}
		ParseTreeNode ts = new ParseTreeNode(currentToken, 1, "type specifier");
		if(!checkCurrentToken(Token.T_INT) && !checkCurrentToken(Token.T_VOID) && !checkCurrentToken(Token.T_STRING)){
			throw new BPLException(currentToken.lineNumber, "Missing 'int' or 'void' or 'string'");
		}
		ts.setChild(0, new ParseTreeNode(currentToken, 0, currentToken.tokenString));
		return ts;
	}

	private ParseTreeNode funDec() throws BPLException{
		if(debug){
			System.out.println("FUN DEC");
		}
		ParseTreeNode fd = new ParseTreeNode(currentToken, 4, "fun dec");
		fd.setChild(0, typeSpecifier());
		getCurrentToken();
		fd.setChild(1, id());
		getCurrentToken();
		if(!checkCurrentToken(Token.T_LPAREN)){
			throw new BPLException(currentToken.lineNumber, "Missing '('");
		}
		getCurrentToken();
		fd.setChild(2, params());
		if(!checkCurrentToken(Token.T_RPAREN)){
			throw new BPLException(currentToken.lineNumber, "Missing ')'");
		}
		getCurrentToken();
		fd.setChild(3, compoundStatement());
		return fd;
	}

	private ParseTreeNode params() throws BPLException{
		if(debug){
			System.out.println("PARAMS");
		}
		ParseTreeNode p = new ParseTreeNode(currentToken, 1, "params");
		if(checkCurrentToken(Token.T_VOID)){
			p.setChild(0, tvoid());
			getCurrentToken();
			return p;
		}
		p.setChild(0, paramList());
		return p;
	}

	private ParseTreeNode paramList() throws BPLException{
		if(debug){
			System.out.println("PARAM LIST");
		}
		ParseTreeNode pl = new ParseTreeNode(currentToken, 2, "param list");
		pl.setChild(0, param());
		if(checkCurrentToken(Token.T_RPAREN)){
			return pl;
		}
		if(!checkCurrentToken(Token.T_COMMA)){
			throw new BPLException(currentToken.lineNumber, "Missing ','");
		}
		getCurrentToken();
		pl.setChild(1, paramList());
		return pl;
	}

	private ParseTreeNode param() throws BPLException{
		if(debug){
			System.out.println("PARAM");
		}
		ParseTreeNode p = new ParseTreeNode(currentToken, 4, "param");
		if(!checkCurrentToken(Token.T_INT) && !checkCurrentToken(Token.T_VOID) && !checkCurrentToken(Token.T_STRING)){
			throw new BPLException(currentToken.lineNumber, "Missing 'int' or 'void' or 'string'");
		}
		p.setChild(0, typeSpecifier());
		getCurrentToken();
		if(checkCurrentToken(Token.T_STAR)){
			p.setChild(1, star());
			getCurrentToken();
			p.setChild(2, id());
		}
		else{
			p.setChild(1, id());
			getCurrentToken();
			if(checkCurrentToken(Token.T_LBRACKET)){
				p.setChild(2, new ParseTreeNode(currentToken, 0, currentToken.tokenString));
				getCurrentToken();
				if(!checkCurrentToken(Token.T_RBRACKET)){
					throw new BPLException(currentToken.lineNumber, "Missing ']'");
				}
				p.setChild(3, new ParseTreeNode(currentToken, 0, currentToken.tokenString));
				getCurrentToken();
			}
		}
		return p;
	}

	private ParseTreeNode compoundStatement() throws BPLException{
		if(debug){
			System.out.println("COMPOUND STATEMENT");
		}
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
		getCurrentToken();
		return cs;
	}

	private ParseTreeNode localDecs() throws BPLException{
		if(debug){
			System.out.println("LOCAL DECS");
		}
		ParseTreeNode ld = new ParseTreeNode(currentToken, 2, "local decs");
		if(!checkCurrentToken(Token.T_INT) && !checkCurrentToken(Token.T_VOID) && !checkCurrentToken(Token.T_STRING)){
			ld.setChild(0, empty());
		}
		else{
			ld.setChild(0, varDec());
			ld.setChild(1, localDecs());
		}
		return ld;
	}

	private ParseTreeNode statementList() throws BPLException{
		if(debug){
			System.out.println("STATEMENT LIST");
		}
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
		if(debug){
			System.out.println("STATEMENT");
		}
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

	private ParseTreeNode expressionStatement() throws BPLException{
		if(debug){
			System.out.println("EXPRESSION STATEMENT");
		}
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

	private ParseTreeNode ifStatement() throws BPLException{
		if(debug){
			System.out.println("IF STATEMENT");
		}
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
		if(debug){
			System.out.println("WHILE STATEMENT");
		}
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
			throw new BPLException(currentToken.lineNumber, "Missing ')'");
		}
		getCurrentToken();
		w.setChild(1, statement());
		return w;
	}

	private ParseTreeNode returnStatement() throws BPLException{
		if(debug){
			System.out.println("RETURN STATEMENT");
		}
		if(!checkCurrentToken(Token.T_RETURN)){
			throw new BPLException(currentToken.lineNumber, "Missing 'return'");
		}
		ParseTreeNode ret = new ParseTreeNode(currentToken, 1, "return statement");
		getCurrentToken();
		if(checkCurrentToken(Token.T_SEMICOLON)){
			return ret;
		}
		ret.setChild(0, expression());
		getCurrentToken();
		if(!checkCurrentToken(Token.T_SEMICOLON)){
			throw new BPLException(currentToken.lineNumber, "Missing ';'");
		}
		getCurrentToken();
		return ret;
	}

	private ParseTreeNode writeStatement() throws BPLException{
		if(debug){
			System.out.println("WRITE STATEMENT");
		}
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
		getCurrentToken();
		return w;
	}

//TODO
	private ParseTreeNode expression() throws BPLException{
		if(debug){
			System.out.println("EXPRESSION");
		}
		ParseTreeNode e = new ParseTreeNode(currentToken, 2, "expression");
		if(checkCurrentToken(Token.T_ID)){
			e.setChild(0, id());
			/*e.setChild(0, var());
			getCurrentToken();
			if(!checkCurrentToken(Token.T_EQ)){
				throw new BPLException(currentToken.lineNumber, "Missing '='");
			}
			getCurrentToken();
			e.setChild(1, expression());*/
		}
		return e;
	}

	private ParseTreeNode var() throws BPLException{
		if(debug){
			System.out.println("VAR");
		}
		ParseTreeNode v = new ParseTreeNode(currentToken, 4, "var");
		if(checkCurrentToken(Token.T_STAR)){
			v.setChild(0, star());
			getCurrentToken();
			v.setChild(1, id());
			getCurrentToken();
		}
		else{
			v.setChild(0, id());
			getCurrentToken();
			if(checkCurrentToken(Token.T_LBRACKET)){
				v.setChild(1, new ParseTreeNode(currentToken, 0, currentToken.tokenString));
				getCurrentToken();
				v.setChild(2, expression());
				if(!checkCurrentToken(Token.T_RBRACKET)){
					throw new BPLException(currentToken.lineNumber, "Missing ']'");
				}
				getCurrentToken();
			}
		}
		return v;
	}

	private ParseTreeNode compExp() throws BPLException{
		ParseTreeNode ce = new ParseTreeNode(currentToken, 4, "comp exp");
		return ce;
	}

	private ParseTreeNode id() throws BPLException{
		if(debug){
			System.out.println("ID");
		}
		if(!checkCurrentToken(Token.T_ID)){
			throw new BPLException(currentToken.lineNumber, "Missing '<id>'");
		}
		return new ParseTreeNode(currentToken, 0, currentToken.tokenString);
	}

	private ParseTreeNode num() throws BPLException{
		if(debug){
			System.out.println("NUM");
		}
		if(!checkCurrentToken(Token.T_NUM)){
			throw new BPLException(currentToken.lineNumber, "Missing '<num>'");
		}
		return new ParseTreeNode(currentToken, 0, currentToken.tokenString);
	}

	private ParseTreeNode star() throws BPLException{
		if(debug){
			System.out.println("STAR");
		}
		if(!checkCurrentToken(Token.T_STAR)){
			throw new BPLException(currentToken.lineNumber, "Missing '*'");
		}
		return new ParseTreeNode(currentToken, 0, currentToken.tokenString);
	}

	private ParseTreeNode tvoid() throws BPLException{
		if(debug){
			System.out.println("VOID");
		}
		if(!checkCurrentToken(Token.T_VOID)){
			throw new BPLException(currentToken.lineNumber, "Missing 'void'");
		}
		return new ParseTreeNode(currentToken, 0, "void");
	}

	private ParseTreeNode empty(){
		if(debug){
			System.out.println("EMPTY");
		}
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