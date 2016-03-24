package Compiler;

import java.util.Scanner;
import java.util.LinkedList;
import java.util.HashMap;
import java.io.File;
import java.lang.Exception;
import java.lang.StringBuilder;
import java.lang.String;

public class BPLTypeChecker{

	private boolean debug = true;

	private HashMap<String, ParseTreeNode> symbolTable;
	private LinkedList<ParseTreeNode> localDecs;

	public BPLTypeChecker(String filename) throws BPLException{
		parser = new BPLParser(filename);
		symbolTable = new HashMap<String, ParseTreeNode>();
		topDownPass(parser.getParseTree());
		bottomUpPass();
	}

	private void topDownPass(ParseTreeNode root){
		if(!checkVarDec(root)){
			checkFunDec(root);
		}

		for(ParseTreeNode child:root.children){
			if(child != null){
				topDownPass(child);
			}
		}
	}

	private void bottomUpPass(){

	}

	private boolean checkVarDec(ParseTreeNode root){
		if(root.kind == "var dec"){
			ParseTreeNode child = root.getChild(1);
			if(child.kind == "*"){
				child = root.getChild(2);
			}
			symbolTable.put(child.kind, root);
			return true;
		}
		return false;
	}


	public static void main(String[ ] args) {
		String filename ;
		BPLParser myTypeChecker;
		try{
			filename = args[0] ;
			myTypeChecker = new BPLTypeChecker(filename);
		}
		catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Please enter a filename! "+e.getMessage());
		}
		catch(BPLException b){
			System.out.println(b.getMessage());
		}
		
	}
}

