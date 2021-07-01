package pos_h2;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import myUtilities.MessageHandler;
import myUtilities.SystemUtilities;
import pos_h2_database.DB_Invoice;
import pos_h2_database.DB_Item;
import pos_h2_database.Invoice;
import pos_h2_database.Item;

public class InvoiceDialog extends javax.swing.JDialog {

    private DefaultTableModel dtm1, dtm2, dtm3;
    
    private HashMap<String, Invoice> invoices;
    private ArrayList<String> idList;
    
    private MainFrame main;
    
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
        updateInvoicesStatus();
        
        TableColumnModel tcm = table_items.getColumnModel();
        tcm.removeColumn(tcm.getColumn(0));
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
                
            }
        });
    }
    
    private void tableOnClickDeterminer(DefaultTableModel dtm)
    {
        if(dtm.getColumnCount() == 1)
        {
            processInvoices();
            processItems();
        }
        else if(dtm.getColumnCount() == 3)
        {
            processItems();
        }
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
        {
            processInvoices();
            processItems();
        }
    }
    
    private void processInvoices()
    {
        int selectedRow = 0;
        if(table_invoices.getRowCount() > 0)
            selectedRow = table_invoices.getSelectedRow();
        
        clearTable(dtm2);
        DB_Invoice invoiceDb = new DB_Invoice();
        
        String invoiceName = table_tables.getValueAt(table_tables.getSelectedRow(), 0).toString();
        
        invoices = invoiceDb.processData(invoiceName, !checkbox_unpaidOnly.isSelected());
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
            table_invoices.setRowSelectionInterval(0, selectedRow < dtm2.getRowCount() ? selectedRow : 0);
        table_invoices.setRowHeight(30);
        
        if(dtm2.getRowCount() > 0)
            processItems();
    }
    
    private void processItems()
    {
        clearTable(dtm3);
        if(table_invoices.getRowCount() > 0)
        {
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
        updateInvoicesStatus();
    }
    
    private void updateInvoicesStatus()
    {
        double totalInvoice = 0.0;
        
        label_total.setText((char)8369 + " " + 0.0);
        
        for(int i = 0; idList != null && i < idList.size(); i++)
        {
            String id = idList.get(i);
            ArrayList<Item> items = invoices.get(id).getItem();
            totalInvoice += updateCurrentInvoiceStatus(items, id);
        }
        label_totalInvoice.setText((char)8369 + " " + totalInvoice);
    }
    
    private double updateCurrentInvoiceStatus(ArrayList<Item> items, String id)
    {
        double total = 0.0;
        
        for(Item item : items)
        {
            double quantity = Double.parseDouble(item.getQuantity());
            double price = Double.parseDouble(item.getPrice());
            total += quantity * price;
        }
        
        if(table_invoices.getValueAt(table_invoices.getSelectedRow(), 0).toString().equals(id))
            label_total.setText((char)8369 + " " + total);
        
        return total;
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
        button_payInvoice = new javax.swing.JButton();
        button_removeItem = new javax.swing.JButton();
        button_removeInvoice = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        label_total = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        label_totalInvoice = new javax.swing.JLabel();
        checkbox_unpaidOnly = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(889, 471));

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

        button_payInvoice.setText("Pay Invoice");
        button_payInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_payInvoiceActionPerformed(evt);
            }
        });

        button_removeItem.setText("Remove Item");
        button_removeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_removeItemActionPerformed(evt);
            }
        });

        button_removeInvoice.setText("Remove Invoice");
        button_removeInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_removeInvoiceActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Total:");

        label_total.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        label_total.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_total.setText("0.0");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Total:");

        label_totalInvoice.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        label_totalInvoice.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_totalInvoice.setText("0.0");

        checkbox_unpaidOnly.setSelected(true);
        checkbox_unpaidOnly.setText("Show Only Unpaid Invoices");
        checkbox_unpaidOnly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkbox_unpaidOnlyActionPerformed(evt);
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_totalInvoice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(checkbox_unpaidOnly)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(button_payInvoice)
                        .addGap(18, 18, 18)
                        .addComponent(button_removeInvoice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_newInvoice))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_removeItem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(button_addItem)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(label_totalInvoice, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                                .addComponent(checkbox_unpaidOnly))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(button_newInvoice)
                                .addComponent(button_payInvoice)
                                .addComponent(button_removeInvoice)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(button_addItem)
                                .addComponent(button_removeItem)
                                .addComponent(label_total, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_newCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_removeCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
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
        
        String customer = su.inputStringUser("Enter customer name: ");
        
        if(customer != null)
        {
            invoiceDb.newCustomer(customer);
            processTables();
        }
    }//GEN-LAST:event_button_newCustomerActionPerformed

    private void button_removeCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_removeCustomerActionPerformed
        if(table_tables.getRowCount() > 0)
        {
            DB_Invoice invoiceDb = new DB_Invoice();
            String customer = table_tables.getValueAt(table_tables.getSelectedRow(), 0).toString();

            invoiceDb.deleteCustomer(customer);
            processTables();
        }
    }//GEN-LAST:event_button_removeCustomerActionPerformed

    private void button_newInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_newInvoiceActionPerformed
        MessageHandler mh = new MessageHandler();
        if(table_tables.getRowCount() > 0)
            addNewInvoice();
        else mh.warning("There are no customers available!");
    }//GEN-LAST:event_button_newInvoiceActionPerformed

    private void button_addItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_addItemActionPerformed
        MessageHandler mh = new MessageHandler();
        if(table_invoices.getRowCount() > 0)
            openItem();
        else mh.warning("There are no invoices available!");
    }//GEN-LAST:event_button_addItemActionPerformed

    private void button_removeInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_removeInvoiceActionPerformed
        if(table_invoices.getRowCount() > 0)
        {
            String id = table_invoices.getValueAt(table_invoices.getSelectedRow(), 0).toString();
            
            DB_Invoice invoiceDb = new DB_Invoice();
            invoiceDb.deleteData(table_tables.getValueAt(table_tables.getSelectedRow(), 0).toString(), false, id);
            processTables();
        }
    }//GEN-LAST:event_button_removeInvoiceActionPerformed

    private void button_removeItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_removeItemActionPerformed
        if(table_items.getRowCount() > 0)
        {
            String id = table_items.getModel().getValueAt(table_items.getSelectedRow(), 0).toString();
            
            DB_Invoice invoiceDb = new DB_Invoice();
            invoiceDb.deleteData(table_tables.getValueAt(table_tables.getSelectedRow(), 0).toString(), true, id);
            processInvoices();
        }
    }//GEN-LAST:event_button_removeItemActionPerformed

    private void button_payInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_payInvoiceActionPerformed
        SystemUtilities su = new SystemUtilities();
        MessageHandler mh = new MessageHandler();
        
        String payment = su.inputNumberUser("Enter Payment:");
        if(payment != null)
        {
            double pay = Double.parseDouble(payment);
            double currentPayment = Double.parseDouble(label_total.getText().substring(2));
            if(pay == currentPayment)
            {
                DB_Invoice invoiceDb = new DB_Invoice();
                
                String id = table_invoices.getValueAt(table_invoices.getSelectedRow(), 0).toString();
                
                Invoice invoice = invoices.get(id);
                invoice.setStatus("Paid");
                
                invoiceDb.updateInvoice(table_tables.getValueAt(table_tables.getSelectedRow(), 0).toString(), invoice);
                
                processInvoices();
                
            }else mh.warning("Payment is too much or not enough!");
        }
    }//GEN-LAST:event_button_payInvoiceActionPerformed

    private void checkbox_unpaidOnlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkbox_unpaidOnlyActionPerformed
        processInvoices();
        processItems();
        updateInvoicesStatus();
        setButtons();
    }//GEN-LAST:event_checkbox_unpaidOnlyActionPerformed

    private void setButtons()
    {
        boolean unpaid = checkbox_unpaidOnly.isSelected();
        
        button_addItem.setEnabled(unpaid);
        button_removeItem.setEnabled(unpaid);
        button_payInvoice.setEnabled(unpaid);
    }
    
    private void addNewInvoice()
    {
        MessageHandler mh = new MessageHandler();
        if(table_tables.getRowCount() > 0)
        {
            DB_Invoice invoiceDb = new DB_Invoice();
            SystemUtilities su = new SystemUtilities();

            String customerName = table_tables.getValueAt(table_tables.getSelectedRow(), 0).toString();

            String newId = invoiceDb.getAvailableIDInvoice(customerName);

            Invoice invoice = new Invoice();
            ArrayList<Item> list = new ArrayList<>();
            Item item = new Item();
            
            invoice.setId_invoice(newId);
            invoice.setDate(su.getCurrentDateTime());
            invoice.setStatus("Unpaid");
            list.add(item);
            invoice.setItem(list);

            invoiceDb.insertInvoice(customerName, invoice);
            processTables();
        } else mh.warning("No customer available!");
    }
    
    private void openItem()
    {
        ItemDialog itemDialog = new ItemDialog(main, this, true, false);
        int x = (main.getWidth() - itemDialog.getWidth()) / 2;
        int y = (main.getHeight() - itemDialog.getHeight()) / 2;
        itemDialog.setLocation(x,y);
        itemDialog.setVisible(true);
    }
    
    public void addItem(Item item)
    {
        SystemUtilities su = new SystemUtilities();
        
        int quantityToBuy = Integer.parseInt(item.getQuantityToBuy());
        int quantityLeft = Integer.parseInt(item.getQuantity()) - quantityToBuy;
        
        item.setQuantityToBuy(null);
        
        item.setQuantity(quantityToBuy + "");
        Invoice invoice = new Invoice();
        invoice.setId_invoice(table_invoices.getValueAt(table_invoices.getSelectedRow(), 0).toString());
        invoice.setDate(su.getCurrentDateTime());
        invoice.setStatus("Unpaid");
        
        ArrayList<Item> theItem = new ArrayList<>();
        theItem.add(item);
        invoice.setItem(theItem);
        
        DB_Invoice invoiceDb = new DB_Invoice();
        invoiceDb.insertInvoice(table_tables.getValueAt(table_tables.getSelectedRow(), 0).toString(), invoice);
        
        item.setQuantity(quantityLeft + "");
        DB_Item itemDb = new DB_Item();
        itemDb.updateData(item);
        processInvoices();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_addItem;
    private javax.swing.JButton button_newCustomer;
    private javax.swing.JButton button_newInvoice;
    private javax.swing.JButton button_payInvoice;
    private javax.swing.JButton button_removeCustomer;
    private javax.swing.JButton button_removeInvoice;
    private javax.swing.JButton button_removeItem;
    private javax.swing.JCheckBox checkbox_unpaidOnly;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_total;
    private javax.swing.JLabel label_totalInvoice;
    private javax.swing.JTable table_invoices;
    private javax.swing.JTable table_items;
    private javax.swing.JTable table_tables;
    // End of variables declaration//GEN-END:variables
}
