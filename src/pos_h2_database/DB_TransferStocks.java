/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pos_h2_database;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import myUtilities.DatabaseFunctions;

public class DB_TransferStocks {
    private final String table = "table_transfer";
    
    private final String ID = "ID";
    private final String TS_ID = "TS_ID";
    private final String I_ID = "I_ID";
    private final String I_QUANTITY = "I_QUANTITY";
    private final String DATE = "DATE";
    private final String S_ID = "S_ID";
    
    private HashMap<String, StockTransfer> stockTransfer = new HashMap<>();
    private ArrayList<String> idList = new ArrayList<>();
    
    public DB_TransferStocks(){
        DatabaseFunctions dbf = new DatabaseFunctions();
        dbf.createTable(table, columnToKeys(true));
    }
    
    public void insertData(StockTransfer stockTransfer){
        DatabaseFunctions df = new DatabaseFunctions();
        df.insertData(table, columnToKeys(false), dataToKeys(stockTransfer, true));
    }
    
    public String[] dataToKeys(StockTransfer stockTransfer, boolean removeFirstItem){
        String[] keysWithId = {
            stockTransfer.getId(),
            stockTransfer.getStockTransferId(),
            stockTransfer.getItemId(),
            stockTransfer.getItemQuantity(),
            stockTransfer.getDate(),
            stockTransfer.getStoreId()
        };
        String[] keys = {
            stockTransfer.getStockTransferId(),
            stockTransfer.getItemId(),
            stockTransfer.getItemQuantity(),
            stockTransfer.getDate(),
            stockTransfer.getStoreId()
        };
        return removeFirstItem ? keys : keysWithId;
    }
    
    public String[] columnToKeys(boolean withAttr){
        DatabaseFunctions df = new DatabaseFunctions();
        
        String[] keys = {ID, TS_ID, I_ID, I_QUANTITY, DATE, S_ID};
        String[] keysWithAttr = {
            df.makeIntAttr(ID, false, false, true, true),
            df.makeIntAttr(TS_ID, false, false, false, false),
            df.makeIntAttr(I_ID, false, false, false, false),
            df.makeDoubleAttr(I_QUANTITY, false, false, false, false),
            df.makeVarcharAttr(DATE, 150, false),
            df.makeIntAttr(S_ID, false, false, false, false)
        };
        return withAttr ? keysWithAttr : keys;
    }
    
    public ArrayList<String> getIdList(){
        return idList;
    }
    
    public int getAvailableId(){
        DatabaseFunctions df = new DatabaseFunctions();
        String myQuery = "SELECT DISTINCT(" + TS_ID + ") as distID FROM " + table + " ORDER BY distID DESC LIMIT 1";
        String[] keys = {"distID"};
        
        HashMap<String, ArrayList> map = df.customReturnQuery(myQuery, keys);
        ArrayList<String> data = map.get("distID");
        
        if(data.size() <= 0)
            return 1;
        if(data.get(0) == null)
            return -1;
        
        return Integer.parseInt(data.get(0)) + 1;
    }
    
}
