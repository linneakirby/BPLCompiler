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
	private int labelCount = -1;

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

	private void addDepthParamList(ParseTreeNode pl, int depth, int pos){
		ParseTreeNode param = pl.getChild(0);
		ParseTreeNode paramList = pl.getChild(1);
		addDepthVar(param, depth, pos);
		if(paramList != null){
			addDepthParamList(paramList, depth, (pos+1));
		}
	}

	private void addDepthVar(ParseTreeNode node, int depth, int pos){
		ParseTreeNode id = node.getChild(0);
		if(!id.kind.equals("id")){
			id = node.getChild(1);
		}
		id.setDepth(depth);
		id.getChild(0).setDepth(depth);
	
		/*if(id.getChild(0).getType().contains("arr")){
			//TODO pos += node.getChild(2).
		}*/
		id.setPosition(pos);
		id.getChild(0).setPosition(pos);
	}

	private int addDepthsHelper(ParseTreeNode node, int depth, int pos){
		if(node.kind.equals("param list")){
			addDepthParamList(node, 1, 0);
			return -1;
		}
		else if(node.kind.equals("fun dec")){
			pos = 0;
		}
		else if(node.kind.equals("compound statement")){
			if(depth == 0){
				depth = 2;
			}
			else{
				depth++;
			}
		}
		else if(node.kind.equals("var dec")){
			addDepthVar(node, depth, pos);
			return pos+1;
		}
		for(ParseTreeNode child: node.getChildren()){
			if(child != null){
				int p = addDepthsHelper(child, depth, pos);
				if(p != -1){
					pos = p;
				}
			}
		}

		if(node.kind.equals("fun dec") || node.kind.equals("compound statement")){
			return -1;
		}
		return pos;
	}

	private void addDepths(ParseTreeNode node){
	
		addDepthsHelper(node, 0, 0);

		/*for(ParseTreeNode child: node.getChildren()){
			if(child != null){
				addDepthsHelper(child, 0, 0);
			}
		}*/
	//	checkDepths(node);
	}

	private void checkDepths(ParseTreeNode node){
		for (ParseTreeNode child: node.getChildren()){
			if(child != null){
				System.out.println(child.kind+" assigned depth "+child.getDepth()+" and position "+child.getPosition());
				checkDepths(child);
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

	private int getMaxPos(ParseTreeNode fd){
		if(fd.kind.equals("var dec")){
			if(!fd.getChild(1).kind.equals("id")){
				fd = fd.getChild(2);
			}
			else{
				fd = fd.getChild(1);
			}
			return fd.getChild(0).getPosition();
		}
		else if(fd.getChildren().length == 0){
			return -1;
		}
		int max = -1;
		for(ParseTreeNode child: fd.getChildren()){
			if(child != null){
				int pos = getMaxPos(child);
				if(pos > max){
					max = pos;
				}
			}
		}
		return max;
	}

	private void generateFunDec(ParseTreeNode fd){
		String id = fd.getChild(1).getChild(0).kind;
		System.out.println(id+":");
		System.out.println("movq %rsp, %rbx #move stack pointer to fp");
		ParseTreeNode cs = fd.getChild(3);
		int space2Alloc = getMaxPos(fd)+1;

		System.out.println("subq $"+(8*space2Alloc)+", %rsp #decrement stack pointer by "+(8*space2Alloc)+" to make room for local vars");
		generateStatementList(cs.getChild(1), (8*space2Alloc));
		System.out.println("addq $"+(8*space2Alloc)+", %rsp #remove local vars");
		System.out.println("ret #return");
	}

	private void generateStatementList(ParseTreeNode sl, int stackSize){
		ParseTreeNode s = sl.getChild(0);
		if(!s.kind.equals("empty")){
			generateStatement(s, stackSize);
			generateStatementList(sl.getChild(1), stackSize);
		}
	}

	private void generateStatement(ParseTreeNode s, int stackSize){
		ParseTreeNode child = s.getChild(0);
		if(child.kind.equals("if statement")){
			generateIfStatement(child, stackSize);
		}
		else if(child.kind.equals("while statement")){
			generateWhileStatement(child, stackSize);
		}
		else if(child.kind.equals("return statement")){
			generateReturnStatement(child, stackSize);
		}
		else if(child.kind.equals("write statement") || child.kind.equals("writeln statement")){
			generateWriteStatement(child);
		}
		else if(child.kind.equals("expression statement")){
			evaluateExpression(child.getChild(0));
		}
		else if(child.kind.equals("compound statement")){
			generateCompoundStatement(child, stackSize);
		}
	}

	private void generateCompoundStatement(ParseTreeNode node, int stackSize){
		generateStatementList(node.getChild(1), stackSize);
	}

	private void generateIfStatement(ParseTreeNode node, int stackSize){
		ParseTreeNode exp = node.getChild(0);
		ParseTreeNode s1 = node.getChild(1);
		ParseTreeNode s2 = node.getChild(2);
		evaluateExpression(exp);

		String label1 = makeLabel();
		String label2 = "TEST";

		System.out.println("cmp $0, %eax #compare result to 0");
		System.out.println("je "+label1+" #jump to "+label1+" if false");
		generateStatement(s1, stackSize);
		if(s2 != null){
			label2 = makeLabel();
			System.out.println("jmp "+label2+" #jump to "+label2+" to skip else");
		}

		System.out.println(label1+":");

		if(s2 != null){
			generateStatement(s2, stackSize);
			System.out.println(label2+":");
		}

	}

	private void generateWhileStatement(ParseTreeNode node, int stackSize){
		ParseTreeNode exp = node.getChild(0);
		ParseTreeNode s = node.getChild(1);

		String label1 = makeLabel();
		String label2 = makeLabel();

		System.out.println(label1+":");
		evaluateExpression(exp);
		System.out.println("cmp $0, %eax #compare result to 0");
		System.out.println("je "+label2+" #jump to "+label2+" if false");
		generateStatement(s, stackSize);
		System.out.println("jmp "+label1+" #jump to "+label1+" to continue while loop");
		System.out.println(label2+":");
	}

	private void generateReturnStatement(ParseTreeNode node, int stackSize){
		ParseTreeNode exp = node.getChild(0);
		if(exp != null){
			evaluateExpression(exp);
		}
		System.out.println("addq $"+stackSize+", %rsp #restoring stack to original size");
		System.out.println("ret #return");
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
				System.out.println("movq $"+strings.get(str)+", %rsi #move "+strings.get(str)+" into 2nd arg to prepare for printing");
				System.out.println("movq $.WriteStringString, %rdi #prepare to write a string");
				System.out.println("movl $0, %eax #reset ret");
				System.out.println("call printf");
			}
		}
		
	}

	private String makeLabel(){
		labelCount++;
		return "L"+labelCount;
	}

	private int evaluateArgList(ParseTreeNode al){
		if(al != null && !al.kind.equals("empty")){
			int x = 1+evaluateArgList(al.getChild(1));
			evaluateExpression(al.getChild(0));
			System.out.println("push %rax #push arg onto stack");	
			return x;
		}
		return 0;
	}

	private void evaluateFunCall(ParseTreeNode fc){
		ParseTreeNode args = fc.getChild(1);
		ParseTreeNode al = args.getChild(0);
		int x = evaluateArgList(al);
		System.out.println("push %rbx #push fp");
		System.out.println("call "+fc.getChild(0).getChild(0).kind+" #call "+fc.getChild(0).getChild(0).kind);
		System.out.println("pop %rbx #pop fp");
		System.out.println("addq $"+x*8+", %rsp #remove args from stack");
	}

	private void evaluateFactor(ParseTreeNode factor){
		ParseTreeNode child = factor.getChild(0);
		if(child.kind.equals("expression")){
			evaluateExpression(child);
		}
		else if(child.kind.equals("num")){
			System.out.println("movq $"+child.getChild(0).kind+", %rax #move num to rax");
		}
		else if(child.kind.equals("string literal")){
			String string = strings.get(child.getChild(0).kind);
			System.out.println("movq $"+string+", %rax #move string literal to rax");
		}
		else if(child.kind.equals("read")){

		}
		else if(child.kind.equals("*")){
			ParseTreeNode id = factor.getChild(1);

		}
		else if(child.kind.equals("id")){
			ParseTreeNode exp = factor.getChild(2);
			//if <id>[EXPRESSION]
			if(exp != null){
			//TODO
			}
			//else <id>
			else{
				ParseTreeNode dec = child.getChild(0).getDeclaration();
				
				if(dec.kind.equals("param") || dec.kind.equals("var dec")){
					if(dec.getChild(1).kind.equals("*")){
						dec = dec.getChild(2);
					}
					else{
						dec = dec.getChild(1);
					}
				}
				
				String id = child.getChild(0).kind;

				int decDepth = dec.getDepth();
					
				if(decDepth == 0){
					System.out.println("movq "+id+", %rax #move "+id+" to rax");
				}
				else if(decDepth == 1){ //params
					int pos = 16+8*dec.getPosition();
					System.out.println("movq "+pos+"(%rbx), %rax #move "+id+" to rax");
				}
				else{ //local vars
					int pos = -8*dec.getPosition()-8;
					System.out.println("movq "+pos+"(%rbx), %rax #move "+id+" to rax");
				}

			}
		}
		else if(child.kind.equals("fun call")){
			evaluateFunCall(child);
		}
	}

	private void evaluateF(ParseTreeNode f){
	
		ParseTreeNode child = f.getChild(0);
		if(child.kind.equals("-")){
			ParseTreeNode f2 = f.getChild(1);
			evaluateF(f2);
			System.out.println("negl %rax");
		}
		else if (child.kind.equals("&")){
			ParseTreeNode factor = f.getChild(1);
			evaluateFactor(factor);

		}
		else if(child.kind.equals("*")){
			ParseTreeNode factor = f.getChild(1);
			evaluateFactor(factor);
		
		}
		else{
			evaluateFactor(child);
		}
	}

	private void evaluateT(ParseTreeNode t){
		ParseTreeNode f = t.getChild(2);
		evaluateF(f);
		ParseTreeNode mulop = t.getChild(1);
		if(mulop != null){
			System.out.println("push %rax");
			ParseTreeNode t2 = t.getChild(0);
			evaluateT(t2);
			String mo = mulop.getChild(0).kind;
			if(mo.equals("*")){
				System.out.println("imul 0(%rsp), %rax");
			}
			else{
				System.out.println("movq 0(%rsp), %rbp");
				System.out.println("cltq");
				System.out.println("cqto");
				System.out.println("idivl %ebp #divide");
				if(mo.equals("%")){
					System.out.println("movq %rdx, %rax #get result of mod");
				}
			}
			System.out.println("add $8, %rsp #pop the stack");
		}
	}

	private void evaluateE(ParseTreeNode e){
		ParseTreeNode t = e.getChild(2);
		evaluateT(t);
		ParseTreeNode addop = e.getChild(1);
		if(addop != null){
			System.out.println("push %rax");
			ParseTreeNode e2 = e.getChild(0);
			evaluateE(e2);
			String ao = addop.getChild(0).kind;
			if(ao.equals("+")){
				System.out.println("add 0(%rsp), %rax");
			}
			else{
				System.out.println("sub 0(%rsp), %rax");
			}
			System.out.println("add $8, %rsp #pop the stack");
		}
	}

	private void relopCompare(String relop){
		String label = makeLabel();
		String label2 = makeLabel();
		System.out.println("cmp %eax, 0(%rsp) #relop compare");
		if(relop.equals("<=")){
			System.out.println("jg "+label);
		}
		else if(relop.equals("<")){
			System.out.println("jge "+label);
		}
		else if(relop.equals("==")){
			System.out.println("jne "+label);
		}
		else if(relop.equals("!=")){
			System.out.println("je "+label);
		}
		else if(relop.equals(">")){
			System.out.println("jle "+label);
		}
		else if(relop.equals(">=")){
			System.out.println("jl "+label);
		}
		System.out.println("mov $1, %rax #condition is true");
		System.out.println("jmp "+label2);
		System.out.println(label+":");
		System.out.println("mov $0, %rax #condition is false");
		System.out.println(label2+":");
		System.out.println("add $8, %rsp #pop the stack");
	}

	private void evaluateCompExp(ParseTreeNode node){
		ParseTreeNode e = node.getChild(0);
		evaluateE(e);
		ParseTreeNode relop = node.getChild(1);
		if(relop != null){
			System.out.println("push %rax");
			ParseTreeNode e2 = node.getChild(2);
			evaluateE(e2);
			String rel = relop.getChild(0).kind;
			relopCompare(rel);
		}
	}

	private void evaluateIntExpression(ParseTreeNode node){
		ParseTreeNode child = node.getChild(0);
		if(child.kind.equals("comp exp")){
			evaluateCompExp(child);
		}
		else{ //VAR = EXPRESSION
			ParseTreeNode varChild0 = child.getChild(0);
			evaluateIntExpression(node.getChild(1));
			if(varChild0.kind.equals("*")){ //*<id>
				//TODO
			}
			else{
				ParseTreeNode varChild1 = child.getChild(1);
				if(varChild1 != null){ //<id>[EXPRESSION]
					//TODO
				}
				else{ //<id>
					ParseTreeNode dec = child.getChild(0).getChild(0).getDeclaration();
					if(dec.kind.equals("param") || dec.kind.equals("var dec")){
						if(dec.getChild(1).kind.equals("*")){
							dec = dec.getChild(2);
						}
						else{
							dec = dec.getChild(1);
						}
					}
				
					String id = child.getChild(0).getChild(0).kind;
					int decDepth = dec.getDepth();
				
					if(decDepth == 0){
						System.out.println("movq %rax, "+id+" #set value of "+id);
					}
					else if(decDepth == 1){ //params
						int pos = 16+8*dec.getPosition();
						System.out.println("movq %rax, "+pos+"(%rbx) #move rax into position");
					}
					else{ //local vars
						int pos = -8*dec.getPosition()-8;
						System.out.println("movq %rax, "+pos+"(%rbx) #move rax into position");
					}
					
				}
			}
		}
	}

	private void evaluateStringExpression(ParseTreeNode node){
//TODO
	}

	private void evaluateExpression(ParseTreeNode exp){
		if(exp.getType().equals("int") || exp.getType().equals("void")){
			evaluateIntExpression(exp);
		}
		else if(exp.getType().equals("string")){
			evaluateStringExpression(exp);
		}
		else if(exp.getType().equals("int ptr")){
//TODO			
		}
		else if(exp.getType().equals("string ptr")){
//TODO			
		}
		else if(exp.getType().equals("int arr")){
//TODO			
		}
		else if(exp.getType().equals("string arr")){
//TODO			
		}
	}

	private void generateRead(ParseTreeNode node){

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
