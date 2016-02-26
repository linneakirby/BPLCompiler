import static org.junit.Assert.*;
import org.junit.*;
import Compiler.*;
import java.io.File;

public class BPLParserTest{

	@Test
	public void testExpressionStatement(){
		try{
			BPLParser parse = new BPLParser("testFiles/x.txt");
			assertEquals("LINE 1: program {\nLINE 1: 	statement {\nLINE 1: 		expression statement {\nLINE 1: 			expression {\nLINE 1: 				x {}\n			}\n		}\n	}\n}\n", parse.toString());
		}
		catch(BPLException b){
			System.out.println(b.getMessage());
		}
	}

	@Test
	public void testCompoundStatement(){
		try{
			BPLParser parse = new BPLParser("testFiles/xyz.txt");
			assertEquals("LINE 1: program {\nLINE 1: 	statement {\nLINE 1: 		compound statement {\nLINE 1: 			local decs {\nLINE 1: 				empty {}\n			}\nLINE 1: 			statement list {\nLINE 1: 				statement {\nLINE 1: 					expression statement {\nLINE 1: 						expression {\nLINE 1: 							x {}\n						}\n					}\n				}\nLINE 1: 				statement list {\nLINE 1: 					statement {\nLINE 1: 						expression statement {\nLINE 1: 							expression {\nLINE 1: 								y {}\n							}\n						}\n					}\nLINE 1: 					statement list {\nLINE 1: 						statement {\nLINE 1: 							expression statement {\nLINE 1: 								expression {\nLINE 1: 									z {}\n								}\n							}\n						}\nLINE 1: 						statement list {\nLINE 1: 							empty {}\n						}\n					}\n				}\n			}\n		}\n	}\n}\n", parse.toString());
		}
		catch(BPLException b){
			System.out.println(b.getMessage());
		}
	}

	@Test
	public void testIf(){
		try{
			BPLParser parse = new BPLParser("testFiles/if.txt");
			assertEquals("LINE 1: program {\nLINE 1: 	statement {\nLINE 1: 		compound statement {\nLINE 1: 			local decs {\nLINE 1: 				empty {}\n			}\nLINE 1: 			statement list {\nLINE 1: 				statement {\nLINE 1: 					if statement {\nLINE 1: 						expression {\nLINE 1: 							e {}\n						}\nLINE 1: 						statement {\nLINE 1: 							expression statement {\nLINE 1: 								expression {\nLINE 1: 									s {}\n								}\n							}\n						}\n					}\n				}\nLINE 1: 				statement list {\nLINE 1: 					empty {}\n				}\n			}\n		}\n	}\n}\n", parse.toString());	
		}
		catch(BPLException b){
			System.out.println(b.getMessage());
		}
	}

	@Test
	public void testLocalDecs(){
		try{
			BPLParser parse = new BPLParser("testFiles/localDecs.txt");
			assertEquals("LINE 1: program {\nLINE 1: 	statement {\nLINE 1: 		compound statement {\nLINE 1: 			local decs {\nLINE 1: 				var dec {\nLINE 1: 					int {}\nLINE 1: 					x {}\n				}\nLINE 1: 				local decs {\nLINE 1: 					empty {}\n				}\n			}\nLINE 1: 			statement list {\nLINE 1: 				statement {\nLINE 1: 					expression statement {\nLINE 1: 						empty {}\n					}\n				}\nLINE 1: 				statement list {\nLINE 1: 					statement {\nLINE 1: 						expression statement {\nLINE 1: 							expression {\nLINE 1: 								x {}\n							}\n						}\n					}\nLINE 1: 					statement list {\nLINE 1: 						empty {}\n					}\n				}\n			}\n		}\n	}\n}\n", parse.toString());
		}
		catch(BPLException b){
			System.out.println(b.getMessage());
		}
	}
}