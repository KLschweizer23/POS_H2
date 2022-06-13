

package myUtilities.system_utilities;

import myUtilities.MessageHandler;
import myUtilities.system_utilities.interfaces.UserInputInterface;

public class UserInput implements UserInputInterface{
    
    /**
     * 
     * @param message String to display.
     * @return Returns the inputted Number of User.
     */
    @Override
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
    @Override
    public String inputStringUser(String message)
    {            
        MessageHandler mh = new MessageHandler();
        
        String string;
        boolean pass;
        do
        {
            string = mh.input(message);
            if(string == null)
                pass = true;
            else
            {
                pass = !string.isBlank();
                if(pass)
                    return string;
            }
        }while(!pass);
        
        return string;
    }
    
    /**
     * 
     * @param message String to display.
     * @return Returns the inputted Password of User.
     */
    @Override
    public String inputPasswordUser(String message)
    {            
        MessageHandler mh = new MessageHandler();
        
        String password;
        boolean pass;
        do
        {
            password = mh.password(message);
            if(password == null)
                pass = true;
            else
            {
                pass = !password.isBlank();
                if(pass)
                    return password;
            }
        }while(!pass);
        
        return password;
    }
    
    /**
     * Checks whether a String can be a number.
     * 
     * @param string The string to check.
     * @return returns {@code true} if string can be a number and {@code false} if not.
     */
    @Override
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
    
}
