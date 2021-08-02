package pos_h2;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import static pos_h2.MainFrame.myFrame;
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
        
        prepareDate(combobox_fromMonth, 1);
        changeDay(combobox_fromMonth, combobox_fromDay, combobox_fromYear, 1, 2021);
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
        table_transactions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me)
            {
                if(SwingUtilities.isLeftMouseButton(me))
                {
                    String id = table_transactions.getValueAt(table_transactions.getSelectedRow(), 0).toString();
                    TransactionDetailsDialog tDialog = new TransactionDetailsDialog(myFrame, true, transactions.get(id));
                    int x = (getWidth() - tDialog.getWidth()) / 2;
                    int y = (getHeight() - tDialog.getHeight()) / 2;
                    tDialog.setLocation(x,y);
                    tDialog.setVisible(true);
                }
            }
        });
    }
    private void prepareCurrentDate()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");  
        LocalDateTime now = LocalDateTime.now();  
        String date = dtf.format(now);
        int selectedYear = Integer.parseInt(date.split("/")[0]);
        int selectedMonth = Integer.parseInt(date.split("/")[1]);
        int selectedDay = Integer.parseInt(date.split("/")[2]);
        
        prepareDate(combobox_toMonth, selectedMonth);
        changeDay(combobox_toMonth, combobox_toDay, combobox_toYear, selectedDay, selectedYear);
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
        String toDate = combobox_toYear.getSelectedItem() + "-" + twoDigits(combobox_toMonth.getSelectedIndex() + 1) + "-" + twoDigits(combobox_toDay.getSelectedIndex() + 2);
        
        transactions = tDb.processData(fromDate, toDate);
        ArrayList<String> idLists = tDb.getIdList();
        for(int i = idLists.size() - 1; i >= 0; i--)
        {
            Transaction trans = transactions.get(idLists.get(i));
            double currentTotal = getTotalAmount(trans.getItem());
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
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        label_sales = new javax.swing.JLabel();
        button_salesReport = new javax.swing.JButton();

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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel7.setText("Sales");

        label_sales.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        label_sales.setText("0.0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(label_sales)))
                .addContainerGap(501, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(label_sales)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        button_salesReport.setText("Make Daily Sales Report");
        button_salesReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_salesReportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(combobox_fromMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(combobox_toMonth, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jLabel3))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)))
                        .addGap(6, 6, 6)
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
                            .addComponent(combobox_fromYear, 0, 73, Short.MAX_VALUE)
                            .addComponent(combobox_toYear, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_salesReport)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(combobox_fromMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combobox_fromDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combobox_fromYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(combobox_toMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combobox_toDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combobox_toYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_salesReport)))
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(28, 28, 28))
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE))
                .addContainerGap())
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

    private void button_salesReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_salesReportActionPerformed
        
    }//GEN-LAST:event_button_salesReportActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_salesReport;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_sales;
    private javax.swing.JTable table_transactions;
    // End of variables declaration//GEN-END:variables
}
