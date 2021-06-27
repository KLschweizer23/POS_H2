package pos_h2_database;

import java.util.ArrayList;
import java.util.HashMap;
import myUtilities.DatabaseFunctions;

public class DB_Login {
        
    private final String table = "table_login";
    
    private final String ID = "ID";
    private final String SALES_CLERK = "SALES_CLERK";
    private final String STATUS = "STATUS";
    private final String TIME_IN = "TIME_IN";
    private final String TIME_OUT = "TIME_OUT";
    
    private HashMap<String, Log> logs = new HashMap<>();
    private ArrayList<String> idList = new ArrayList<>();
    
    public DB_Login()
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        dbf.createTable(table, columnToKeys(true));
    }
    
    public HashMap<String, Log> processData(String keyword, int colIndex)
    {
        logs.clear();
        idList.clear();
        
        DatabaseFunctions dbf = new DatabaseFunctions();
        String[] keys = columnToKeys(false);
        
        HashMap<String, ArrayList> map = dbf.selectAllData(table, keys, keyword, colIndex, ID, true);
        for(int i = 0; i < (map.get(ID) == null ? 0 : map.get(ID).size()); i++)
        {
            Log log = new Log();
            String id = map.get(ID).get(i).toString();
            
            log.setId(id);
            log.setSalesClerk(map.get(SALES_CLERK).get(i).toString());
            log.setStatus(map.get(STATUS).get(i).toString());
            log.setTimeIn(map.get(TIME_IN).get(i).toString());
            log.setTimeOut(map.get(TIME_OUT).get(i).toString());
            logs.put(id, log);
            idList.add(id);
        }
        return logs;
    }
    
    public void insertData(Log log)
    {
        DatabaseFunctions df = new DatabaseFunctions();
        df.insertData(table, columnToKeys(false), dataToKeys(log, true));
    }
    
    public void updateData(Log log)
    {
        DatabaseFunctions df = new DatabaseFunctions();
        df.updateData(table, columnToKeys(false), dataToKeys(log, false));
    }
    
    public void deleteData(Log log)
    {
        DatabaseFunctions df = new DatabaseFunctions();
        df.deleteData(table, ID, log.getId());
    }
    
    public String[] dataToKeys(Log log, boolean removeFirstItem)
    {
        String[] keysWithId ={
            log.getId(),
            log.getSalesClerk(),
            log.getStatus(),
            log.getTimeIn(),
            log.getTimeOut()
        };
        String[] keys ={
            log.getSalesClerk(),
            log.getStatus(),
            log.getTimeIn(),
            log.getTimeOut()
        };
        return removeFirstItem ? keys : keysWithId;
    }
    public String[] columnToKeys(boolean withAttr)
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        
        String[] keys = {ID, SALES_CLERK, STATUS, TIME_IN, TIME_OUT};
        String[] keysWithAttr = 
        {
            dbf.makeIntAttr(ID, false, false, true, true),
            dbf.makeVarcharAttr(SALES_CLERK, 150, false),
            dbf.makeVarcharAttr(STATUS, 150, false),
            dbf.makeVarcharAttr(TIME_IN, 150, true),
            dbf.makeVarcharAttr(TIME_OUT, 150, true)
        };
        return withAttr ? keysWithAttr : keys;
    }
    
    public ArrayList<String> getIdList() {
        return idList;
    }
}
