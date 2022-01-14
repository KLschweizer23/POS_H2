package pos_h2_database;

import extraClasses.ChartData;
import extraClasses.InventoryObject;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import myUtilities.DatabaseFunctions;
import myUtilities.MessageHandler;

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
    
    public HashMap<String, Transaction> processData(String fromDate, String toDate)
    {
        transaction.clear();
        idList.clear();
        
        DatabaseFunctions dbf = new DatabaseFunctions();
        String[] keys = columnToKeys(false);
        HashMap<String, ArrayList> map;
        if(fromDate == null && toDate == null)
        {
            map = dbf.selectAllData(table, keys, "", 0, T_ID, true);
        }
        else
        {
            String query = dbf.selectAll() + dbf.from(table) + dbf.where(DATE) + dbf.between(fromDate, toDate);
            map = dbf.customReturnQuery(query, keys);
        }
        
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
    
    public String getTotalSales(String fromDate, String toDate){
        DatabaseFunctions df = new DatabaseFunctions();
        String myQuery = "SELECT " + TOTAL_AMOUNT + " FROM " + table + " WHERE " + DATE + " >= '" + fromDate + "' AND " + DATE + " <= '" + toDate + "' GROUP BY " + T_ID + ";";
        String[] keys = {TOTAL_AMOUNT};
        
        HashMap<String, ArrayList> data = df.customReturnQuery(myQuery, keys);
        ArrayList<String> dataList = data.get(TOTAL_AMOUNT);
        double total = 0.0;
        for(int i = 0; i < dataList.size(); i++)
            total += Double.parseDouble(dataList.get(i).toString());
        return total + "";
    }
    
    public String getTotalItemsSold(String fromDate, String toDate){
        DatabaseFunctions df = new DatabaseFunctions();
        String myQuery = "SELECT SUM(" + I_QUANTITY + ") as total FROM " + table + " WHERE " + DATE + " >= '" + fromDate + "' AND " + DATE + " <= '" + toDate + "'";
        String[] keys = {"total"};
        
        HashMap<String, ArrayList> data = df.customReturnQuery(myQuery, keys);
        ArrayList<String> dataList = data.get("total");
        System.out.println(dataList);
        if(dataList.get(0) == null || dataList.size() <= 0)
            return "0.0";
        return dataList.get(0);
    }
    
    public String getTotalTransactions(String fromDate, String toDate){
        DatabaseFunctions df = new DatabaseFunctions();
        String myQuery = "SELECT COUNT(DISTINCT(" + T_ID + ")) as total FROM " + table + " WHERE " + DATE + " >= '" + fromDate + "' AND " + DATE + " <= '" + toDate + "'";
        String[] keys = {"total"};
        
        HashMap<String, ArrayList> data = df.customReturnQuery(myQuery, keys);
        ArrayList<String> dataList = data.get("total");
        if(dataList.get(0) == null || dataList.size() <= 0)
            return "0.0";
        return dataList.get(0);
    }
    
    public String getAverageAmountPerTransactions(String fromDate, String toDate){
        DatabaseFunctions df = new DatabaseFunctions();
        String myQuery = "SELECT AVG(total) as total FROM (SELECT DISTINCT(" + T_ID + "), " + TOTAL_AMOUNT + " as total FROM " + table + " WHERE " + DATE + " >= '" + fromDate + "' AND " + DATE + " <= '" + toDate + "') as newTable ";
        String[] keys = {"total"};
        
        HashMap<String, ArrayList> data = df.customReturnQuery(myQuery, keys);
        ArrayList<String> dataList = data.get("total");
        if(dataList.get(0) == null || dataList.size() <= 0)
            return "0.0";
        return dataList.get(0).toString();
    }
    
    public String getMostSoldItemRate(String fromDate, String toDate){
        DatabaseFunctions df = new DatabaseFunctions();
        String myQuery = "SELECT t1." + I_NAME + ", SUM(t1." + I_QUANTITY + ") as quantity, t1." + I_ARTICLE + ", t1." + I_BRAND + ", (SUM(t1." + I_QUANTITY + ") / t2.total) * 100 as rate "
                + " FROM " + table + " t1, (SELECT SUM(" + I_QUANTITY + ") as total FROM " + table + " WHERE " + DATE + " >= '" + fromDate + "' AND " + DATE + " <= '" + toDate + "'" + ") as t2 " 
                + " WHERE t1." + DATE + " >= '" + fromDate + "' AND t1." + DATE + " <= '" + toDate + "'"
                + " GROUP BY t1." + I_ID + " ORDER BY quantity DESC LIMIT 1";
        String[] keys = {I_NAME, I_ARTICLE, I_BRAND, "rate"};
        
        HashMap<String, ArrayList> data = df.customReturnQuery(myQuery, keys);
        ArrayList<String> nameList = data.get(I_NAME);
        ArrayList<String> articleList = data.get(I_ARTICLE);
        ArrayList<String> brandList = data.get(I_BRAND);
        ArrayList<String> rateList = data.get("rate");
        if(nameList.size() <= 0)
            return "None";
        return nameList.get(0) + " " + articleList.get(0) + " (" + brandList.get(0) + ") - " + new DecimalFormat().format(Double.parseDouble(rateList.get(0))) + "% of total Items";
    }
    
    public String getLeastSoldItemRate(String fromDate, String toDate){
        DatabaseFunctions df = new DatabaseFunctions();
        String myQuery = "SELECT t1." + I_NAME + ", SUM(t1." + I_QUANTITY + ") as quantity, t1." + I_ARTICLE + ", t1." + I_BRAND + ", (SUM(t1." + I_QUANTITY + ") / t2.total) * 100 as rate "
                + " FROM " + table + " t1, (SELECT SUM(" + I_QUANTITY + ") as total FROM " + table + " WHERE " + DATE + " >= '" + fromDate + "' AND " + DATE + " <= '" + toDate + "'" + ") as t2 " 
                + " WHERE t1." + DATE + " >= '" + fromDate + "' AND t1." + DATE + " <= '" + toDate + "'"
                + " GROUP BY t1." + I_ID + " ORDER BY quantity ASC LIMIT 1";
        String[] keys = {I_NAME, I_ARTICLE, I_BRAND, "rate"};
        
        HashMap<String, ArrayList> data = df.customReturnQuery(myQuery, keys);
        ArrayList<String> nameList = data.get(I_NAME);
        ArrayList<String> articleList = data.get(I_ARTICLE);
        ArrayList<String> brandList = data.get(I_BRAND);
        ArrayList<String> rateList = data.get("rate");
        if(nameList.size() <= 0)
            return "None";
        return nameList.get(0) + " " + articleList.get(0) + " (" + brandList.get(0) + ") - " + new DecimalFormat().format(Double.parseDouble(rateList.get(0))) + "% of total Items";
    }
    
    public String getMostSalesClerk(String fromDate, String toDate){
        DatabaseFunctions df = new DatabaseFunctions();
        String myQuery = "SELECT T_CLERK, SUM(TOTAL_AMOUNT) as total FROM (SELECT DISTINCT(T_ID), T_CLERK, TOTAL_AMOUNT FROM table_transaction " + " WHERE " + DATE + " >= '" + fromDate + "' AND " + DATE + " <= '" + toDate + "') as newT GROUP BY T_CLERK ORDER BY total DESC LIMIT 1";
        String[] keys = {T_CLERK, "total"};
        
        HashMap<String, ArrayList> data = df.customReturnQuery(myQuery, keys);
        ArrayList<String> clerkList = data.get(T_CLERK);
        if(clerkList.size() <= 0)
            return "None";
        return clerkList.get(0).substring(0, clerkList.get(0).length() - 1);
    }
    
    public ArrayList<ChartData> getChartDataSalesByDate(String fromDate, String toDate){
        ArrayList<ChartData> cList = new ArrayList<>();
        
        LocalDate fromDate_main = LocalDate.parse(fromDate);
        LocalDate toDate_main = LocalDate.parse(toDate);
        String[] seriesName = {"Sales", "Transaction", "Items Sold", "Avg. Sales/Transactions"};
        for(int i = 0; i < seriesName.length; i++){
            String date = "";
            for(int j = 1; !date.equals(toDate_main.toString()); j++){
                LocalDate dayDate = fromDate_main.plus(j - 1, ChronoUnit.DAYS);
                date = dayDate.toString();
                System.out.println(date);
                Double value = 0.0;
                switch(i)
                {
                    case 0:
                        value = Double.parseDouble(getTotalSales(date, date));
                        break;
                    case 1:
                        value = Double.parseDouble(getTotalTransactions(date, date));
                        break;
                    case 2:
                        value = Double.parseDouble(getTotalItemsSold(date, date));
                        break;
                    case 3:
                        value = Double.parseDouble(getAverageAmountPerTransactions(date, date));
                        break;
                }
                cList.add(new ChartData(seriesName[i], date.substring(5), value));
            }
        }
        return cList;
    }
    
    private ArrayList<ChartData> getChartDataSalesProcess(String currentDate, int interval, boolean isAMonth){
        ArrayList<ChartData> cList = new ArrayList<>();
        
        int dayInterval = interval;
        LocalDate thisDate = LocalDate.parse(currentDate);
        LocalDate pastDate = thisDate.minus(dayInterval - 1, ChronoUnit.DAYS);
        String[] seriesName = {"Sales", "Transaction", "Items Sold", "Avg. Sales/Transactions"};
        for(int i = 0; i < seriesName.length; i++){
            if(!isAMonth){
                for(int j = 1; j <= dayInterval; j++){
                    LocalDate dayDate = pastDate.plus(j - 1, ChronoUnit.DAYS);
                    String date = dayDate.toString();
                    Double value = 0.0;
                    switch(i)
                    {
                        case 0:
                            value = Double.parseDouble(getTotalSales(date, date));
                            break;
                        case 1:
                            value = Double.parseDouble(getTotalTransactions(date, date));
                            break;
                        case 2:
                            value = Double.parseDouble(getTotalItemsSold(date, date));
                            break;
                        case 3:
                            value = Double.parseDouble(getAverageAmountPerTransactions(date, date));
                            break;
                    }
                    cList.add(new ChartData(seriesName[i], date.substring(5), value));
                }
            }else{
                int endOfMonth = thisDate.withDayOfMonth(thisDate.lengthOfMonth()).getDayOfMonth();
                boolean shouldDisplay = true;
                String fromDate = "";
                for(int j = 1, k = 1; j <= endOfMonth; j++){
                    if(shouldDisplay){
                        fromDate = thisDate.withDayOfMonth(j).toString();
                    }
                    if(thisDate.withDayOfMonth(j).getDayOfWeek().toString().equals("SATURDAY") || j == endOfMonth){
                        String toDate = thisDate.withDayOfMonth(j).toString();
                        
                        Double value = 0.0;
                        switch(i){
                            case 0:
                                value = Double.parseDouble(getTotalSales(fromDate, toDate));
                                break;
                            case 1:
                                value = Double.parseDouble(getTotalTransactions(fromDate, toDate));
                                break;
                            case 2:
                                value = Double.parseDouble(getTotalItemsSold(fromDate, toDate));
                                break;
                            case 3:
                                value = Double.parseDouble(getAverageAmountPerTransactions(fromDate, toDate));
                                break;
                        }
                        cList.add(new ChartData(seriesName[i], "Week " + k, value));
                        
                        k++;
                        shouldDisplay = true;
                        continue;
                    }
                    shouldDisplay = false;
                }
            }
        }
        return cList;
    }
    
    public ArrayList<ChartData> getChartDataSalesDaily(String currentDate){
        int days = 5;
        boolean isAMonth = false;
        return getChartDataSalesProcess(currentDate, days, isAMonth);
    }    
    
    public ArrayList<ChartData> getChartDataSalesWeekly(String currentDate){
        int days = 7;
        boolean isAMonth = false;
        return getChartDataSalesProcess(currentDate, days, isAMonth);
    }
        
    public ArrayList<ChartData> getChartDataSalesMonthly(String currentDate){
        int days = 31;
        boolean isAMonth = true;
        return getChartDataSalesProcess(currentDate, days, isAMonth);
    }
    
    public Double getSoldById(String id, String currentDate){
        DatabaseFunctions df = new DatabaseFunctions();
        String myQuery = "SELECT SUM(" + I_QUANTITY + ") as sum FROM " + table + " WHERE " + I_ID + " = " + id + " AND " + DATE + " >= '" + currentDate + "' AND " + DATE + " <= '" + currentDate + "'";
        String[] keys = {"sum"};
        
        HashMap<String, ArrayList> map = df.customReturnQuery(myQuery, keys);
        ArrayList<String> dataList = map.get("sum");
        
        if(dataList.get(0) == null)
            return 0.0;
        
        return Double.parseDouble(dataList.get(0));
    }
    
    public ArrayList<InventoryObject> getInventory(String currentDate, String keyword){
        DB_Item db_item = new DB_Item();
        DB_Invoice db_invoice = new DB_Invoice();
        DB_TransferStocks db_transferStocks = new DB_TransferStocks();
        
        ArrayList<InventoryObject> iList = new ArrayList<>();
        ArrayList<String> idList = db_item.getAllIdList();
        LocalDate date = LocalDate.parse(currentDate);
        
        if(keyword.equals("daily")){
            System.out.println("size: " + idList.size());
            for(int i = 0; i < idList.size(); i++){
                InventoryObject io = new InventoryObject();
                String currentId = idList.get(i);
                System.out.println(currentId);
                if(db_item.getNameById(currentId) == null)
                    continue;
                
                io.setName(db_item.getNameById(currentId));
                io.setBrand(db_item.getBrandById(currentId));
                io.setArticle(db_item.getArticleById(currentId));
                
                io.setLeft(db_item.getLeftById(currentId, date.toString(), date.toString().equals(LocalDate.now().minus(1, ChronoUnit.DAYS).toString())));
                
                io.setSold(getSoldById(currentId, date.toString()));
                io.setInvoiced(db_invoice.getInvoicedItemById(currentId, date.toString()));
                io.setTransfer(db_transferStocks.getTransfferedStockById(currentId, date.toString()));
                iList.add(io);
            }
            System.out.println("end");
        }
        
        return iList;
    }
    
}