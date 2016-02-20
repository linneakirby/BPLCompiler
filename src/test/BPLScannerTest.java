import static org.junit.Assert.*;
import org.junit.Test;
import Compiler.*;
import java.io.File;

public class BPLScannerTest{
	@Test
	public void getOneToken(){
		/*File curDir = new File(".");
		File[] filesList = curDir.listFiles();
        for(File f : filesList){
            if(f.isFile()){
                System.out.println(f.getName());
            }
        }*/
		BPLScanner scan = new BPLScanner("test.txt");
		try{
			scan.getNextToken();
			assertEquals("int", scan.nextToken().tokenString);
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
}