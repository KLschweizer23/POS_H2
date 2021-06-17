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
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import pos_h2_database.*;

public class ItemDialog extends javax.swing.JDialog {

    HashMap<String, Item> item;
    private ArrayList<String> idList;
    private DefaultTableModel dtm;
    
    private int rowHeight = 30;
    private int selectedRow = 0;
    
    MainFrame main;
    
    private void createColumns()
    {
        dtm = new DefaultTableModel(0,0)
        {
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
                item.get(id).getPrice()
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
        getRootPane().registerKeyboardAction(e -> {this.dispose();}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), property);
        
            //TABLE CONTROL
        getRootPane().registerKeyboardAction(e -> {
            controlTable(true);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), property);
        
        getRootPane().registerKeyboardAction(e -> {
            controlTable(false);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), property);
        
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
        main.addItem(item.get(itemTable.getValueAt(itemTable.getSelectedRow(), 0).toString()));
        dispose();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 795, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(field_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_add, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_cancel)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(field_filter)
                    .addComponent(button_add, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(button_cancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    public ItemDialog(MainFrame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        main = parent;
        
        createColumns();
        processTable("", 0);
        setupTable();
        setup();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_add;
    private javax.swing.JButton button_cancel;
    private javax.swing.JTextField field_filter;
    private javax.swing.JTable itemTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
