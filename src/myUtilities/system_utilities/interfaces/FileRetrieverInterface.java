
package myUtilities.system_utilities.interfaces;

import java.io.File;

public interface FileRetrieverInterface {
    
    String getSingleStringFromFile(File file);
    
    String getSingleStringFromFile(String fileName);
    
    String getFile(File file);
    
}
