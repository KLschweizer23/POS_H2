
package pos_h2_database.utility;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * The DatabaseFunctions Class is to help creating small SQL Codes
 * divided into functions. 
 * <br><br>
 * <b>NOTE:</b> Create your Schema First.
 * <br><br>
 * 
 * @author KL_Schweizer
 * @version 1.0
 * @since 2021-06-15
 */
public class DatabaseFunctions 
{
    
    private Connection getConnection()
    {
        MyConnection myCon = new MyConnection();
        return myCon.get();
    }
    
    /**
     * 
     * @param tableName Name for table to create eg. <i>SampleTable</i>.
     * @param colAndAttr Columns with their Attributes as a String ready for execution.
     */
    public void createTable(String tableName, String colAndAttr)
    {
        String create = "CREATE TABLE IF NOT EXISTS " + tableName + "(" + colAndAttr + ")";
        
    }
    
    /**
     * 
     * @param query Query to execute.
     * @throws SQLException Throws the Origin of Exception.
     */
    public void executeQuery(String query) throws SQLException
    {
        Connection con = getConnection();
        PreparedStatement execQuery = con.prepareStatement(query);
        execQuery.executeUpdate();
        con.close();
    }    
    /**
     * executeReturnQuery
     * 
     * @param query Query to execute.
     * @param keys Keys should be equal to Column Names 
     * 
     * @return Returns HashMap with your keys and values as ArrrayList
     * 
     * @throws SQLException Throws the Origin of Exception.
     */
    public HashMap executeReturnQuery(String query, String[] keys) throws SQLException
    {
        Connection con = getConnection();
        PreparedStatement execQuery = con.prepareStatement(query);
        ResultSet result = execQuery.executeQuery();
        
        ArrayList[] array = returnKeyLists(result, keys);
        HashMap map = new HashMap();
        
        for(int i = 0; i < keys.length; i++)
        {
            map.put(keys[i], array[0]);
        }
        
        con.close();
        
        return map;
    }
    
    private ArrayList[] returnKeyLists(ResultSet result, String[] keys) throws SQLException
    {
        ArrayList[] array = new ArrayList[keys.length];
        
        while(result.next())
        {
            for(int i = 0; i < keys.length; i++)
            {
                array[0].add(result.getString(keys[i]));
            }
        }
        return array;
    }
}
