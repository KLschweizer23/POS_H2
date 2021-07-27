/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package KL_LookAndFeel;

/**
 *
 * @author KL_Schweizer
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
public class Hover extends MouseAdapter {
    
    private JComponent component;
    private Color lastColor;

    public Hover(JComponent component) {
        this.component = component;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(e.getComponent() instanceof JLabel)
        {
            System.out.println("JLabel");
        }
        else if(e.getComponent() instanceof JButton)
        {
            JButton button = (JButton) e.getComponent();
            lastColor = button.getBackground();
            button.setBackground(Color.gray);
        }
        else if(e.getComponent() instanceof JTextField)
        {
            JTextField textField = (JTextField) e.getComponent();
            textField.setBorder(BorderFactory.createLineBorder(Color.yellow, 1, true));
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if(e.getComponent() instanceof JLabel)
        {

        }
        else if(e.getComponent() instanceof JButton)
        {
            JButton button = (JButton) e.getComponent();
            button.setBackground(lastColor);
        }
        else if(e.getComponent() instanceof JTextField)
        {
            JTextField textField = (JTextField) e.getComponent();
            textField.setBorder(BorderFactory.createLineBorder(Color.black, 1, true));
        }
    }
}