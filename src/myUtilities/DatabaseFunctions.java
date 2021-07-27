
package myUtilities;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * The DatabaseFunctions Class is to help creating small SQL Codes
 * divided into functions.<br> 
 * This class utilizes {@code HashMap<K, V>} for easier retrieval of data with the given keys.<br>
 * Keys can be columns, which is also use to retrieve data for {@code ResultSet} data.
 * 
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
     * @param   tableName           Name for table to create eg. <i>SampleTable</i>.
     * @param   columnAndAttr       Columns with their Attributes in an array String format.<br>
     *                              <i>e.g.</i><br>
     *                              array[0] = "ID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY"<br>
     *                              array[1] = "NAME VARCHAR(150) NOT NULL"<br>
     *                              ...<br>
     *                              array[nth] = "..."<br>
     */
    public void createTable(String tableName, String[] columnAndAttr)
    {
        String create = "CREATE TABLE IF NOT EXISTS " + tableName + "(" + arrayToString(columnAndAttr, false, false, false) + ")";
        try
        {
            executeQuery(create);
        }catch(SQLException sqlE){mh.error(manageErrorMessage("There was an error creating the table " + tableName + "!", sqlE.toString()));}
    }
    
    /**
     * 
     * @param   tableName           Name of table to Drop.
     */
    public void dropTable(String tableName)
    {
        String drop = "DROP TABLE " + tableName;
        try
        {
            executeQuery(drop);
        }catch(SQLException sqlE){mh.error(manageErrorMessage("There was an error deleting the table " + tableName + "!", sqlE.toString()));}
    }
    
    /**
     * 
     * @param   tableName           Name of the table to insert.
     * @param   column              Name of Columns of the table in an array String format.
     * @param   data                Data to insert in table in an array String format.
     */
    public void insertData(String tableName, String[] column, String[] data)
    {
        if(column.length == data.length + 1)
        {
            String insert = "INSERT INTO " + tableName + "(" + arrayToString(column, true, false, false) + ") VALUES (" + arrayToString(data, false, true, true) + ");";
            try
            {
                executeQuery(insert);
            }catch(SQLException sqlE){mh.error(manageErrorMessage("There was an error inserting into the Table " + tableName + "!", sqlE.toString()));}
        }else mh.error("<html>Column size and Data size doesn't match. There should only be a difference of one (1) where column should be greater than data.<br>Column: " + column.length + "; Data: " + data.length + "</html>");
    }
    
    /**
     * 
     * @param   tableName           Name of the table to update.
     * @param   column              Name of Columns of the table in an array String format.
     * @param   data                Data to update in table in an array String format.
     *                              <br><b>NOTE:</b><br>
     *                              Include ID at the very first item of both array and will be use as basis of the WHERE condition:<br>
     *                              {@code " WHERE " + column[0] + " = " + data[0]}
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
            }catch(SQLException sqlE){mh.error(manageErrorMessage("There was an error updating the Table " + tableName + "!", sqlE.toString()));}
        }else mh.error("Column size and Data size doesn't match. There should be no difference. column: " + column.length + "; data: " + data.length);
    }    
    
    /**
     * 
     * @param   tableName           Name of the table to delete data.
     * @param   column              Name of Column of the table for condition.
     * @param   data                data to be deleted that is on the column on {@code column}.
     */
    public void deleteData(String tableName, String column, String data)
    {
        String condition = whereEquals(column, data);
        
        String delete = "DELETE FROM " + tableName + condition;
        try
        {
            executeQuery(delete);
        }catch(SQLException sqlE){mh.error(manageErrorMessage("There was an error deleting data in the Table " + tableName + "!", sqlE.toString()));}
    }
    
    /**
     * 
     * @param   table               Table to retrieve Data.
     * @param   column              Columns of Table to retrieve Data.
     * @param   filterKey           String to filter retrieving data. "" If none.
     * @param   columnIndexFilter   Index of {@code column} to get column to filter data.
     * @param   orderBy             Orders the Data with the given column name.
     * @param   isAscending         If {@code true} sorts data in ascending order, descending if {@code false}.
     * @return                      Returns a HashMap with keys as your column and values as ArrayList of data in columns.
     */
    public HashMap selectAllData(String table, String[] column, String filterKey, int columnIndexFilter, String orderBy, boolean isAscending)
    {
        HashMap<String, ArrayList> map = new HashMap();
        String ascOrDesc = isAscending ? "ASC" : "DESC";
        String query = "SELECT * FROM " + table + " WHERE " + column[columnIndexFilter] + " LIKE '%" + goodString(filterKey) + "%' ORDER BY " + orderBy + " " + ascOrDesc;
        try
        {
            map = executeReturnQuery(query, column);
        }catch(SQLException sqlE){mh.error(manageErrorMessage("There was an error Retrieving Data from Database -> '" + query + "'!", sqlE.toString()));}
        return map;
    }
    
    /**
     * 
     * @param   query               Your query to execute.
     * @param   keyName             Name of column and also to retrieve data on ResultSet
     * @return                      Returns a {@code HashMap<String, ArrayList>} with the data on ArrayList and key as {@code keyName}.
     */
    public HashMap customReturnQuery(String query, String[] keyName)
    {
        HashMap<String, ArrayList> map = new HashMap<>();
        try
        {
            map = executeReturnQuery(query, keyName);
        }catch(SQLException sqlE){mh.error(manageErrorMessage("There was an error performing this query ->'" + query + "'!", sqlE.toString()));}
        
        return map;
    }
    
    /**
     * 
     * @param   query               Query to execute.
     * @throws                      SQLException Throws the Origin of Exception.
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
     * @param   query               Query to execute.
     * @param   column              column should be same to Column Names 
     * 
     * @return                      Returns HashMap with your keys(column) and values as ArrrayList
     * 
     * @throws                      SQLException Throws the Origin of Exception.
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
     * @param   result              ResultSet from database to perform operation.
     * @param   keys                Get Column Names to retrieve in database.
     * @return                      Returns an array of ArrayList of the data.
     * @throws                      SQLException 
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
     * @param   array               The String in array format to process.
     * @param   removeFirstItem     either removes the first item or not.<br>
     *                              {@code TRUE} - Removes the first item in array.<br>
     *                              {@code FALSE} - Does not remove the first item in array.
     * @param   isData              Surrounds ' if {@code array} is a data.
     * @param   isBadString         Whether Data is safe for sql query or not.
     *                              {@code TRUE} - Adds a escape sequence for apostrophes. <br>
     *                              {@code FALSE} - Original Data.
     * @return                      Returns a single string of the given array.
     */
    private String arrayToString(String[] array, boolean removeFirstItem, boolean isData, boolean isBadString)
    {
        String returnVal = "";
        String isDataChar = isData ? "'" : "";
        
        for(int i = removeFirstItem ? 1 : 0; i < array.length; i++)
        {
            returnVal += "," + isDataChar + (isBadString ? goodString(array[i]) : array[i]) + isDataChar;
        }
        
        returnVal = returnVal.substring(1);
        
        return returnVal;
    }
    
    /**
     * 
     * @param   column              Column of table for reference.
     * @param   data                Data to be updated
     * @return                      Returns a single String for SET in updating a table.
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
     * @param   data                Data to process.
     * @return                      Returns a String for safety SQL Execution.
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
     * @param   data                Data for the LIKE clause
     * @return                      Returns a LIKE clause
     */
    public String like (String data)
    {
        return " LIKE '%" + goodString(data) + "%' ";
    }
    
    
    /**
     * 
     * @return                      Returns a WHERE clause
     */
    public String where()
    {
        return " WHERE ";
    }
    
    /**
     * 
     * @param   column              Column for WHERE clause
     * @return                      Returns a WHERE clause
     */
    public String where(String column)
    {
        return " WHERE " + column + " ";
    }
    
    /**
     * 
     * @param   column              Column from table for the condition.
     * @param   data                Data to be used for the condition.
     * @return                      Returns a string for where equal condition
     */
    public String whereEquals (String column, String data)
    {
        return " WHERE " + column + " = '" + goodString(data) + "' ";
    }
    
    /**
     * 
     * @param   column              Column from table for the condition.
     * @param   data                Data to be used for the condition.
     * @return                      Returns a string for where not equal condition
     */
    public String whereNotEquals (String column, String data)
    {
        return " WHERE " + column + " != '" + goodString(data) + "' ";
    }
    
    /**
     * 
     * @return                      Returns a string for {@code AND} condition.
     */
    public String and ()
    {
        return "AND ";
    }    
    
    /**
     * 
     * @param   column              Column from table for the condition.
     * @param   data                Data to be used for the condition.
     * @return                      Returns a string for and equal condition
     */
    public String andEquals (String column, String data)
    {
        return "AND " + column + " = '" + goodString(data) + "' ";
    }    
    
    /**
     * 
     * @param   column              Column from table for the condition.
     * @param   data                Data to be used for the condition.
     * @return                      Returns a string for and not equal condition
     */
    public String andNotEquals (String column, String data)
    {
        return "AND " + column + " != '" + goodString(data) + "' ";
    }
  
    /**
     * 
     * @param   keys                Columns you want to select in a String array format.
     * @return                      Returns a string for execution.
     */
    public String select(String[] keys)
    {
        return "SELECT " + arrayToString(keys, false, false, true) + " ";
    } 
    
    /**
     * 
     * @param   schemaName          Name of Schema to get tables.
     * @param   alias               Name or Alias for the column.
     *                              <br><br>
     *                              <b>NOTE:</b> Add an {@code AND} condition to filter tables.
     *                              <br><br>
     * @return Returns a string for execution.
     */
    public String getTables(String schemaName, String alias)
    {
        return "SELECT TABLE_NAME AS '" + alias + "' FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + schemaName + "' ";
    }
    
    /**
     * 
     * @return                      Returns a string for execution.
     */
    public String selectAll()
    {
        return "SELECT * ";
    } 
    
    /**
     * 
     * @param   selectString        Table to select.
     * @param   asName              Alias of the column.
     * @return                      Return as a String.
     */
    public String selectSingleAs(String selectString, String asName)
    {
        return "SELECT " + selectString + " as " + asName + " ";
    }
    
    /**
     * 
     * @param   tableName           Table to select.
     * @return                      Returns a string for execution.
     */
    public String from(String tableName)
    {
        return "FROM " + tableName + " ";
    }
    
    /**
     * 
     * @param   firstParam          First Parameter for between function.
     * @param   secondParam         Second Parameter for between function.
     * @return                      Returns a string for execution.
     */
    public String between(String firstParam, String secondParam)
    {
        return "BETWEEN '" + firstParam + "' AND '" + secondParam + "' ";
    }
    
    /**
     * 
     * @param   column              Name of Column.
     * @param   varSize             Size of Varchar with a range of 0-255.
     * @param   isNull              If column data can be Null or Not Null<br>
     *                              <b>TRUE</b> - Then column can have a Null Value;
     *                              <b>FALSE</b> - Then column cannot have a Null Value;
     * @return                      Returns a String.
     */
    public String makeVarcharAttr(String column, int varSize, boolean isNull)
    {
        String makeNull = isNull ? "NULL" : " NOT NULL ";
        
        String colAndAttr = column + " VARCHAR(" + varSize + ") " + makeNull;
        
        return colAndAttr;
    }
    
    /**
     * 
     * @param   column              Name of Column.
     * @param   isSigned            Is Column Signed.
     * @param   isNull              Can column contain null values.
     * @param   autoIncrement       If this column increments automatically.
     * @param   isPrimary           Is Column a primary key.
     * @return                      Returns a String.
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
     * @param   column              Name of Column.
     * @param   isSigned            Is Column Signed.
     * @param   isNull              Can column contain null values.
     * @param   autoIncrement       If this column increments automatically.
     * @param   isPrimary           Is Column a primary key.
     * @return                      Returns a String.
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
    //--------------------------END-------------------------\\
    /**
     * 
     * @param   array               Array to modify and remove first item.
     * @return                      Returns the same array with it's first item removed.
     */
    private String[] removeFirstItem(String[] array)
    {
        String[] newArray = new String[array.length - 1];
        for(int i = 1; i < array.length; i++)
            newArray[i - 1] = array[i];
        return newArray;
    }
    
    /**
     * Recommended for JOptionPane, It contains html tags to allow multiple lines.
     * 
     * @param   firstMessage        Personal Message.
     * @param   exceptionMessage    Message of the exception to manage.
     * @return                      Returns a String with {@code <html>} tags to create multiple lines.
     */
    private String manageErrorMessage(String firstMessage, String exceptionMessage)
    {
        int half = exceptionMessage.length() / 2;
        
        String returnString = "<html>" + firstMessage + "<br><b>Error:</b><br>" + 
                exceptionMessage.substring(0, half) + "<br>" +
                exceptionMessage.substring(half) + "</html>";
        
        return returnString;
    }
}