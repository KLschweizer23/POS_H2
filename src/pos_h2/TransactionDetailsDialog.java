package pos_h2;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import myUtilities.MessageHandler;
import myUtilities.SystemUtilities;
import pos_h2_database.DB_Discount;
import pos_h2_database.DB_Login;
import pos_h2_database.DB_Transaction;
import pos_h2_database.Discount;
import pos_h2_database.Item;
import pos_h2_database.Transaction;

public class TransactionDetailsDialog extends javax.swing.JDialog {

    Transaction currentTransaction;
    
    DefaultTableModel dtm;
    
    public TransactionDetailsDialog(MainFrame parent, boolean modal, Transaction transaction) {
        super(parent, modal);
        initComponents();
        
        currentTransaction = transaction;
        
        createColumns();
        prepareDetails();
        prepareTable();
        setupTable();
        commands();
    }
    
    private void commands()
    {
        getRootPane().registerKeyboardAction(e -> {
                dispose();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    
    private void prepareDetails()
    {
        String id = currentTransaction.getId();
        String salesClerk = currentTransaction.getT_clerk();
        String date = currentTransaction.getDate();
        String totalAmount = currentTransaction.getTotalAmount();
        String payment = currentTransaction.getPayment();
        String discount = discount(salesClerk);
        
        salesClerk = getSalesClerkOnly(salesClerk);
        
        label_id.setText(id);
        label_salesClerk.setText(salesClerk);
        label_date.setText(date);
        label_totalAmount.setText((char)8369 + " " + totalAmount);
        label_payment.setText((char)8369 + " " + payment);
        label_discount.setText(discount);
    }
    
    private void setupTable(){
        table_items.addMouseListener(new MouseListener() {
            private boolean onTable = false;
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if(onTable)
                {
                    Point p = e.getPoint();
                    int y = p.y / 30;
                    if(y < dtm.getRowCount())
                        makeVoid();
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {
            }
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                onTable = true;
            }
            @Override
            public void mouseExited(MouseEvent e) {
                onTable = false;
            }
        });
        
        table_items.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int y = p.y / 30;
                if(y < dtm.getRowCount())
                    table_items.setRowSelectionInterval(0, y);
            }
        });
    }
    
    private void makeVoid(){
        SystemUtilities su = new SystemUtilities();
        DB_Login loginDb = new DB_Login();
        String authorizationPassword = su.inputPasswordUser("Enter administrator's password to adjust/void item!");
        if(authorizationPassword != null){
            if(loginDb.checkPassword(authorizationPassword)){
                Item item = currentTransaction.getItem().get(table_items.getSelectedRow());
                String newQuantity = su.inputNumberUser(
                        "<html>"
                                + "Enter new quantity for this item!" + "<br>"
                                + "<i>Note: Enter 0 to void the item.</i>" + "<br>"
                                + "Old Quantity: <b>" + item.getQuantity() + "</b>" + "<br>" + 
                        "</html>"
                );
                if(newQuantity != null){
                    double quantity = Double.parseDouble(newQuantity);
                    if(quantity <= Double.parseDouble(item.getQuantity())){
                        if(quantity == 0){
                            DB_Transaction transactionDb = new DB_Transaction();
                            transactionDb.removeItem(currentTransaction, item);
                        }else{
                            DB_Transaction transactionDb = new DB_Transaction();
                            transactionDb.adjustItem(currentTransaction, item, quantity);
                        }
                    }else{
                        new MessageHandler().warning("<html>New Quantity should be lesser than the actual quantity!<br>Make a new transaction instead</html>");
                    }
                }
            }else new MessageHandler().warning("Wrong password!");
        }
    }
    
    private String discount(String clerkWithID){
        String id = "";
        for(int i = 0; i < clerkWithID.length(); i++){
            if(clerkWithID.charAt(i) == '-'){
                i += 1;
                id = clerkWithID.substring(i);
                
                DB_Discount discountDb = new DB_Discount();
                HashMap<String, Discount> map = discountDb.processData("", 0);
                Discount discount = map.get(id);
                return discount.getName() + ":" + discount.getValue() + "%";
            }
        }
        
        
        return "";
    }
    
    private String getSalesClerkOnly(String salesClerk){
        for(int i = 0; i < salesClerk.length(); i++){
            if(salesClerk.charAt(i) == '-')
                return salesClerk.substring(0, i);
        }
        return salesClerk;
    }
    
    private void prepareTable()
    {
        ArrayList<Item> listOfItems = currentTransaction.getItem();
        for(Item item : listOfItems)
        {
            String[] rowData = {
                item.getId(),
                item.getName(),
                item.getArticle(),
                item.getBrand(),
                item.getQuantity(),
                (char)8369 + " " + item.getPrice()
            };
            dtm.addRow(rowData);
        }
        table_items.setRowHeight(30);
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
        table_items.setModel(dtm);
        dtm.addColumn("ID");
        dtm.addColumn("Item");
        dtm.addColumn("Article");
        dtm.addColumn("Brand");
        dtm.addColumn("Quantity");
        dtm.addColumn("Price");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table_items = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        label_salesClerk = new javax.swing.JLabel();
        label_date = new javax.swing.JLabel();
        label_salesClerk1 = new javax.swing.JLabel();
        label_salesClerk2 = new javax.swing.JLabel();
        label_totalAmount = new javax.swing.JLabel();
        label_payment = new javax.swing.JLabel();
        label_salesClerk3 = new javax.swing.JLabel();
        label_salesClerk4 = new javax.swing.JLabel();
        label_id = new javax.swing.JLabel();
        label_discount = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane1.setBackground(new java.awt.Color(224, 224, 224));
        jScrollPane1.setBorder(null);

        table_items.setBackground(new java.awt.Color(255, 255, 255));
        table_items.setEnabled(false);
        table_items.setFillsViewportHeight(true);
        table_items.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_items.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_items.setShowGrid(false);
        table_items.setShowHorizontalLines(true);
        table_items.getTableHeader().setResizingAllowed(false);
        table_items.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_items);

        label_salesClerk.setBackground(new java.awt.Color(255, 255, 255));
        label_salesClerk.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        label_salesClerk.setForeground(new java.awt.Color(0, 0, 0));
        label_salesClerk.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_salesClerk.setText("Sales Clerk");
        label_salesClerk.setFocusable(false);

        label_date.setBackground(new java.awt.Color(255, 255, 255));
        label_date.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        label_date.setForeground(new java.awt.Color(0, 0, 0));
        label_date.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_date.setText("Date");
        label_date.setFocusable(false);

        label_salesClerk1.setBackground(new java.awt.Color(255, 255, 255));
        label_salesClerk1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        label_salesClerk1.setForeground(new java.awt.Color(0, 0, 0));
        label_salesClerk1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_salesClerk1.setText("Total");
        label_salesClerk1.setFocusable(false);

        label_salesClerk2.setBackground(new java.awt.Color(255, 255, 255));
        label_salesClerk2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        label_salesClerk2.setForeground(new java.awt.Color(0, 0, 0));
        label_salesClerk2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_salesClerk2.setText(":");
        label_salesClerk2.setFocusable(false);

        label_totalAmount.setBackground(new java.awt.Color(255, 255, 255));
        label_totalAmount.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        label_totalAmount.setForeground(new java.awt.Color(0, 0, 0));
        label_totalAmount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_totalAmount.setText("0.0");
        label_totalAmount.setFocusable(false);

        label_payment.setBackground(new java.awt.Color(255, 255, 255));
        label_payment.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        label_payment.setForeground(new java.awt.Color(0, 0, 0));
        label_payment.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_payment.setText("0.0");
        label_payment.setFocusable(false);

        label_salesClerk3.setBackground(new java.awt.Color(255, 255, 255));
        label_salesClerk3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        label_salesClerk3.setForeground(new java.awt.Color(0, 0, 0));
        label_salesClerk3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_salesClerk3.setText(":");
        label_salesClerk3.setFocusable(false);

        label_salesClerk4.setBackground(new java.awt.Color(255, 255, 255));
        label_salesClerk4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        label_salesClerk4.setForeground(new java.awt.Color(0, 0, 0));
        label_salesClerk4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_salesClerk4.setText("Payment");
        label_salesClerk4.setFocusable(false);

        label_id.setBackground(new java.awt.Color(255, 255, 255));
        label_id.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label_id.setForeground(new java.awt.Color(204, 204, 204));
        label_id.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_id.setText("00");
        label_id.setFocusable(false);

        label_discount.setBackground(new java.awt.Color(255, 255, 255));
        label_discount.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        label_discount.setForeground(new java.awt.Color(0, 0, 0));
        label_discount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_discount.setText("Discount");
        label_discount.setFocusable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_salesClerk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_date)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 283, Short.MAX_VALUE)
                        .addComponent(label_id))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(label_salesClerk1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(label_salesClerk2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_totalAmount))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(label_salesClerk4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label_salesClerk3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_payment)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(label_discount)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_salesClerk)
                    .addComponent(label_date)
                    .addComponent(label_id))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_salesClerk1)
                    .addComponent(label_salesClerk2)
                    .addComponent(label_totalAmount)
                    .addComponent(label_discount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_salesClerk4)
                    .addComponent(label_salesClerk3)
                    .addComponent(label_payment))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_date;
    private javax.swing.JLabel label_discount;
    private javax.swing.JLabel label_id;
    private javax.swing.JLabel label_payment;
    private javax.swing.JLabel label_salesClerk;
    private javax.swing.JLabel label_salesClerk1;
    private javax.swing.JLabel label_salesClerk2;
    private javax.swing.JLabel label_salesClerk3;
    private javax.swing.JLabel label_salesClerk4;
    private javax.swing.JLabel label_totalAmount;
    private javax.swing.JTable table_items;
    // End of variables declaration//GEN-END:variables
}
