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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import myUtilities.MessageHandler;
import pos_h2_database.Clerk;
import pos_h2_database.DB_Transaction;

import pos_h2_database.Item;
import pos_h2_database.Transaction;

public class MainFrame extends javax.swing.JFrame {
    static MainFrame myFrame;
    
    ArrayList<String> idList = new ArrayList();
    HashMap<String, Item> item = new HashMap<>();
    
    Clerk currentClerk;
    
    DefaultTableModel dtm;
    
    int selectedRow = -1;
    
    public MainFrame() {
        initComponents();
        
        do
            loginForm();
        while(currentClerk == null);
        
        setDetails();        
        setup();
        createColumns();
        setHeader(table_display, Color.WHITE, new Dimension(0,30), Color.black);
    }
    private void setDetails()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
        LocalDateTime now = LocalDateTime.now();  
        String date = dtf.format(now);
        
        String clerkName = currentClerk.getFirstname() + " " + currentClerk.getMiddlename().charAt(0) + ". " + currentClerk.getLastname().charAt(0) + ".";
        
        label_salesClerk.setText(clerkName);
        label_date.setText(date);
        
    }
    private void loginForm()
    {
        LoginForm login = new LoginForm(this, true);
        int x = (getWidth() - login.getWidth()) / 2;
        int y = (getHeight() - login.getHeight()) / 2;
        login.setLocation(x,y);
        login.setVisible(true);
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
        table_display.setModel(dtm);
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
            if(table_display.getRowCount() > 0)
                table_display.setRowSelectionInterval(0, selectedRow);
            table_display.setRowHeight(30);
            adjustViewport();
            updateStatus();
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
        table_display.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int y = p.y / 30;
                if(y < dtm.getRowCount())
                    table_display.setRowSelectionInterval(0, y);
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
        getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK), "openItem");
        getRootPane().getActionMap().put("openItem", openItem);
                
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
            adjustQuantityToBuy(item.get(table_display.getValueAt(table_display.getSelectedRow(), 0).toString()), 1);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0), property);  
        
        getRootPane().registerKeyboardAction(e ->{
            adjustQuantityToBuy(item.get(table_display.getValueAt(table_display.getSelectedRow(), 0).toString()), 1);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), property);
                
        getRootPane().registerKeyboardAction(e ->{
            adjustQuantityToBuy(item.get(table_display.getValueAt(table_display.getSelectedRow(), 0).toString()), -1);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), property);
        
        getRootPane().registerKeyboardAction(e ->{
            makeTransaction();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK), property);
        
        getRootPane().registerKeyboardAction(e ->{
            cancelTransaction();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, KeyEvent.CTRL_DOWN_MASK), property);   
        
        getRootPane().registerKeyboardAction(e ->{
            itemDb();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK), property);
                
        getRootPane().registerKeyboardAction(e ->{
            salesClerkDb();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK), property);
    }
    private void controlTable(boolean goDown)
    {
        if(table_display.getRowCount() > 0)
            if(goDown)
                if(table_display.getSelectedRow() + 1 < table_display.getRowCount())
                    table_display.setRowSelectionInterval(0, table_display.getSelectedRow() + 1);
                else
                    table_display.setRowSelectionInterval(0,0);
            else        
                if(table_display.getSelectedRow() != 0)
                    table_display.setRowSelectionInterval(0, table_display.getSelectedRow() - 1);
                else
                    table_display.setRowSelectionInterval(0,table_display.getRowCount() - 1);
        
        selectedRow = table_display.getSelectedRow();
        adjustViewport();
    }
    private void adjustViewport()
    {
        JViewport viewport = (JViewport)table_display.getParent();
        Rectangle rect = table_display.getCellRect(table_display.getSelectedRow(), 0, true);
        Point pt = viewport.getViewPosition();
        rect.setLocation(rect.x-pt.x, (rect.y-pt.y) + 30);
        table_display.scrollRectToVisible(rect);
    }
    private void adjustQuantityToBuy(Item item, int amount)
    {
        int current = Integer.parseInt(item.getQuantityToBuy());
        if(!(current == 1 && amount == -1))
            item.setQuantityToBuy(Integer.parseInt(item.getQuantityToBuy()) + amount + "");
        selectedRow = table_display.getSelectedRow();
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
            updateStatus();
        }
    }
    private void deleteItemAtTable()
    {
        if(dtm.getRowCount() > 0)
        {
            String id = table_display.getValueAt(table_display.getSelectedRow(), 0).toString();

            item.remove(id);
            dtm.removeRow(table_display.getSelectedRow());

            for(int i = 0; i < idList.size(); i++)
                if(id.equals(idList.get(i)))
                    idList.remove(i);

            selectedRow = selectedRow == dtm.getRowCount() - 1 ? selectedRow : selectedRow - 1;
            
            updateStatus();
            
            processTable();
        }
    }
    private void updateStatus()
    {
        int totalItems = item.size();
        double totalAmount = getTotalAmount();
        
        label_totalItem.setText(totalItems + "");
        label_balance.setText((char)8369 + " " + totalAmount);
        label_totalAmount.setText((char)8369 + " " + totalAmount);
        checkBalance(totalAmount);
    }
    private void checkBalance(Double totalAmount)
    {
        double payment = Double.parseDouble(field_payment.getText().isBlank() ? "0" : field_payment.getText());
        
        if(payment >= totalAmount)
        {
            jLabel16.setText(payment - totalAmount + "");
            label_balance.setForeground(Color.green);
            label_balance.setText((char)8369 + " 0.0");
        }
        else
        {
            jLabel16.setText(totalAmount - payment + "");
            label_balance.setForeground(Color.red);
            label_balance.setText((char)8369 + " " + (totalAmount - payment));
        }
    }
    private Double getTotalAmount()
    {
        Double totalAmount = 0.0;
        for(int i = 0; i < idList.size(); i++)
        {
            int quantity = Integer.parseInt(item.get(idList.get(i)).getQuantityToBuy());
            double price = Double.parseDouble(item.get(idList.get(i)).getPrice());
            totalAmount += quantity * price;
        }
        return totalAmount;
    }
    private void makeTransaction()
    {
        MessageHandler mh = new MessageHandler();
        if(item.size() > 0)
        {
            for(int i = 0; i < item.size(); i++)
            {
                String id = table_display.getValueAt(i, 0).toString();
                
                DB_Transaction tDb = new DB_Transaction();
                Transaction transaction = new Transaction();
                
                transaction.setT_id("");
                transaction.setT_clerk(currentClerk.getName());
                transaction.setDate(label_date.getText());
                
                Item newItem = new Item();
                ArrayList<Item> listOfItem = new ArrayList<>();
                
                newItem.setId(item.get(id).getId());
                newItem.setQuantityToBuy(item.get(id).getQuantityToBuy());
                newItem.setPrice(item.get(id).getPrice());
                
                listOfItem.add(newItem);
                
                transaction.setItem(listOfItem);
                
                tDb.insertData(transaction);
            }
        } else mh.warning("There is no item to buy!");
    }
    private void cancelTransaction()
    {
        item.clear();
        idList.clear();
        field_payment.setText("");
    }
    public void setCurrentClerk (Clerk clerk)
    {
        currentClerk = clerk;
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
        table_display = new javax.swing.JTable();
        button_add = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_salesClerk = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_date = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_totalItem = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_totalAmount = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_balance = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_history = new javax.swing.JTable();
        button_makeTransaction = new javax.swing.JButton();
        button_cancelTransaction = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        jLabel15 = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        jSeparator8 = new javax.swing.JSeparator();
        jLabel16 = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        jLabel17 = new javax.swing.JLabel();
        field_payment = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuItem_add = new javax.swing.JMenuItem();
        menuItem_remove = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
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

        table_display.setBackground(new java.awt.Color(255, 255, 255));
        table_display.setFillsViewportHeight(true);
        table_display.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_display.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_display.setShowGrid(false);
        table_display.setShowHorizontalLines(true);
        table_display.getTableHeader().setResizingAllowed(false);
        table_display.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_display);

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
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap(898, Short.MAX_VALUE)
                        .addComponent(button_add, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
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
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(204, 204, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setFocusable(false);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Sales Clerk");
        jLabel2.setFocusable(false);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText(":");
        jLabel3.setFocusable(false);

        label_salesClerk.setBackground(new java.awt.Color(255, 255, 255));
        label_salesClerk.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        label_salesClerk.setForeground(new java.awt.Color(255, 255, 255));
        label_salesClerk.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_salesClerk.setText("Name");
        label_salesClerk.setFocusable(false);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Date");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText(":");
        jLabel5.setFocusable(false);

        label_date.setBackground(new java.awt.Color(255, 255, 255));
        label_date.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        label_date.setForeground(new java.awt.Color(255, 255, 255));
        label_date.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_date.setText("Date");
        label_date.setFocusable(false);

        jSeparator2.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator2.setForeground(new java.awt.Color(255, 255, 255));

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Total Item");
        jLabel6.setFocusable(false);

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText(":");
        jLabel7.setFocusable(false);

        label_totalItem.setBackground(new java.awt.Color(255, 255, 255));
        label_totalItem.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        label_totalItem.setForeground(new java.awt.Color(255, 255, 255));
        label_totalItem.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_totalItem.setText("0");
        label_totalItem.setFocusable(false);

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Total");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText(":");
        jLabel9.setFocusable(false);

        label_totalAmount.setBackground(new java.awt.Color(255, 255, 255));
        label_totalAmount.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        label_totalAmount.setForeground(new java.awt.Color(255, 255, 255));
        label_totalAmount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_totalAmount.setText((char)8369 + " 0.00");
        label_totalAmount.setFocusable(false);

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Balance");
        jLabel10.setFocusable(false);

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText(":");
        jLabel11.setFocusable(false);

        label_balance.setBackground(new java.awt.Color(255, 255, 255));
        label_balance.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        label_balance.setForeground(new java.awt.Color(255, 255, 255));
        label_balance.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_balance.setText((char)8369 + " 0.00");
        label_balance.setFocusable(false);

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Payment");
        jLabel12.setFocusable(false);

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText(":");
        jLabel13.setFocusable(false);

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText((char)8369 + "");
        jLabel23.setFocusable(false);

        jSeparator4.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator4.setForeground(new java.awt.Color(255, 255, 255));

        jScrollPane2.setFocusable(false);

        table_history.setFillsViewportHeight(true);
        table_history.setFocusable(false);
        table_history.setShowVerticalLines(false);
        table_history.getTableHeader().setResizingAllowed(false);
        table_history.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_history);

        button_makeTransaction.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        button_makeTransaction.setText("Make Transaction");
        button_makeTransaction.setFocusable(false);
        button_makeTransaction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_makeTransactionActionPerformed(evt);
            }
        });

        button_cancelTransaction.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        button_cancelTransaction.setText("Cancel Transaction");
        button_cancelTransaction.setFocusable(false);
        button_cancelTransaction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelTransactionActionPerformed(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Information");
        jLabel14.setFocusable(false);

        jSeparator5.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator5.setForeground(new java.awt.Color(255, 255, 255));

        jSeparator6.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator6.setForeground(new java.awt.Color(255, 255, 255));

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Details");
        jLabel15.setFocusable(false);

        jSeparator7.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator7.setForeground(new java.awt.Color(255, 255, 255));

        jSeparator8.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator8.setForeground(new java.awt.Color(255, 255, 255));

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("0.0");
        jLabel16.setFocusable(false);

        jSeparator9.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator9.setForeground(new java.awt.Color(255, 255, 255));

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Payment");
        jLabel17.setFocusable(false);

        field_payment.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        field_payment.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                field_paymentKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator5))
                    .addComponent(button_makeTransaction, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button_cancelTransaction, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator4)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(40, 40, 40)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel5))
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel3)))
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel9))
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel7)))
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addComponent(jLabel12)
                                    .addGap(32, 32, 32)
                                    .addComponent(jLabel13)
                                    .addGap(18, 18, 18)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(field_payment, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel16))))
                        .addGap(0, 64, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(label_balance)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator8))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator7))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_date)
                                    .addComponent(label_totalItem)
                                    .addComponent(label_totalAmount)
                                    .addComponent(label_salesClerk))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(label_salesClerk))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(label_date))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15)
                    .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(jLabel7))
                    .addComponent(label_totalItem, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addGap(17, 17, 17)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(label_totalAmount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17)))
                .addGap(4, 4, 4)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(label_balance))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel23)
                    .addComponent(jLabel16)
                    .addComponent(field_payment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_makeTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_cancelTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        menuItem_add.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_DOWN_MASK));
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
        jMenu1.add(jSeparator1);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem1.setText("Make Transaction");
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_BACK_SPACE, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem2.setText("Cancel Transaction");
        jMenu1.add(jMenuItem2);

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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE)
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
        salesClerkDb();
    }//GEN-LAST:event_menuItem_salesClerkDbActionPerformed
    
    private void salesClerkDb()
    {
        ClerkDb clerkDb = new ClerkDb(this, true);
        int x = (getWidth() - clerkDb.getWidth()) / 2;
        int y = (getHeight() - clerkDb.getHeight()) / 2;
        clerkDb.setLocation(x,y);
        clerkDb.setVisible(true);
    }
            
    private void menuItem_itemDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_itemDbActionPerformed
        itemDb();
    }//GEN-LAST:event_menuItem_itemDbActionPerformed

    private void itemDb()
    {
        ItemDb itemDb = new ItemDb(this, true);
        int x = (getWidth() - itemDb.getWidth()) / 2;
        int y = (getHeight() - itemDb.getHeight()) / 2;
        itemDb.setLocation(x,y);
        itemDb.setVisible(true);
    }
    
    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        
    }//GEN-LAST:event_formWindowGainedFocus

    private void button_cancelTransactionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelTransactionActionPerformed
        cancelTransaction();
    }//GEN-LAST:event_button_cancelTransactionActionPerformed

    private void button_makeTransactionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_makeTransactionActionPerformed
        makeTransaction();
    }//GEN-LAST:event_button_makeTransactionActionPerformed

    private void field_paymentKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_field_paymentKeyReleased
        checkBalance(getTotalAmount());
    }//GEN-LAST:event_field_paymentKeyReleased
    
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
    private javax.swing.JButton button_cancelTransaction;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_makeTransaction;
    private javax.swing.JTextField field_payment;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JLabel label_balance;
    private javax.swing.JLabel label_date;
    private javax.swing.JLabel label_salesClerk;
    private javax.swing.JLabel label_totalAmount;
    private javax.swing.JLabel label_totalItem;
    private javax.swing.JMenuItem menuItem_add;
    private javax.swing.JMenuItem menuItem_findPrice;
    private javax.swing.JMenuItem menuItem_itemDb;
    private javax.swing.JMenuItem menuItem_remove;
    private javax.swing.JMenuItem menuItem_salesClerkDb;
    private javax.swing.JTable table_display;
    private javax.swing.JTable table_history;
    // End of variables declaration//GEN-END:variables
}
