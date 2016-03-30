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
		ParseTreeNode root = parser.getParseTree();
		findReferences(root);
		typeCheck(root);
	}

	//top-down pass that finds all references to variables and functions
	private void findReferences(ParseTreeNode root) throws BPLException{
		ParseTreeNode declarationlist = root.getChild(0);
		ParseTreeNode d;
		ParseTreeNode child;
		while(declarationlist.getChild(1) != null){
			d = declarationlist.getChild(0);
			child = d.getChild(0);
			if(!checkVarDec(child)){
				checkFunDec(child);
			}
			declarationlist = declarationlist.getChild(1);
		}
		d = declarationlist.getChild(0);
		child = d.getChild(0);
		if(!checkVarDec(child)){
			checkFunDec(child);
		}
	}

	//type-checks all references
	private String typeCheck(ParseTreeNode node) throws BPLException{
		for(ParseTreeNode child:node.getChildren()){
			if(child != null){
				typeCheck(child);
			}
		}
		String type;
		if(node.kind.equals("if statement")){
			//make sure condition is 'int'
			type = typeCheck(node.getChild(0));
			if(!type.equals("int")){
				throw new BPLException("ERROR: for if statement on line "+node.getLineNumber()+" expected type 'int' but was assigned type '"+type+"'");
			}
			return "int";
		}
		else if(node.kind.equals("while statement")){
			//check for 'int'
			type = typeCheck(node.getChild(0));
			if(!type.equals("int")){
				throw new BPLException("ERROR: for while statement on line "+node.getLineNumber()+" expected type 'int' but was assigned type '"+type+"'");
			}
			return "int";
		}
		else if(node.kind.equals("return statement")){
			//could be 'int' or 'string'; make sure not 'void'
			if(node.getChild(0) != null){
				return typeCheck(node.getChild(0));
			}
		}
		/*else if(node.kind.equals("write statement") || node.kind.equals("writeln statement")){
			//check for 'string'
		}*/
		else if(node.kind.equals("expression")){

			if(node.getChild(0).kind.equals("comp exp")){
				return typeCheck(node.getChild(0));
			}
		}
		else if(node.kind.equals("comp exp")){

		}
		else if(node.kind.equals("E")){

		}
		else if(node.kind.equals("T")){

		}
		else if(node.kind.equals("F")){

		}
		else if(node.kind.equals("factor")){

		}
		else if(node.kind.equals("read")){
			return "string";
		}
		else if(node.kind.equals("fun call")){

		}
		return "none";
	}

	//checks to see if a node is a function declaration
	//if so, adds it to the list of global variables
	//and adds its children to the list of local variables
	//then, recursively checks to see if any of its children are local decs or references
	private boolean checkFunDec(ParseTreeNode root) throws BPLException{
		if(root.kind == "fun dec"){
			putGlobalDec(root);
			localDecs = new LinkedList<ParseTreeNode>();
			ParseTreeNode paramlist = root.getChild(2).getChild(0);
			while(paramlist != null && paramlist.kind == "param list"){
				ParseTreeNode p = paramlist.getChild(0);
				localDecs.add(p);
				paramlist = p.getChild(1);
				if(debug){
					System.out.println("Adding "+p.kind.toUpperCase()+" \""+p.getChild(1).getChild(0).kind+"\" to the local declarations");
				}
			}
			for(ParseTreeNode child:root.getChildren()){
				if(child != null){
					checkLocalDecOrReference(child);
				}
			}
			return true;
		}
		return false;
	}

	//checks to see if a node is a variable declaration
	//if so, adds it to the list of global variables
	private boolean checkVarDec(ParseTreeNode root){
		if(root.kind == "var dec"){
			putGlobalDec(root);
			return true;
		}
		return false;
	}

	//checks to make sure an ID has a declaration
	private void checkReference(ParseTreeNode child) throws BPLException{
		boolean referenceFound = false;
		ParseTreeNode dec = symbolTable.get(child.kind);
		if(dec != null){
			if(child.getLineNumber() != dec.getLineNumber()){
				child.setDeclaration(dec);
				if(debug){
					System.out.println("Connecting \""+child.kind+"\" on line "+child.getLineNumber()+" to global declaration on line "+dec.getLineNumber());
				}
			}
			referenceFound = true;
		}
		for(int i=0; i<localDecs.size(); i++){
			dec = localDecs.get(i);
			int index = 1;
			ParseTreeNode decChild = dec.getChild(index);
			while(decChild.kind == "*" || decChild.kind == "type specifier"){
				index++;
				decChild = dec.getChild(index);
			}
			decChild = decChild.getChild(0);
			if(decChild.kind.equals(child.kind)){
				if(child.getLineNumber() != dec.getLineNumber()){
					child.setDeclaration(dec);
					if(debug){
						System.out.println("Connecting \""+child.kind+"\" on line "+child.getLineNumber()+" to local declaration on line "+dec.getLineNumber());
					}
				}
				referenceFound = true;
			}
		}
		if(!referenceFound){
			throw new BPLException("ERROR: \""+child.kind+"\" on line "+child.getLineNumber()+" is undeclared!");
		}
	}

	//adds a global declaration to the symbol table
	private void putGlobalDec(ParseTreeNode root){
		int i = 1;
		ParseTreeNode child = root.getChild(i);
		while(child.kind == "*" || child.kind == "type specifier"){
			i++;
			child = root.getChild(i);
		}
		symbolTable.put(child.getChild(0).kind, root);
			if(debug){
				System.out.println("Adding "+root.kind.toUpperCase()+" \""+child.getChild(0).kind+"\" to the global symbol table");
			}
	}

	//checks recursively to see if a node or any of its children is a local declaration or a reference
	//if its a local dec, adds it to the list of local decs
	private boolean checkLocalDecOrReference(ParseTreeNode child) throws BPLException{
		if(child.kind == "var dec"){
			putLocalDec(child);
			return true;
		}
		if(child.kind == "id"){
			checkReference(child.getChild(0));
		}
		for(ParseTreeNode grandchild:child.getChildren()){
			if(grandchild != null){
				checkLocalDecOrReference(grandchild);
			}
		}
		return false;
	}

	//adds a node to the list of local declarations
	private void putLocalDec(ParseTreeNode root){
		int i = 1;
		ParseTreeNode child = root.getChild(i);
			while(child.kind == "*" || child.kind == "type specifier"){
				i++;
				child = root.getChild(i);
			}
			localDecs.add(root);
			if(debug){
				System.out.println("Adding "+root.kind.toUpperCase()+" \""+child.getChild(0).kind+"\" to the local declarations");
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
			e.printStackTrace();
			System.out.println("Please enter a filename! "+e.getMessage());
		}
		catch(BPLException b){
			System.out.println(b.getMessage());
		}
		
	}
}

