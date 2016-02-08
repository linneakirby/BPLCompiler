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
	private boolean done;

	public BPLScanner(String filename){
		try{
			filescan = new Scanner(new File(filename));
			lineNumber = 0;
			currentLine = "";
			resetMarkers();
			getNextToken();
			System.out.println(nextToken().type+" "+nextToken().tokenString+" "+nextToken().lineNumber);
		}
		catch(Exception e){
			System.out.println("Your file is invalid! ");
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
			isId = true;
		}
		else if(isSymbol(currchar)){
			isSpecialSymbol = true;
		}
	}

	private void finishSpecialSymbolToken(){

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

	private void resetMarkers(){
		done = true;
		isNum = false;
		isId = false;
		isSpecialSymbol = false;
	}

	//checks to see if a new line is needed
	private void checkLine(){
		if(peekIndex >= currentLine.length()){
			getCurrentLine();
			peekIndex = 0;
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
		if(tokenSoFar.length() == 0){ //if the token is empty
			currchar = peek;
			getTokenType();
		}
		else if(Character.isDigit(peek) && (isNum || isId)){
			currchar = peek;
		}
		else if(Character.isLetter(peek) && (isId)){
			currchar = peek;
		}
		else if(isSymbol(peek)){
			//TODO
		}
		else{
			finishToken();
		}
	}

	//sets nextToken
	public void getNextToken(){
		done = false;
		tokenSoFar = new StringBuilder();
		checkLine();
		while((peekIndex < currentLine.length()) && !done){
			peek();
			checkValid();
			tokenSoFar.append(currchar);
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

	public static void main(String[ ] args) {
		String filename ;
		BPLScanner myScanner;
		try{
			filename = args[0] ;
			myScanner = new BPLScanner(filename);
			/*while (myScanner.nextToken().type != Token.T_EOF) { 
				try {
					myScanner.getNextToken();
					//System.out.println(myScanner.nextToken());
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