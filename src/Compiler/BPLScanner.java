package Compiler;

import java.util.Scanner;
import java.io.File;
import java.lang.Exception;
import java.lang.StringBuilder;
import java.lang.String;

public class BPLScanner{
	public Scanner filescan;
	private Token nextToken;
	private String currentLine;
	private int peekIndex;
	private char currchar;
	private char peek;
	private int lineNumber;
	private StringBuilder tokenSoFar;
	private boolean isId;
	private boolean isNum;
	private boolean isSpecialSymbol;
	private boolean isString;
	private boolean isComment;
	private boolean done;
	private boolean valid;
	private boolean end;

	public BPLScanner(String filename){
		try{
			filescan = new Scanner(new File(filename));
			lineNumber = 0;
			valid = false;
			end = false;
			currentLine = "";
			resetMarkers();
			nextToken = new Token(Token.T_ID, "test", 100);
			getCurrentLine();
			peek();
		}
		catch(Exception e){
			System.out.println("Your file is invalid! "+e);
		}
	}

	public Token nextToken(){
		return nextToken;
	}

	//sets currentLine and sets its peekIndex to 0
	private void getCurrentLine(){
		if(filescan.hasNextLine()){
			currentLine = filescan.nextLine();
			peekIndex = 0;
			lineNumber++;
			//System.out.println("lineNumber is now: "+lineNumber);
		}
		else{
			end = true;
		}
	}

	//sets peek
	private void peek(){
		peek = currentLine.charAt(peekIndex);
		peekIndex++;
	}

	//checks to see if a character is a symbol
	private boolean isSymbol(char c){
		if(c == '(' || c == ')' || c == ';' || c == ',' || c == '[' || c == ']'
			|| c == '{' || c == '}' || c == '<' || c == '=' || c == '>' || c == '!'
			|| c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '&'){
			return true;
		}
		return false;
	}

	private void getTokenType(){
		if(Character.isDigit(currchar)){
			isNum = true;
		}
		else if(Character.isLetter(currchar)){
			isId = true;
		}
		else if(isSymbol(currchar)){
			isSpecialSymbol = true;
		}
		else if(currchar == '"'){
			isString = true;
		}
	}

	private void resetMarkers(){
		done = true;
		isNum = false;
		isId = false;
		isSpecialSymbol = false;
		isString = false;
		isComment = false;
	}

	//checks to see if a new line is needed
	private void checkLine(){
		if(peekIndex >= currentLine.length()){
			getCurrentLine();
		}
	}

	private void checkSymbol(){
		if(currchar == '<' || currchar == '=' || currchar == '!' || currchar == '>'){
			if(peek == '='){
				valid = true;
			}
		}
		else if(peek == '*'){
			if(currchar == '/'){
				valid = true;
				isComment = true;
			}
		}
		else{
			finishToken();
		}
	}

	private void getString(){
		peek();
		while(peek != '"'){
			currchar = peek;
			tokenSoFar.append(currchar);
			peek();
		}
		currchar = peek;
		tokenSoFar.append(currchar);
		finishToken();
		checkLine();
		if(!end){
			peek();
			while(Character.isWhitespace(peek)){
				peek();
			}
		}
	}

	private void finishString(){
		String tokenString = tokenSoFar.toString();
		nextToken = new Token(Token.T_STRINGLITERAL, tokenString, lineNumber);
	}

	private void getComment(){
		isSpecialSymbol = false;
		while(isComment){
			checkLine();
			peek();
			checkValid();
		}
	}

	private void startToken(){
		while(Character.isWhitespace(peek)){
			peek();
		}
		done = false;
		tokenSoFar = new StringBuilder();
		currchar = peek;
		tokenSoFar.append(currchar);
		getTokenType();
		//checkLine();
		//System.out.println("lineNumber is: "+lineNumber);
		/*done = false;
		tokenSoFar = new StringBuilder();*/
		/*while(Character.isWhitespace(peek)){
			peek();
		}*/
		/*currchar = peek;
		tokenSoFar.append(currchar);
		getTokenType();*/
	}

	private void finishToken(){
		if(isNum){
			nextToken = new Token(Token.T_NUM, tokenSoFar.toString(), lineNumber);
			resetMarkers();
		}
		else if(isId){
			checkKeyword();
			resetMarkers();
		}
		else if(isSpecialSymbol){
			finishSpecialSymbolToken();
			resetMarkers();
		}
		else if(isString){
			finishString();
			resetMarkers();
		}
		else if(isComment){
			isComment = false;
			checkLine();
			if(!end){
				peek();
				while(Character.isWhitespace(peek)){
					peek();
				}
			}
			resetMarkers();
			startToken();
		}
		else if(Character.isWhitespace(currchar) || isComment){
			resetMarkers();
		}
		else{
			nextToken = new Token(Token.T_EOF, "", lineNumber);
			resetMarkers();
		}

	}

	//checks to see if peek is a valid addition to the end of tokenSoFar
	private void checkValid(){
		if(Character.isDigit(peek) && (isNum || isId)){
			valid = true;
		}
		else if(Character.isLetter(peek) && (isId)){
			valid = true;
		}
		else if(isComment){
			if(peek == '*'){
				peek();
				if(peek == '/'){
					finishToken();
				}
			}
		}
		else if(isSymbol(peek) && (isSpecialSymbol)){
			checkSymbol();
		}
		else if(Character.isWhitespace(peek)){
			finishToken();
			while(Character.isWhitespace(peek)){
				peek();
			}
		}
		else{
			finishToken();
		}
	}

	//sets nextToken
	public void getNextToken() throws Exception{
		if(end){
			nextToken = new Token(Token.T_EOF, "", lineNumber);
			currentLine = "";
		}
		else{
			startToken();
			while((peekIndex <= currentLine.length()) && !done){
				if(peekIndex == currentLine.length()){
					finishToken();
					checkLine();
					if(!end){
						while(currentLine.length() == 0){
							checkLine(); //make sure the line isn't empty
						}
						peek();
					}
				}
				else if(isComment){	
					getComment();

				}
				else if(isString){
					getString();
				}
				else{
					peek();
					checkValid();
					if(valid){
						currchar = peek;
						tokenSoFar.append(currchar);
						valid = false;
					}
				}
			}
		}
	}

	private void finishSpecialSymbolToken(){
		String tokenString = tokenSoFar.toString();
		if(tokenString.equals(";")){
			nextToken = new Token(Token.T_SEMICOLON, tokenString, lineNumber);
		}
		else if(tokenString.equals(",")){
			nextToken = new Token(Token.T_COMMA, tokenString, lineNumber);
		}
		else if(tokenString.equals("[")){
			nextToken = new Token(Token.T_LBRACKET, tokenString, lineNumber);
		}
		else if(tokenString.equals("]")){
			nextToken = new Token(Token.T_RBRACKET, tokenString, lineNumber);
		}
		else if(tokenString.equals("{")){
			nextToken = new Token(Token.T_LCURLY, tokenString, lineNumber);
		}
		else if(tokenString.equals("}")){
			nextToken = new Token(Token.T_RCURLY, tokenString, lineNumber);
		}
		else if(tokenString.equals("(")){
			nextToken = new Token(Token.T_LPAREN, tokenString, lineNumber);
		}
		else if(tokenString.equals(")")){
			nextToken = new Token(Token.T_RPAREN, tokenString, lineNumber);
		}
		else if(tokenString.equals("<")){
			nextToken = new Token(Token.T_LESS, tokenString, lineNumber);
		}
		else if(tokenString.equals(">")){
			nextToken = new Token(Token.T_GREATER, tokenString, lineNumber);
		}
		else if(tokenString.equals("<=")){
			nextToken = new Token(Token.T_LESSEQ, tokenString, lineNumber);
		}
		else if(tokenString.equals(">=")){
			nextToken = new Token(Token.T_GREATEREQ, tokenString, lineNumber);
		}
		else if(tokenString.equals("==")){
			nextToken = new Token(Token.T_EQEQ, tokenString, lineNumber);
		}
		else if(tokenString.equals("!=")){
			nextToken = new Token(Token.T_NOTEQ, tokenString, lineNumber);
		}
		else if(tokenString.equals("+")){
			nextToken = new Token(Token.T_PLUS, tokenString, lineNumber);
		}
		else if(tokenString.equals("-")){
			nextToken = new Token(Token.T_MINUS, tokenString, lineNumber);
		}
		else if(tokenString.equals("*")){
			nextToken = new Token(Token.T_STAR, tokenString, lineNumber);
		}
		else if(tokenString.equals("/")){
			nextToken = new Token(Token.T_SLASH, tokenString, lineNumber);
		}
		else if(tokenString.equals("%")){
			nextToken = new Token(Token.T_PERCENT, tokenString, lineNumber);
		}
		else if(tokenString.equals("&")){
			nextToken = new Token(Token.T_AND, tokenString, lineNumber);
		}
		else if(tokenString.equals("=")){
			nextToken = new Token(Token.T_EQ, tokenString, lineNumber);
		}
	}

	//checks to see if an Id token is actually a keyword
	private void checkKeyword(){
		String tokenString = tokenSoFar.toString();
		if(tokenString.equals("int")){
			nextToken = new Token(Token.T_INT, tokenString, lineNumber);
		}
		else if(tokenString.equals("void")){
			nextToken = new Token(Token.T_VOID, tokenString, lineNumber);
		}
		else if(tokenString.equals("string")){
			nextToken = new Token(Token.T_STRING, tokenString, lineNumber);
		}
		else if(tokenString.equals("if")){
			nextToken = new Token(Token.T_IF, tokenString, lineNumber);
		}
		else if(tokenString.equals("else")){
			nextToken = new Token(Token.T_ELSE, tokenString, lineNumber);
		}
		else if(tokenString.equals("while")){
			nextToken = new Token(Token.T_WHILE, tokenString, lineNumber);
		}
		else if(tokenString.equals("return")){
			nextToken = new Token(Token.T_RETURN, tokenString, lineNumber);
		}
		else if(tokenString.equals("write")){
			nextToken = new Token(Token.T_WRITE, tokenString, lineNumber);
		}
		else if(tokenString.equals("writeln")){
			nextToken = new Token(Token.T_WRITELN, tokenString, lineNumber);
		}
		else if(tokenString.equals("read")){
			nextToken = new Token(Token.T_READ, tokenString, lineNumber);
		}
		else{
			nextToken = new Token(Token.T_ID, tokenString, lineNumber);
		}
	}

	public void printToken(){
		System.out.printf("Token: %3d \t String: %-8s \t Line Number: %3d\n",nextToken().type,nextToken().tokenString,nextToken().lineNumber);
	}

	public static void main(String[ ] args) {
		String filename ;
		BPLScanner myScanner;
		try{
			filename = args[0] ;
			myScanner = new BPLScanner(filename);
			while (myScanner.nextToken().type != Token.T_EOF) { 
				try {
					myScanner.getNextToken();
					myScanner.printToken();
				}
				catch (Exception e) {
				System.out.println(e);
				}
			}
		}
		catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Please enter a filename! "+e.getMessage());
		}
		
	}
}