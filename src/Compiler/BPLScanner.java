package Compiler;

import java.util.Scanner;
import java.io.File;
import java.lang.Exception;

public class BPLScanner{
	public Scanner scan;
	public Token nextToken;
	public String currentLine;

	public BPLScanner(String filename){
		try{
			scan = new Scanner(new File(filename));
		}
		catch(Exception e){
			System.out.println("Your file is invalid! "+e.getMessage());
		}
		//nextToken = getNextToken();
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
				nextToken = new NumberToken(Token.T_NUM, tokenString, lineNumber);
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
		System.out.println("Test");
		String filename ;
		BPLScanner myScanner;
		try{
			filename = args[0] ;
			myScanner = new BPLScanner(filename);
		}
		catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Please enter a filename! "+e.getMessage());
		}
		/*while (myScanner.nextToken.type != Token.T_EOF) {
			try {
				myScanner.getNextToken();
				System.out.println(myScanner.nextToken);
			}
			catch (Exception e) {
			System.out.println(e);
			}
		}*/
	}
}