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
	private boolean comment;
	private boolean done;

	public BPLScanner(String filename){
		try{
			filescan = new Scanner(new File(filename));
			lineNumber = 0;
			currentLine = "";
			resetMarkers();
			nextToken = new Token(Token.T_ID, "test", 0);
			getCurrentLine();
			peek();
			//getNextToken();
			//System.out.println(nextToken().type+" "+nextToken().tokenString+" "+nextToken().lineNumber);
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
		}
		else{
			nextToken = new Token(Token.T_EOF, "", lineNumber);
			currentLine = "";
		}
	}

	//sets peek
	private void peek(){
		peek = currentLine.charAt(peekIndex);
		peekIndex++;
		//System.out.println(peek);
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
			//System.out.println(currchar);
			isId = true;
		}
		else if(isSymbol(currchar)){
			isSpecialSymbol = true;
		}
	}

	private void resetMarkers(){
		done = true;
		isNum = false;
		isId = false;
		isSpecialSymbol = false;
		comment = false;
	}

	//checks to see if a new line is needed
	private void checkLine(){
		if(peekIndex >= currentLine.length()){
			//finishToken();
			getCurrentLine();
			peekIndex = 0;
		}
	}

	private void checkSymbol(){
		if(currchar == '<' || currchar == '=' || currchar == '!' || currchar == '>'){
			if(peek == '='){
				currchar = peek;
			}
		}
		else if(currchar == '*'){
			if(peek == '/'){
				currchar = peek;
			}
		}
		else{
			finishToken();
		}
	}

	private void finishToken(){
		if(isNum){
			nextToken = new Token(Token.T_NUM, tokenSoFar.toString(), lineNumber);
		}
		else if(isId){
			checkKeyword();
		}
		else if(isSpecialSymbol){
			finishSpecialSymbolToken();
		}
		else{
			nextToken = new Token(Token.T_EOF, "", lineNumber);
		}
		resetMarkers();

	}

	//checks to see if peek is a valid addition to the end of tokenSoFar
	private void checkValid(){
		/*if(tokenSoFar.length() == 0){ //if the token is empty
			currchar = peek;
			getTokenType();
		}*/
		if(Character.isDigit(peek) && (isNum || isId)){
			currchar = peek;
		}
		else if(Character.isLetter(peek) && (isId)){
			currchar = peek;
		}
		else if(isSymbol(peek)){
			checkSymbol();
		}
		else if(Character.isWhitespace(peek)){
			while(Character.isWhitespace(peek)){
				peek();
			}
			finishToken();
		}
		else{
			finishToken();
		}
	}

	//sets nextToken
	public void getNextToken(){
		checkLine();
		done = false;
		tokenSoFar = new StringBuilder();
		while((peekIndex < currentLine.length()) && !done){
			currchar = peek;
			tokenSoFar.append(currchar);
			getTokenType();
			peek();
			checkValid();
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

	/*public void getNextToken() throws Exception {
		int i = 0; // i points at the next character to handle
		int j=0;
		while (i < currentLine.length() && IsWhitespace(currentLine.charAt(i))){
			i += 1;
			if (i == currentLine.length()) {
				if (input.hasNextLine()) {
					currentLine = input.nextLine();
					lineNumber += 1;
					getNextToken();
				}
			else {
				nextToken = new Token(Token.T_EOF, "", lineNumber);
				currentLine = "";
			}
		}
		else { // i < currentLine.length()
			char ch = currentLine.charAt(i);
			if (isDigit(ch)) {
				j = i+1;
				while (j < currentLine.length() && isDigit(currentLine.charAt(j)) )
					j += 1;
				String tokenString = currentLine.substring(i, j);
				nextToken = new Token(Token.T_NUM, tokenString, lineNumber);
				currentLine = currentLine.substring(j);
			}
			else if (isLetter(ch)) {
				j = i+1;
				while (j < currentLine.length() && isAlphaNum(currentLine.charAt(j)))
					j += 1;
				String tokenString = currentLine.substring(i, j);
				nextToken = new Token(Token.T_ID, tokenString, lineNumber);
				currentLine = currentLine.substring(j);
			}
			else if (ch == '+') {
				nextToken = new Token(Token.T_PLUS, "+", lineNumber);
				currentLine = currentLine.substring(i+1);
			}
		}
	}
}*/

	public void printToken(){
		System.out.printf("Token: %3d \t String: %-5s \t Line Number: %3d\n",nextToken().type,nextToken().tokenString,nextToken().lineNumber);
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