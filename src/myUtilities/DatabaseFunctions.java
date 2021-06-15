
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
    MessageHandler mh = new MessageHandler();
    
    private Connection getConnection()
    {
        MyConnection myCon = new MyConnection();
        return myCon.get();
    }
    
    /**
     * 
     * @param tableName Name for table to create eg. <i>SampleTable</i>.
     * @param columnAndAttr Columns with their Attributes in an array String format.<br>
     * <i>e.g.</i><br>
     * array[0] = "ID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY"
     * array[1] = "NAME VARCHAR(150) NOT NULL"
     * ...
     * array[nth] = "..."
     */
    public void createTable(String tableName, String[] columnAndAttr)
    {
        String create = "CREATE TABLE IF NOT EXISTS " + tableName + "(" + arrayToString(columnAndAttr, false) + ")";
        try
        {
            executeQuery(create);
        }catch(SQLException sqlE){mh.error("There was an error creating The Table! Error Message: " + sqlE);}
    }
    
    /**
     * 
     * @param tableName Name of the table to insert.
     * @param column Name of Columns of the table in an array String format.
     * @param data Data to insert in table in an array String format.
     */
    public void insertData(String tableName, String[] column, String[] data)
    {
        if(column.length == data.length + 1)
        {
            String insert = "INSERT INTO " + tableName + "(" + arrayToString(column, true) + ") VALUES (" + arrayToString(data, false) + ");";
            try
            {
                executeQuery(insert);
            }catch(SQLException sqlE){mh.error("There was an error inserting into the Table '" + tableName + "'! Error Message: " + sqlE);}
        }else mh.error("Column size and Data size doesn't match. There should only be a difference of one (1). column: " + column.length + "; data: " + data.length);
    }
    
    /**
     * 
     * @param tableName Name of the table to update.
     * @param column Name of Columns of the table in an array String format.
     * @param data Data to update in table in an array String format.
     * <br><b>NOTE:</b><br>
     * Include ID at the very first item of both array and will be use as basis of the WHERE condition:<br>
     * <u>" WHERE " + column[0] + " = " + data[0]</u>
     */
    public void updateData(String tableName, String[] column, String[] data)
    {
        if(column.length == data.length)
        {
            String condition = whereEquals(column[0], data[0]);
            
            String update = "UPDATE " + tableName + " SET " + arrayToStringSetter(column, data) + condition;
            try
            {
                executeQuery(update);
            }catch(SQLException sqlE){mh.error("There was an error updating the Table '" + tableName + "'! Error Message: " + sqlE);}
        }else mh.error("Column size and Data size doesn't match. There should be no difference. column: " + column.length + "; data: " + data.length);
    }    
    
    
    public void deleteData(String tableName, String column, String data)
    {
        String condition = whereEquals(column, data);
        
        String delete = "DELETE FROM " + tableName + condition;
        try
        {
            executeQuery(delete);
        }catch(SQLException sqlE){mh.error("There was an error deleting data in the Table '" + tableName + "'! Error Message: " + sqlE);}
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
    
    /**
     * 
     * @param result ResultSet from database to perform operation.
     * @param keys Get Column Names to retrieve in database.
     * @return Returns an array of ArrayList of the data.
     * @throws SQLException 
     */
    private ArrayList[] returnKeyLists(ResultSet result, String[] keys) throws SQLException
    {
        ArrayList[] array = new ArrayList[keys.length];//Arrayed ArrayList with a length of how many keys was passed
        
        while(result.next()) // moves cursor through rows
        {
            for (String key : keys) {
                array[0].add(result.getString(key));
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

    /**
     * 
     * @param array The String in array format to process.
     * @param removeFirstItem either removes the first item or not.<br>
     * <i>TRUE</i> - Removes the first item in array.<br>
     * <i>FALSE</i> - Does not remove the first item in array.
     * @return Returns a single string of the given array.
     */
    private String arrayToString(String[] array, boolean removeFirstItem)
    {
        String returnVal = "";
        
        for(int i = removeFirstItem ? 1 : 0; i < array.length; i++)
        {
            returnVal += "," + array[i];
        }
        
        returnVal = returnVal.substring(1);
        
        return returnVal;
    }
    
    /**
     * 
     * @param column Column of table for reference.
     * @param data Data to be updated
     * @return Returns a single String for SET in updating a table.
     */
    private String arrayToStringSetter (String[] column, String[] data)
    {
        String[] setData = new String[column.length];
        for(int i = 0; i < column.length; i++)
        {
            setData[i] = column[i] + " = '" + data[i] + "'";
        }
        String settedString = arrayToString(setData, true);
        
        return settedString;
    }
    
    
    //-----------------------SIMPLE OOP FUNCTIONS FOR DATBASE--------------------\\
    /**
     * 
     * @param column Column from table for the condition.
     * @param data Data to be used for the condition.
     * @return Returns a string for where equal condition
     */
    public String whereEquals (String column, String data)
    {
        return " WHERE " + column + " = '" + data + "'";
    }
    
    /**
     * 
     * @param column Column from table for the condition.
     * @param data Data to be used for the condition.
     * @return Returns a string for where not equal condition
     */
    public String whereNotEquals (String column, String data)
    {
        return " WHERE " + column + " != '" + data + "'";
    }
    
    /**
     * 
     * @param column Column from table for the condition.
     * @param data Data to be used for the condition.
     * @return Returns a string for and equal condition
     */
    public String andEquals (String column, String data)
    {
        return " AND " + column + " = '" + data + "'";
    }    
    
    /**
     * 
     * @param column Column from table for the condition.
     * @param data Data to be used for the condition.
     * @return Returns a string for and not equal condition
     */
    public String andNotEquals (String column, String data)
    {
        return " AND " + column + " != '" + data + "'";
    }
}
