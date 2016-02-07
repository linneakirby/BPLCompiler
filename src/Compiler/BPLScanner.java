package Compiler;

import java.util.Scanner;
import java.io.File;
import java.lang.Exception;
import java.lang.StringBuilder;

public class BPLScanner{
	public Scanner filescan;
	private Token nextToken;
	private String currentLine;
	private int peekIndex;
	private char currchar;
	private char peek;
	private int lineNumber;
	private StringBuilder tokenSoFar;

	public BPLScanner(String filename){
		try{
			filescan = new Scanner(new File(filename));
			lineNumber = 0;
			currentLine = "";
			getNextToken();
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

	//sets peek and checks it to see if it can fit in the current token
	private void peek(){
		peek = currentLine.charAt(peekIndex);
		peekIndex++;
		System.out.println(peek);
	}

	//checks to see if peek is a valid addition to the end of tokenSoFar
	private void checkValid(){

	}

	//sets nextToken
	public void getNextToken(){
		tokenSoFar = new StringBuilder();
		getCurrentLine();
		while(peekIndex < currentLine.length){
			peek();
			checkValid();
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