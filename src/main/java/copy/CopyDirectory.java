package copy;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class CopyDirectory {

	public static void main(String[] args) {
		
		File source = new File("C:\\Users\\yourUser\\Desktop\\source\\");
		File dest = new File("C:\\Users\\yourUser\\Desktop\\dest\\");
		try {
//			FileUtils.copyFile(source, dest);
		    FileUtils.copyDirectory(source, dest);
		    System.out.println("It's done!");
		} catch (IOException e) {
		    e.printStackTrace();
		}
        
	}
}
