package myUtilities;

/**
 * Just to make some functions centralized and easy to call.
 * 
 * @author KL_Schweizer
 */
public class SystemUtilities 
{
    
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
}
