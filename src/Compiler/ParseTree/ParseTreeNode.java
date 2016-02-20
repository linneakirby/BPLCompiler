package Compiler.ParseTree;

import java.util.Scanner;
import java.io.File;
import java.lang.Exception;
import java.lang.StringBuilder;
import java.lang.String;

public abstract class ParseTreeNode{
	private int lineNumber;
	private String kind;
	private ParseTreeNode[] children;
	private int numchildren;
	private ParseTreeNode next;

	public ParseTreeNode(Token tok, int numchildren, String kind){
		this.lineNumber = tok.lineNumber;
		this.numchildren = numchildren;
		this.kind = kind;
		children = new ParseTreeNode[this.numchildren];
	}

	public ParseTreeNode getChild(int i){
		return children[i];
	}

	public void setChild(int i, ParseTreeNode child){
		children[i] = child;
		child.setParent(this);
	}

	public ParseTreeNode getNext(){
		return next;
	}

	public void setNext(ParseTreeNode next){
		this.next = next;
	}

	public String toString(){
		return kind;
	}
}