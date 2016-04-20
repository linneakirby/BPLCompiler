package Compiler;

import java.util.Scanner;
import java.io.File;
import java.lang.Exception;
import java.lang.StringBuilder;
import java.lang.String;

public class BPLCodeGenerator{

	public BPLCodeGenerator(String filename) throws BPLException{
		BPLTypeChecker typeChecker = new BPLTypeChecker(filename);
		codeGenerate(typeChecker.parseTree);
	}

	private void codeGenerate(ParseTreeNode root){
		printRodata(root);
	}

	//prints .rodata of the program
	private void printRodata(ParseTreeNode root){
		System.out.println(".section .rodata");
		System.out.println(".WriteIntString: .string \"%d \"");
		System.out.println(".WritelnString: .string \"\\n\"");

		findStrings(root);

		System.out.println(".text");
		System.out.println(".globl main");
	}

	//recursive method that finds all strings in the program and prints them to .rodata
	private void findStrings(ParseTreeNode root){
		for(ParseTreeNode child:root.getChildren()){
			findStrings();
		}
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