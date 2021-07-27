package KL_LookAndFeel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JPasswordField;

/**
 * Rounds a JTextField
 * 
 * @author KL_Schweizer
 */

public class RoundedJPasswordField extends JPasswordField {
    
    private Shape shape;
    
    private boolean isNoBorder = false;
    
    final private int horizontalDiameter = 20;
    final private int verticalDiameter = 20;
    
    /**
     * 
     * @param       size            size of JTextField
     */
    public RoundedJPasswordField(int size) {
        super(size);
        setOpaque(false);
    }
    
    /**
     * 
     * @param       size            size of JTextField
     * @param       noBorder        set border
     */
    public RoundedJPasswordField(int size, boolean noBorder) {
        super(size);
        setOpaque(false);
        super.setOpaque(false);
        super.setBackground(Color.white);
        
        isNoBorder = noBorder;
        this.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 2));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), horizontalDiameter, verticalDiameter);
        paintChildren(g2);
        super.paintComponent(g2);
    }
    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.gray);
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, horizontalDiameter, verticalDiameter);
        super.paintBorder(g);
    }
    @Override
    public boolean contains(int x, int y) {
         if (shape == null || !shape.getBounds().equals(getBounds())) {
             shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 15, 15);
         }
         return shape.contains(x, y);
    }
}