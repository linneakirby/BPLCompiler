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
		BPLParser parser = new BPLParser(filename);
		symbolTable = new HashMap<String, ParseTreeNode>();
		topDownPass(parser.getParseTree());
		bottomUpPass();
	}

	private void topDownPass(ParseTreeNode root){
		if(!checkVarDec(root)){
			checkFunDec(root);
		}
		for(ParseTreeNode child:root.getChildren()){
			if(child != null){
				//System.out.println(child.kind);
				topDownPass(child);
			}
		}
	}

	private void bottomUpPass(){

	}

	private boolean checkFunDec(ParseTreeNode root){
		if(root.kind == "fun dec"){
			putGlobalDec(root);
			localDecs = new LinkedList<ParseTreeNode>();
			ParseTreeNode paramlist = root.getChild(2).getChild(0);
			while(paramlist != null && paramlist.kind == "param list"){
				ParseTreeNode p = paramlist.getChild(0);
				localDecs.add(p);
				paramlist = p.getChild(1);
				if(debug){
					System.out.println("Adding "+p.kind.toUpperCase()+" \""+p.getChild(1).kind+"\" to the local declarations");
				}
			}
			return true;
		}

		return false;
	}

	private boolean checkVarDec(ParseTreeNode root){
		if(root.kind == "var dec"){
			putGlobalDec(root);
			return true;
		}
		return false;
	}

	private void putGlobalDec(ParseTreeNode root){
		int i = 1;
		ParseTreeNode child = root.getChild(i);
			while(child.kind == "*" || child.kind == "type specifier"){
				i++;
				child = root.getChild(i);
			}
			symbolTable.put(child.kind, root);
			if(debug){
				System.out.println("Adding "+root.kind.toUpperCase()+" \""+child.kind+"\" to the global symbol table");
			}
	}


	public static void main(String[ ] args) {
		String filename ;
		BPLTypeChecker myTypeChecker;
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

