import static org.junit.Assert.*;
import org.junit.*;
import Compiler.*;
import java.io.File;

public class BPLParserTest{

	@Test
	public void testX(){
		try{
			BPLParser parse = new BPLParser("x.txt");
			assertEquals("LINE 1: program {\nLINE 1: 	statement {\nLINE 1: 		expression statement {\nLINE 1: 			expression {\nLINE 1: 				x {}\n			}\n		}\n	}\n}\n", parse.toString());
		}
		catch(BPLException b){
			System.out.println(b.getMessage());
		}
	}

	@Test
	public void testXyz(){
		try{
			BPLParser parse = new BPLParser("xyz.txt");
			assertEquals("LINE 1: program {\nLINE 1: 	statement {\nLINE 1: 		compound statement {\nLINE 1: 			local decs {\nLINE 1: 				empty {}\n			}\nLINE 1: 			statement list {\nLINE 1: 				statement {\nLINE 1: 					expression statement {\nLINE 1: 						expression {\nLINE 1: 							x {}\n						}\n					}\n				}\nLINE 1: 				statement list {\nLINE 1: 					statement {\nLINE 1: 						expression statement {\nLINE 1: 							expression {\nLINE 1: 								y {}\n							}\n						}\n					}\nLINE 1: 					statement list {\nLINE 1: 						statement {\nLINE 1: 							expression statement {\nLINE 1: 								expression {\nLINE 1: 									z {}\n								}\n							}\n						}\nLINE 1: 						statement list {\nLINE 1: 							empty {}\n						}\n					}\n				}\n			}\n		}\n	}\n}\n", parse.toString());
		}
		catch(BPLException b){
			System.out.println(b.getMessage());
		}
	}

	@Test
	public void testIf(){
		try{
			BPLParser parse = new BPLParser("if.txt");
			assertEquals("LINE 1: program {\nLINE 1: 	statement {\nLINE 1: 		compound statement {\nLINE 1: 			local decs {\nLINE 1: 				empty {}\n			}\nLINE 1: 			statement list {\nLINE 1: 				statement {\nLINE 1: 					if statement {\nLINE 1: 						expression {\nLINE 1: 							e {}\n						}\nLINE 1: 						statement {\nLINE 1: 							expression statement {\nLINE 1: 								expression {\nLINE 1: 									s {}\n								}\n							}\n						}\n					}\n				}\nLINE 1: 				statement list {\nLINE 1: 					empty {}\n				}\n			}\n		}\n	}\n}\n", parse.toString());	
		}
		catch(BPLException b){
			System.out.println(b.getMessage());
		}
	}
}