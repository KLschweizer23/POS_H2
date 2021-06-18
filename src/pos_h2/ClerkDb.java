package pos_h2;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import myUtilities.MessageHandler;
import pos_h2_database.*;

public class ClerkDb extends javax.swing.JDialog {

    MessageHandler mh = new MessageHandler();
    
    DefaultTableModel dtm;
    
    ArrayList<String> idList = new ArrayList<>();
    HashMap<String, Clerk> clerk = new HashMap<>();
    
    boolean goodPassword = true;
    
    private int rowHeight = 30;
    
    public ClerkDb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        createColumns();
        setupTable();
        processTable();
    }    
    private void createColumns()
    {
        dtm = new DefaultTableModel(0,0)
        {
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        clerkTable.setModel(dtm);
        dtm.addColumn("ID");
        dtm.addColumn("Firstname");
        dtm.addColumn("Lastname");
        dtm.addColumn("Middlename");
        dtm.addColumn("Username");
        dtm.addColumn("Password");
    }
    private void processTable()
    {
        DB_Clerk clerkDb = new DB_Clerk();
        
        clearTable();
        
        idList = clerkDb.getIdList();
        clerk = clerkDb.processData("", 0);
        for(int i = 0; i < clerk.size(); i++)
        {
            String id = idList.get(i);
            String pass = "";
            for(int j = 0; j < clerk.get(id).getPassword().length(); j++)
                pass += (char) 8226;
            String[] rowData =
            {
                clerk.get(id).getId(),
                clerk.get(id).getFirstname(),
                clerk.get(id).getMiddlename(),
                clerk.get(id).getLastname(),
                clerk.get(id).getUser(),
                pass
            };
            dtm.addRow(rowData);
        }
        clerkTable.setRowHeight(rowHeight);
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
        clerkTable.addMouseListener(new MouseListener() {
            private boolean onTable = false;
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if(onTable)
                {
                    Point p = e.getPoint();
                    int y = p.y / rowHeight;
                    if(y < dtm.getRowCount())
                        setFields(clerk.get(clerkTable.getValueAt(y, 0).toString()));
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
        
        clerkTable.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int y = p.y / rowHeight;
                if(y < dtm.getRowCount())
                    clerkTable.setRowSelectionInterval(0, y);
            }
        });
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        clerkTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        button_add = new javax.swing.JButton();
        button_update = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        field_firstname = new javax.swing.JTextField();
        field_middlename = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        field_lastname = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        button_clear = new javax.swing.JButton();
        label_labelId = new javax.swing.JLabel();
        label_id = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        field_user = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        field_password = new javax.swing.JPasswordField();
        field_passwordConfirm = new javax.swing.JPasswordField();
        jLabel6 = new javax.swing.JLabel();
        label_passwordStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jScrollPane1.setFocusable(false);

        clerkTable.setFillsViewportHeight(true);
        clerkTable.setFocusable(false);
        clerkTable.setRequestFocusEnabled(false);
        clerkTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        clerkTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        clerkTable.getTableHeader().setResizingAllowed(false);
        clerkTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(clerkTable);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Firstname");
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

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Middlename");
        jLabel2.setFocusable(false);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Lastname");
        jLabel3.setFocusable(false);

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

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("User");
        jLabel4.setFocusable(false);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Password");
        jLabel5.setFocusable(false);

        field_passwordConfirm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                field_passwordConfirmKeyReleased(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Confirm Password");
        jLabel6.setFocusable(false);

        label_passwordStatus.setFont(new java.awt.Font("Tahoma", 2, 10)); // NOI18N
        label_passwordStatus.setForeground(new java.awt.Color(255, 0, 0));
        label_passwordStatus.setFocusable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(field_firstname)
                    .addComponent(field_middlename)
                    .addComponent(field_lastname)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(button_add, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_update, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_labelId)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_id, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_clear))
                    .addComponent(field_user)
                    .addComponent(field_password)
                    .addComponent(field_passwordConfirm)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(label_passwordStatus))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(button_clear)
                    .addComponent(label_labelId)
                    .addComponent(label_id))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(field_firstname, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(field_middlename, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(field_lastname, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(field_user, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(field_password, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(field_passwordConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_passwordStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_add)
                    .addComponent(button_update)
                    .addComponent(button_delete))
                .addContainerGap())
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
        noErrorText(field_firstname) &&
        noErrorText(field_middlename) &&
        noErrorText(field_lastname) &&
        noErrorText(field_user) &&
        goodPassword
        ;
        if(goodFields)
        {
            DB_Clerk clerkDb = new DB_Clerk();

            Clerk clerk = getClerk();

            clerkDb.insertData(clerk);
            processTable();
            emptyFields();
        } else mh.warning("Please fill the fields properly!");
    }//GEN-LAST:event_button_addActionPerformed
   
    private void button_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_updateActionPerformed
        boolean goodFields =
        noErrorText(field_firstname) &&
        noErrorText(field_middlename) &&
        noErrorText(field_lastname)
        ;
        if(goodFields)
        {
            DB_Clerk clerkDb = new DB_Clerk();

            Clerk clerk = getClerk();
            clerk.setId(label_id.getText());
            clerkDb.updateData(clerk);
            processTable();
            emptyFields();
        } else mh.warning("Please fill the fields properly!");
    }//GEN-LAST:event_button_updateActionPerformed
    private Clerk getClerk()
    {
        Clerk clerk = new Clerk();
        clerk.setFirstname(field_firstname.getText());
        clerk.setMiddlename(field_middlename.getText());
        clerk.setLastname(field_lastname.getText());
        clerk.setUser(field_user.getText());
        String pass = "";
        for(int i = 0; i < field_password.getPassword().length; i++)
        {
            pass += field_password.getPassword()[i];
        }
        clerk.setPassword(pass);
        
        return clerk;
    }
    private void button_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clearActionPerformed
        emptyFields();
    }//GEN-LAST:event_button_clearActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        deleteData();
    }//GEN-LAST:event_button_deleteActionPerformed

    private void field_passwordConfirmKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_field_passwordConfirmKeyReleased
        char[] pass = field_password.getPassword();
        char[] passConfirm = field_passwordConfirm.getPassword();
        
        if(pass.length == passConfirm.length)
        {
            goodPassword = true;
            for(int i = 0; i < pass.length; i++)
            {
                if(pass[i] != passConfirm[i])
                    goodPassword = false;
            }
        }
        else
            goodPassword = false;
        
        if(goodPassword)
        {
            label_passwordStatus.setText("Password Match!");
            label_passwordStatus.setForeground(Color.green);
        }
        else
        {
            label_passwordStatus.setText("Password Doesn't Match!");
            label_passwordStatus.setForeground(Color.red);
        }
    }//GEN-LAST:event_field_passwordConfirmKeyReleased
    private void deleteData()
    {
        DB_Clerk clerkDb = new DB_Clerk();
        
        Clerk clerk = getClerk();
        clerk.setId(label_id.getText());
        clerkDb.deleteData(clerk);
        processTable();
        emptyFields();
    }
    private void emptyFields()
    {
        label_id.setText("000");
        
        field_firstname.setText("");
        setTextFieldFormat(field_firstname, true);
        field_middlename.setText("");
        setTextFieldFormat(field_middlename, true);
        field_lastname.setText("");
        setTextFieldFormat(field_lastname, true);
        
        field_user.setText("");
        setTextFieldFormat(field_user, true);
        
        field_password.setText("");
        field_passwordConfirm.setText("");
        
        label_passwordStatus.setText("");
        
        button_add.setEnabled(true);
        button_update.setEnabled(false);
        button_delete.setEnabled(false);
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
    private void setFields(Clerk clerk)
    {
        label_id.setText(clerk.getId());
        
        field_firstname.setText(clerk.getFirstname());
        field_lastname.setText(clerk.getLastname());
        field_middlename.setText(clerk.getMiddlename());
        field_user.setText(clerk.getUser());
        
        button_add.setEnabled(false);
        button_update.setEnabled(true);
        button_delete.setEnabled(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_add;
    private javax.swing.JButton button_clear;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_update;
    private javax.swing.JTable clerkTable;
    private javax.swing.JTextField field_firstname;
    private javax.swing.JTextField field_lastname;
    private javax.swing.JTextField field_middlename;
    private javax.swing.JPasswordField field_password;
    private javax.swing.JPasswordField field_passwordConfirm;
    private javax.swing.JTextField field_user;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_id;
    private javax.swing.JLabel label_labelId;
    private javax.swing.JLabel label_passwordStatus;
    // End of variables declaration//GEN-END:variables
}
