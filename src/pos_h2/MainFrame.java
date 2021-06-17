package pos_h2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import pos_h2_database.Item;

public class MainFrame extends javax.swing.JFrame {

    static MainFrame myFrame;
    
    ArrayList<String> idList = new ArrayList();
    HashMap<String, Item> item = new HashMap<>();
    DefaultTableModel dtm;
    
    int selectedRow = -1;
    
    public MainFrame() {
        initComponents();
        
        setup();
        createColumns();
        setHeader(displayTable, Color.WHITE, new Dimension(0,30), Color.black);
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
        displayTable.setModel(dtm);
        dtm.addColumn("ID");
        dtm.addColumn("Item");
        dtm.addColumn("Article");
        dtm.addColumn("Brand");
        dtm.addColumn("Quantity");
        dtm.addColumn("Price");
    }
    private void processTable()
    {
        if(!item.isEmpty())
        {
            clearTable();
            for(int i = 0; i < item.size(); i++)
            {
                String id = idList.get(i);
                String[] rowData =
                {
                    item.get(id).getId(),
                    item.get(id).getName(),
                    item.get(id).getArticle(),
                    item.get(id).getBrand(),
                    item.get(id).getQuantityToBuy(),
                    (char) 8369 + " " + (Double.parseDouble(item.get(id).getPrice()) * Integer.parseInt(item.get(id).getQuantityToBuy()))
                };
                dtm.addRow(rowData);
            }
            if(displayTable.getRowCount() > 0)
                displayTable.setRowSelectionInterval(0, selectedRow);
            displayTable.setRowHeight(30);
            adjustViewport();
        }
    }
    private void setHeader(JTable table, Color background, Dimension dim, Color foreground)
    {
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(background);
        headerRenderer.setPreferredSize(dim);
        headerRenderer.setForeground(foreground);
        
        for (int i = 0; i < table.getModel().getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
    }
    private void clearTable()
    {
        for(int i = 0; dtm.getRowCount() != 0;)
            dtm.removeRow(i);
    }
    private void setup()
    {
        displayTable.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int y = p.y / 30;
                if(y < dtm.getRowCount())
                    displayTable.setRowSelectionInterval(0, y);
            }
        });
        //FONTS || TEXTS
        Font big = new Font("Autobus Bold", Font.PLAIN, 65);
        jLabel1.setFont(big);
        button_add.setIcon(getScaledImageIcon("plus_icon.png", 25, 25));
        button_delete.setIcon(getScaledImageIcon("minus_icon.png", 25, 25));
        
        //COMMANDS
        Action openItem = new AbstractAction("openItem") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                openItemDialog();
            }
        };
        button_add.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "openItem");
        button_add.getActionMap().put("openItem", openItem);
                
        int property = JComponent.WHEN_IN_FOCUSED_WINDOW;
        
        getRootPane().registerKeyboardAction(e -> {
            deleteItemAtTable();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), property);
        
        getRootPane().registerKeyboardAction(e -> {
            controlTable(true);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), property);
        
        getRootPane().registerKeyboardAction(e -> {
            controlTable(false);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), property);
        
        getRootPane().registerKeyboardAction(e ->{
            adjustQuantityToBuy(item.get(displayTable.getValueAt(displayTable.getSelectedRow(), 0).toString()), 1);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0), property);  
        
        getRootPane().registerKeyboardAction(e ->{
            adjustQuantityToBuy(item.get(displayTable.getValueAt(displayTable.getSelectedRow(), 0).toString()), 1);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), property);
                
        getRootPane().registerKeyboardAction(e ->{
            adjustQuantityToBuy(item.get(displayTable.getValueAt(displayTable.getSelectedRow(), 0).toString()), -1);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), property);
    }
    private void controlTable(boolean goDown)
    {
        if(displayTable.getRowCount() > 0)
            if(goDown)
                if(displayTable.getSelectedRow() + 1 < displayTable.getRowCount())
                    displayTable.setRowSelectionInterval(0, displayTable.getSelectedRow() + 1);
                else
                    displayTable.setRowSelectionInterval(0,0);
            else        
                if(displayTable.getSelectedRow() != 0)
                    displayTable.setRowSelectionInterval(0, displayTable.getSelectedRow() - 1);
                else
                    displayTable.setRowSelectionInterval(0,displayTable.getRowCount() - 1);
        
        selectedRow = displayTable.getSelectedRow();
        adjustViewport();
    }
    private void adjustViewport()
    {
        JViewport viewport = (JViewport)displayTable.getParent();
        Rectangle rect = displayTable.getCellRect(displayTable.getSelectedRow(), 0, true);
        Point pt = viewport.getViewPosition();
        rect.setLocation(rect.x-pt.x, (rect.y-pt.y) + 30);
        displayTable.scrollRectToVisible(rect);
    }
    private void adjustQuantityToBuy(Item item, int amount)
    {
        int current = Integer.parseInt(item.getQuantityToBuy());
        if(!(current == 1 && amount == -1))
            item.setQuantityToBuy(Integer.parseInt(item.getQuantityToBuy()) + amount + "");
        selectedRow = displayTable.getSelectedRow();
        processTable();
    }
    
    public void addItem(Item item)
    {
        String id = item.getId();
        boolean isAvailable = true;
        
        for(int i = 0; i < idList.size(); i++)
            if(idList.get(i).equals(id))
                isAvailable = false;
        
        if(isAvailable)
        {
            item.setQuantityToBuy("1");
        
            this.idList.add(id);
            this.item.put(id, item);
            selectedRow++;
            processTable();
        }
    }
    private void deleteItemAtTable()
    {
        if(dtm.getRowCount() > 0)
        {
            String id = displayTable.getValueAt(displayTable.getSelectedRow(), 0).toString();

            item.remove(id);
            dtm.removeRow(displayTable.getSelectedRow());

            for(int i = 0; i < idList.size(); i++)
                if(id.equals(idList.get(i)))
                    idList.remove(i);

            selectedRow = selectedRow == dtm.getRowCount() - 1 ? selectedRow : selectedRow - 1;

            processTable();
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
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        displayTable = new javax.swing.JTable();
        button_add = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_salesClerk = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_date = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuItem_add = new javax.swing.JMenuItem();
        menuItem_remove = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        menuItem_findPrice = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        menuItem_itemDb = new javax.swing.JMenuItem();
        menuItem_salesClerkDb = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Point of Sale");
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });

        jPanel1.setBackground(new java.awt.Color(33, 33, 33));
        jPanel1.setPreferredSize(new java.awt.Dimension(1406, 757));

        jPanel3.setBackground(new java.awt.Color(202, 38, 38));
        jPanel3.setFocusable(false);

        jLabel1.setBackground(new java.awt.Color(224, 224, 224));
        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(224, 224, 224));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Point of Sale");
        jLabel1.setFocusable(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setFocusable(false);

        jScrollPane1.setBackground(new java.awt.Color(224, 224, 224));
        jScrollPane1.setBorder(null);

        displayTable.setBackground(new java.awt.Color(255, 255, 255));
        displayTable.setFillsViewportHeight(true);
        displayTable.setFocusable(false);
        displayTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        displayTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        displayTable.setShowGrid(false);
        displayTable.setShowHorizontalLines(true);
        displayTable.getTableHeader().setResizingAllowed(false);
        displayTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(displayTable);

        button_add.setBackground(new java.awt.Color(204, 51, 51));
        button_add.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        button_add.setForeground(new java.awt.Color(224, 224, 224));
        button_add.setBorder(null);
        button_add.setFocusable(false);
        button_add.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        button_add.setPreferredSize(new java.awt.Dimension(15, 15));
        button_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_addActionPerformed(evt);
            }
        });

        button_delete.setBackground(new java.awt.Color(173, 37, 37));
        button_delete.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        button_delete.setForeground(new java.awt.Color(224, 224, 224));
        button_delete.setBorder(null);
        button_delete.setFocusable(false);
        button_delete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        button_delete.setPreferredSize(new java.awt.Dimension(15, 15));
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_add, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(button_add, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(204, 204, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setFocusable(false);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Sales Clerk");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText(":");

        label_salesClerk.setBackground(new java.awt.Color(255, 255, 255));
        label_salesClerk.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        label_salesClerk.setForeground(new java.awt.Color(0, 0, 0));
        label_salesClerk.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_salesClerk.setText("Name");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Date");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText(":");

        label_date.setBackground(new java.awt.Color(255, 255, 255));
        label_date.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        label_date.setForeground(new java.awt.Color(0, 0, 0));
        label_date.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_date.setText("Date");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_salesClerk)
                    .addComponent(label_date))
                .addContainerGap(208, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(label_salesClerk))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(label_date))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jMenu1.setText("File");

        menuItem_add.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuItem_add.setText("Add Item");
        menuItem_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_addActionPerformed(evt);
            }
        });
        jMenu1.add(menuItem_add);

        menuItem_remove.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItem_remove.setText("Remove Item");
        jMenu1.add(menuItem_remove);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        menuItem_findPrice.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuItem_findPrice.setText("Find Price");
        jMenu2.add(menuItem_findPrice);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Database");

        menuItem_itemDb.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuItem_itemDb.setText("Item Database");
        menuItem_itemDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_itemDbActionPerformed(evt);
            }
        });
        jMenu3.add(menuItem_itemDb);

        menuItem_salesClerkDb.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuItem_salesClerkDb.setText("Sales Clerk Database");
        menuItem_salesClerkDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_salesClerkDbActionPerformed(evt);
            }
        });
        jMenu3.add(menuItem_salesClerkDb);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 733, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_addActionPerformed
        openItemDialog();
    }//GEN-LAST:event_button_addActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        deleteItemAtTable();
    }//GEN-LAST:event_button_deleteActionPerformed

    private void menuItem_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_addActionPerformed
        openItemDialog();
    }//GEN-LAST:event_menuItem_addActionPerformed

    private void menuItem_salesClerkDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_salesClerkDbActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_menuItem_salesClerkDbActionPerformed

    private void menuItem_itemDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_itemDbActionPerformed
        ItemDb itemDb = new ItemDb(this, true);
        int x = (getWidth() - itemDb.getWidth()) / 2;
        int y = (getHeight() - itemDb.getHeight()) / 2;
        itemDb.setLocation(x,y);
        itemDb.setVisible(true);
    }//GEN-LAST:event_menuItem_itemDbActionPerformed

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        
    }//GEN-LAST:event_formWindowGainedFocus
    
    private void openItemDialog()
    {        
        ItemDialog item = new ItemDialog(this, true);
        int x = (getWidth() - item.getWidth()) / 2;
        int y = (getHeight() - item.getHeight()) / 2;
        item.setLocation(x,y);
        item.setVisible(true);
    }
    private ImageIcon getScaledImageIcon(String imageName, int height, int width)
    {
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource("/Images/" + imageName)).getImage());
        Image image = imageIcon.getImage();
        Image newImage = image.getScaledInstance(height, width, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newImage);
        return imageIcon;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                myFrame = new MainFrame();
                myFrame.setVisible(true);
                myFrame.setExtendedState(myFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                myFrame.setMaximumSize(myFrame.getSize());
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_add;
    private javax.swing.JButton button_delete;
    private javax.swing.JTable displayTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_date;
    private javax.swing.JLabel label_salesClerk;
    private javax.swing.JMenuItem menuItem_add;
    private javax.swing.JMenuItem menuItem_findPrice;
    private javax.swing.JMenuItem menuItem_itemDb;
    private javax.swing.JMenuItem menuItem_remove;
    private javax.swing.JMenuItem menuItem_salesClerkDb;
    // End of variables declaration//GEN-END:variables
}
