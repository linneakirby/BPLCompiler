import static org.junit.Assert.*;
import org.junit.Test;
import Compiler.*;
import java.io.File;

public class BPLParserTest{
	@Test
	public void oneId(){
		BPLParser parse = new BPLParser("x.txt");
		try{
			assertEquals("<program {\n\t<statement {\n\t\t<expression statement {\n\t\t\t<expression {\n\t\t\t\t<x {}>\n\t\t\t}>\n\t\t}>\n\t}>\n}>", parse.currentToken().tokenString);
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
}