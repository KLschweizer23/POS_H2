package pos_h2_database;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import myUtilities.DatabaseFunctions;

public class DB_Item {
    
    private final String table = "table_item";
    
    private final String ID = "ID";
    private final String NAME = "ITEM_NAME";
    private final String BRAND = "BRAND";
    private final String ARTICLE = "ARTICLE";
    private final String QUANTITY = "QUANTITY";
    private final String PRICE = "PRICE";
    private final String SOLD = "SOLD";
    
    private HashMap<String, Item> item = new HashMap<>();
    private ArrayList<String> idList = new ArrayList<>();
    
    public DB_Item()
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        dbf.createTable(table, columnToKeys(true));
    }
    
    public HashMap<String, Item> processData(String keyword, int colIndex)
    {
        item.clear();
        idList.clear();
        
        DatabaseFunctions dbf = new DatabaseFunctions();
        String[] keys = columnToKeys(false);
        
        HashMap<String, ArrayList> map = dbf.selectAllData(table, keys, keyword, colIndex, NAME, true);
        for(int i = 0; i < (map.get(ID) == null ? 0 : map.get(ID).size()); i++)
        {
            Item itemObject = new Item();
            String id = map.get(ID).get(i).toString();
            
            itemObject.setId(id);
            itemObject.setName(map.get(NAME).get(i).toString());
            itemObject.setBrand(map.get(BRAND).get(i).toString());
            itemObject.setArticle(map.get(ARTICLE).get(i).toString());
            itemObject.setQuantity(map.get(QUANTITY).get(i).toString());
            itemObject.setPrice(map.get(PRICE).get(i).toString());
            itemObject.setSold(map.get(SOLD).get(i).toString());
            
            item.put(id, itemObject);
            idList.add(id);
        }
        return item;
    }
    
    public void insertData(Item item)
    {
        DatabaseFunctions df = new DatabaseFunctions();
        df.insertData(table, columnToKeys(false), dataToKeys(item, true));
    }
    
    public void updateData(Item item)
    {
        DatabaseFunctions df = new DatabaseFunctions();
        df.updateData(table, columnToKeys(false), dataToKeys(item, false));
    }
    
    public void deleteData(Item item)
    {
        DatabaseFunctions df = new DatabaseFunctions();
        df.deleteData(table, ID, item.getId());
    }
    
    public String[] dataToKeys(Item item, boolean removeFirstItem)
    {
        String[] keysWithId ={
            item.getId(),
            item.getName(), 
            item.getBrand(), 
            item.getArticle(), 
            item.getQuantity(), 
            item.getPrice(),
            item.getSold()
        };
        String[] keys ={
            item.getName(), 
            item.getBrand(), 
            item.getArticle(), 
            item.getQuantity(), 
            item.getPrice(),
            item.getSold()
        };
        return removeFirstItem ? keys : keysWithId;
    }
    public String[] columnToKeys(boolean withAttr)
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        
        String[] keys = {ID, NAME, BRAND, ARTICLE, QUANTITY, PRICE, SOLD};
        String[] keysWithAttr = 
        {
            dbf.makeIntAttr(ID, false, false, true, true),
            dbf.makeVarcharAttr(NAME, 150, false),
            dbf.makeVarcharAttr(BRAND, 150, false),
            dbf.makeVarcharAttr(ARTICLE, 150, false),
            dbf.makeIntAttr(QUANTITY, false, false, false, false),
            dbf.makeDoubleAttr(PRICE, false, false, false, false),
            dbf.makeIntAttr(SOLD, false, false, false, false)
        };
        return withAttr ? keysWithAttr : keys;
    }
    
    public ArrayList<String> getIdList() {
        return idList;
    }
    
    public ArrayList<String> getAllIdList(){
        ArrayList<String> idList = new ArrayList<>();
        
        DatabaseFunctions df = new DatabaseFunctions();
        String myQuery = "SELECT " + ID + " FROM " + table;
        String[] keys = {ID};
        
        HashMap<String, ArrayList> data = df.customReturnQuery(myQuery, keys);
        ArrayList<String> clerkList = data.get(ID);
        if(clerkList.get(0) == null || clerkList.size() <= 0)
            return idList;
        idList = clerkList;
        
        return idList;
    }
    
    public String getNameById(String id){
        DatabaseFunctions df = new DatabaseFunctions();
        String myQuery = "SELECT " + NAME + " FROM " + table + " WHERE " + ID + " = " + id;
        String[] keys = {NAME};
        
        HashMap<String, ArrayList> data = df.customReturnQuery(myQuery, keys);
        ArrayList<String> dataList = data.get(NAME);
        if(dataList.get(0) == null || dataList.size() <= 0)
            return null;
        return dataList.get(0);
    }
    
    public String getBrandById(String id){
        DatabaseFunctions df = new DatabaseFunctions();
        String myQuery = "SELECT " + BRAND + " FROM " + table + " WHERE " + ID + " = " + id;
        String[] keys = {BRAND};
        
        HashMap<String, ArrayList> data = df.customReturnQuery(myQuery, keys);
        ArrayList<String> dataList = data.get(BRAND);
        if(dataList.get(0) == null || dataList.size() <= 0)
            return null;
        return dataList.get(0);
    }
    
    public String getArticleById(String id){
        DatabaseFunctions df = new DatabaseFunctions();
        String myQuery = "SELECT " + ARTICLE + " FROM " + table + " WHERE " + ID + " = " + id;
        String[] keys = {ARTICLE};
        
        HashMap<String, ArrayList> data = df.customReturnQuery(myQuery, keys);
        ArrayList<String> dataList = data.get(ARTICLE);
        if(dataList.get(0) == null || dataList.size() <= 0)
            return null;
        return dataList.get(0);
    }
    
    public Double getLeftById(String id, String currentDate, boolean isYesterday){
        DatabaseFunctions df = new DatabaseFunctions();
        
        DB_Transaction db_transaction = new DB_Transaction();
        DB_Invoice db_invoice = new DB_Invoice();
        DB_TransferStocks db_transferStocks = new DB_TransferStocks();
        
        LocalDate date = LocalDate.parse(currentDate);
        
        String myQuery = "SELECT " + QUANTITY + " FROM " + table + " WHERE " + ID + " = " + id;
        String[] keys = {QUANTITY};
        
        Double result = 0.0;
        HashMap<String, ArrayList> data = df.customReturnQuery(myQuery, keys);
        ArrayList<String> dataList = data.get(QUANTITY);
        if(dataList.get(0) == null || dataList.size() <= 0)
            return 0.0;
        
        result = Double.parseDouble(dataList.get(0));
        if(isYesterday){
            String dateToVoidItem = date.plus(1, ChronoUnit.DAYS).toString();
            Double sold = db_transaction.getSoldById(id, dateToVoidItem);
            Double invoiced = db_invoice.getInvoicedItemById(id, dateToVoidItem);
            Double transferred = db_transferStocks.getTransfferedStockById(id, dateToVoidItem);
            
            result += sold + invoiced + transferred;
        }
        return result;
    }
}