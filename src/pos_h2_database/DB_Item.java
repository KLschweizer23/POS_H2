package pos_h2_database;

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
        
        HashMap<String, ArrayList> map = dbf.selectAllData(table, keys, keyword, colIndex, NAME);
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
}