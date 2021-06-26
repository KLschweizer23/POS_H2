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
    private final String I_NAME = "I_NAME";
    private final String I_ARTICLE = "I_ARTICLE";
    private final String I_BRAND = "I_BRAND";
    private final String I_QUANTITY = "I_QUANTITY";
    private final String I_PRICE = "I_PRICE";
    private final String TOTAL_AMOUNT = "TOTAL_AMOUNT";
    private final String PAYMENT = "PAYMENT";
    
    private HashMap<String, Transaction> transaction = new HashMap<>();
    private ArrayList<String> idList = new ArrayList<>();
    
    public DB_Transaction()
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        dbf.createTable(table, columnToKeys(true));
    }
    
    public HashMap<String, Transaction> processData()
    {
        transaction.clear();
        idList.clear();
        
        DatabaseFunctions dbf = new DatabaseFunctions();
        String[] keys = columnToKeys(false);
        
        HashMap<String, ArrayList> map = dbf.selectAllData(table, keys, "", 0, T_ID, true);
        for(int i = 0; i < (map.get(ID) == null ? 0 : map.get(ID).size());)
        {
            Transaction transactionObject = new Transaction();
            String id = map.get(T_ID).get(i).toString();
            
            transactionObject.setId(id);
            transactionObject.setT_id(id);
            transactionObject.setT_clerk(map.get(T_CLERK).get(i).toString());
            transactionObject.setDate(map.get(DATE).get(i).toString());
            transactionObject.setTotalAmount(map.get(TOTAL_AMOUNT).get(i).toString());
            transactionObject.setPayment(map.get(PAYMENT).get(i).toString());
            ArrayList<Item> listOfItems = new ArrayList<>();
            do
            {            
                Item item = new Item();
                item.setId(map.get(I_ID).get(i).toString());
                item.setName(map.get(I_NAME).get(i).toString());
                item.setArticle(map.get(I_ARTICLE).get(i).toString());
                item.setBrand(map.get(I_BRAND).get(i).toString());
                item.setQuantity(map.get(I_QUANTITY).get(i).toString());
                item.setPrice(map.get(I_PRICE).get(i).toString());
                listOfItems.add(item);
                i++;
            }while(i < map.get(ID).size() && map.get(T_ID).get(i - 1).toString().equals(map.get(T_ID).get(i).toString()));
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
        
        HashMap<String, ArrayList> data = df.customReturnQuery(myQuery, keys);
        ArrayList<String> idList = data.get(T_ID);
        int availableValue = getAvailableValue(idList);
        return availableValue + "";
    }
    
    private int getAvailableValue(ArrayList<String> list)
    {
        if(list.size() > 0)
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
        } else return 1;
    }
    
    public String[] dataToKeys(Transaction transaction, boolean removeFirstItem)
    {
        String[] keysWithId ={
            transaction.getId(),
            transaction.getT_id(), 
            transaction.getT_clerk(), 
            transaction.getDate(),
            transaction.getItem().get(0).getId(),
            transaction.getItem().get(0).getName(),
            transaction.getItem().get(0).getArticle(),
            transaction.getItem().get(0).getBrand(),
            transaction.getItem().get(0).getQuantity(),
            transaction.getItem().get(0).getPrice(),
            transaction.getTotalAmount(),
            transaction.getPayment()
        };
        String[] keys ={
            transaction.getT_id(), 
            transaction.getT_clerk(), 
            transaction.getDate(),
            transaction.getItem().get(0).getId(),
            transaction.getItem().get(0).getName(),
            transaction.getItem().get(0).getArticle(),
            transaction.getItem().get(0).getBrand(),
            transaction.getItem().get(0).getQuantity(),
            transaction.getItem().get(0).getPrice(),
            transaction.getTotalAmount(),
            transaction.getPayment()
        };
        return removeFirstItem ? keys : keysWithId;
    }
    public String[] columnToKeys(boolean withAttr)
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        
        String[] keys = {ID, T_ID, T_CLERK, DATE, I_ID, I_NAME, I_ARTICLE, I_BRAND, I_QUANTITY, I_PRICE, TOTAL_AMOUNT, PAYMENT};
        String[] keysWithAttr = 
        {
            dbf.makeIntAttr(ID, false, false, true, true),
            dbf.makeIntAttr(T_ID, false, false, false, false),
            dbf.makeVarcharAttr(T_CLERK, 150, false),
            dbf.makeVarcharAttr(DATE, 150, false),
            dbf.makeIntAttr(I_ID, false, false, false, false),
            dbf.makeVarcharAttr(I_NAME, 150, false),
            dbf.makeVarcharAttr(I_ARTICLE, 150, false),
            dbf.makeVarcharAttr(I_BRAND, 150, false),
            dbf.makeIntAttr(I_QUANTITY, false, false, false, false),
            dbf.makeDoubleAttr(I_PRICE, false, false, false, false),
            dbf.makeDoubleAttr(TOTAL_AMOUNT, false, false, false, false),
            dbf.makeDoubleAttr(PAYMENT, false, false, false, false)
        };
        return withAttr ? keysWithAttr : keys;
    }
    
    public ArrayList<String> getIdList() {
        return idList;
    }
}