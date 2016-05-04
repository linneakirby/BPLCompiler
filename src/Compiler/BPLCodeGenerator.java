package Compiler;

import java.util.Scanner;
import java.io.File;
import java.lang.Exception;
import java.lang.StringBuilder;
import java.lang.String;
import java.util.HashMap;
import java.util.ArrayList;

public class BPLCodeGenerator{

	private HashMap<String, String> strings;
	private ArrayList<String> globalVariables;

	public BPLCodeGenerator(String filename) throws BPLException{
		BPLTypeChecker typeChecker = new BPLTypeChecker(filename);
		strings = new HashMap<String, String>();
		globalVariables = new ArrayList<String>();
		codeGenerate(typeChecker.parseTree);
	}

	private void codeGenerate(ParseTreeNode root){
		printRodata(root);
		generateCode(root);
	}

	//prints .rodata of the program
	private void printRodata(ParseTreeNode root){
		System.out.println(".section .rodata");
		System.out.println(".WriteIntString: .string \"%d \"");
		System.out.println(".WritelnString: .string \"\\n\"");
		System.out.println(".WriteStringString: .string \"%s\"");

		findStrings(root, 0);
		findGlobalVariables(root);
		addDepths(root);

		System.out.println(".text");
		System.out.println(".globl main");
	}

	//recursive method that finds all strings in the program and prints them to .rodata
	private int findStrings(ParseTreeNode node, int count){
		for(ParseTreeNode child:node.getChildren()){
			if(child != null){
				if(child.kind.equals("string literal")){
						if(!strings.containsKey(child.getChild(0).kind)){
						String label = ".s"+count+": .string "+child.getChild(0).kind;
						System.out.println(label);
						strings.put(child.getChild(0).kind, ".s"+count);
						count++;
					}
				}
				count = findStrings(child, count);
			}
		}
		return count;
	}

	private void addDepthsHelper(ParseTreeNode node, int depth){
		int d = depth;
		if(node.kind.equals("param")){
			node.setDepth(1);

		}
		else if(node.kind.equals("compound statement")){
			depth++;
		}
		else if(node.kind.equals("var dec")){
			node.setDepth(d);
		}
		for(ParseTreeNode child: node.getChildren()){
			if(child != null){
				addDepthsHelper(child, d);
			}
		}
	}

	private void addDepths(ParseTreeNode node){
		for(ParseTreeNode child: node.getChildren()){
			if(child != null){
				addDepthsHelper(child, 2);
			}
		}
	}

	//check to see if the declaration is a global variable and, if it is, print it to .rodata
	private void checkDeclaration(ParseTreeNode dec){
		String id;
		ParseTreeNode idNode;
		int size = 8;

		ParseTreeNode child = dec.getChild(0);
		if(child.kind.equals("var dec")){
			ParseTreeNode grandchild = child.getChild(1);
			if(!grandchild.kind.equals("id")){
				grandchild = child.getChild(2);
			}
			idNode = grandchild.getChild(0);
			id = idNode.kind;
			if(child.getChild(2) != null && child.getChild(2).kind.equals("[")){
				String num = child.getChild(3).getChild(0).kind;
				size = size * Integer.parseInt(num);
			}
			idNode.setDepth(0);
			globalVariables.add(id);
			System.out.println(".comm "+id+", "+size+", 32");
		}
	}

	//recursive method that finds all the global variables
	private void findGlobalVariables(ParseTreeNode node){
		for(ParseTreeNode child: node.getChildren()){
			if(child != null){
				if(child.kind.equals("declaration")){
					checkDeclaration(child);
				}
				else if(child.kind.equals ("declaration list")){
					findGlobalVariables(child);
				}
			}
		}
	}


	//generates code
	private void generateCode(ParseTreeNode node){
		if(node.kind.equals("fun dec")){
			generateFunDec(node);
		}
		/*else if(node.kind.equals("if statement")){
			generateIfStatement(node);
		}
		else if(node.kind.equals("while statement")){
			generateWhileStatement(node);
		}
		else if(node.kind.equals("return statement")){
			generateReturnStatement(node);
		}
		else if(node.kind.equals("write statement") || node.kind.equals("writeln statement")){
			generateWriteStatement(node);
		}
		else if(node.kind.equals("expression")){
			generateExpression(node);
		}
		else if(node.kind.equals("comp exp")){
			generateCompExp(node);
		}
		else if(node.kind.equals("E")){
			generateE(node);
		}
		else if(node.kind.equals("T")){
			generateT(node);
		}
		else if(node.kind.equals("F")){
			generateF(node);
		}
		else if(node.kind.equals("factor")){
			generateFactor(node);
		}
		else if(node.kind.equals("read")){
			generateRead(node);
		}
		else if(node.kind.equals("fun call")){
			generateFunCall(node);
		}*/
		for(ParseTreeNode child:node.getChildren()){
			if(child != null){
				generateCode(child);
			}
		}
	}

	private void generateFunDec(ParseTreeNode fd){
		String id = fd.getChild(1).getChild(0).kind;
		System.out.println(id+":");
		ParseTreeNode cs = fd.getChild(3);
		generateStatementList(cs.getChild(1));
	}

	private void generateStatementList(ParseTreeNode sl){
		ParseTreeNode s = sl.getChild(0);
		if(!s.kind.equals("empty")){
			generateStatement(s);
			generateStatementList(sl.getChild(1));
		}
	}

	private void generateStatement(ParseTreeNode s){
		ParseTreeNode child = s.getChild(0);
		if(child.kind.equals("if statement")){
			generateIfStatement(child);
		}
		else if(child.kind.equals("while statement")){
			generateWhileStatement(child);
		}
		else if(child.kind.equals("return statement")){
			generateReturnStatement(child);
		}
		else if(child.kind.equals("write statement") || child.kind.equals("writeln statement")){
			generateWriteStatement(child);
		}
		else if(child.kind.equals("expression statement")){
			generateExpression(child.getChild(0));
		}
		else if(child.kind.equals("compound statement")){
			generateCompoundStatement(child);
		}
	}

	private void generateCompoundStatement(ParseTreeNode node){

	}

	private void generateIfStatement(ParseTreeNode node){

	}

	private void generateWhileStatement(ParseTreeNode node){

	}

	private void generateReturnStatement(ParseTreeNode node){

	}


	private String findString(ParseTreeNode node){
		for(ParseTreeNode child:node.getChildren()){
			if(child != null){
				if(child.kind.equals("string literal")){
					return child.getChild(0).kind;
				}
				else{
					String s = findString(child);
					if(!s.equals("none")){
						return s;
					}
				}
			}
		}
		return "none";
	}

	//code generate a write statement
	private void generateWriteStatement(ParseTreeNode node){
		if(node.kind.equals("writeln statement")){
			System.out.println("movq $.WritelnString, %rdi #prepare to write a new line");
			System.out.println("movl $0, %eax #reset ret");
			System.out.println("call printf");
		}
		else{
			ParseTreeNode exp = node.getChild(0);
			String type = exp.getType();
			if(type.equals("int")){
				evaluateExpression(exp);
				System.out.println("movq %rax, %rsi #move num into 2nd arg to prepare for printing");
				System.out.println("movq $.WriteIntString, %rdi #prepare to write an int");
				System.out.println("movl $0, %eax #reset ret");
				System.out.println("call printf");
			}
			else if(type.equals("string")){
				String str = findString(node);
				System.out.println("movq "+strings.get(str)+", %rsi #move "+strings.get(str)+" into 2nd arg to prepare for printing");
				System.out.println("movq $.WriteStringString, %rdi #prepare to write a string");
				System.out.println("movl $0, %eax #reset ret");
				System.out.println("call printf");
			}
		}
		
	}

	private void evaluateCompExp(ParseTreeNode node){
		System.out.println("movq $5, %rax");
		//ParseTreeNode child0 = node.getChild(0);
	}

	private void evaluateIntExpression(ParseTreeNode node){
		ParseTreeNode child = node.getChild(0);
		if(child.kind.equals("comp exp")){
			evaluateCompExp(child);
		}
		else{

		}
	}

	private void evaluateStringExpression(ParseTreeNode node){

	}

	private void evaluateExpression(ParseTreeNode exp){
		if(exp.getType().equals("int")){
			evaluateIntExpression(exp);
		}
		else if(exp.getType().equals("string")){
			evaluateStringExpression(exp);
		}
		else if(exp.getType().equals("int ptr")){
			
		}
		else if(exp.getType().equals("string ptr")){
			
		}
		else if(exp.getType().equals("int arr")){
			
		}
		else if(exp.getType().equals("string arr")){
			
		}
	}

	private void generateExpression(ParseTreeNode node){

	}

	private void generateCompExp(ParseTreeNode node){

	}

	private void generateE(ParseTreeNode node){

	}

	private void generateT(ParseTreeNode node){

	}

	private void generateF(ParseTreeNode node){

	}

	private void generateFactor(ParseTreeNode node){

	}

	private void generateRead(ParseTreeNode node){

	}

	private void generateFunCall(ParseTreeNode node){

	}


	public static void main(String[ ] args) {
		String filename ;
		BPLCodeGenerator myCodeGenerator;
		try{
			filename = args[0] ;
			myCodeGenerator = new BPLCodeGenerator(filename);
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