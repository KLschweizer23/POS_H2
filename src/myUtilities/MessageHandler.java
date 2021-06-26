package myUtilities;

import javax.swing.JOptionPane;

public class MessageHandler
{
    public void message(String message)
    {
        JOptionPane.showMessageDialog(null, message, "System Message", JOptionPane.INFORMATION_MESSAGE);
    }
    public void error(String message)
    {
        JOptionPane.showMessageDialog(null, message, "System Error", JOptionPane.ERROR_MESSAGE);
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
}
