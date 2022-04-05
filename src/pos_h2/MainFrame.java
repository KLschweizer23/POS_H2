package pos_h2;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import myUtilities.MessageHandler;
import myUtilities.SystemUtilities;
import pos_h2_database.Clerk;
import pos_h2_database.DB_Discount;
import pos_h2_database.DB_Item;
import pos_h2_database.DB_Login;
import pos_h2_database.DB_Transaction;
import pos_h2_database.Discount;

import pos_h2_database.Item;
import pos_h2_database.Log;
import pos_h2_database.Transaction;

public class MainFrame extends javax.swing.JFrame {
    static MainFrame myFrame;
    
    ArrayList<String> idList = new ArrayList();
    
    HashMap<String, Item> item = new HashMap<>();
    HashMap<String, Transaction> transactions = new HashMap<>();
    
    Clerk currentClerk;
    Log currentLog;
    
    DefaultTableModel dtm, dtm2;
    
    int selectedRow = -1;
    
    public MainFrame() {
        initComponents();
        
        createColumns();
        createColumns2();
        login();
        setDetails();   
        updateHistory();
        setup();
        setHeader(table_display, Color.WHITE, new Dimension(0,30), Color.black);
        prepareDiscounts();
        
        menu_database.setEnabled(currentClerk.getFirstname().equals("admin"));
    }
    private void login()
    {
        do
            loginForm();
        while(currentClerk == null);
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
    
    private void prepareDiscounts(){
        DB_Discount discountDb = new DB_Discount();
        HashMap<String, Discount> discounts = discountDb.processData("", 0);
        comboBox_discount.removeAllItems();
        comboBox_discount.addItem("None");
        for(String key : discounts.keySet()){
            comboBox_discount.addItem("(" + key + ")-" + discounts.get(key).getName() + "/" + discounts.get(key).getValue() + "%");
        }
    }
    
    private void updateHistory()
    {
        for(int i = 0; dtm2.getRowCount() != 0;)
            dtm2.removeRow(i);
        DB_Transaction tDb = new DB_Transaction();
        transactions = tDb.processData(null, null);
        ArrayList<String> idLists = tDb.getIdList();
        for(int i = idLists.size() - 1; i >= 0; i--)
        {
            Transaction trans = transactions.get(idLists.get(i));
            String[] rowData = {
                trans.getT_id(),
                (char)8369 + " " + trans.getTotalAmount(),
                trans.getDate()
            };
            dtm2.addRow(rowData);
        }
        table_history.setRowHeight(20);
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
    private void loginForm()
    {
        LoginFormDialog login = new LoginFormDialog(this, true);
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
    private void createColumns2()
    {
        dtm2 = new DefaultTableModel(0,0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        table_history.setModel(dtm2);
        dtm2.addColumn("ID");
        dtm2.addColumn("Amount");
        dtm2.addColumn("Date");
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
        }
        table_display.setRowHeight(30);
        adjustViewport();
        updateStatus();
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
        JFrame frame = this;
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                confirmExit();
            }
        });
        
        //TABLES
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
        
        table_history.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int y = p.y / 20;
                if(y < dtm2.getRowCount())
                    table_history.setRowSelectionInterval(0, y);
            }
        });
        table_history.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me)
            {
                if(SwingUtilities.isLeftMouseButton(me))
                {
                    String id = table_history.getValueAt(table_history.getSelectedRow(), 0).toString();
                    TransactionDetailsDialog tDialog = new TransactionDetailsDialog(myFrame, true, transactions.get(id));
                    int x = (getWidth() - tDialog.getWidth()) / 2;
                    int y = (getHeight() - tDialog.getHeight()) / 2;
                    tDialog.setLocation(x,y);
                    tDialog.setVisible(true);
                }
            }
        });
        //FONTS || TEXTS
        Font big = new Font("Autobus Bold", Font.PLAIN, 65);
        jLabel1.setFont(big);
        button_add.setIcon(getScaledImageIcon("plus_icon.png", 25, 25));
        button_delete.setIcon(getScaledImageIcon("minus_icon.png", 25, 25));
        button_settings.setIcon(getScaledImageIcon("settings_icon.jpg", 20, 20));
        //COMMANDS
                
        int property = JComponent.WHEN_IN_FOCUSED_WINDOW;
        
        getRootPane().registerKeyboardAction(e -> {
                openItemDialog(false);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.ALT_DOWN_MASK), property);
        
        getRootPane().registerKeyboardAction(e -> {
                openItemDialog(false);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK), property);
        
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
            if (table_display.getRowCount() > 0)
            adjustQuantityToBuy(item.get(table_display.getValueAt(table_display.getSelectedRow(), 0).toString()), 1);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0), property);  
        
        getRootPane().registerKeyboardAction(e ->{
            if (table_display.getRowCount() > 0)
                adjustQuantityToBuy(item.get(table_display.getValueAt(table_display.getSelectedRow(), 0).toString()), 1);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), property);
                
        getRootPane().registerKeyboardAction(e ->{
            if (table_display.getRowCount() > 0)
                adjustQuantityToBuy(item.get(table_display.getValueAt(table_display.getSelectedRow(), 0).toString()), 1);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), property);
                
        getRootPane().registerKeyboardAction(e ->{
            if (table_display.getRowCount() > 0)
                adjustQuantityToBuy(item.get(table_display.getValueAt(table_display.getSelectedRow(), 0).toString()), -1);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), property);
                
        getRootPane().registerKeyboardAction(e ->{
            if (table_display.getRowCount() > 0)
                adjustQuantityToBuy(item.get(table_display.getValueAt(table_display.getSelectedRow(), 0).toString()), -1);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), property);
        
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
                
        getRootPane().registerKeyboardAction(e ->{
            openItemDialog(true);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ADD, KeyEvent.ALT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK), property);
                
        getRootPane().registerKeyboardAction(e ->{
            confirmExit();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK), property);
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
        if(table_display.getRowCount() > 0)
        {
            MessageHandler mh = new MessageHandler();

            int current = Integer.parseInt(item.getQuantityToBuy());
            if(!(current == 1 && amount == -1))
            {
                int currentQuantity = Integer.parseInt(item.getQuantity());
                if(current + amount > currentQuantity)
                    mh.warning("<html>There's not enough stocks for this item!<br>"
                            + "Item: <b>" + item.getName() + "</b><br>"
                                    + "Quantity: <b>" + item.getQuantity() + "</b><br></html>");
                else
                    item.setQuantityToBuy(current + amount + "");
            }
            selectedRow = table_display.getSelectedRow();
            processTable();
        }
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
        SystemUtilities su = new SystemUtilities();
        boolean isANumber = su.isANumber(field_payment.getText());
        if(isANumber || field_payment.getText().isBlank())
        {
            double payment = Double.parseDouble(field_payment.getText().isBlank() ? "0" : field_payment.getText());

            if(payment >= totalAmount)
            {
                label_change.setText(payment - totalAmount + "");
                label_balance.setForeground(Color.green);
                label_balance.setText((char)8369 + " 0.0");
            }
            else
            {
                label_change.setText("");
                label_balance.setForeground(Color.red);
                label_balance.setText((char)8369 + " " + (totalAmount - payment));
            }
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
        return (totalAmount - (totalAmount * getDiscount()));
    }
    private void makeTransaction()
    {
        MessageHandler mh = new MessageHandler();
        if(!currentClerk.getFirstname().equals("admin"))
        {
            if(item.size() > 0)
            {
                double payment = Double.parseDouble(field_payment.getText().isBlank() ? "0" : field_payment.getText());
                double totalAmount = Double.parseDouble(label_totalAmount.getText().substring(2));
                if(!field_payment.getText().isBlank() && payment >= totalAmount)
                {
                    DB_Transaction tDb = new DB_Transaction();
                    SystemUtilities su = new SystemUtilities();
                    String tId = tDb.getAvailableTID();
                    
                    DecimalFormat df = new DecimalFormat("0.##");
                    df.setRoundingMode(RoundingMode.UP);
                    String discount = getDiscountProcess();
                    
                    for(int i = 0; i < item.size(); i++)
                    {
                        String id = table_display.getValueAt(i, 0).toString();
 
                        item.get(id).setQuantityToBuy(table_display.getValueAt(i, 4).toString());
                        
                        Transaction transaction = new Transaction();

                        transaction.setT_id(tId);
                        transaction.setT_clerk(currentClerk.getName() + discount);
                        transaction.setDate(su.getCurrentDate());
                        transaction.setTotalAmount(df.format(totalAmount));
                        transaction.setPayment(payment + "");

                        Item newItem = new Item();
                        ArrayList<Item> listOfItem = new ArrayList<>();

                        newItem.setId(item.get(id).getId());
                        newItem.setName(item.get(id).getName());
                        newItem.setArticle(item.get(id).getArticle());
                        newItem.setBrand(item.get(id).getBrand());
                        newItem.setQuantity(item.get(id).getQuantityToBuy());
                        newItem.setPrice(item.get(id).getPrice());

                        listOfItem.add(newItem);

                        transaction.setItem(listOfItem);

                        tDb.insertData(transaction);
                        updateQuantity(item.get(id));
                    }
                    mh.message("<html>Transaction was finished!<br>Press <b>OK</b> to continue...</html>");
                    cancelTransaction();
                    updateHistory();
                    processTable();
                    table_display.requestFocus();
                } else mh.warning("Invalid payment!");
            } else mh.warning("There are no items to buy!");
        } else mh.warning("There are no sales clerk!");
    }
    private String getDiscountProcess(){
        String data = "";
        if(comboBox_discount.getSelectedIndex() == 0){
            data = "";
        }else{
            String item = comboBox_discount.getSelectedItem().toString();
            String id = "-";
            boolean onlyOnce = false;
            for(int i = 0; i < item.length(); i++){
                if(!onlyOnce && item.charAt(i) == '('){
                    onlyOnce = true;
                    i += 1;
                    for(; item.charAt(i) != ')'; i++){
                        id += item.charAt(i);
                    }
                }
            }
            data = id;
        }
        return data;
    }
    private void updateQuantity(Item item)
    {
        int currentQuantity = Integer.parseInt(item.getQuantity());
        int currentSold = Integer.parseInt(item.getSold());
        
        int quantityToBuy = Integer.parseInt(item.getQuantityToBuy());
        
        item.setQuantity((currentQuantity - quantityToBuy) + "");
        item.setSold((currentSold + quantityToBuy) + "");
        
        DB_Item itemDb = new DB_Item();
        itemDb.updateData(item);
    }
    private void cancelTransaction()
    {
        item.clear();
        idList.clear();
        field_payment.setText("");
        selectedRow = -1;
        for(int i = dtm.getRowCount() - 1; i >= 0; i--)
            dtm.removeRow(i);
        processTable();
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

        jMenuItem7 = new javax.swing.JMenuItem();
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
        label_change = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        jLabel17 = new javax.swing.JLabel();
        field_payment = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        comboBox_discount = new javax.swing.JComboBox<>();
        button_settings = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuItem_add = new javax.swing.JMenuItem();
        menuItem_remove = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        menuItem_findPrice = new javax.swing.JMenuItem();
        menu_database = new javax.swing.JMenu();
        menuItem_itemDb = new javax.swing.JMenuItem();
        menuItem_salesClerkDb = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        menuItem_invoice = new javax.swing.JMenuItem();
        menuItem_sales = new javax.swing.JMenuItem();
        menuItem_log = new javax.swing.JMenuItem();

        jMenuItem7.setText("jMenuItem7");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
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

        jPanel4.setBackground(new java.awt.Color(102, 102, 102));
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
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_add, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1015, Short.MAX_VALUE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(102, 102, 102));
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

        jScrollPane2.setFocusable(false);

        table_history.setFillsViewportHeight(true);
        table_history.setFocusable(false);
        table_history.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_history.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
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

        label_change.setBackground(new java.awt.Color(255, 255, 255));
        label_change.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        label_change.setForeground(new java.awt.Color(255, 255, 255));
        label_change.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_change.setFocusable(false);

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

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Discount");
        jLabel16.setFocusable(false);

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText(":");
        jLabel18.setFocusable(false);

        comboBox_discount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBox_discountActionPerformed(evt);
            }
        });

        button_settings.setContentAreaFilled(false);
        button_settings.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button_settings.setFocusable(false);
        button_settings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_settingsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
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
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                            .addComponent(jLabel10)
                                            .addGap(39, 39, 39))
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                            .addComponent(jLabel12)
                                            .addGap(32, 32, 32)))
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel16)
                                        .addGap(32, 32, 32)))
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel18)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_balance)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(comboBox_discount, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(field_payment, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(button_settings, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_change))))))
                        .addContainerGap(46, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(button_makeTransaction, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(button_cancelTransaction, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator8))
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
                                        .addGap(0, 99, Short.MAX_VALUE))))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator5)))
                        .addContainerGap())))
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
                    .addComponent(label_change)
                    .addComponent(field_payment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(jLabel18)
                        .addComponent(comboBox_discount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(button_settings, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addComponent(button_makeTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_cancelTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        menuItem_add.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.ALT_DOWN_MASK));
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
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_BACK_SPACE, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem2.setText("Cancel Transaction");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);
        jMenu1.add(jSeparator3);

        jMenuItem3.setText("Logout");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItem4.setText("Exit");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        menuItem_findPrice.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuItem_findPrice.setText("Find Price");
        jMenu2.add(menuItem_findPrice);

        jMenuBar1.add(jMenu2);

        menu_database.setText("Database");
        menu_database.setEnabled(false);

        menuItem_itemDb.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuItem_itemDb.setText("Item Database");
        menuItem_itemDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_itemDbActionPerformed(evt);
            }
        });
        menu_database.add(menuItem_itemDb);

        menuItem_salesClerkDb.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuItem_salesClerkDb.setText("Sales Clerk Database");
        menuItem_salesClerkDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_salesClerkDbActionPerformed(evt);
            }
        });
        menu_database.add(menuItem_salesClerkDb);
        menu_database.add(jSeparator10);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ADD, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem5.setText("Add Stocks");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        menu_database.add(jMenuItem5);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem6.setText("Transfer Stocks");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        menu_database.add(jMenuItem6);

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem8.setText("View Transferred Stocks");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        menu_database.add(jMenuItem8);

        jMenuBar1.add(menu_database);

        jMenu3.setText("Others");

        menuItem_invoice.setText("Invoice");
        menuItem_invoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_invoiceActionPerformed(evt);
            }
        });
        jMenu3.add(menuItem_invoice);

        menuItem_sales.setText("Sales (BETA)");
        menuItem_sales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_salesActionPerformed(evt);
            }
        });
        jMenu3.add(menuItem_sales);

        menuItem_log.setText("Log Record");
        menuItem_log.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_logActionPerformed(evt);
            }
        });
        jMenu3.add(menuItem_log);

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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 720, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_addActionPerformed
        openItemDialog(false);
    }//GEN-LAST:event_button_addActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        deleteItemAtTable();
    }//GEN-LAST:event_button_deleteActionPerformed

    private void menuItem_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_addActionPerformed
        openItemDialog(false);
    }//GEN-LAST:event_menuItem_addActionPerformed

    private void menuItem_salesClerkDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_salesClerkDbActionPerformed
        salesClerkDb();
    }//GEN-LAST:event_menuItem_salesClerkDbActionPerformed
    
    private void salesClerkDb()
    {
        MessageHandler mh = new MessageHandler();
        if(currentClerk.getFirstname().equals("admin"))
        {
            ClerkDbDialog clerkDb = new ClerkDbDialog(this, true);
            int x = (getWidth() - clerkDb.getWidth()) / 2;
            int y = (getHeight() - clerkDb.getHeight()) / 2;
            clerkDb.setLocation(x,y);
            clerkDb.setVisible(true);
        } else mh.warning("Non-admin are not authorized with this function!");
    }
            
    private void menuItem_itemDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_itemDbActionPerformed
        itemDb();
    }//GEN-LAST:event_menuItem_itemDbActionPerformed

    private void itemDb()
    {
        MessageHandler mh = new MessageHandler();
        if(currentClerk.getFirstname().equals("admin"))
        {
            ItemDbDialog itemDb = new ItemDbDialog(this, true);
            int x = (getWidth() - itemDb.getWidth()) / 2;
            int y = (getHeight() - itemDb.getHeight()) / 2;
            itemDb.setLocation(x,y);
            itemDb.setVisible(true);
        } else mh.warning("Non-admin are not authorized with this function!");
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

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        makeTransaction();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        cancelTransaction();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        recordLogout();
        currentClerk = null;
        dispose();
        myFrame = new MainFrame();
        myFrame.setVisible(true);
        myFrame.setExtendedState(myFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        myFrame.setMaximumSize(myFrame.getSize());
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void recordLogout()
    {
        DB_Login logDb = new DB_Login();
        SystemUtilities su = new SystemUtilities();
        
        Log log = new Log();
        log.setSalesClerk(currentClerk.getName());
        log.setStatus("OUT");
        log.setTimeOut(su.getCurrentDateTime());
        
        logDb.insertData(log);
    }
    
    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        confirmExit();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void confirmExit()
    {
        MessageHandler mh = new MessageHandler();
        int choice = mh.confirm("System is closing...");
        if(choice == JOptionPane.OK_OPTION)
        {
            recordLogout();
            System.exit(0);
        }
    }
    
    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        openItemDialog(true);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void menuItem_logActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_logActionPerformed
        openLogDialog();
    }//GEN-LAST:event_menuItem_logActionPerformed

    private void menuItem_invoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_invoiceActionPerformed
        openInvoiceDialog();
    }//GEN-LAST:event_menuItem_invoiceActionPerformed

    private void menuItem_salesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_salesActionPerformed
        openSalesDialog();
    }//GEN-LAST:event_menuItem_salesActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        openStocksTransfer();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void button_settingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_settingsActionPerformed
        boolean available = false;
        
        if(available){
            DiscountDialog discount = new DiscountDialog(this, true);
            int x = (getWidth() - discount.getWidth()) / 2;
            int y = (getHeight() - discount.getHeight()) / 2;
            discount.setLocation(x,y);
            discount.setVisible(true);
        }else{
            MessageHandler mh = new MessageHandler();
            mh.message("This functions has been disabled!");
        }
    }//GEN-LAST:event_button_settingsActionPerformed

    private void comboBox_discountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBox_discountActionPerformed
        updateStatus();
    }//GEN-LAST:event_comboBox_discountActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        viewStocks();
    }//GEN-LAST:event_jMenuItem8ActionPerformed
    
    private double getDiscount(){
        if(comboBox_discount.getSelectedIndex() != 0){
            String item = comboBox_discount.getSelectedItem().toString();
            String value = "";
            for(int i = 0; i < item.length(); i++){
                if(item.charAt(i) == '/'){
                    i += 1;
                    String tempItem = item;
                    value += tempItem.substring(i, tempItem.length() - 1);
                }
            }
            Double discountValue = Double.parseDouble(value);
            return (discountValue / 100);
        }
        return 0;
    }
    
    private void viewStocks(){
        ViewTransferStockDialog viewTransfer = new ViewTransferStockDialog(this, true);
        int x = (getWidth() - viewTransfer.getWidth()) / 2;
        int y = (getHeight() - viewTransfer.getHeight()) / 2;
        viewTransfer.setLocation(x,y);
        viewTransfer.setVisible(true);
    }
    
    private void openStocksTransfer(){
        TransferStockDialog transfer = new TransferStockDialog(this, true);
        int x = (getWidth() - transfer.getWidth()) / 2;
        int y = (getHeight() - transfer.getHeight()) / 2;
        transfer.setLocation(x,y);
        transfer.setVisible(true);
    }
    
    private void openSalesDialog()
    {
        SalesDialog sales = new SalesDialog(this, true);
        int x = (getWidth() - sales.getWidth()) / 2;
        int y = (getHeight() - sales.getHeight()) / 2;
        sales.setLocation(x,y);
        sales.setVisible(true);
    }
    private void openInvoiceDialog()
    {
        InvoiceDialog invoice = new InvoiceDialog(this, true);
        int x = (getWidth() - invoice.getWidth()) / 2;
        int y = (getHeight() - invoice.getHeight()) / 2;
        invoice.setLocation(x,y);
        invoice.setVisible(true);
    }
    private void openLogDialog()
    {
        LogDialog log = new LogDialog(this, true);
        int x = (getWidth() - log.getWidth()) / 2;
        int y = (getHeight() - log.getHeight()) / 2;
        log.setLocation(x,y);
        log.setVisible(true);
    }
    private void openItemDialog(boolean stockMode)
    {
        MessageHandler mh = new MessageHandler();
        if((stockMode && currentClerk.getFirstname().equals("admin")) || !stockMode)
        {
            ItemDialog item = new ItemDialog(this, null, true, stockMode);
            int x = (getWidth() - item.getWidth()) / 2;
            int y = (getHeight() - item.getHeight()) / 2;
            item.setLocation(x,y);
            item.setVisible(true);
        }
        else mh.warning("Non-admin are not authorized with this function!");
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
//        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//            System.out.println(info.getClassName());
//            if ("Nimbus".equals(info.getName())) {
//                break;
//            }
//        }
        try {
                javax.swing.UIManager.setLookAndFeel(new FlatLightLaf());
            } 
        catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            myFrame = new MainFrame();
            myFrame.setVisible(true);
            myFrame.setExtendedState(myFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            myFrame.setMaximumSize(myFrame.getSize());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_add;
    private javax.swing.JButton button_cancelTransaction;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_makeTransaction;
    private javax.swing.JButton button_settings;
    private javax.swing.JComboBox<String> comboBox_discount;
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
    private javax.swing.JLabel jLabel18;
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
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JLabel label_balance;
    private javax.swing.JLabel label_change;
    private javax.swing.JLabel label_date;
    private javax.swing.JLabel label_salesClerk;
    private javax.swing.JLabel label_totalAmount;
    private javax.swing.JLabel label_totalItem;
    private javax.swing.JMenuItem menuItem_add;
    private javax.swing.JMenuItem menuItem_findPrice;
    private javax.swing.JMenuItem menuItem_invoice;
    private javax.swing.JMenuItem menuItem_itemDb;
    private javax.swing.JMenuItem menuItem_log;
    private javax.swing.JMenuItem menuItem_remove;
    private javax.swing.JMenuItem menuItem_sales;
    private javax.swing.JMenuItem menuItem_salesClerkDb;
    private javax.swing.JMenu menu_database;
    private javax.swing.JTable table_display;
    private javax.swing.JTable table_history;
    // End of variables declaration//GEN-END:variables
}
