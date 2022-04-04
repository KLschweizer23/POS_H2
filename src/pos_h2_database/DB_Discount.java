package pos_h2_database;

import java.util.ArrayList;
import java.util.HashMap;
import myUtilities.DatabaseFunctions;

public class DB_Discount {
    
    private final String table = "table_discount";
    
    private final String ID = "ID";
    private final String NAME = "NAME";
    private final String VALUE = "VALUE";
    
    private HashMap<String, Discount> discounts = new HashMap<>();
    private ArrayList<String> idList = new ArrayList<>();
    
    public DB_Discount(){
        DatabaseFunctions dbf = new DatabaseFunctions();
        dbf.createTable(table, columnToKeys(true));
    }
    
    public HashMap<String, Discount> processData(String keyword, int colIndex){
        discounts.clear();
        idList.clear();
        
        DatabaseFunctions dbf = new DatabaseFunctions();
        String[] keys = columnToKeys(false);
        
        HashMap<String, ArrayList> map = dbf.selectAllData(table, keys, keyword, colIndex, NAME, true);
        for(int i = 0; i < (map.get(ID) == null ? 0 : map.get(ID).size()); i++){
            Discount discount = new Discount();
            String id = map.get(ID).get(i).toString();
            
            discount.setId(id);
            discount.setName(map.get(NAME).get(i).toString());
            discount.setValue(map.get(VALUE).get(i).toString());
            
            discounts.put(id, discount);
            idList.add(id);
        }
        return discounts;
    }
    
    public void insertData(Discount discount){
        DatabaseFunctions df = new DatabaseFunctions();
        df.insertData(table, columnToKeys(false), dataToKeys(discount, true));
    }
    
    public void updateData(Discount discount)
    {
        DatabaseFunctions df = new DatabaseFunctions();
        df.updateData(table, columnToKeys(false), dataToKeys(discount, false));
    }
    
    public void deleteData(Discount discount)
    {
        DatabaseFunctions df = new DatabaseFunctions();
        df.deleteData(table, VALUE, NAME);
        df.deleteData(table, ID, discount.getId());
    }
    
    public String[] dataToKeys(Discount discount, boolean removeFirstItem)
    {
        String[] keysWithId ={
            discount.getId(),
            discount.getName(), 
            discount.getValue()
        };
        String[] keys ={
            discount.getName(), 
            discount.getValue()
        };
        return removeFirstItem ? keys : keysWithId;
    }
    
    public String[] columnToKeys(boolean withAttr)
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        
        String[] keys = {ID, NAME, VALUE};
        String[] keysWithAttr = 
        {
            dbf.makeIntAttr(ID, false, false, true, true),
            dbf.makeVarcharAttr(NAME, 255, false),
            dbf.makeDoubleAttr(VALUE, false, false, false, false),
        };
        return withAttr ? keysWithAttr : keys;
    }

    public ArrayList<String> getIdList() {
        return idList;
    }
}
