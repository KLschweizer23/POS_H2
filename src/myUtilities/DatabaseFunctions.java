
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
     * array[0] = "ID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY"<br>
     * array[1] = "NAME VARCHAR(150) NOT NULL"<br>
     * ...<br>
     * array[nth] = "..."<br>
     */
    public void createTable(String tableName, String[] columnAndAttr)
    {
        String create = "CREATE TABLE IF NOT EXISTS " + tableName + "(" + arrayToString(columnAndAttr, false, false, false) + ")";
        try
        {
            executeQuery(create);
        }catch(SQLException sqlE){mh.error("<html>There was an error creating The Table!<br><b>Error:</b><br>"
                + sqlE.toString().substring(0, sqlE.toString().length() / 2) + "<br>"
                + sqlE.toString().substring(sqlE.toString().length()/ 2) + "<br>"
                + "</html>");}
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
            String insert = "INSERT INTO " + tableName + "(" + arrayToString(column, true, false, false) + ") VALUES (" + arrayToString(data, false, true, true) + ");";
            try
            {
                System.out.println(insert);
                executeQuery(insert);
            }catch(SQLException sqlE){mh.error("There was an error inserting into the Table '" + tableName + "'! Error Message: " + sqlE);}
        }else mh.error("<html>Column size and Data size doesn't match. There should only be a difference of one (1) where column should be greater than data.<br>Column: " + column.length + "; Data: " + data.length + "</html>");
    }
    
    /**
     * 
     * @param tableName Name of the table to update.
     * @param column Name of Columns of the table in an array String format.
     * @param data Data to update in table in an array String format.
     * <br><b>NOTE:</b><br>
     * Include ID at the very first item of both array and will be use as basis of the WHERE condition:<br>
     * {@code " WHERE " + column[0] + " = " + data[0]}
     */
    public void updateData(String tableName, String[] column, String[] data)
    {
        if(column.length == data.length)
        {
            String condition = whereEquals(column[0], data[0]);
            System.out.println(condition);
            String update = "UPDATE " + tableName + " SET " + arrayToStringSetter(column, data) + condition;
            try
            {
                System.out.println(update);
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
        }catch(SQLException sqlE){mh.error("<html>There was an error deleting data in the Table '" + tableName + "'! <br><b>Error Message:</b><br> " + sqlE + "</html>");}
    }
    
    /**
     * 
     * @param table Table to retrieve Data.
     * @param column Columns of Table to retrieve Data.
     * @param filterKey String to filter retrieving data. "" If none.
     * @param columnIndexFilter Index of {@code column} to get column to filter data.
     * @param orderBy Orders the Data with the given column name.
     * @return Returns a HashMap with keys as your column and values as ArrayList of data in columns.
     */
    public HashMap selectAllData(String table, String[] column, String filterKey, int columnIndexFilter, String orderBy)
    {
        HashMap<String, ArrayList> map = new HashMap();
        String query = "SELECT * FROM " + table + " WHERE " + column[columnIndexFilter] + " LIKE '%" + goodString(filterKey) + "%' ORDER BY " + orderBy;
        try
        {
            map = executeReturnQuery(query, column);
        }catch(SQLException sqlE){mh.error("There was an error Retrieving Data from Database -> '" + query + "'! Error Message: " + sqlE);}
        return map;
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
        HashMap<String, ArrayList> map = new HashMap();
        
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
        
        for(int i = 0; i < keys.length; i++)
        {
            array[i] = new ArrayList<String>();
        }
        
        while(result.next()) // moves cursor through rows
        {
            for (int i = 0; i < keys.length; i++) {
                array[i].add(result.getString(keys[i]));
                /*
                 * 1st array will get the 1st key column on row i
                 * 2nd array will get the 2nd key column on row i
                 * ...
                 * nth array i will get the nth key column on row i
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
     * {@code TRUE} - Removes the first item in array.<br>
     * {@code FALSE} - Does not remove the first item in array.
     * @param isData Surrounds ' if {@code array} is a data.
     * @param isBadString Whether Data is safe for sql query or not.
     * {@code TRUE} - Adds a escape sequence for apostrophes. <br>
     * {@code FALSE} - Original Data.
     * @return Returns a single string of the given array.
     */
    private String arrayToString(String[] array, boolean removeFirstItem, boolean isData, boolean isBadString)
    {
        String returnVal = "";
        String isDataChar = isData ? "'" : "";
        
        for(int i = removeFirstItem ? 1 : 0; i < array.length; i++)
        {
            System.out.println(returnVal);
            returnVal += "," + isDataChar + (isBadString ? goodString(array[i]) : array[i]) + isDataChar;
            System.out.println(returnVal + " - f");
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
            setData[i] = column[i] + " = '" + goodString(data[i]) + "'";
        }
        String settedString = arrayToString(setData, true, false, false);
        
        return settedString;
    }
    
    /**
     * 
     * @param data Data to process.
     * @return Returns a String for safety SQL Execution.
     */
    private String goodString(String data)
    {
        if(data == null)
            return null;
        
        String temp = data.replaceAll("'", "\\\\'");
        return temp;
    }
    
    //-----------------------SIMPLE OOP FUNCTIONS FOR DATBASE--------------------\\
    
    /**
     * 
     * @param data Data for the LIKE clause
     * @return Returns a LIKE clause
     */
    public String likeEquals (String data)
    {
        return " LIKE %" + goodString(data) + "%";
    }
    
    /**
     * 
     * @param column Column for WHERE clause
     * @return Returns a WHERE clause
     */
    public String where(String column)
    {
        return " WHERE " + column;
    }
    
    /**
     * 
     * @param column Column from table for the condition.
     * @param data Data to be used for the condition.
     * @return Returns a string for where equal condition
     */
    public String whereEquals (String column, String data)
    {
        return " WHERE " + column + " = '" + goodString(data) + "' ";
    }
    
    /**
     * 
     * @param column Column from table for the condition.
     * @param data Data to be used for the condition.
     * @return Returns a string for where not equal condition
     */
    public String whereNotEquals (String column, String data)
    {
        return " WHERE " + column + " != '" + goodString(data) + "' ";
    }
    
    /**
     * 
     * @param column Column from table for the condition.
     * @param data Data to be used for the condition.
     * @return Returns a string for and equal condition
     */
    public String andEquals (String column, String data)
    {
        return "AND " + column + " = '" + goodString(data) + "'";
    }    
    
    /**
     * 
     * @param column Column from table for the condition.
     * @param data Data to be used for the condition.
     * @return Returns a string for and not equal condition
     */
    public String andNotEquals (String column, String data)
    {
        return "AND " + column + " != '" + goodString(data) + "'";
    }
  
    /**
     * 
     * @param keys Columns you want to select in a String array format.
     * @return Returns a string for execution.
     */
    public String select(String[] keys)
    {
        return "SELECT " + arrayToString(keys, false, false, true) + " ";
    } 
    
    /**
     * 
     * @return Returns a string for execution.
     */
    public String selectAll()
    {
        return "SELECT * ";
    } 
    
    /**
     * 
     * @param selectString
     * @param asName
     * @return 
     */
    public String selectSingleAs(String selectString, String asName)
    {
        return "SELECT " + selectString + " as " + asName + " ";
    }
    
    /**
     * 
     * @param tableName Table to select.
     * @return Returns a string for execution.
     */
    public String from(String tableName)
    {
        return "FROM " + tableName;
    }
    
    /**
     * 
     * @param column Name of Column.
     * @param varSize Size of Varchar with a range of 0-255.
     * @param isNull If column data can be Null or Not Null<br>
     * <b>TRUE</b> - Then column can have a Null Value;
     * <b>FALSE</b> - Then column cannot have a Null Value;
     * @return 
     */
    public String makeVarcharAttr(String column, int varSize, boolean isNull)
    {
        String makeNull = isNull ? "NULL" : " NOT NULL ";
        
        String colAndAttr = column + " VARCHAR(" + varSize + ") " + makeNull;
        
        return colAndAttr;
    }
    
    /**
     * 
     * @param column Name of Column.
     * @param isSigned Is Column Signed.
     * @param isNull Can column contain null values.
     * @param autoIncrement If this column increments automatically.
     * @param isPrimary Is Column a primary key.
     * @return 
     */
    public String makeIntAttr(String column, boolean isSigned, boolean isNull, boolean autoIncrement, boolean isPrimary)
    {
        
        String makeNull = isNull ? "NULL" : " NOT NULL ";
        String makeSigned = isSigned ? "SIGNED" : "UNSIGNED";
        String makePrimary = isPrimary ? "PRIMARY KEY" : "";
        String increment = autoIncrement ? "AUTO_INCREMENT" : "" ;
        
        String colAndAttr = column + " INTEGER " + makeSigned + " " + makeNull + " " + increment + " " + makePrimary;
                
        return colAndAttr;
    }    
    
    /**
     * 
     * @param column Name of Column.
     * @param isSigned Is Column Signed.
     * @param isNull Can column contain null values.
     * @param autoIncrement If this column increments automatically.
     * @param isPrimary Is Column a primary key.
     * @return 
     */
    public String makeDoubleAttr(String column, boolean isSigned, boolean isNull, boolean autoIncrement, boolean isPrimary)
    {
        
        String makeNull = isNull ? "NULL" : " NOT NULL ";
        String makeSigned = isSigned ? "SIGNED" : "UNSIGNED";
        String makePrimary = isPrimary ? "PRIMARY KEY" : "";
        String increment = autoIncrement ? "AUTO_INCREMENT" : "" ;
        
        String colAndAttr = column + " INTEGER " + makeSigned + " " + makeNull + " " + increment + " " + makePrimary;
                
        return colAndAttr;
    }
    
    /**
     * 
     * @param array Array to modify and remove first item.
     * @return Returns the same array with it's first item removed.
     */
    private String[] removeFirstItem(String[] array)
    {
        String[] newArray = new String[array.length - 1];
        for(int i = 1; i < array.length; i++)
            newArray[i - 1] = array[i];
        return newArray;
    }
}
