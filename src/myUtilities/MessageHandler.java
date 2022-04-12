package myUtilities;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class MessageHandler
{
    public void message(String message)
    {
        JOptionPane.showMessageDialog(null, message, "System Message", JOptionPane.INFORMATION_MESSAGE);
    }
    public void error(String message, boolean exitAfterMessage)
    {
        JOptionPane.showMessageDialog(null, message, "System Error", JOptionPane.ERROR_MESSAGE);
        if(exitAfterMessage)
            System.exit(0);
    }
    public int confirm(String message)
    {
        int choice = JOptionPane.showConfirmDialog(null, message, "System Confirmation",JOptionPane.OK_CANCEL_OPTION);
        return choice;
    }
    public void warning(String message)
    {
        JOptionPane.showMessageDialog(null, message, "System Warning", JOptionPane.WARNING_MESSAGE);
    }
    public String input(String message)
    {
        String in = JOptionPane.showInputDialog(null, message, "");
        return in;
    }
    public String password(String message)
    {
        JPasswordField passField = new JPasswordField(20);
        int choice = JOptionPane.showConfirmDialog(null, passField, "Input Password", JOptionPane.OK_CANCEL_OPTION);
        if(choice == JOptionPane.CANCEL_OPTION) return null;
        return new String(passField.getPassword());
    }
}
