package pos_h2_database;

import java.util.ArrayList;
import java.util.HashMap;
import myUtilities.DatabaseFunctions;

public class DB_Transaction {
    private final String table = "table_transaction";
    
    private final String ID = "ID";
    private final String T_ID = "T_ID";
    private final String T_CLERK = "T_Clerk";
    private final String DATE = "DATE";
    private final String I_ID = "I_ID";
    private final String I_QUANTITY = "I_QUANTITY";
    private final String I_PRICE = "I_PRICE";
    
    private HashMap<String, Transaction> transaction = new HashMap<>();
    private ArrayList<String> idList = new ArrayList<>();
    
    public DB_Transaction()
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        dbf.createTable(table, columnToKeys(true));
    }
    
    public HashMap<String, Transaction> processData(String keyword, int colIndex)
    {
        transaction.clear();
        idList.clear();
        
        DatabaseFunctions dbf = new DatabaseFunctions();
        String[] keys = columnToKeys(false);
        
        HashMap<String, ArrayList> map = dbf.selectAllData(table, keys, keyword, colIndex, T_ID);
        for(int i = 0; i < (map.get(ID) == null ? 0 : map.get(ID).size()); i++ )
        {
            Transaction transactionObject = new Transaction();
            String id = map.get(ID).get(i).toString();
            
            transactionObject.setId(id);
            transactionObject.setT_id(map.get(T_ID).get(i).toString());
            transactionObject.setT_clerk(map.get(T_CLERK).get(i).toString());
            transactionObject.setDate(map.get(DATE).get(i).toString());
            ArrayList<Item> listOfItems = new ArrayList<>();
            do
            {            
                Item item = new Item();
                item.setId(map.get(I_ID).get(i).toString());
                item.setQuantity(map.get(I_QUANTITY).get(i).toString());
                item.setPrice(map.get(I_PRICE).get(i).toString());
                listOfItems.add(item);
                i++;
            }while(map.get(T_ID).get(i - 1).toString().equals(map.get(T_ID).get(i).toString()));
            transactionObject.setItem(listOfItems);
            transaction.put(id, transactionObject);
            idList.add(id);
        }
        return transaction;
    }
    
    public void insertData(Transaction transaction)
    {
        DatabaseFunctions df = new DatabaseFunctions();
        df.insertData(table, columnToKeys(false), dataToKeys(transaction, true));
    }
    
    public String getAvailableTID()
    {
        DatabaseFunctions df = new DatabaseFunctions();
        String myQuery = "SELECT DISTINCT(" + T_ID + ") FROM " + table + " ORDER BY " + T_ID + " ASC ";
        String[] keys = {T_ID};
        
        HashMap<String, ArrayList> data = df.customQuery(myQuery, keys);
        ArrayList<String> idList = data.get(T_ID);
        int availableValue = getAvailableValue(idList);
        return availableValue + "";
    }
    
    private int getAvailableValue(ArrayList<String> list)
    {
        int lastNum = Integer.parseInt(list.get(list.size() - 1));
        list.add(null);
        int availableNum = 0;
        
        for(int i = 1; i <= lastNum + 1; i++)
        {
            availableNum = i;
            if(list.get(i - 1) != null)
            {
                if(availableNum != Integer.parseInt(list.get(i - 1)))
                    return availableNum;
            }
            else
            {
                return availableNum;
            }
        }
        return availableNum;
    }
    
    public String[] dataToKeys(Transaction transaction, boolean removeFirstItem)
    {
        String[] keysWithId ={
            transaction.getId(),
            transaction.getT_id(), 
            transaction.getT_clerk(), 
            transaction.getDate(),
            transaction.getItem().get(0).getId(),
            transaction.getItem().get(0).getQuantity(),
            transaction.getItem().get(0).getPrice(),
        };
        String[] keys ={
            transaction.getT_id(), 
            transaction.getT_clerk(), 
            transaction.getDate(),
            transaction.getItem().get(0).getId(),
            transaction.getItem().get(0).getQuantity(),
            transaction.getItem().get(0).getPrice(),
        };
        return removeFirstItem ? keys : keysWithId;
    }
    public String[] columnToKeys(boolean withAttr)
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        
        String[] keys = {ID, T_ID, T_CLERK, DATE, I_ID, I_QUANTITY, I_PRICE};
        String[] keysWithAttr = 
        {
            dbf.makeIntAttr(ID, false, false, true, true),
            dbf.makeIntAttr(T_ID, false, false, false, false),
            dbf.makeVarcharAttr(T_CLERK, 150, false),
            dbf.makeVarcharAttr(DATE, 150, false),
            dbf.makeIntAttr(I_ID, false, false, false, false),
            dbf.makeIntAttr(I_QUANTITY, false, false, false, false),
            dbf.makeDoubleAttr(I_PRICE, false, false, false, false)
        };
        return withAttr ? keysWithAttr : keys;
    }
    
    public ArrayList<String> getIdList() {
        return idList;
    }
}