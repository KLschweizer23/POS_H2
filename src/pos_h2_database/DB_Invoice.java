package pos_h2_database;

import java.util.ArrayList;
import java.util.HashMap;
import myUtilities.DatabaseFunctions;
import myUtilities.MyConnection;

public class DB_Invoice {
    
    private final String tablePrefix = "invTbl_";
    
    private final String ID = "ID";
    private final String ID_INVOICE = "ID_INVOICE";
    private final String DATE = "DATE";
    private final String STATUS = "STATUS";
    private final String I_ID = "I_ID";
    private final String I_NAME = "I_NAME";
    private final String I_BRAND = "I_BRAND";
    private final String I_ARTICLE = "I_ARTICLE";
    private final String I_QUANTITY = "I_QUANTITY";
    private final String I_PRICE = "I_PRICE";
    
    private HashMap<String, Invoice> invoice = new HashMap<>();
    private ArrayList<String> idList = new ArrayList<>();
    
    public ArrayList<String> getTables()
    {        
        DatabaseFunctions dbf = new DatabaseFunctions();
        MyConnection con = new MyConnection();
        
        String[] keys = {"tables"};
        
        String query = dbf.getTables(con.getSchemaName(), keys[0]) + dbf.and() + "TABLE_NAME " + dbf.like(tablePrefix);
        System.out.println(query);
        HashMap<String, ArrayList> map = dbf.customReturnQuery(query, keys);
        
        ArrayList<String> listOfTables = new ArrayList<>();
        
        for(int i = 0; i < (map.get(keys[0]) == null ? 0 : map.get(keys[0]).size()); i++)
            listOfTables.add(map.get(keys[0]).get(i).toString().substring(tablePrefix.length()));
        
        return listOfTables;
    }
    
    public HashMap<String, Invoice> processData(String invoiceName, boolean getUnpaid)
    {
        invoice.clear();
        idList.clear();
        
        DatabaseFunctions dbf = new DatabaseFunctions();
        String[] keys = columnToKeys(false);
        String query = dbf.selectAll() + dbf.from(tablePrefix + invoiceName) + dbf.whereEquals(STATUS, getUnpaid ? "Paid" : "Unpaid");
        
        HashMap<String, ArrayList> map = dbf.customReturnQuery(query, keys);
        for(int i = 0; i < (map.get(ID) == null ? 0 : map.get(ID).size());)
        {
            Invoice invoiceObj = new Invoice();
            String id = map.get(ID_INVOICE).get(i).toString();
            
            invoiceObj.setId(id);
            invoiceObj.setId_invoice(id);
            invoiceObj.setDate(map.get(DATE).get(i).toString());
            invoiceObj.setStatus(map.get(STATUS).get(i).toString());
            ArrayList<Item> listOfItems = new ArrayList<>();
            System.out.println(map.get(I_NAME).get(i).toString());
            i++;
            while(i < map.get(ID).size() && map.get(ID_INVOICE).get(i - 1).toString().equals(map.get(ID_INVOICE).get(i).toString()))
            {            
                Item item = new Item();
                item.setId(map.get(ID).get(i).toString());
                item.setName(map.get(I_NAME).get(i).toString());
                System.out.println(map.get(I_NAME).get(i).toString());
                item.setArticle(map.get(I_ARTICLE).get(i).toString());
                item.setBrand(map.get(I_BRAND).get(i).toString());
                item.setQuantity(map.get(I_QUANTITY).get(i).toString());
                item.setPrice(map.get(I_PRICE).get(i).toString());
                listOfItems.add(item);
                i++;
            }
            invoiceObj.setItem(listOfItems);
            invoice.put(id, invoiceObj);
            idList.add(id);
        }
        return invoice;
    }
    
    public void newCustomer(String customerName)
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        dbf.createTable(tablePrefix + customerName, columnToKeys(true));
    }
    
    public void deleteCustomer(String customerName)
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        dbf.dropTable(tablePrefix + customerName);
    }
    
    public void insertInvoice(String invoiceName, Invoice invoice)
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        dbf.insertData(tablePrefix + invoiceName, columnToKeys(false), dataToKeys(invoice, true));
    }
    
    public void updateInvoice(String invoiceName, Invoice invoice)
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        String[] myKeys = {ID_INVOICE, ID_INVOICE, DATE, STATUS, I_ID, I_NAME, I_BRAND, I_ARTICLE, I_QUANTITY, I_PRICE};
        dbf.updateData(tablePrefix + invoiceName, myKeys, dataToKeys(invoice, false));
    }
    
    public void deleteData(String invoiceName, boolean individualId, String id)
    {
        String deleteColumn = individualId ? ID : ID_INVOICE;
        DatabaseFunctions df = new DatabaseFunctions();
        df.deleteData(tablePrefix + invoiceName, deleteColumn, id);
    }
    
    public String getAvailableIDInvoice(String invoiceName)
    {
        DatabaseFunctions df = new DatabaseFunctions();
        String myQuery = "SELECT DISTINCT(" + ID_INVOICE + ") FROM " + tablePrefix + invoiceName + " ORDER BY " + ID_INVOICE + " ASC ";
        String[] keys = {ID_INVOICE};
        
        HashMap<String, ArrayList> data = df.customReturnQuery(myQuery, keys);
        ArrayList<String> idList = data.get(ID_INVOICE);
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
    
    public String[] dataToKeys(Invoice invoice, boolean removeFirstItem)
    {
        String[] keysWithId ={
            invoice.getId(),
            invoice.getId_invoice(),
            invoice.getDate(),
            invoice.getStatus(),
            invoice.getItem().get(0).getId(),
            invoice.getItem().get(0).getName(),
            invoice.getItem().get(0).getBrand(),
            invoice.getItem().get(0).getArticle(),
            invoice.getItem().get(0).getQuantity(),
            invoice.getItem().get(0).getPrice(),
        };
        String[] keys ={
            invoice.getId_invoice(),
            invoice.getDate(),
            invoice.getStatus(),
            invoice.getItem().get(0).getId(),
            invoice.getItem().get(0).getName(),
            invoice.getItem().get(0).getBrand(),
            invoice.getItem().get(0).getArticle(),
            invoice.getItem().get(0).getQuantity(),
            invoice.getItem().get(0).getPrice(),
        };
        return removeFirstItem ? keys : keysWithId;
    }
    public String[] columnToKeys(boolean withAttr)
    {
        DatabaseFunctions dbf = new DatabaseFunctions();
        
        String[] keys = {ID, ID_INVOICE, DATE, STATUS, I_ID, I_NAME, I_BRAND, I_ARTICLE, I_QUANTITY, I_PRICE};
        String[] keysWithAttr = 
        {
            dbf.makeIntAttr(ID, false, false, true, true),
            dbf.makeIntAttr(ID_INVOICE, false, false, false, false),
            dbf.makeVarcharAttr(DATE, 150, false),
            dbf.makeVarcharAttr(STATUS, 150, false),
            dbf.makeVarcharAttr(I_ID, 150, true),
            dbf.makeVarcharAttr(I_NAME, 150, true),
            dbf.makeVarcharAttr(I_BRAND, 150, true),
            dbf.makeVarcharAttr(I_ARTICLE, 150, true),
            dbf.makeVarcharAttr(I_QUANTITY, 150, true),
            dbf.makeVarcharAttr(I_PRICE, 150, true)
        };
        return withAttr ? keysWithAttr : keys;
    }
    public String getTablePrefix()
    {
        return tablePrefix;
    }

    public ArrayList<String> getIdList() {
        return idList;
    }
}
