package pos_h2;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import myUtilities.MessageHandler;
import myUtilities.SystemUtilities;
import pos_h2_database.*;

public class ItemDialog extends javax.swing.JDialog {

    private HashMap<String, Item> item;
    private ArrayList<String> idList;
    private DefaultTableModel dtm;
    private DefaultTableModel dtm2;
    
    JFrame frame;
    JDialog dialog;
    
    final private int rowHeight = 30;
    private int selectedRow = 0;
    
    private boolean stockMode = false;
    
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
        itemTable.setModel(dtm);
        dtm.addColumn("ID");
        dtm.addColumn("Item");
        dtm.addColumn("Article");
        dtm.addColumn("Brand");
        dtm.addColumn("Quantity");
        dtm.addColumn("Price");
    }
    private void createColumnsStock()
    {
        dtm2 = new DefaultTableModel(0,0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        displayTable.setModel(dtm2);
        dtm2.addColumn("ID");
        dtm2.addColumn("Item");
        dtm2.addColumn("Quantity");
    }
    
    private void processTable(String keyword, int colIndex)
    {
        DB_Item itemDb = new DB_Item();
        
        clearTable();
        
        idList = itemDb.getIdList();
        
        item = itemDb.processData(keyword, colIndex);
        
        for(int i = 0; i < item.size(); i++)
        {
            String id = idList.get(i);
            String[] rowData =
            {
                item.get(id).getId(),
                item.get(id).getName(),
                item.get(id).getArticle(),
                item.get(id).getBrand(),
                item.get(id).getQuantity(),
                (char)8369 + " " + item.get(id).getPrice()
            };
            dtm.addRow(rowData);
        }
        if(itemTable.getRowCount() > 0)
                itemTable.setRowSelectionInterval(0, selectedRow > itemTable.getRowCount() - 1 ? itemTable.getRowCount() - 1 : selectedRow);
        itemTable.setRowHeight(rowHeight);
        adjustViewport();
    }
    private void clearTable()
    {
        for(int i = 0; dtm.getRowCount() != 0;)
            dtm.removeRow(i);
    }
    private void setupTable()
    {
        itemTable.addMouseListener(new MouseListener() {
            private boolean onTable = false;
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if(onTable)
                {
                    Point p = e.getPoint();
                    int y = p.y / rowHeight;
                    if(y < dtm.getRowCount())
                            addData();
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
        
        itemTable.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int y = p.y / rowHeight;
                if(y < dtm.getRowCount())
                    itemTable.setRowSelectionInterval(0, y);
            }
        });
    }
    private void setup()
    {
        int property = JComponent.WHEN_IN_FOCUSED_WINDOW;
        //COMMANDS
            //ESC
        getRootPane().registerKeyboardAction(e -> {
            this.dispose();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), property);
        
            //TABLE CONTROL
        getRootPane().registerKeyboardAction(e -> {
            controlTable(true);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), property);
        
        getRootPane().registerKeyboardAction(e -> {
            controlTable(false);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), property);
        
            //Add Data
        getRootPane().registerKeyboardAction(e ->{
            addData();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), property);
    }
    private void controlTable(boolean goDown)
    {
        if(itemTable.getRowCount() > 0)
            if(goDown)
                if(itemTable.getSelectedRow() + 1 < itemTable.getRowCount())
                    itemTable.setRowSelectionInterval(0, itemTable.getSelectedRow() + 1);
                else
                    itemTable.setRowSelectionInterval(0,0);
            else        
                if(itemTable.getSelectedRow() != 0)
                    itemTable.setRowSelectionInterval(0, itemTable.getSelectedRow() - 1);
                else
                    itemTable.setRowSelectionInterval(0,itemTable.getRowCount() - 1);
        
        selectedRow = itemTable.getSelectedRow();
        adjustViewport();
    }
    private void adjustViewport()
    {
        JViewport viewport = (JViewport)itemTable.getParent();
        Rectangle rect = itemTable.getCellRect(itemTable.getSelectedRow(), 0, true);
        Point pt = viewport.getViewPosition();
        rect.setLocation(rect.x-pt.x, (rect.y-pt.y) + rowHeight);
        itemTable.scrollRectToVisible(rect);
    }
    private void addData()
    {
        MessageHandler mh = new MessageHandler();
        if(itemTable.getRowCount() > 0)
        {
            Item itemObj = item.get(itemTable.getValueAt(itemTable.getSelectedRow(), 0).toString());

            if(!stockMode && dialog == null)
            {
                if(!itemObj.getQuantity().equals("0"))
                {
                    MainFrame main = (MainFrame)frame;
                    main.addItem(itemObj);
                    dispose();
                } else mh.warning("There's not enough stocks for this item!");
            }
            else if(stockMode && dialog == null)
            {
                SystemUtilities su = new SystemUtilities();
                String quantity = su.inputNumberUser();
                if(quantity != null)
                {
                    String[] rowData = {itemObj.getId(), itemObj.getName(), quantity};
                    dtm2.addRow(rowData);

                    if(displayTable.getRowCount() > 0)
                            displayTable.setRowSelectionInterval(0, 0);
                    displayTable.setRowHeight(20);
                }
            }
            else if(!stockMode && dialog != null)
            {
                if(!itemObj.getQuantity().equals("0"))
                {
                    InvoiceDialog invoiceDialog = (InvoiceDialog)dialog;
                    
                }
            }
        }
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
        itemTable = new javax.swing.JTable();
        button_cancel = new javax.swing.JButton();
        button_add = new javax.swing.JButton();
        field_filter = new javax.swing.JTextField();
        panel_display = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        displayTable = new javax.swing.JTable();
        button_confirm = new javax.swing.JButton();
        button_remove = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jScrollPane1.setFocusable(false);

        itemTable.setFillsViewportHeight(true);
        itemTable.setFocusable(false);
        itemTable.setRequestFocusEnabled(false);
        itemTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        itemTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        itemTable.getTableHeader().setResizingAllowed(false);
        itemTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(itemTable);

        button_cancel.setText("Cancel");
        button_cancel.setFocusable(false);
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        button_add.setText("Add");
        button_add.setFocusable(false);
        button_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_addActionPerformed(evt);
            }
        });

        field_filter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                field_filterKeyReleased(evt);
            }
        });

        panel_display.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPane3.setFocusable(false);

        displayTable.setFillsViewportHeight(true);
        displayTable.setFocusable(false);
        displayTable.setRequestFocusEnabled(false);
        displayTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        displayTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        displayTable.getTableHeader().setResizingAllowed(false);
        displayTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(displayTable);

        button_confirm.setText("Confirm");
        button_confirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_confirmActionPerformed(evt);
            }
        });

        button_remove.setText("Remove");
        button_remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_removeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_displayLayout = new javax.swing.GroupLayout(panel_display);
        panel_display.setLayout(panel_displayLayout);
        panel_displayLayout.setHorizontalGroup(
            panel_displayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_displayLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(button_remove)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                .addComponent(button_confirm)
                .addContainerGap())
        );
        panel_displayLayout.setVerticalGroup(
            panel_displayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_displayLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(panel_displayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_confirm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button_remove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(field_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 234, Short.MAX_VALUE)
                        .addComponent(button_add, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_cancel))
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_display, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(field_filter)
                            .addComponent(button_add, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(button_cancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panel_display, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void field_filterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_field_filterKeyReleased
        processTable(field_filter.getText(), 1);
    }//GEN-LAST:event_field_filterKeyReleased

    private void button_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_addActionPerformed
        addData();
    }//GEN-LAST:event_button_addActionPerformed

    private void button_removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_removeActionPerformed
        if(displayTable.getRowCount() > 0)
        {
            int selectedRow = displayTable.getSelectedRow();
            dtm2.removeRow(selectedRow);
            if(displayTable.getRowCount() > 0)
                displayTable.setRowSelectionInterval(0, selectedRow == displayTable.getRowCount() - 1 ? selectedRow : selectedRow - 1);
        }
    }//GEN-LAST:event_button_removeActionPerformed

    private void button_confirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_confirmActionPerformed
        MessageHandler mh = new MessageHandler();
        int choice = mh.confirm("<html>Press <b>OK</b> to proceed...</html>");
        if(choice == JOptionPane.YES_OPTION)
        {
            for(int i = 0; i < displayTable.getRowCount(); i++)
            {
                Item itemObj = item.get(displayTable.getValueAt(i, 0).toString());
                int currentQuantity = Integer.parseInt(itemObj.getQuantity());
                int additionalQuantity = Integer.parseInt(displayTable.getValueAt(i, 2).toString());
                itemObj.setQuantity((currentQuantity + additionalQuantity) + "");
                DB_Item itemDb = new DB_Item();
                itemDb.updateData(itemObj);
            }
            dispose();
        }
    }//GEN-LAST:event_button_confirmActionPerformed
    public ItemDialog(JFrame parent, JDialog secondParent, boolean modal, boolean stockMode) {
        super(parent, modal);
        initComponents();
        
        dialog = secondParent;
        frame = parent;
        
        this.stockMode = stockMode;
        if(!this.stockMode || dialog != null)
        {
            jPanel1.remove(panel_display);
            revalidate();
            repaint();
        }
        
        createColumnsStock();
        createColumns();
        processTable("", 0);
        setupTable();
        setup();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_add;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_confirm;
    private javax.swing.JButton button_remove;
    private javax.swing.JTable displayTable;
    private javax.swing.JTextField field_filter;
    private javax.swing.JTable itemTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel panel_display;
    // End of variables declaration//GEN-END:variables
}
