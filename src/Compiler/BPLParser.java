package Compiler;

import java.util.Scanner;
import java.util.LinkedList;
import java.util.Stack;
import java.io.File;
import java.lang.Exception;
import java.lang.StringBuilder;
import java.lang.String;

public class BPLParser{
	private BPLScanner scanner;
	private ParseTreeNode program;
	private Token currentToken;
	private LinkedList<Token> cachedTokens;

	//MAKE THIS BOOLEAN TRUE FOR DEBUGGING
	public boolean debug = false;

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

	public void getTokenFromCache(int index){
		try{
			currentToken = cachedTokens.remove(index);
			if(debug){
				System.out.println(currentToken.tokenString);
			}
		}
		catch (Exception e){
			System.out.println(e.toString());
		}
	}

	private void ungetCurrentToken(int index){
		cachedTokens.add(index, currentToken);
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
				getCurrentToken();
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
			getCurrentToken();
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
			//getCurrentToken();
		}
		if(!checkCurrentToken(Token.T_SEMICOLON)){
			throw new BPLException(currentToken.lineNumber, "Missing ';'");
		}
		getCurrentToken();
		return es;
	}

	private ParseTreeNode ifStatement() throws BPLException{
		if(debug){
			System.out.println("IF STATEMENT");
		}
		if(!checkCurrentToken(Token.T_IF)){
			throw new BPLException(currentToken.lineNumber, "Missing 'if'");
		}
		ParseTreeNode i = new ParseTreeNode(currentToken, 3, "if statement");
		getCurrentToken();
		if(!checkCurrentToken(Token.T_LPAREN)){
			throw new BPLException(currentToken.lineNumber, "Missing '('");
		}
		getCurrentToken();
		i.setChild(0, expression());
		//getCurrentToken();
		if(!checkCurrentToken(Token.T_RPAREN)){
			throw new BPLException(currentToken.lineNumber, "Missing ')'");
		}
		getCurrentToken();
		i.setChild(1, statement());
		if(checkCurrentToken(Token.T_ELSE)){
			getCurrentToken();
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
		//getCurrentToken();
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
		//getCurrentToken();
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
		//getCurrentToken();
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

	private ParseTreeNode expression() throws BPLException{
		if(debug){
			System.out.println("EXPRESSION");
		}
		LinkedList<Token> tokens = new LinkedList<Token>();
		Stack<Token> expressionTokens = new Stack<Token>();
		ParseTreeNode e = new ParseTreeNode(currentToken, 2, "expression");
		/*if(checkCurrentToken(Token.T_ID)){
			e.setChild(0, id());
		}*/
		while(!checkCurrentToken(Token.T_SEMICOLON)){
			if(expressionTokens.empty()){
				if(checkCurrentToken(Token.T_LPAREN) || checkCurrentToken(Token.T_LBRACKET)){
					expressionTokens.push(currentToken);
				}
				else if(checkCurrentToken(Token.T_RPAREN) || checkCurrentToken(Token.T_RBRACKET)){
					break;
				}
				else if(checkCurrentToken(Token.T_EQ)){
					tokens.add(currentToken);
					while(cachedTokens.size() > 0){
						tokens.add(cachedTokens.remove());
					}
					while(tokens.size() > 0){
						cachedTokens.add(tokens.remove());
					}
					getCurrentToken();
					e.setChild(0, var());
					if(!checkCurrentToken(Token.T_EQ)){
						throw new BPLException(currentToken.lineNumber, "Missing '='");
					}
					getCurrentToken();
					e.setChild(1, expression());
					return e;
				}
				tokens.add(currentToken);
				getCurrentToken();
			}
			if(!expressionTokens.empty()){
				if(checkCurrentToken(Token.T_LPAREN) || checkCurrentToken(Token.T_LBRACKET)){
					expressionTokens.push(currentToken);
				}
				else if(checkCurrentToken(Token.T_RPAREN) || checkCurrentToken(Token.T_RBRACKET)){
					Token parenbracket = expressionTokens.pop();
					if((!(checkCurrentToken(Token.T_RPAREN)) && (parenbracket.type == Token.T_LPAREN))){
						throw new BPLException(currentToken.lineNumber, "Missing '(' or ')'");
					}
					else if((!(checkCurrentToken(Token.T_RBRACKET)) && (parenbracket.type == Token.T_LBRACKET))){
						throw new BPLException(currentToken.lineNumber, "Missing '[' or ']'");
					}
				}
				tokens.add(currentToken);
				getCurrentToken();
			}
		}
		tokens.add(currentToken);
		while(cachedTokens.size() > 0){
			tokens.add(cachedTokens.remove());
		}
		while(tokens.size() > 0){
			cachedTokens.add(tokens.remove());
		}
		getCurrentToken();
		e.setChild(0, compExp());
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
		if(debug){
			System.out.println("COMP EXP");
		}
		ParseTreeNode ce = new ParseTreeNode(currentToken, 3, "comp exp");
		ce.setChild(0, e());
		getCurrentToken();
		if(checkCurrentToken(Token.T_LESSEQ) || checkCurrentToken(Token.T_LESS) || checkCurrentToken(Token.T_EQEQ)
		 || checkCurrentToken(Token.T_NOTEQ) || checkCurrentToken(Token.T_GREATER) || checkCurrentToken(Token.T_GREATEREQ)){
			ce.setChild(1, relop());
			ce.setChild(2, e());
			getCurrentToken();
		}
		/*else{
			ungetCurrentToken(0);
			getCurrentToken();
		}*/
		return ce;
	}

	private ParseTreeNode relop() throws BPLException{
		if(debug){
			System.out.println("RELOP");
		}
		ParseTreeNode r = new ParseTreeNode(currentToken, 1, "relop");
		if(!checkCurrentToken(Token.T_LESSEQ) && !checkCurrentToken(Token.T_LESS) && !checkCurrentToken(Token.T_EQEQ)
		 && !checkCurrentToken(Token.T_NOTEQ) && !checkCurrentToken(Token.T_GREATER) && !checkCurrentToken(Token.T_GREATEREQ)){
			throw new BPLException(currentToken.lineNumber, "Missing '<=', '<', '==', '!=', '>', or '>='");
		}
		r.setChild(0, new ParseTreeNode(currentToken, 0, currentToken.tokenString));
		getCurrentToken();
		return r;
	}

	private ParseTreeNode e() throws BPLException{
		if(debug){
			System.out.println("E");
		}
		ParseTreeNode ee = new ParseTreeNode(currentToken, 3, "E");
		ee.setChild(0, t());
		getCurrentToken();
		if(checkCurrentToken(Token.T_PLUS) || checkCurrentToken(Token.T_MINUS)){
			ee.setChild(1, addop());
			ee.setChild(2, e());
		}
		else{
			ungetCurrentToken(0);
			//getCurrentToken();
		}
		return ee;
	}

	private ParseTreeNode addop() throws BPLException{
		if(debug){
			System.out.println("ADDOP");
		}
		ParseTreeNode a = new ParseTreeNode(currentToken, 1, "addop");
		if(!checkCurrentToken(Token.T_PLUS) && !checkCurrentToken(Token.T_MINUS)){
			throw new BPLException(currentToken.lineNumber, "Missing '+' or '-'");
		}
		a.setChild(0, new ParseTreeNode(currentToken, 0, currentToken.tokenString));
		getCurrentToken();
		return a;
	}

	private ParseTreeNode t() throws BPLException{
		if(debug){
			System.out.println("T");
		}
		ParseTreeNode tt = new ParseTreeNode(currentToken, 3, "T");
		tt.setChild(0, f());
		getCurrentToken();
		if(checkCurrentToken(Token.T_STAR) || checkCurrentToken(Token.T_SLASH) || checkCurrentToken(Token.T_PERCENT)){
			tt.setChild(1, mulop());
			tt.setChild(2, t());
		}
		else{
			ungetCurrentToken(0);
			//getCurrentToken();
		}
		return tt;
	}

	private ParseTreeNode mulop() throws BPLException{
		if(debug){
			System.out.println("MULOP");
		}
		ParseTreeNode m = new ParseTreeNode(currentToken, 1, "mulop");
		if(!checkCurrentToken(Token.T_STAR) && !checkCurrentToken(Token.T_SLASH) && !checkCurrentToken(Token.T_PERCENT)){
			throw new BPLException(currentToken.lineNumber, "Missing '*' or '/' or '%");
		}
		m.setChild(0, new ParseTreeNode(currentToken, 0, currentToken.tokenString));
		getCurrentToken();
		return m;
	}

	private ParseTreeNode f() throws BPLException{
		if(debug){
			System.out.println("F");
		}
		ParseTreeNode ff = new ParseTreeNode(currentToken, 2, "F");
		if(checkCurrentToken(Token.T_MINUS)){
			ff.setChild(0, new ParseTreeNode(currentToken, 0, currentToken.tokenString));
			getCurrentToken();
			ff.setChild(1, f());
		}
		else if(checkCurrentToken(Token.T_AND) || checkCurrentToken(Token.T_STAR)){
			ff.setChild(0, new ParseTreeNode(currentToken, 0, currentToken.tokenString));
			getCurrentToken();
			ff.setChild(1, factor());
		}
		else{
			ff.setChild(0, factor());
		}
		return ff;
	}

	private ParseTreeNode factor() throws BPLException{
		if(debug){
			System.out.println("FACTOR");
		}
		ParseTreeNode f = new ParseTreeNode(currentToken, 4, "factor");
		if(checkCurrentToken(Token.T_LPAREN)){
			getCurrentToken();
			f.setChild(0, expression());
			if(!checkCurrentToken(Token.T_RPAREN)){
				throw new BPLException(currentToken.lineNumber, "Missing ')'");
			}
			getCurrentToken();
		}
		else if(checkCurrentToken(Token.T_NUM)){
			f.setChild(0, num());
			//getCurrentToken();
		}
		else if(checkCurrentToken(Token.T_STRINGLITERAL)){
			f.setChild(0, stringLiteral());
			//getCurrentToken();
		}
		/*else if(checkCurrentToken(Token.T_STAR)){
			f.setChild(0, star());
			getCurrentToken();
			f.setChild(1, id());
			getCurrentToken();
		}*/
		else if(checkCurrentToken(Token.T_READ)){
			f.setChild(0, new ParseTreeNode(currentToken, 0, "read"));
			getCurrentToken();
			if(!checkCurrentToken(Token.T_LPAREN)){
				throw new BPLException(currentToken.lineNumber, "Missing '('");
			}
			getCurrentToken();
			if(!checkCurrentToken(Token.T_RPAREN)){
				throw new BPLException(currentToken.lineNumber, "Missing ')'");
			}
			//getCurrentToken();
		}
		else{
			ungetCurrentToken(0);
			getTokenFromCache(1);
			if(checkCurrentToken(Token.T_LPAREN)){
				ungetCurrentToken(1);
				getCurrentToken();
				f.setChild(0, funCall());
			}
			else if(checkCurrentToken(Token.T_LBRACKET)){
				ungetCurrentToken(1);
				getCurrentToken();
				f.setChild(0, id());
				getCurrentToken();
				getCurrentToken();
				f.setChild(1, expression());
				if(!checkCurrentToken(Token.T_RBRACKET)){
					throw new BPLException(currentToken.lineNumber, "Missing ']'");
				}
			}
			else{
				ungetCurrentToken(1);
				getCurrentToken();
				f.setChild(0, id());
				//getCurrentToken();
			}
		}
		return f;
	}

	private ParseTreeNode funCall() throws BPLException{
		if(debug){
			System.out.println("FUNCALL");
		}
		ParseTreeNode fc = new ParseTreeNode(currentToken, 2, "fun call");
		fc.setChild(0, id());
		getCurrentToken();
		if(!checkCurrentToken(Token.T_LPAREN)){
			throw new BPLException(currentToken.lineNumber, "Missing '('");
		}
		getCurrentToken();
		fc.setChild(1, args());
		if(!checkCurrentToken(Token.T_RPAREN)){
			throw new BPLException(currentToken.lineNumber, "Missing ')'");
		}
		//getCurrentToken();
		return fc;
	}

	private ParseTreeNode args() throws BPLException{
		if(debug){
			System.out.println("ARGS");
		}
		ParseTreeNode a = new ParseTreeNode(currentToken, 1, "args");
		if(checkCurrentToken(Token.T_RPAREN)){
			a.setChild(0, empty());
			getCurrentToken();
		}
		else{
			a.setChild(0, argList());
		}
		return a;
	}

	private ParseTreeNode argList() throws BPLException{
		if(debug){
			System.out.println("ARGLIST");
		}
		ParseTreeNode al = new ParseTreeNode(currentToken, 2, "arg list");
		al.setChild(0, expression());
		if(checkCurrentToken(Token.T_COMMA)){
			getCurrentToken();
			al.setChild(1, argList());
		}
		return al;
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

	private ParseTreeNode stringLiteral() throws BPLException{
		if(debug){
			System.out.println("STRING LITERAL");
		}
		if(!checkCurrentToken(Token.T_STRINGLITERAL)){
			throw new BPLException(currentToken.lineNumber, "Missing '<string>'");
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
		}
		catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Please enter a filename! "+e.getMessage());
		}
		catch(BPLException b){
			System.out.println(b.getMessage());
		}
		
	}
}