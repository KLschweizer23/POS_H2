package KL_LookAndFeel;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JTextField;

/**
 * Rounds a JTextField
 * 
 * @author KL_Schweizer
 */

public class RoundedJTextField extends JTextField {
    
    private Shape shape;
    
    private boolean isNoBorder = false;
    
    private int horizontalDiameter = 15;
    private int verticalDiameter = 15;
    
    /**
     * 
     * @param       size            size of JTextField
     */
    public RoundedJTextField(int size) {
        super(size);
        setOpaque(false);
    }
    
    /**
     * 
     * @param       size            size of JTextField
     * @param       noBorder        set border
     */
    public RoundedJTextField(int size, boolean noBorder) {
        super(size);
        setOpaque(false);
        
        isNoBorder = noBorder;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
         g.setColor(getBackground());
         g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, horizontalDiameter, verticalDiameter);
         super.paintComponent(g);
    }
    @Override
    protected void paintBorder(Graphics g) {
         if(!isNoBorder)
         {
            g.setColor(getForeground());
            g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
         }
    }
    @Override
    public boolean contains(int x, int y) {
         if (shape == null || !shape.getBounds().equals(getBounds())) {
             shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 15, 15);
         }
         return shape.contains(x, y);
    }
}