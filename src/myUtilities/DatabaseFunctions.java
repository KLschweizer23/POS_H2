
package myUtilities;

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
        DisplayMessage dm = new DisplayMessage();
        String create = "CREATE TABLE IF NOT EXISTS " + tableName + "(" + colAndAttr + ")";
        //executeQuery(create);
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
     * executeReturnQuery concept is to return a list of data of every columns received.
     * 
     * @param query Query to execute.
     * @param column column should be same to Column Names 
     * 
     * @return Returns HashMap with your keys(column) and values as ArrrayList
     * 
     * @throws SQLException Throws the Origin of Exception.
     */
    public HashMap executeReturnQuery(String query, String[] column) throws SQLException
    {
        Connection con = getConnection();
        PreparedStatement execQuery = con.prepareStatement(query);
        ResultSet result = execQuery.executeQuery();
        
        ArrayList[] array = this.returnKeyLists(result, column);
        HashMap map = new HashMap();
        
        for(int i = 0; i < column.length; i++)
        {
            map.put(column[i], array[i]);
        }
        
        con.close();
        
        return map;
    }
    
    private ArrayList[] returnKeyLists(ResultSet result, String[] keys) throws SQLException
    {
        ArrayList[] array = new ArrayList[keys.length];//Arrayed ArrayList with a length of how many keys was passed
        
        while(result.next()) // moves cursor through rows
        {
            for(int i = 0; i < keys.length; i++)
            {
                array[0].add(result.getString(keys[i]));
                /*
                 * 1st array will get the 1st key column on row i
                 * 2nd array will get the 2nd key column on row i
                 * ...
                 * nth array will get the nth key column on row i
                 *
                 * then repeats the process at the next row.
                 */
            }
        }
        return array;
    }
}
