package pos_h2_database;

import java.util.ArrayList;
import java.util.HashMap;
import myUtilities.DatabaseFunctions;

public class DB_Clerk {
    
    private final String table = "table_clerk";
    
    private final String ID = "ID";
    private final String FIRSTNAME = "FIRSTNAME";
    private final String MIDDLENAME = "MIDDLENAME";
    private final String LASTNAME = "LASTNAME";
    
    private HashMap<String, Clerk> clerk = new HashMap<>();
    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<String> nameList = new ArrayList<>();

    public DB_Clerk()
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        dbf.createTable(table, columnToKeys(true));
    }
    
    public HashMap<String, Clerk> processData(String keyword, int colIndex)
    {
        clerk.clear();
        idList.clear();
        nameList.clear();
        
        DatabaseFunctions dbf = new DatabaseFunctions();
        String[] keys = columnToKeys(false);
        
        HashMap<String, ArrayList> map = dbf.selectAllData(table, keys, keyword, colIndex, FIRSTNAME);
        for(int i = 0; i < (map.get(ID) == null ? 0 : map.get(ID).size()); i++)
        {
            Clerk clerkObject = new Clerk();
            String id = map.get(ID).get(i).toString();
            
            clerkObject.setId(id);
            String fName = map.get(FIRSTNAME).get(i).toString();
            String mName = map.get(MIDDLENAME).get(i).toString();
            String lName = map.get(LASTNAME).get(i).toString();
            
            clerkObject.setFirstname(fName);
            clerkObject.setMiddlename(mName);
            clerkObject.setLastname(lName);
            
            String name = lName + "." + fName.charAt(0) + "." + lName.charAt(0) + "." + id;
            
            clerkObject.setName(name);
            
            clerk.put(id, clerkObject);
            idList.add(id);
            nameList.add(name);
        }
        return clerk;
    }

    public void insertData(Clerk clerk)
    {
        DatabaseFunctions df = new DatabaseFunctions();
        df.insertData(table, columnToKeys(false), dataToKeys(clerk, true));
    }
    
    public void updateData(Clerk clerk)
    {
        DatabaseFunctions df = new DatabaseFunctions();
        df.updateData(table, columnToKeys(false), dataToKeys(clerk, false));
    }
    
    public void deleteData(Clerk clerk)
    {
        DatabaseFunctions df = new DatabaseFunctions();
        df.deleteData(table, ID, clerk.getId());
    }
    
    public String[] dataToKeys(Clerk clerk, boolean removeFirstClerk)
    {
        String[] keysWithId ={
            clerk.getId(),
            clerk.getFirstname(),
            clerk.getMiddlename(),
            clerk.getLastname()
        };
        String[] keys ={
            clerk.getFirstname(),
            clerk.getMiddlename(),
            clerk.getLastname()
        };
        return removeFirstClerk ? keys : keysWithId;
    }
    
    public String[] columnToKeys(boolean withAttr)
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        
        String[] keys = {ID, FIRSTNAME, MIDDLENAME, LASTNAME};
        String[] keysWithAttr = 
        {
            dbf.makeIntAttr(ID, false, false, true, true),
            dbf.makeVarcharAttr(FIRSTNAME, 150, false),
            dbf.makeVarcharAttr(MIDDLENAME, 150, false),
            dbf.makeVarcharAttr(LASTNAME, 150, false)
        };
        return withAttr ? keysWithAttr : keys;
    }
    public ArrayList<String> getIdList() {
        return idList;
    }

    public ArrayList<String> getNameList() {
        return nameList;
    }
            
}
