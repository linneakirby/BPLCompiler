package Compiler;

import java.lang.Exception;
import java.lang.Throwable;

public class BPLException extends Exception{

	public BPLException(String message){
		super(message);
	}

	public BPLException(int lineNumber, String message){
		super("Exception in line "+lineNumber+": "+message);
	}

}