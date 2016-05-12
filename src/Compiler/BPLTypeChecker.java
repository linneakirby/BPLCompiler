package Compiler;

import java.util.Scanner;
import java.util.LinkedList;
import java.util.HashMap;
import java.io.File;
import java.lang.Exception;
import java.lang.StringBuilder;
import java.lang.String;

public class BPLTypeChecker{

	private boolean debug = false;

	private HashMap<String, ParseTreeNode> symbolTable;
	private LinkedList<ParseTreeNode> localDecs;
	public ParseTreeNode parseTree;

	public BPLTypeChecker(String filename) throws BPLException{
		BPLParser parser = new BPLParser(filename);
		symbolTable = new HashMap<String, ParseTreeNode>();
		parseTree = parser.getParseTree();
		findReferences(parseTree);
		typeCheck(parseTree);
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


	private String setType(ParseTreeNode node, String type){
		for(ParseTreeNode child:node.getChildren()){
			if(child != null){
				if(child.kind == "id"){
					setType(child.getChild(0), type);
					child.setExpression(child.getChild(0).kind);
					return child.getChild(0).kind;
				}
				child.setExpression(setType(child, type));
				return child.getExpression();

			}
		}
		return node.getExpression();
	}

	//type-checks all references
	private String typeCheck(ParseTreeNode node) throws BPLException{
		if(node.kind.equals("if statement")){
			return typeCheckIfStatement(node);
		}
		else if(node.kind.equals("while statement")){
			return typeCheckWhileStatement(node);
		}
		else if(node.kind.equals("return statement")){
			return typeCheckReturnStatement(node);
		}
		/*else if(node.kind.equals("write statement") || node.kind.equals("writeln statement")){
			//check for 'string'
		}*/
		else if(node.kind.equals("expression")){
			return typeCheckExpression(node);
		}
		else if(node.kind.equals("comp exp")){
			return typeCheckCompExp(node);
		}
		else if(node.kind.equals("E")){
			return typeCheckE(node);
		}
		else if(node.kind.equals("T")){
			return typeCheckT(node);
		}
		else if(node.kind.equals("F")){
			return typeCheckF(node);
		}
		else if(node.kind.equals("factor")){
			return typeCheckFactor(node);
		}
		else if(node.kind.equals("read")){
			return typeCheckRead(node);
		}
		else if(node.kind.equals("fun call")){
			return typeCheckFunCall(node);
		}
		for(ParseTreeNode child:node.getChildren()){
			if(child != null){
				typeCheck(child);
			}
		}
		return "none";
	}

	private String typeCheckIfStatement(ParseTreeNode node) throws BPLException{
		//make sure condition is 'int'
		String type = typeCheck(node.getChild(0));
		if(!type.equals("int")){
			throw new BPLException("ERROR: for IF STATEMENT on line "+node.getLineNumber()+" expected type 'int' but was assigned type \""+type+"\"");
		}
		if(debug){
			System.out.println("IF STATEMENT on line "+node.getLineNumber()+" assigned type \"int\"");
		}
		node.getChild(0).setType(type);
		typeCheck(node.getChild(1));
		if(node.getChild(2) != null){
			typeCheck(node.getChild(2));
		}
		return "none";
	}

	private String typeCheckWhileStatement(ParseTreeNode node) throws BPLException{
		//check for 'int'
		String type = typeCheck(node.getChild(0));
		if(!type.equals("int")){
			throw new BPLException("ERROR: for WHILE STATEMENT on line "+node.getLineNumber()+" expected type 'int' but was assigned type \""+type+"\"");
		}
		if(debug){
			System.out.println("WHILE STATEMENT on line "+node.getLineNumber()+" assigned type \"int\"");
		}
		node.getChild(0).setType(type);
		typeCheck(node.getChild(1));
		return "none";
	}

	private String typeCheckReturnStatement(ParseTreeNode node) throws BPLException{
		//could be 'int' or 'string'; make sure not 'void'
		if(node.getChild(0) != null){
			String type = typeCheck(node.getChild(0));
			if(debug){
				System.out.println("RETURN STATEMENT on line "+node.getChild(0).getLineNumber()+" assigned type \""+type+"\"");
			}
			node.getChild(0).setType(type);
			return type;
		}
		return "none";
	}

	private String typeCheckExpression(ParseTreeNode node) throws BPLException{

			if(node.getChild(0).kind.equals("comp exp")){
				String type = typeCheck(node.getChild(0));
				node.getChild(0).setType(type);
				node.setType(type);
				if(debug){
					System.out.println("EXPRESSION on line "+node.getChild(0).getLineNumber()+" assigned type \""+type+"\"");
					System.out.println("GETTING TYPE: "+node.getType());
				}
				return type;
			}
			else{
				//get var type
				String varType = typeCheckVar(node.getChild(0));
				String expType = typeCheck(node.getChild(1));
				if(!varType.equals(expType)){
						throw new BPLException("ERROR: for EXPRESSION on line "+node.getLineNumber()+" expected type \""+varType+"\" arr but was assigned type \""+expType+"\"");
				}				
				node.getChild(0).setType(varType);
				node.getChild(1).setType(expType);
				node.setType(varType);
				if(debug){
					System.out.println("GETTING TYPEE: "+node.getType());
				}
			}
			return "none"; //TODO: FIX THIS
	}

	private String typeCheckVar(ParseTreeNode node) throws BPLException{
		ParseTreeNode varId = node.getChild(0);
		ParseTreeNode varDec;
		String varType = "test";
		if(varId.kind.equals("id")){ //<id> or <id>[EXPRESSION]
			varDec = varId.getChild(0).getDeclaration();
			varType = varDec.getChild(0).getChild(0).kind;
				if(varDec.getChild(1) != null && varDec.getChild(1).kind.equals("*")){
					varType = varType.concat(" ptr");
				}
				if(node.getChild(1) != null && node.getChild(1).kind.equals("[")){ //<id>[EXPRESSION]
					String expType = typeCheckExpression(node.getChild(2));
					
					node.getChild(2).setType(expType);


					if(varDec.getChild(2) == null || !varDec.getChild(2).kind.equals("[")){
						throw new BPLException("ERROR: for VAR on line "+node.getLineNumber()+" expected type \""+varType+"\" arr but was assigned type \""+varType+"\"");
					}
					//varType = varType.concat(" arr");
				}
		}
		else if(node.getChild(1).kind.equals("id")){ // *<id>
			varId = node.getChild(1);
			varDec = varId.getChild(0).getDeclaration();
			varType = varDec.getChild(0).getChild(0).kind;
			varType = varType.concat(" ptr");
		}
		node.setType(varType);
		return varType;
	}

	private String typeCheckCompExp(ParseTreeNode node) throws BPLException{
		String eType = typeCheck(node.getChild(0));
		node.getChild(0).setType(eType);
		if(!eType.contains("int") && !eType.contains("string") && !eType.contains("void")){
				throw new BPLException("ERROR: for E on line "+node.getChild(0).getLineNumber()+" expected type \"int\" or \"string\" or \"arr\" or \"ptr\" but was assigned type \""+eType+"\"");
		}
		if(node.getChild(2) != null){
			String e2Type = typeCheck(node.getChild(2));
			node.getChild(2).setType(e2Type);	
			if(!e2Type.equals("int")){
				throw new BPLException("ERROR: for E on line "+node.getChild(2).getLineNumber()+" expected type \"int\" but was assigned type \""+e2Type+"\"");
			}
			if(!eType.equals(e2Type)){
				throw new BPLException("ERROR: for COMP EXP on line "+node.getChild(0).getLineNumber()+" expected type \"int\" but was assigned type \""+eType+"\" and \""+e2Type+"\"");
			}
			node.setType(eType);
		}
		if(debug){
			System.out.println("COMP EXP on line "+node.getChild(0).getLineNumber()+" assigned type \"int\"");
		}
		return eType;
	}

	private String typeCheckE(ParseTreeNode node) throws BPLException{
		String tType = typeCheck(node.getChild(2));
		node.getChild(2).setType(tType);
		if(!tType.contains("int") && !tType.contains("string") && !tType.contains("void")){
			throw new BPLException("ERROR: for T on line "+node.getChild(2).getLineNumber()+" expected type \"int\" or \"string\" or \"arr\" or \"ptr\" but was assigned type \""+tType+"\"");
		}
		if(node.getChild(0) != null){
			String eType = typeCheck(node.getChild(0));	
			node.getChild(0).setType(eType);
			if(!eType.equals("int")){
				throw new BPLException("ERROR: for E on line "+node.getChild(0).getLineNumber()+" expected type \"int\" but was assigned type \""+eType+"\"");
			}
			if(!tType.equals(eType)){
				throw new BPLException("ERROR: for E on line "+node.getChild(2).getLineNumber()+" expected type \"int\" but was assigned type \""+eType+"\" and \""+tType+"\"");
			}
			node.setType(eType);
		}
		if(debug){
			System.out.println("E on line "+node.getChild(2).getLineNumber()+" assigned type \"int\"");
		}
		return tType;
	}

	private String typeCheckT(ParseTreeNode node) throws BPLException{
		String fType = typeCheck(node.getChild(2));
		node.getChild(2).setType(fType);
		if(!fType.contains("int") && !fType.contains("string") && !fType.contains("void")){
				throw new BPLException("ERROR: for F on line "+node.getChild(2).getLineNumber()+" expected type \"int\" or \"string\" or \"arr\" or \"ptr\" but was assigned type \""+fType+"\"");
		}
		if(node.getChild(0) != null){
			String tType = typeCheck(node.getChild(0));	
			node.getChild(0).setType(tType);
			if(!tType.equals("int")){
				throw new BPLException("ERROR: for T on line "+node.getChild(0).getLineNumber()+" expected type \"int\" but was assigned type \""+tType+"\"");
			}
			if(!fType.equals(tType)){
				throw new BPLException("ERROR: for T on line "+node.getChild(2).getLineNumber()+" expected type \"int\" but was assigned type \""+tType+"\" and \""+fType+"\"");
			}
			node.setType(tType);
		}
		if(debug){
			System.out.println("T on line "+node.getChild(2).getLineNumber()+" assigned type \"int\"");
		}
		return fType;
	}

	private String typeCheckF(ParseTreeNode node) throws BPLException{
		ParseTreeNode f = node.getChild(1);
		String type;
		//&Factor or *Factor or -Factor
		if (f != null){
			type = typeCheck(f);
			ParseTreeNode f0 = node.getChild(0);
			if(f0.kind.equals("-") && !type.equals("int")){
				throw new BPLException("ERROR: for F on line "+node.getChild(1).getLineNumber()+" expected type \"int\" but was assigned type \""+type+"\"");
			}
			/*else if(f0.kind.equals("*")){
				if(!type.contains("ptr")){
					throw new BPLException("ERROR: for F on line "+node.getChild(1).getLineNumber()+" expected type \"int ptr\" or \"string ptr\" but was assigned type \""+type+"\"");
				}
				type.replace(" ptr", "");
			}*/
			else if(f0.kind.equals("&")){
				type+=" ptr";
			}
			else if (!type.equals("int") && !type.equals("string")){
				throw new BPLException("ERROR: for F on line "+node.getChild(1).getLineNumber()+" expected type \"int\" or \"string\" but was assigned type \""+type+"\"");
			}
			f.setType(type);
			f0.setType(type);
			node.setType(type);
		}
		//Factor
		else{
			type = typeCheck(node.getChild(0));
			node.getChild(0).setType(type);
			if(!type.contains("int") && !type.contains("string") && !type.contains("void")){
				throw new BPLException("ERROR: for F on line "+node.getChild(0).getLineNumber()+" expected type \"int\" or \"string\" or \"arr\" or \"ptr\" but was assigned type \""+type+"\"");
			}
			node.setType(type);
		}
		if(debug){
			System.out.println("F on line "+node.getLineNumber()+" assigned type \""+type+"\"");
		}
		return type;
	}

	private String typeCheckFactor(ParseTreeNode node) throws BPLException{
		ParseTreeNode factor0 = node.getChild(0);
		ParseTreeNode factor1 = node.getChild(1);
		ParseTreeNode factor2 = node.getChild(2);
		ParseTreeNode dec;
		String type;
		//(EXPRESSION) or FUN_CALL or read()
		if(factor0.kind.equals("expression") || factor0.kind.equals("fun call") || factor0.kind.equals("read")){
			return typeCheck(factor0);
		}
		//<id> or <id>[EXPRESSION]
		else if(factor0.kind.equals("id")){
			dec = factor0.getChild(0).getDeclaration();
			type = dec.getChild(0).getChild(0).kind;
			if(factor2 != null){
				String expType = typeCheckExpression(factor2);
				if(!expType.equals("int")){
					throw new BPLException("ERROR: for Factor on line "+factor2.getLineNumber()+" expected type \"int\" but was assigned type \""+expType+"\"");
				}
				factor2.setType(expType);
			}
		}
		//*<id>
		else if(factor0.kind.equals("*")){
			dec = factor1.getChild(0).getDeclaration();
			type = dec.getChild(0).getChild(0).kind;
			type.concat(" ptr");
		}
		//<num>
		else if(factor0.kind.equals("num")){
			if(debug){
				System.out.println("FACTOR on line "+node.getLineNumber()+" assigned type \"int\"");
			}
			type = "int";
		}
		//<string>
		else if(factor0.kind.equals("string literal")){
			if(debug){
				System.out.println("FACTOR on line "+node.getLineNumber()+" assigned type \"string\"");
			}
			type = "string";
		}
		else{
			throw new BPLException("ERROR: FACTOR on line "+node.getLineNumber()+" assigned invalid type!");
		}
		if(debug){
			System.out.println("FACTOR on line "+node.getLineNumber()+" assigned type \""+type+"\"");
		}
		node.setType(type);
		return type;
	}

	private String typeCheckRead(ParseTreeNode node) throws BPLException{
		if(debug){
				System.out.println("READ on line "+node.getLineNumber()+" assigned type \"int\"");
			}
			node.setType("int");
			return "int";
	}

	private String typeCheckFunCall(ParseTreeNode node) throws BPLException{
		ParseTreeNode args = node.getChild(1);
		ParseTreeNode argList = args.getChild(0);
		ParseTreeNode exp;
		ParseTreeNode dec = node.getChild(0).getChild(0).getDeclaration();
		String decType = dec.getChild(0).getChild(0).kind;
		ParseTreeNode params = dec.getChild(2);
		ParseTreeNode paramList = params.getChild(0);
		ParseTreeNode param;
		String pType;
		String type;
		if(!argList.kind.equals("empty")){
			exp = argList.getChild(0);
			type = typeCheck(exp);
			param = paramList.getChild(0);
			pType = param.getChild(0).getChild(0).kind;
			if(!type.equals(pType)){
				throw new BPLException("ERROR: EXPRESSION on line "+node.getLineNumber()+" expected type \""+pType+"\" but was assigned type \""+type+"\"");
			}
			argList = argList.getChild(1);
			paramList = paramList.getChild(1);
			while(argList != null){
				exp = argList.getChild(0);
				param = paramList.getChild(0);
				type = typeCheck(exp);
				pType = param.getChild(0).getChild(0).kind;
				if(!type.equals(pType)){
					throw new BPLException("ERROR: EXPRESSION on line "+node.getLineNumber()+" expected type \""+pType+"\" but was assigned type \""+type+"\"");
				}
				argList = argList.getChild(1);
				paramList = paramList.getChild(1);
			}
		}
		checkReturnType(node, decType);
		node.setType(decType);
		return decType;
	}

	private void checkReturnType(ParseTreeNode node, String expType) throws BPLException{
		String type;
		for(ParseTreeNode child:node.getChildren()){
			if(child != null){
				if(child.kind.equals("return statement")){
					type = typeCheck(child);
					if(!type.equals(expType)){
						throw new BPLException("ERROR: FUN CALL on line "+node.getLineNumber()+" expected type \""+expType+"\" but was assigned type \""+type+"\"");
					}
				}
				checkReturnType(child, expType);
			}
		}
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
			ParseTreeNode p = null;
			while(paramlist != null && paramlist.kind == "param list"){
				p = paramlist.getChild(0);
				paramlist = paramlist.getChild(1);
				localDecs.add(p);
				if(debug){
					System.out.println("Adding "+p.kind.toUpperCase()+" \""+p.getChild(1).getChild(0).kind+"\" to the local declarations");
				}
			}
			if(p != null){
				localDecs.add(p);
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
			while(decChild.kind.equals("*") || decChild.kind.equals("type specifier")){
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
		String type = "";
		ParseTreeNode child = root.getChild(i);
			while(child.kind.equals("*") || child.kind.equals("type specifier")){
				i++;
				if(child.kind.equals("*")){
					type = "*";
				}
				child = root.getChild(i);
			}
			localDecs.add(root);
			type+=child.getChild(0).kind;
			if(debug){
				System.out.println("Adding "+root.kind.toUpperCase()+" \""+type+"\" to the local declarations");
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

