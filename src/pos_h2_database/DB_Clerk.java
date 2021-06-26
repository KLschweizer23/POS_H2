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
    private final String USER = "USER";
    private final String PASSWORD = "PASSWORD";
    
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
            String user = map.get(USER).get(i).toString();
            String pass = map.get(PASSWORD).get(i).toString();
            
            clerkObject.setFirstname(fName);
            clerkObject.setMiddlename(mName);
            clerkObject.setLastname(lName);
            
            clerkObject.setUser(user);
            clerkObject.setPassword(pass);
            
            String name = lName + "." + fName.charAt(0) + "." + mName.charAt(0) + "." + id;
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
            clerk.getLastname(),
            clerk.getUser(),
            clerk.getPassword()
        };
        String[] keys ={
            clerk.getFirstname(),
            clerk.getMiddlename(),
            clerk.getLastname(),
            clerk.getUser(),
            clerk.getPassword()
        };
        return removeFirstClerk ? keys : keysWithId;
    }
    
    public String[] columnToKeys(boolean withAttr)
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        
        String[] keys = {ID, FIRSTNAME, MIDDLENAME, LASTNAME, USER, PASSWORD};
        String[] keysWithAttr = 
        {
            dbf.makeIntAttr(ID, false, false, true, true),
            dbf.makeVarcharAttr(FIRSTNAME, 150, false),
            dbf.makeVarcharAttr(MIDDLENAME, 150, false),
            dbf.makeVarcharAttr(LASTNAME, 150, false),
            dbf.makeVarcharAttr(USER, 150, false),
            dbf.makeVarcharAttr(PASSWORD, 150, false)
        };
        return withAttr ? keysWithAttr : keys;
    }
    
    public Clerk checkPassword(String user, char[] password)
    {
        processData("", 0);
        boolean accountExist = true;
        String id = "";
        for(int i = 0; i < clerk.size(); i++)
        {
            accountExist = true;
            id = idList.get(i);
            if(clerk.get(id).getPassword().length() == password.length && clerk.get(id).getUser().equals(user))
                for(int j = 0; j < password.length; j++)
                    if(password[j] != clerk.get(idList.get(i)).getPassword().charAt(j))
                    {
                        System.out.println(password[j] + " != " + clerk.get(idList.get(i)).getPassword().charAt(j));
                        accountExist = false;
                    }
                    else
                        ;
            else
                accountExist = false;
        }
        if(accountExist)
        {
            System.out.println(clerk.get(id).getName());
            return clerk.get(id);
        }
        else
        {
            System.out.println("NOTHING");
            return null;
        }
    }
    
    public ArrayList<String> getIdList() {
        return idList;
    }

    public ArrayList<String> getNameList() {
        return nameList;
    }
            
}
