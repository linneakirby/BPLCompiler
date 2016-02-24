import static org.junit.Assert.*;
import org.junit.*;
import Compiler.*;
import java.io.File;

public class BPLParserTest{

	@Test
	public void x(){
		BPLParser parse = new BPLParser("x.txt");
		assertEquals("program {\n\tstatement {\n\t\texpression statement {\n\t\t\texpression {\n\t\t\t\tx {}\n\t\t\t}\n\t\t}\n\t}\n}", parse.toString());
	}

	@Test
	public void xyz(){
		BPLParser parse = new BPLParser("xyz.txt");
		assertEquals("program {\n\tstatement {\n\t\tcompound statement {\n\t\t\tstatement list {\n\t\t\t\tstatement {\n\t\t\t\t\texpression statement {\n\t\t\t\t\t\texpression {\n\t\t\t\t\t\t\tx {}\n\t\t\t\t\t\t}\n\t\t\t\t\t}\n\t\t\t\t}\n\t\t\t\tstatement list {\n\t\t\t\t\tstatement {\n\t\t\t\t\t\texpression statement {\n\t\t\t\t\t\t\texpression {\n\t\t\t\t\t\t\t\ty {}\n\t\t\t\t\t\t\t}\n\t\t\t\t\t\t}\n\t\t\t\t\t}\n\t\t\t\t\tstatement list {\n\t\t\t\t\t\tstatement {\n\t\t\t\t\t\t\texpression statement {\n\t\t\t\t\t\t\t\texpression {\n\t\t\t\t\t\t\t\t\tz {}\n\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t}\n\t\t\t\t\t\t}\n\t\t\t\t\t\tstatement list {\n\t\t\t\t\t\t\tempty {}\n\t\t\t\t\t\t}\n\t\t\t\t\t}\n\t\t\t\t}\n\t\t\t}\n\t\t}\n\t}\n}", parse.toString());
	}
}