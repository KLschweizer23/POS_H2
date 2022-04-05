package pos_h2_database;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import myUtilities.DatabaseFunctions;
/**
 *
 * @author KL_Schweizer
 */
public class DB_Store {
    public final String table = "table_store";
    
    private final String ID = "ID";
    private final String NAME = "NAME";
    private final String DETAILS = "DETAILS";
    
    private HashMap<String, Store> store = new HashMap<>();
    private ArrayList<String> idList = new ArrayList<>();
    
    public DB_Store(){
        DatabaseFunctions dbf = new DatabaseFunctions();
        dbf.createTable(table, columnToKeys(true));
    }
    
    public HashMap<String, Store> processData(String keyword, int colIndex){
        store.clear();
        idList.clear();
        
        DatabaseFunctions df = new DatabaseFunctions();
        String[] keys = columnToKeys(false);
        
        HashMap<String, ArrayList> map = df.selectAllData(table, keys, keyword, colIndex, NAME, true);
        for(int i = 0; i < (map.get(ID) == null ? 0 : map.get(ID).size()); i++){
            Store storeObject = new Store();
            String id = map.get(ID).get(i).toString();
            
            storeObject.setId(id);
            storeObject.setName(map.get(NAME).get(i).toString());
            storeObject.setDetails(map.get(DETAILS).get(i).toString());
            
            store.put(id, storeObject);
            idList.add(id);
        }
        return store;
    }
    
    public void insertData(Store store){
        DatabaseFunctions df = new DatabaseFunctions();
        df.insertData(table, columnToKeys(false), dataToKeys(store, true));
    }
    
    public void updateData(Store store){
        DatabaseFunctions df = new DatabaseFunctions();
        df.updateData(table, columnToKeys(false), dataToKeys(store, false));
    }
    
    public void deleteData(Store store){
        DatabaseFunctions df = new DatabaseFunctions();
        df.deleteData(table, ID, store.getId());
    }
    
    public String[] columnToKeys(boolean withAttr){
        DatabaseFunctions df = new DatabaseFunctions();
        
        String[] keys = {ID, NAME, DETAILS};
        String[] keysWithAttr = {
            df.makeIntAttr(ID, false, false, true, true),
            df.makeVarcharAttr(NAME, 255, false),
            df.makeVarcharAttr(DETAILS, 255, false)
        };
        return withAttr ? keysWithAttr : keys;
    }
    
    public String[] dataToKeys(Store store, boolean removeFirstItem){
        String[] keysWithId = {
            store.getId(),
            store.getName(),
            store.getDetails()
        };
        String[] keys = {
            store.getName(),
            store.getDetails()
        };
        return removeFirstItem ? keys : keysWithId;
    }
    
    public ArrayList<String> getIdList(){
        return idList;
    }
    
}
