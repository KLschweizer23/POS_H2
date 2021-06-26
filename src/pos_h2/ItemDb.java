package pos_h2;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import myUtilities.MessageHandler;
import pos_h2_database.DB_Item;
import pos_h2_database.Item;

public class ItemDb extends javax.swing.JDialog {

    MessageHandler mh = new MessageHandler();
    
    
    private int rowHeight = 30;
     
    private ArrayList<String> idList = new ArrayList<>();
    private HashMap<String, Item> item = new HashMap<>();
    
    DefaultTableModel dtm;
    
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
    private void setup()
    {
        //COMMANDS

    }
    private void processTable(String keyword, int col)
    {
        DB_Item itemDb = new DB_Item();
        
        clearTable();
        
        idList = itemDb.getIdList();
        item = itemDb.processData(keyword, col);
        
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
        itemTable.setRowHeight(rowHeight);
    }
    private void clearTable()
    {
        for(int i = 0; dtm.getRowCount() != 0;)
        {
            dtm.removeRow(i);
        }
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
                        setFields(item.get(itemTable.getValueAt(y, 0)));
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
        jLabel1 = new javax.swing.JLabel();
        button_add = new javax.swing.JButton();
        button_update = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        field_item = new javax.swing.JTextField();
        field_article = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        field_brand = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        field_quantity = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        field_price = new javax.swing.JTextField();
        button_clear = new javax.swing.JButton();
        label_labelId = new javax.swing.JLabel();
        label_id = new javax.swing.JLabel();

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

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Item");
        jLabel1.setFocusable(false);

        button_add.setText("Add");
        button_add.setFocusable(false);
        button_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_addActionPerformed(evt);
            }
        });

        button_update.setText("Update");
        button_update.setEnabled(false);
        button_update.setFocusable(false);
        button_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_updateActionPerformed(evt);
            }
        });

        button_delete.setText("Delete");
        button_delete.setEnabled(false);
        button_delete.setFocusable(false);
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        field_item.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                field_itemKeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Article");
        jLabel2.setFocusable(false);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Brand");
        jLabel3.setFocusable(false);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("Quantity");
        jLabel4.setFocusable(false);

        field_quantity.setText("0");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Price");
        jLabel5.setFocusable(false);

        button_clear.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        button_clear.setText("CLR");
        button_clear.setFocusable(false);
        button_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_clearActionPerformed(evt);
            }
        });

        label_labelId.setFont(new java.awt.Font("Tahoma", 2, 8)); // NOI18N
        label_labelId.setText("Id:");
        label_labelId.setFocusable(false);

        label_id.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        label_id.setText("000");
        label_id.setFocusable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(field_item)
                    .addComponent(field_article)
                    .addComponent(field_brand)
                    .addComponent(field_quantity)
                    .addComponent(field_price)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(button_add, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                        .addComponent(button_update, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                        .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_labelId)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_id, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_clear)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(button_clear)
                    .addComponent(label_labelId)
                    .addComponent(label_id))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(field_item, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(field_article, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(field_brand, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(field_quantity, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(field_price, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_add)
                    .addComponent(button_update)
                    .addComponent(button_delete))
                .addGap(45, 45, 45))
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

    private void button_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_addActionPerformed
        boolean goodFields =
                noErrorText(field_item) &&
                noErrorText(field_article) &&
                noErrorText(field_brand) &&
                noErrorNumber(field_quantity) &&
                noErrorNumber(field_price)
                ;
        if(goodFields)
        {
            DB_Item itemDb = new DB_Item();
            
            Item item = getItem();
            
            itemDb.insertData(item);
            processTable("", 0);
            emptyFields();
        } else mh.warning("Please fill the fields properly!");
    }//GEN-LAST:event_button_addActionPerformed

    private void button_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clearActionPerformed
        emptyFields();
    }//GEN-LAST:event_button_clearActionPerformed

    private void button_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_updateActionPerformed
        boolean goodFields =
                noErrorText(field_item) &&
                noErrorText(field_article) &&
                noErrorText(field_brand) &&
                noErrorNumber(field_quantity) &&
                noErrorNumber(field_price)
                ;
        if(goodFields)
        {
            DB_Item itemDb = new DB_Item();
            
            Item item = getItem();
            item.setId(label_id.getText());
            itemDb.updateData(item);
            processTable("", 0);
            emptyFields();
        } else mh.warning("Please fill the fields properly!");
    }//GEN-LAST:event_button_updateActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        deleteData();
    }//GEN-LAST:event_button_deleteActionPerformed

    private void field_itemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_field_itemKeyReleased
        String keyword = field_item.getText();
        processTable(keyword, 1);
    }//GEN-LAST:event_field_itemKeyReleased
    private void deleteData()
    {
        DB_Item itemDb = new DB_Item();
        
        Item item = getItem();
        item.setId(label_id.getText());
        itemDb.deleteData(item);
        processTable("", 0);
        emptyFields();
    }
    private void emptyFields()
    {
        label_id.setText("000");
        
        field_item.setText("");
        setTextFieldFormat(field_item, true);
        field_article.setText("");
        setTextFieldFormat(field_article, true);
        field_brand.setText("");
        setTextFieldFormat(field_brand, true);
        field_quantity.setText("0");
        setTextFieldFormat(field_quantity, true);
        field_price.setText("");
        setTextFieldFormat(field_price, true);
        
        field_quantity.setEnabled(false);
        
        button_add.setEnabled(true);
        button_update.setEnabled(false);
        button_delete.setEnabled(false);
    }
    private boolean numberChecker(String s)
    {
        boolean valid = true;
        int dot = 0;
        for(int i = 0; i < s.length(); i++)
        {
            switch(s.charAt(i))
            {
                case '0':case '1':case '2':case '3':case '4':case '5':case '6':case '7':case '8':case '9':case '.':
                    if(s.charAt(i)=='.')
                        dot++;
                    break;
                default:
                    valid = false;
                    break;
            }
        }
        if(dot > 1)
            valid = false;
        
        return valid;
    }
    private boolean emptyChecker(String s)
    {
        boolean isEmpty = true;
        
        if(s.length() > 0)
            isEmpty = false;
        return isEmpty;
    }
    private boolean noErrorText(JTextField textField)
    {
        if(emptyChecker(textField.getText().trim().strip()))
        {
            setTextFieldFormat(textField, false);
            return false;
        }
        else
        {
            setTextFieldFormat(textField, true);
            return true;
        }
    }
    private void setTextFieldFormat(JTextField textField, boolean noError)
    {
        if(noError)
        {
            textField.setBackground(Color.white);
            textField.setForeground(Color.black);
        }
        else
        {
            textField.setBackground(Color.red);
            textField.setForeground(Color.white);
        }
    }
    private boolean noErrorNumber(JTextField textField)
    {
        if(emptyChecker(textField.getText().trim().strip()) || !numberChecker(textField.getText().trim().strip()))
        {
            textField.setBackground(Color.red);
            textField.setForeground(Color.white);
            return false;
        }
        else
        {
            textField.setBackground(Color.white);
            textField.setForeground(Color.black);
            return true;
        }
    }
    private void setFields(Item item)
    {
        label_id.setText(item.getId());
        
        field_item.setText(item.getName());
        field_brand.setText(item.getBrand());
        field_article.setText(item.getArticle());
        field_quantity.setText(item.getQuantity());
        field_price.setText(item.getPrice());
        
        field_quantity.setEnabled(true);
        
        button_add.setEnabled(false);
        button_update.setEnabled(true);
        button_delete.setEnabled(true);
    }
    private Item getItem()
    {
        Item item = new Item();
        item.setName(field_item.getText());
        item.setArticle(field_article.getText());
        item.setBrand(field_brand.getText());
        item.setQuantity(field_quantity.getText());
        item.setPrice(field_price.getText());
        item.setSold("0");
        
        return item;
    }
    /**
     * Creates new form ItemDb
     */
    public ItemDb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        createColumns();
        processTable("", 0);
        setupTable();
        setup();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_add;
    private javax.swing.JButton button_clear;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_update;
    private javax.swing.JTextField field_article;
    private javax.swing.JTextField field_brand;
    private javax.swing.JTextField field_item;
    private javax.swing.JTextField field_price;
    private javax.swing.JTextField field_quantity;
    private javax.swing.JTable itemTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_id;
    private javax.swing.JLabel label_labelId;
    // End of variables declaration//GEN-END:variables
}
