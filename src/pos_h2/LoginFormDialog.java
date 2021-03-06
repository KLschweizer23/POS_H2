package pos_h2;

import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import myUtilities.MessageHandler;
import myUtilities.SystemUtilities;
import pos_h2_database.*;

public class LoginFormDialog extends javax.swing.JDialog {

    MessageHandler mh = new MessageHandler();
    MainFrame main;
    
    private final String adminUser = "admin";
    
    public LoginFormDialog(MainFrame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        main = parent;
        
        commands();
        setAlwaysOnTop(true);
        field_password.setEchoChar('\u25CF');
        
        SystemUtilities su = new SystemUtilities();
        
        String currentUser = "h2";
        
        //label_logo.setIcon(su.getScaledImageIcon("logo_" + currentUser + ".jpg", 382, 270));
    }
    
    private void commands()
    {
        int property = JComponent.WHEN_IN_FOCUSED_WINDOW;
        
        jPanel1.registerKeyboardAction(e -> {
            System.exit(0);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), property);
        
        jPanel1.registerKeyboardAction(e -> {
            login();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), property);
        
        field_username.registerKeyboardAction(e -> {
            login();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), property);
        
        field_password.registerKeyboardAction(e -> {
            login();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), property);
        
        getRootPane().registerKeyboardAction(e -> {
            login();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), property);
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
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        field_username = new javax.swing.JTextField();
        button_login = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        field_password = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        button_login1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        label_logo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        jPanel1.setMaximumSize(new java.awt.Dimension(639, 273));
        jPanel1.setName(""); // NOI18N

        jPanel2.setMaximumSize(new java.awt.Dimension(276, 273));
        jPanel2.setMinimumSize(new java.awt.Dimension(276, 273));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Username");

        //new RoundedJTextField(64, true)
        field_username.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        field_username.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        field_username.setPreferredSize(new java.awt.Dimension(64, 25));

        //new KLButton("Login")
        button_login.setText("Login");
        button_login.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(225, 225, 225)));
        button_login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_loginActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Login");

        //new RoundedJPasswordField(64, true)
        field_password.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        field_password.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        field_password.setPreferredSize(new java.awt.Dimension(64, 25));
        field_password.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                field_passwordActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Password");

        //new KLButton("Login")
        button_login1.setText("Exit");
        button_login1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(225, 225, 225)));
        button_login1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_login1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(field_password, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(field_username, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(button_login1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_login, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(field_username, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(field_password, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(button_login, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_login1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(357, 273));

        label_logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_logo.setFocusable(false);
        label_logo.setRequestFocusEnabled(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_logo, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_logo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
   
    private void button_loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_loginActionPerformed
        login();
    }//GEN-LAST:event_button_loginActionPerformed
    
    private void login()
    {
        DB_Clerk clerkDb = new DB_Clerk();
        Clerk clerk = clerkDb.checkPassword(field_username.getText(), field_password.getPassword());
        DB_Login logDb = new DB_Login();
        
        String pass = "";
        
        for(char x : field_password.getPassword())
            pass += x;
        if (field_username.getText().equals(adminUser) && logDb.checkPassword(pass))
        {
            clerk = new Clerk();
            clerk.setId("admin");
            clerk.setName("admin");
            clerk.setFirstname("admin");
            clerk.setMiddlename("admin"); 
            clerk.setLastname("admin");
            main.setCurrentClerk(clerk);
            recordLogin(clerk);
            setAlwaysOnTop(false);
            mh.message("Admin successfully logged in!");
            dispose();
        } 
        else if(clerk != null)
        {
            main.setCurrentClerk(clerk);
            recordLogin(clerk);
            setAlwaysOnTop(false);
            mh.message("You have successfully logged in!");
            dispose();
        }
        else
        {
            setAlwaysOnTop(false);
            mh.error("Credentials not found!", false);
        }
        
    }
    private void recordLogin(Clerk clerk)
    {
        DB_Login logDb = new DB_Login();
        SystemUtilities su = new SystemUtilities();
        
        Log log = new Log();
        log.setSalesClerk(clerk.getName());
        log.setStatus("IN");
        log.setTimeIn(su.getCurrentDateTime());
        
        logDb.insertData(log);
    }
    private void field_passwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_passwordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_field_passwordActionPerformed

    private void button_login1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_login1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_button_login1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_login;
    private javax.swing.JButton button_login1;
    private javax.swing.JPasswordField field_password;
    private javax.swing.JTextField field_username;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel label_logo;
    // End of variables declaration//GEN-END:variables
}