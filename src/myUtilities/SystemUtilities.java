package myUtilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Just to make some functions centralized and easy to call.
 * 
 * @author KL_Schweizer
 */
public class SystemUtilities 
{
    //-----------------------------USER FUNCTIONS--------------------------\\
    
    /**
     * 
     * @param message String to display.
     * @return Returns the inputted Number of User.
     */
    public String inputNumberUser(String message)
    {            
        MessageHandler mh = new MessageHandler();
        
        String quantity;
        boolean pass;
        do
        {
            quantity = mh.input(message);
            if(quantity == null)
                pass = true;
            else
            {
                pass = isANumber(quantity);
                if(pass)
                    return quantity;
            }
        }while(!pass);
        
        return quantity;
    }
    
    /**
     * 
     * @param message String to display.
     * @return Returns the inputted String of User.
     */
    public String inputStringUser(String message)
    {            
        MessageHandler mh = new MessageHandler();
        
        String quantity;
        boolean pass;
        do
        {
            quantity = mh.input(message);
            if(quantity == null)
                pass = true;
            else
            {
                pass = !quantity.isBlank();
                if(pass)
                    return quantity;
            }
        }while(!pass);
        
        return quantity;
    }
    
    //----------------------------STRING FUNCTIONS-------------------------\\
    /**
     * Checks whether a String can be a number.
     * 
     * @param string The string to check.
     * @return returns {@code true} if string can be a number and {@code false} if not.
     */
    public boolean isANumber(String string) 
    {
        boolean valid = false;
        if (string != null) 
        {
            valid = (string.length() > 0);
            int dot = 0;
            for (int i = 0; i < string.length(); i++) {
                switch (string.charAt(i)) 
                {
                    case '.':case '0':case '1':case '2':case '3':case '4':case '5':
                    case '6':case '7':case '8':case '9':
                    if (string.charAt(i) == '.')
                        dot++; 
                    break;
                    default:
                        valid = false;
                    break;
                } 
            } 
        if (dot > 1)
            valid = false; 
        } 
        return valid;
    }
    
    //----------------------------TIME FUNCTIONS-------------------------\\
    /**
     * Gets Current Local Time
     * 
     * @return Returns current local time in "{@code yyyy-MM-dd HH:mm:ss.SSS}" format.
     */
    public String getCurrentDateTime()
    {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }
}
