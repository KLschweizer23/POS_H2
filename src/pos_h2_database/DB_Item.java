package pos_h2_database;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;
import myUtilities.DatabaseFunctions;

public class DB_Item {
    
    private final String table = "table_item";
    
    private final String ID = "ID";
    private final String NAME = "NAME";
    private final String BRAND = "BRAND";
    private final String ARTICLE = "ARTICLE";
    private final String QUANTITY = "QUANTITY";
    private final String PRICE = "PRICE";
    private final String SOLD = "SOLD";
    
    HashMap<String, Item> item = new HashMap<>();
    
    public DB_Item()
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        dbf.createTable(table, columnToKeys(true));
    }
    
    public HashMap<String, Item> processData()
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        String[] keys = columnToKeys(false);
        
        HashMap<String, ArrayList> map = dbf.selectAllData(table, keys);
        
        for(int i = 0; i < map.get("ID").size(); i++)
        {
            Item itemObject = new Item();
            itemObject.setId(map.get("ID").get(i).toString());
            itemObject.setName(map.get("NAME").get(i).toString());
            itemObject.setBrand(map.get("BRAND").get(i).toString());
            itemObject.setArticle(map.get("ARTICLE").get(i).toString());
            itemObject.setQuantity(map.get("QUANTITY").get(i).toString());
            itemObject.setPrice(map.get("PRICE").get(i).toString());
            itemObject.setSold(map.get("SOLD").get(i).toString());
            
            item.put(map.get("ID").get(i).toString(), itemObject);
        }
        return item;
    }
    
    private String[] columnToKeys(boolean withAttr)
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
}
