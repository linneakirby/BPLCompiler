import static org.junit.Assert.*;
import org.junit.Test;
import Compiler.*;
import java.io.File;

public class BPLParserTest{
	@Test
	public void oneId(){
		BPLParser parse = new BPLParser("x.txt");
		try{
			System.out.println(parse.toString());

			//assertEquals("int", scan.nextToken().tokenString);
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
}