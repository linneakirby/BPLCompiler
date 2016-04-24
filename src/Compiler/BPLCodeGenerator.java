package Compiler;

import java.util.Scanner;
import java.io.File;
import java.lang.Exception;
import java.lang.StringBuilder;
import java.lang.String;
import java.util.HashMap;

public class BPLCodeGenerator{

	private HashMap<String, String> strings;

	public BPLCodeGenerator(String filename) throws BPLException{
		BPLTypeChecker typeChecker = new BPLTypeChecker(filename);
		strings = new HashMap<String, String>();
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

	//generates code
	private void generateCode(ParseTreeNode node){
		if(node.kind.equals("if statement")){
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
		}
		for(ParseTreeNode child:node.getChildren()){
			if(child != null){
				generateCode(child);
			}
		}
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
			System.out.println("movq $.WritelnString, %rdi //prepare to write a new line");
			System.out.println("movl $0, %eax //reset ret");
			System.out.println("call printf");
		}
		else{
			String type = node.getChild(0).getType();
			if(type.equals("int")){
				//TODO: evaluate expression
				System.out.println("movq $.WriteIntString, %rdi //prepare to write an int");
				System.out.println("movl $0, %eax //reset ret");
				System.out.println("call printf");
			}
			else if(type.equals("string")){
				String str = findString(node);
				System.out.println("movq "+strings.get(str)+", %rsi //move "+strings.get(str)+" into 2nd arg to prepare for printing");
				System.out.println("movq $.WriteStringString, %rdi //prepare to write a string");
				System.out.println("movl $0, %eax //reset ret");
				System.out.println("call printf");
			}
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