package Compiler;

import java.util.Scanner;
import java.io.File;
import java.lang.Exception;
import java.lang.StringBuilder;
import java.lang.String;

public class ParseTreeNode{
	private int lineNumber;
	public String kind; //for grammar
	private ParseTreeNode[] children;
	private int numchildren;
	private ParseTreeNode declaration;
	private String expression; //actual code
	private String type; //for type checking

	public ParseTreeNode(Token tok, int numchildren, String kind){
		this.lineNumber = tok.lineNumber;
		this.numchildren = numchildren;
		this.kind = kind;
		children = new ParseTreeNode[this.numchildren];
	}

	public ParseTreeNode getChild(int i){
		return children[i];
	}

	public ParseTreeNode[] getChildren(){
		return children;
	}

	public void setChild(int i, ParseTreeNode child){
		children[i] = child;
	}

	public void setType(String t){
		type = t;
	}

	public String getType(){
		return type;
	}

	public void setExpression(String s){
		expression = s;
	}

	public String getExpression(){
		return expression;
	}

	public ParseTreeNode getDeclaration(){
		return declaration;
	}

	public void setDeclaration(ParseTreeNode dec){
		this.declaration = dec;
	}

	public int getLineNumber(){
		return lineNumber;
	}

	public StringBuilder toStringHelper(StringBuilder nodeString, int depth){
		nodeString.append("LINE " +lineNumber+": ");
		for(int i=0; i<depth; i++){
			nodeString.append("   ");
		}
		if(children.length == 0){
			nodeString.append(kind+"   {}\n");
		}
		else{
			nodeString.append(kind+"   {\n");
			for (ParseTreeNode child:children) {
				if(child != null){
					child.toStringHelper(nodeString, depth+1);
				}
			}
			for(int i=0; i<depth; i++){
				nodeString.append("   ");
			}
			nodeString.append("        }\n");
		}
		return nodeString;
	}

	public String toString(){
		StringBuilder nodeString = new StringBuilder();
		return toStringHelper(nodeString, 0).toString();
	}
}