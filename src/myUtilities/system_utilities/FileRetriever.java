
package myUtilities.system_utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import myUtilities.MyConnection;
import myUtilities.system_utilities.interfaces.FileRetrieverInterface;

public class FileRetriever implements FileRetrieverInterface{
    
    @Override
    public String getSingleStringFromFile(File file){
        return getFile(file);
    }
    
    @Override
    public String getSingleStringFromFile(String fileName){
        return getFile(new File(fileName));
    }
    
    @Override
    public String getFile(File file){        
        String singleString = null;
        try {
            Scanner reader = new Scanner(file);
            while(reader.hasNextLine())
                singleString = reader.nextLine();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MyConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return singleString;
    }
    
}
