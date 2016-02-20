package Compiler.ParseTree;

import java.util.Scanner;
import java.io.File;
import java.lang.Exception;
import java.lang.StringBuilder;
import java.lang.String;

public class StatementNode extends ParseTreeNode{

	public StatementNode(Token tok){
			super(Token tok, 1, "statement");
	}
}