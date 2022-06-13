
package myUtilities.system_utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import myUtilities.system_utilities.interfaces.DateTimeInterface;

public class DateTimeFunctions implements DateTimeInterface {
    
    @Override
    public String getCurrentDate(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    
    @Override
    public String getCurrentDateTime(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }
    
    @Override
    public String getCurrentDateTime(String format){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
    }
    
}
