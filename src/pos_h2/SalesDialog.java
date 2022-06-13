package pos_h2;

import extraClasses.ChartData;
import extraClasses.InventoryObject;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import myUtilities.MessageHandler;
import myUtilities.system_utilities.SystemUtilities;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import static pos_h2.MainFrame.myFrame;
import pos_h2_database.DB_Invoice;
import pos_h2_database.DB_Transaction;
import pos_h2_database.Item;
import pos_h2_database.Transaction;

public class SalesDialog extends javax.swing.JDialog {

    DefaultTableModel dtm;
    
    HashMap<String, Transaction> transactions = new HashMap<>();
    
    boolean ready = false;
    double totalAmount = 0;
    
    MainFrame myFrame;
    
    public SalesDialog(MainFrame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        myFrame = parent;
        
//        prepareDate(combobox_fromMonth, 1);
//        changeDay(combobox_fromMonth, combobox_fromDay, combobox_fromYear, 1, 2021);
        prepareCurrentDate();
        createColumns();
        prepareTable();
        setupTable();
        ready = true;
    }
    private void updateStatus()
    {
        label_sales.setText((char) 8369 + " " + totalAmount);
    }
    private void setupTable()
    {
        table_transactions.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int y = p.y / 30;
                if(y < dtm.getRowCount())
                    table_transactions.setRowSelectionInterval(0, y);
            }
        });
    }
    private void prepareCurrentDate()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
        LocalDateTime now = LocalDateTime.now();  
        String date = dtf.format(now);
        int selectedYear = Integer.parseInt(date.split("-")[0]);
        int selectedMonth = Integer.parseInt(date.split("-")[1]);
        int selectedDay = Integer.parseInt(date.split("-")[2]);
        
        LocalDateTime past = now.minus(6, ChronoUnit.DAYS);
        String pastDate = dtf.format(past);
        int pastYear = Integer.parseInt(pastDate.split("-")[0]);
        int pastMonth = Integer.parseInt(pastDate.split("-")[1]);
        int pastDay = Integer.parseInt(pastDate.split("-")[2]);
        
        prepareDate(combobox_toMonth, selectedMonth);
        changeDay(combobox_toMonth, combobox_toDay, combobox_toYear, selectedDay, selectedYear);
        prepareDate(combobox_fromMonth, pastMonth);
        changeDay(combobox_fromMonth, combobox_fromDay, combobox_fromYear, pastDay, pastYear);
    }
    private void prepareDate(JComboBox monthCombo,int monthIndex)
    {
        String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (String month1 : month)
            monthCombo.addItem(month1);
            
        monthCombo.setSelectedIndex(monthIndex - 1);
    }
    
    private void changeDay(JComboBox monthCombo, JComboBox dayCombo, JComboBox yearCombo, int selectedDay, int selectedYear)
    {
        int days[] = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int year[] = {2015,2016,2017,2018,2019,2020,2021,2022,2023,2024,2025,2026,2027,2028,2029,2030,2031,2032,2033,2034,2035,2036,2037,2038,2039,2040};
        
        int selectedMonth = monthCombo.getSelectedIndex();
        
        dayCombo.removeAllItems();
        yearCombo.removeAllItems();
        
        for(int i = 0; i < days[selectedMonth]; i++)
            dayCombo.addItem(i + 1);
        
        for(int i = 0; i < year.length; i++)
            yearCombo.addItem(year[i]);
        
        dayCombo.setSelectedIndex(selectedDay - 1);
        yearCombo.setSelectedItem(selectedYear);
    }
    
    private void createColumns()
    {
        dtm = new DefaultTableModel(0,0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        table_transactions.setModel(dtm);
        dtm.addColumn("ID");
        dtm.addColumn("Total Amount");
        dtm.addColumn("Date");
    }
    
    private void prepareTable()
    {
        for(int i = 0; dtm.getRowCount() != 0;)
            dtm.removeRow(i);
        totalAmount = 0;
        
        DB_Transaction tDb = new DB_Transaction();
        
        String fromDate = combobox_fromYear.getSelectedItem() + "-" + twoDigits(combobox_fromMonth.getSelectedIndex() + 1) + "-" + twoDigits(combobox_fromDay.getSelectedIndex() + 1);
        String toDate = combobox_toYear.getSelectedItem() + "-" + twoDigits(combobox_toMonth.getSelectedIndex() + 1) + "-" + twoDigits(combobox_toDay.getSelectedIndex() + 1);
        
        transactions = tDb.processData(fromDate, toDate);
        ArrayList<String> idLists = tDb.getIdList();
        for(int i = idLists.size() - 1; i >= 0; i--)
        {
            Transaction trans = transactions.get(idLists.get(i));
            double currentTotal = Double.parseDouble(trans.getTotalAmount());
            totalAmount += currentTotal;
            String[] rowData = {
                trans.getT_id(),
                (char)8369 + " " + currentTotal,
                trans.getDate()
            };
            dtm.addRow(rowData);
        }
        table_transactions.setRowHeight(30);
        updateStatus();
    }
    private String twoDigits(int index)
    {
        String num = index + "";
        if(num.length() == 1)
            num = "0" + num;
        
        return num;
    }
    private double getTotalAmount(ArrayList<Item> item)
    {
        double totalAmount = 0;
        for(int i = 0; i < item.size(); i++)
        {
            double price = Double.parseDouble(item.get(i).getPrice());
            double quantity = Double.parseDouble(item.get(i).getQuantity());
            totalAmount += price * quantity;
        }
        return totalAmount;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table_transactions = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        combobox_fromMonth = new javax.swing.JComboBox<>();
        combobox_fromDay = new javax.swing.JComboBox<>();
        combobox_fromYear = new javax.swing.JComboBox<>();
        combobox_toMonth = new javax.swing.JComboBox<>();
        combobox_toDay = new javax.swing.JComboBox<>();
        combobox_toYear = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btn_salesMonthly = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        label_sales = new javax.swing.JLabel();
        btn_salesCustom = new javax.swing.JButton();
        btn_salesWeekly = new javax.swing.JButton();
        btn_salesDaily = new javax.swing.JButton();
        label_process = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane1.setFocusable(false);

        table_transactions.setFillsViewportHeight(true);
        table_transactions.setFocusable(false);
        table_transactions.setRequestFocusEnabled(false);
        table_transactions.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_transactions.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_transactions.getTableHeader().setResizingAllowed(false);
        table_transactions.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_transactions);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("From :");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("To     :");

        combobox_fromMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combobox_fromMonthActionPerformed(evt);
            }
        });

        combobox_fromDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combobox_fromDayActionPerformed(evt);
            }
        });

        combobox_fromYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combobox_fromYearActionPerformed(evt);
            }
        });

        combobox_toMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combobox_toMonthActionPerformed(evt);
            }
        });

        combobox_toDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combobox_toDayActionPerformed(evt);
            }
        });

        combobox_toYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combobox_toYearActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("/");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("/");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("/");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("/");

        btn_salesMonthly.setText("Generate Monthly Sales Report");
        btn_salesMonthly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salesMonthlyActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Total Sales:");

        label_sales.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        label_sales.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_sales.setText("0.0");

        btn_salesCustom.setText("Generate Custom Sales Report");
        btn_salesCustom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salesCustomActionPerformed(evt);
            }
        });

        btn_salesWeekly.setText("Generate Weekly Sales Report");
        btn_salesWeekly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salesWeeklyActionPerformed(evt);
            }
        });

        btn_salesDaily.setText("Generate Daily Sales Report");
        btn_salesDaily.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salesDailyActionPerformed(evt);
            }
        });

        label_process.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        label_process.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_process.setText("No Process");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(combobox_fromMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(combobox_toMonth, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 13, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(combobox_toDay, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combobox_fromDay, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jLabel6))
                            .addComponent(jLabel5))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(combobox_fromYear, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(combobox_toYear, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_process)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_sales))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 683, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_salesMonthly, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_salesCustom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                            .addComponent(btn_salesWeekly, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_salesDaily, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(combobox_fromMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(combobox_toMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel3)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(combobox_fromDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combobox_fromYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(combobox_toDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combobox_toYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(28, 28, 28))
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_salesDaily, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_salesWeekly, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_salesMonthly, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_salesCustom, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(label_sales)
                        .addComponent(label_process))
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void combobox_fromMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox_fromMonthActionPerformed
        if(ready)
        {
            changeDay(combobox_fromMonth, combobox_fromDay, combobox_fromYear, 1, Integer.parseInt(combobox_fromYear.getSelectedItem().toString()));
            prepareTable();
        }
    }//GEN-LAST:event_combobox_fromMonthActionPerformed

    private void combobox_toMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox_toMonthActionPerformed
        if(ready)
        {
            changeDay(combobox_toMonth, combobox_toDay, combobox_toYear, 1, Integer.parseInt(combobox_toYear.getSelectedItem().toString()));
            prepareTable();
        }
    }//GEN-LAST:event_combobox_toMonthActionPerformed

    private void combobox_fromDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox_fromDayActionPerformed
        if(ready)
            prepareTable();
    }//GEN-LAST:event_combobox_fromDayActionPerformed

    private void combobox_toDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox_toDayActionPerformed
        if(ready)
            prepareTable();
    }//GEN-LAST:event_combobox_toDayActionPerformed

    private void combobox_fromYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox_fromYearActionPerformed
        if(ready)
            prepareTable();
    }//GEN-LAST:event_combobox_fromYearActionPerformed

    private void combobox_toYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox_toYearActionPerformed
        if(ready)
            prepareTable();
    }//GEN-LAST:event_combobox_toYearActionPerformed

    private void btn_salesMonthlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salesMonthlyActionPerformed
       createMonthlySalesReport();
    }//GEN-LAST:event_btn_salesMonthlyActionPerformed

    private void btn_salesCustomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salesCustomActionPerformed
        createSalesReportByDate("custom", false);
    }//GEN-LAST:event_btn_salesCustomActionPerformed

    private void btn_salesWeeklyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salesWeeklyActionPerformed
        createWeeklySalesReport();
    }//GEN-LAST:event_btn_salesWeeklyActionPerformed

    private void btn_salesDailyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salesDailyActionPerformed
        createDailySalesReport();
    }//GEN-LAST:event_btn_salesDailyActionPerformed
    
    private void createDailySalesReport(){
        createSalesReportByDate("daily", true);
    }

    private void createWeeklySalesReport(){
        createSalesReportByDate("weekly", false);
    }

    private void createMonthlySalesReport(){
        createSalesReportByDate("monthly", false);
    }

    private void enableButtons(boolean enable){
        btn_salesDaily.setEnabled(enable);
        btn_salesWeekly.setEnabled(enable);
        btn_salesMonthly.setEnabled(enable);
        btn_salesCustom.setEnabled(enable);
    }
    
    private void createSalesReportByDate(String keyword, boolean withInventory){
        if(table_transactions.getRowCount() >= 0){
            SwingWorker<Void, String[]> worker = new SwingWorker(){
                @Override
                protected Object doInBackground() throws Exception {
                    DB_Transaction dB_Transaction = new DB_Transaction();

                    label_process.setText("Processing Report...");
                    progressBar.setIndeterminate(true);
                    enableButtons(false);
                    try{
                        String reportPath = System.getProperty("user.dir") + "\\DailyReport.jrxml";

                        Map<String, Object> parameters = new HashMap<>();
                        parameters.put("logo", getClass().getResource("/Images/logo.png"));//needs to change for flexibility

                        String year = combobox_fromYear.getSelectedItem().toString();
                        int initialMonth = combobox_fromMonth.getSelectedIndex() + 1;
                        String month = initialMonth > 9 ? initialMonth + "" : "0" + initialMonth;
                        int initialDay = Integer.parseInt(combobox_fromDay.getSelectedItem().toString());
                        String day = initialDay > 9 ? initialDay + "" : "0" + initialDay;

                        String fromDate =  year + "-" + month + "-" + day;

                        year = combobox_toYear.getSelectedItem().toString();
                        initialMonth = combobox_toMonth.getSelectedIndex() + 1;
                        month = initialMonth > 9 ? initialMonth + "" : "0" + initialMonth;
                        initialDay = Integer.parseInt(combobox_toDay.getSelectedItem().toString());
                        day = initialDay > 9 ? initialDay + "" : "0" + initialDay;

                        String toDate =  year + "-" + month + "-" + day;

                        if(keyword.equals("daily")){
                            fromDate = toDate;
                        }else if(keyword.equals("weekly")){
                            fromDate = LocalDate.parse(toDate).minus(6, ChronoUnit.DAYS).toString();
                        }else if(keyword.equals("monthly")){
                            LocalDate currentDate = LocalDate.parse(toDate);
                            LocalDate firstMonthDate = currentDate.withDayOfMonth(1);
                            LocalDate lastMonthDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
                            fromDate = firstMonthDate.toString();
                            toDate = lastMonthDate.toString();
                        }
                        Double invoiceSales = new DB_Invoice().getTotalInvoiceSales(fromDate, toDate);
                        Double sales = Double.parseDouble(dB_Transaction.getTotalSales(fromDate, toDate));

                        String totalSales = (char)8369 + " " + new DecimalFormat("###,###,##0.00").format(sales);
                        String totalItemsSold = dB_Transaction.getTotalItemsSold(fromDate, toDate) + " Item/s";
                        String totalTransactions = dB_Transaction.getTotalTransactions(fromDate, toDate) + " Transaction/s";
                        String averageAmountPerTransactions = (char)8369 + " " + new DecimalFormat("###,###,##0.00").format(Double.parseDouble(dB_Transaction.getAverageAmountPerTransactions(fromDate, toDate)));
                        String mostSoldItemRate = dB_Transaction.getMostSoldItemRate(fromDate, toDate);
                        String leastSoldItemRate = dB_Transaction.getLeastSoldItemRate(fromDate, toDate);

                        String mostSalesClerk = dB_Transaction.getMostSalesClerk(fromDate, toDate);

                        String invoiceMessage = invoiceSales <= 0 ? "" : " + " + (char)8369 + " " + new DecimalFormat("###,###,##0.00").format(invoiceSales) + " invoice sales!";
                        parameters.put("date", LocalDate.parse(fromDate).format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) + "-" + LocalDate.parse(toDate).format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                        parameters.put("totalSales", totalSales);
                        parameters.put("invoiceSales", invoiceMessage);
                        parameters.put("totalItemsSold", totalItemsSold);
                        parameters.put("totalTransactions", totalTransactions);
                        parameters.put("averageAmountPerTransactions", averageAmountPerTransactions);
                        parameters.put("mostSoldItemRate", mostSoldItemRate);
                        parameters.put("leastSoldItemRate", leastSoldItemRate);

                        parameters.put("mostSalesClerk", mostSalesClerk);

                        parameters.put("hasInventoryStatus", withInventory);

                        List<ChartData> cList = new ArrayList<>();

                        List<InventoryObject> collectionList = new ArrayList<>();
                        if(withInventory){
                            if(toDate.equals(LocalDate.now().toString()) || toDate.equals(LocalDate.now().minus(1, ChronoUnit.DAYS).toString())){
                                collectionList = dB_Transaction.getInventory(toDate, keyword);
                            }else{
                                MessageHandler mh = new MessageHandler();
                                mh.warning("Unable to create inventory report! To Date should be current date!");
                            }
                        }
                        for(int i = 0; i < collectionList.size(); i++){
                            System.out.println(collectionList.get(i).getName() + " " + i);
                        }
                        JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(collectionList);
                        parameters.put("CollectionBeanParam", itemsJRBean);

                        if(keyword.equals("custom")){
                            cList = dB_Transaction.getChartDataSalesByDate(fromDate, toDate);
                        }else if(keyword.equals("daily")){
                            cList = dB_Transaction.getChartDataSalesDaily(toDate);
                        }else if(keyword.equals("weekly")){
                            cList = dB_Transaction.getChartDataSalesWeekly(toDate);
                        }else if(keyword.equals("monthly")){
                            cList = dB_Transaction.getChartDataSalesMonthly(toDate);
                        }

                        parameters.put("ChartData", new JRBeanCollectionDataSource(cList));

                        InputStream input = new FileInputStream(new File(reportPath));
                        JasperDesign jdesign = JRXmlLoader.load(input);

                        JasperReport jreport = JasperCompileManager.compileReport(jdesign);
                        JasperPrint jprint = JasperFillManager.fillReport(jreport, parameters, new JREmptyDataSource());

                        JasperViewer.viewReport(jprint, false);
                    }catch(Exception ex)
                    {
                        JOptionPane.showMessageDialog(null, ex);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    label_process.setText("No Process");
                    progressBar.setIndeterminate(false);
                    enableButtons(true);
                    new MessageHandler().message("Sales Report created!");
                    dispose();
                }
            };
            worker.execute();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_salesCustom;
    private javax.swing.JButton btn_salesDaily;
    private javax.swing.JButton btn_salesMonthly;
    private javax.swing.JButton btn_salesWeekly;
    private javax.swing.JComboBox<String> combobox_fromDay;
    private javax.swing.JComboBox<String> combobox_fromMonth;
    private javax.swing.JComboBox<String> combobox_fromYear;
    private javax.swing.JComboBox<String> combobox_toDay;
    private javax.swing.JComboBox<String> combobox_toMonth;
    private javax.swing.JComboBox<String> combobox_toYear;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_process;
    private javax.swing.JLabel label_sales;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTable table_transactions;
    // End of variables declaration//GEN-END:variables
}
