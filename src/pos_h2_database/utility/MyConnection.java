package pos_h2_database.utility;

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
    private final String schemaName = "POS_SCHEMA";
    
    private final String errorTitle = "Connection Error!";
    private final String errorMsg = "There was a problem connecting to database. Error Message:";
    
    public Connection get()
    {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/" + schemaName;
        String username = "root";
        String password = "umtc";
        Connection con = null;
        try
        {
            Class.forName(driver);
            con = DriverManager.getConnection(url,username,password);
        }catch(ClassNotFoundException | SQLException e){JOptionPane.showMessageDialog(null, errorMsg + e, errorTitle, JOptionPane.ERROR_MESSAGE);}
        return con;
    }
    
}
