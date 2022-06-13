package myUtilities;

import myUtilities.system_utilities.SystemUtilities;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
/**
 *
 * @author KL_Schweizer
 */
public class MyConnection 
{
    private final String schemaName = "POS_schema"; //Change this with the name of your schema
    
    private final String errorTitle = "Connection Error!";//Change this with your Title Message Error
    private final String errorMsg = "There was a problem connecting to database. Error Message:";//Change this with your Message Error
    
    public Connection get()
    {
        SystemUtilities su = new SystemUtilities();
        
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://" + su.getSingleStringFromFile("config.txt") + ":3306/" + schemaName;
        String username = "root";
        String password = "umtc";
        Connection con = null;
        try
        {
            Class.forName(driver);
            con = DriverManager.getConnection(url,username,password);
        }catch(ClassNotFoundException | SQLException e){JOptionPane.showMessageDialog(null, errorMsg + e, errorTitle, JOptionPane.ERROR_MESSAGE);System.exit(0);}
        return con;
    }
    
    public String getSchemaName()
    {
        return schemaName;
    }
}
