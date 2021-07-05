package KL_LookAndFeel;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author KL_Schweizer
 */
public class KLButton extends JButton{

    private Color color1 = new Color(255, 255, 255);
    private Graphics g;
    
    public KLButton(String text) {
        super(text); 
        setContentAreaFilled(false);
        setFocusPainted(false);
        
        final JButton b = this;
        final Border raisedBevelBorder = BorderFactory.createRaisedBevelBorder();
        final Insets insets = raisedBevelBorder.getBorderInsets(b);
        final EmptyBorder emptyBorder = new EmptyBorder(insets);
        b.setBorder(emptyBorder);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.getModel().addChangeListener((ChangeEvent e) -> {
            ButtonModel model1 = (ButtonModel) e.getSource();
            if (model1.isRollover()) {
                
            } else {
                
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        this.g = g;
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setPaint(new GradientPaint(new Point(0, 0), getBackground(), new Point(0, getHeight()/3), color1));
        g2.fillRoundRect(0, 0, getWidth(), getHeight()/3, 15, 15);
        g2.setPaint(new GradientPaint(new Point(0, getHeight()/3), color1, new Point(0, getHeight()), getBackground()));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.dispose();

        super.paintComponent(g);
    }
}
