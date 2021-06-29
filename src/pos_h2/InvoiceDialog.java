package pos_h2;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import myUtilities.SystemUtilities;
import pos_h2_database.DB_Invoice;
import pos_h2_database.Invoice;
import pos_h2_database.Item;

public class InvoiceDialog extends javax.swing.JDialog {

    DefaultTableModel dtm1, dtm2, dtm3;
    
    HashMap<String, Invoice> invoices;
    ArrayList<String> idList;
    
    MainFrame main;
    
    public InvoiceDialog(MainFrame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        main = parent;
        
        createColumns1();
        createColumns2();
        createColumns3();
        setupTable(table_tables, dtm1);
        setupTable(table_invoices, dtm2);
        setupTable(table_items, dtm3);
        processTables();
    }

    public void createColumns1()
    {
        dtm1 = new DefaultTableModel(0,0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        table_tables.setModel(dtm1);
        dtm1.addColumn("Customers");
    }

    public void createColumns2()
    {
        dtm2 = new DefaultTableModel(0,0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        table_invoices.setModel(dtm2);
        dtm2.addColumn("Invoice ID");
        dtm2.addColumn("Date");
        dtm2.addColumn("Status");
    }

    public void createColumns3()
    {
        dtm3 = new DefaultTableModel(0,0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        table_items.setModel(dtm3);
        dtm3.addColumn("ID");
        dtm3.addColumn("Item");
        dtm3.addColumn("Article");
        dtm3.addColumn("Brand");
        dtm3.addColumn("Quantity");
        dtm3.addColumn("Price");
    }
    
    private void clearTable(DefaultTableModel dtm)
    {
        for(int i = 0; dtm.getRowCount() != 0;)
            dtm.removeRow(i);
    }
    
private void setupTable(JTable table, DefaultTableModel dtm)
    {
        table.addMouseListener(new MouseListener() {
            private boolean onTable = false;
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if(onTable)
                {
                    Point p = e.getPoint();
                    int y = p.y / 30;
                    if(y < dtm.getRowCount())
                        tableOnClickDeterminer(dtm);
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
        
        table.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int y = p.y / 30;
                if(y < dtm.getRowCount())
                    table.setRowSelectionInterval(0, y);
            }
        });
    }
    
    private void tableOnClickDeterminer(DefaultTableModel dtm)
    {
        if(dtm.getColumnCount() == 1)
            processInvoices();
        else if(dtm.getColumnCount() == 3)
            processItems();
    }
    
    private void processTables()
    {
        clearTable(dtm1);
        DB_Invoice invoiceDb = new DB_Invoice();
        ArrayList<String> invList = invoiceDb.getTables();
        
        for(int i = 0; i < invList.size(); i++)
        {
            String[] rowData = {invList.get(i)};
            dtm1.addRow(rowData);
        }
        if(dtm1.getRowCount() > 0)
            table_tables.setRowSelectionInterval(0, 0);
        table_tables.setRowHeight(30);
        
        if(dtm1.getRowCount() > 0)
            processInvoices();
    }
    
    private void processInvoices()
    {
        clearTable(dtm2);
        DB_Invoice invoiceDb = new DB_Invoice();
        
        String invoiceName = table_tables.getValueAt(table_tables.getSelectedRow(), 0).toString();
        
        invoices = invoiceDb.processData(invoiceName);
        idList = invoiceDb.getIdList();
        
        for(int i = 0; i < invoices.size(); i++)
        {
            String id = idList.get(i);
            String[] rowData =
            {
                invoices.get(id).getId_invoice(),
                invoices.get(id).getDate(),
                invoices.get(id).getStatus()
            };
            dtm2.addRow(rowData);
        }
        if(dtm2.getRowCount() > 0)
            table_invoices.setRowSelectionInterval(0, 0);
        table_invoices.setRowHeight(30);
        
        if(dtm2.getRowCount() > 0)
            processItems();
    }
    
    private void processItems()
    {
        clearTable(dtm3);
        String id = table_invoices.getValueAt(table_invoices.getSelectedRow(), 0).toString();
        Invoice invoice = invoices.get(id);
        ArrayList<Item> items = invoice.getItem();
        for(Item item : items)
        {
            String[] rowData =
            {
                item.getId(),
                item.getName(),
                item.getArticle(),
                item.getBrand(),
                item.getQuantity(),
                item.getPrice()
            };
            dtm3.addRow(rowData);
        }
        if(dtm3.getRowCount() > 0)
            table_items.setRowSelectionInterval(0, 0);
        table_items.setRowHeight(30);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_tables = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_invoices = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_items = new javax.swing.JTable();
        button_newCustomer = new javax.swing.JButton();
        button_newInvoice = new javax.swing.JButton();
        button_addItem = new javax.swing.JButton();
        button_removeCustomer = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane1.setFocusable(false);

        table_tables.setFillsViewportHeight(true);
        table_tables.setFocusable(false);
        table_tables.setRequestFocusEnabled(false);
        table_tables.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_tables.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_tables.getTableHeader().setResizingAllowed(false);
        table_tables.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_tables);

        jScrollPane2.setFocusable(false);

        table_invoices.setFillsViewportHeight(true);
        table_invoices.setFocusable(false);
        table_invoices.setRequestFocusEnabled(false);
        table_invoices.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_invoices.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_invoices.getTableHeader().setResizingAllowed(false);
        table_invoices.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_invoices);

        jScrollPane3.setFocusable(false);

        table_items.setFillsViewportHeight(true);
        table_items.setFocusable(false);
        table_items.setRequestFocusEnabled(false);
        table_items.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_items.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_items.getTableHeader().setResizingAllowed(false);
        table_items.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(table_items);

        button_newCustomer.setText("New Customer");
        button_newCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_newCustomerActionPerformed(evt);
            }
        });

        button_newInvoice.setText("New Invoice");
        button_newInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_newInvoiceActionPerformed(evt);
            }
        });

        button_addItem.setText("Add Item");
        button_addItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_addItemActionPerformed(evt);
            }
        });

        button_removeCustomer.setText("Remove Customer");
        button_removeCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_removeCustomerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(button_newCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button_removeCustomer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(button_newInvoice)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_addItem)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(button_newInvoice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_addItem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_newCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_removeCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_newCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_newCustomerActionPerformed
        DB_Invoice invoiceDb = new DB_Invoice();
        SystemUtilities su = new SystemUtilities();
        
        String customer = su.inputStringUser();
        
        invoiceDb.newCustomer(customer);
    }//GEN-LAST:event_button_newCustomerActionPerformed

    private void button_removeCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_removeCustomerActionPerformed
        if(table_tables.getRowCount() > 0)
        {
            DB_Invoice invoiceDb = new DB_Invoice();
            String customer = table_tables.getValueAt(table_tables.getSelectedRow(), 0).toString();

            invoiceDb.deleteCustomer(customer);
        }
    }//GEN-LAST:event_button_removeCustomerActionPerformed

    private void button_newInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_newInvoiceActionPerformed
        addNewInvoice();
    }//GEN-LAST:event_button_newInvoiceActionPerformed

    private void button_addItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_addItemActionPerformed
        String id = table_invoices.getValueAt(table_invoices.getSelectedRow(), 0).toString();
        addItem(id);
    }//GEN-LAST:event_button_addItemActionPerformed

    private void addNewInvoice()
    {
        DB_Invoice invoiceDb = new DB_Invoice();
        String customerName = table_tables.getValueAt(table_tables.getSelectedRow(), 0).toString();
        
        String newId = invoiceDb.getAvailableIDInvoice(customerName);
        openItem(newId);
    }
    
    private void openItem(String id)
    {
        ItemDialog itemDialog = new ItemDialog(main, this, true, false);
        int x = (getWidth() - itemDialog.getWidth()) / 2;
        int y = (getHeight() - itemDialog.getHeight()) / 2;
        itemDialog.setLocation(x,y);
        itemDialog.setVisible(true);
    }
    
    private void addItem(String id)
    {
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_addItem;
    private javax.swing.JButton button_newCustomer;
    private javax.swing.JButton button_newInvoice;
    private javax.swing.JButton button_removeCustomer;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable table_invoices;
    private javax.swing.JTable table_items;
    private javax.swing.JTable table_tables;
    // End of variables declaration//GEN-END:variables
}
