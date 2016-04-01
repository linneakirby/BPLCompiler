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
				throw new BPLException("ERROR: for IF STATEMENT on line "+node.getLineNumber()+" expected type 'int' but was assigned type \""+type+"\"");
			}
			if(debug){
				System.out.println("IF STATEMENT on line "+node.getLineNumber()+" assigned type \"int\"");
			}
			return "int";
		}
		else if(node.kind.equals("while statement")){
			//check for 'int'
			type = typeCheck(node.getChild(0));
			if(!type.equals("int")){
				throw new BPLException("ERROR: for WHILE STATEMENT on line "+node.getLineNumber()+" expected type 'int' but was assigned type \""+type+"\"");
			}
			if(debug){
				System.out.println("WHILE STATEMENT on line "+node.getLineNumber()+" assigned type \"int\"");
			}
			return "int";
		}
		else if(node.kind.equals("return statement")){
			//could be 'int' or 'string'; make sure not 'void'
			if(node.getChild(0) != null){
				type = typeCheck(node.getChild(0));
				if(debug){
					System.out.println("RETURN STATEMENT on line "+node.getChild(0).getLineNumber()+" assigned type \""+type+"\"");
				}
				return type;
			}
		}
		/*else if(node.kind.equals("write statement") || node.kind.equals("writeln statement")){
			//check for 'string'
		}*/
		else if(node.kind.equals("expression")){

			if(node.getChild(0).kind.equals("comp exp")){
				type = typeCheck(node.getChild(0));
				if(debug){
					System.out.println("EXPRESSION on line "+node.getChild(0).getLineNumber()+" assigned type \""+type+"\"");
				}
				return type;
			}
			else{
				//TODO: check var == expression
			}
		}
		else if(node.kind.equals("comp exp")){
			String eType = typeCheck(node.getChild(0));
			if(!eType.equals("int")){
					throw new BPLException("ERROR: for E on line "+node.getChild(0).getLineNumber()+" expected type \"int\" but was assigned type \""+eType+"\"");
			}
			if(node.getChild(2) != null){
				String e2Type = typeCheck(node.getChild(2));	
				if(!e2Type.equals("int")){
					throw new BPLException("ERROR: for E on line "+node.getChild(2).getLineNumber()+" expected type \"int\" but was assigned type \""+e2Type+"\"");
				}
				if(!eType.equals(e2Type)){
					throw new BPLException("ERROR: for COMP EXP on line "+node.getChild(0).getLineNumber()+" expected type \"int\" but was assigned type \""+eType+"\" and \""+e2Type+"\"");
				}
			}
			if(debug){
				System.out.println("COMP EXP on line "+node.getChild(0).getLineNumber()+" assigned type \"int\"");
			}
			return eType;
		}
		else if(node.kind.equals("E")){
			String tType = typeCheck(node.getChild(2));
			if(!tType.equals("int")){
					throw new BPLException("ERROR: for T on line "+node.getChild(2).getLineNumber()+" expected type \"int\" but was assigned type \""+tType+"\"");
			}
			if(node.getChild(0) != null){
				String eType = typeCheck(node.getChild(0));	
				if(!eType.equals("int")){
					throw new BPLException("ERROR: for E on line "+node.getChild(0).getLineNumber()+" expected type \"int\" but was assigned type \""+eType+"\"");
				}
				if(!tType.equals(eType)){
					throw new BPLException("ERROR: for E on line "+node.getChild(2).getLineNumber()+" expected type \"int\" but was assigned type \""+eType+"\" and \""+tType+"\"");
				}
			}
			if(debug){
				System.out.println("E on line "+node.getChild(2).getLineNumber()+" assigned type \"int\"");
			}
			return tType;
		}
		else if(node.kind.equals("T")){
			String fType = typeCheck(node.getChild(2));
			if(!fType.equals("int")){
					throw new BPLException("ERROR: for F on line "+node.getChild(2).getLineNumber()+" expected type \"int\" but was assigned type \""+fType+"\"");
			}
			if(node.getChild(0) != null){
				String tType = typeCheck(node.getChild(0));	
				if(!tType.equals("int")){
					throw new BPLException("ERROR: for T on line "+node.getChild(0).getLineNumber()+" expected type \"int\" but was assigned type \""+tType+"\"");
				}
				if(!fType.equals(tType)){
					throw new BPLException("ERROR: for T on line "+node.getChild(2).getLineNumber()+" expected type \"int\" but was assigned type \""+tType+"\" and \""+fType+"\"");
				}
			}
			if(debug){
				System.out.println("T on line "+node.getChild(2).getLineNumber()+" assigned type \"int\"");
			}
			return fType;
		}
		else if(node.kind.equals("F")){
			String ftype = typeCheck(node.getChild(0));
			if(node.getChild(1) != null){
				ftype = typeCheck(node.getChild(1));
				if(!ftype.equals("int") && !ftype.equals("string")){
					throw new BPLException("ERROR: for F on line "+node.getChild(1).getLineNumber()+" expected type \"int\" or \"string\" but was assigned type \""+ftype+"\"");
				}
				if(debug){
					System.out.println("F on line "+node.getChild(1).getLineNumber()+" assigned type \""+ftype+"\"");
				}
				return ftype;
			}
			if(debug){
				System.out.println("F on line "+node.getChild(0).getLineNumber()+" assigned type \""+ftype+"\"");
			}
			return ftype;
		}
		else if(node.kind.equals("factor")){

		}
		else if(node.kind.equals("read")){
			if(debug){
				System.out.println("READ on line "+node.getLineNumber()+" assigned type \"int\"");
			}
			return "int";
		}
		else if(node.kind.equals("fun call")){
			ParseTreeNode args = node.getChild(1);
			ParseTreeNode argList = args.getChild(0);
			ParseTreeNode exp;
			if(!argList.kind.equals("empty")){
				exp = argList.getChild(1);
				while(exp != null){
					type = typeCheck(exp);
					//TODO: type check each expression
					argList = argList.getChild(0);
					exp = argList.getChild(1);
				}
				exp = argList.getChild(0);
				type = typeCheck(exp);
				//TODO: type check last expression
			}
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

