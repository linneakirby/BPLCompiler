package Compiler;

import java.util.Scanner;
import java.io.File;
import java.lang.Exception;

public class Token{
	public static final int T_ID = 0;
	public static final int T_NUM = 1;
	public static final int T_INT = 2;
	public static final int T_VOID = 3;
	public static final int T_STRING = 4;
	public static final int T_IF = 5;
	public static final int T_ELSE = 6;
	public static final int T_WHILE = 7;
	public static final int T_RETURN = 8;
	public static final int T_WRITE = 9;
	public static final int T_WRITELN = 10;
	public static final int T_READ = 11;
	public static final int T_SEMICOLON = 12;
	public static final int T_COMMA = 13;
	public static final int T_LBRACKET = 14;
	public static final int T_RBRACKET = 15;
	public static final int T_LCURLY = 16;
	public static final int T_RCURLY = 17;
	public static final int T_LPAREN = 18;
	public static final int T_RPAREN = 19;
	public static final int T_LESS = 20;
	public static final int T_GREATER = 21;
	public static final int T_LESSEQ = 22;
	public static final int T_GREATEREQ = 23;
	public static final int T_EQEQ = 24;
	public static final int T_NOTEQ = 25;
	public static final int T_PLUS = 26;
	public static final int T_MINUS = 27;
	public static final int T_STAR = 28;
	public static final int T_SLASH = 29;
	public static final int T_PERCENT = 30;
	public static final int T_AND = 31;
	public static final int T_EQ = 32;
	public static final int T_STRINGLITERAL = 33;
	public static final int T_EOF = 34;

	public String tokenString;
	public int lineNumber;
	public int type;

	public Token(int type, String tokenString, int lineNumber){
		this.type = type;
		this.tokenString = tokenString;
		this.lineNumber = lineNumber;
	}

}