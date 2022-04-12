package myUtilities;

import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * Just to make some functions centralized and easy to call.
 * 
 * @author KL_Schweizer
 */
public class SystemUtilities 
{
    //-----------------------------USER FUNCTIONS--------------------------\\
    
    /**
     * 
     * @param message String to display.
     * @return Returns the inputted Number of User.
     */
    public String inputNumberUser(String message)
    {            
        MessageHandler mh = new MessageHandler();
        
        String quantity;
        boolean pass;
        do
        {
            quantity = mh.input(message);
            if(quantity == null)
                pass = true;
            else
            {
                pass = isANumber(quantity);
                if(pass)
                    return quantity;
            }
        }while(!pass);
        
        return quantity;
    }
    
    /**
     * 
     * @param message String to display.
     * @return Returns the inputted String of User.
     */
    public String inputStringUser(String message)
    {            
        MessageHandler mh = new MessageHandler();
        
        String string;
        boolean pass;
        do
        {
            string = mh.input(message);
            if(string == null)
                pass = true;
            else
            {
                pass = !string.isBlank();
                if(pass)
                    return string;
            }
        }while(!pass);
        
        return string;
    }
    
    /**
     * 
     * @param message String to display.
     * @return Returns the inputted Password of User.
     */
    public String inputPasswordUser(String message)
    {            
        MessageHandler mh = new MessageHandler();
        
        String password;
        boolean pass;
        do
        {
            password = mh.password(message);
            if(password == null)
                pass = true;
            else
            {
                pass = !password.isBlank();
                if(pass)
                    return password;
            }
        }while(!pass);
        
        return password;
    }
    
    //----------------------------STRING FUNCTIONS-------------------------\\
    /**
     * Checks whether a String can be a number.
     * 
     * @param string The string to check.
     * @return returns {@code true} if string can be a number and {@code false} if not.
     */
    public boolean isANumber(String string) 
    {
        boolean valid = false;
        if (string != null) 
        {
            valid = (string.length() > 0);
            int dot = 0;
            for (int i = 0; i < string.length(); i++) {
                switch (string.charAt(i)) 
                {
                    case '.':case '0':case '1':case '2':case '3':case '4':case '5':
                    case '6':case '7':case '8':case '9':
                    if (string.charAt(i) == '.')
                        dot++; 
                    break;
                    default:
                        valid = false;
                    break;
                } 
            } 
        if (dot > 1)
            valid = false; 
        } 
        return valid;
    }
    
    //------------------------IMAGE/ICONS FUNCTIONS---------------------\\
    /**
     * Gets the image on a default resource path in "Images" folder/package
     * 
     * @param imageName Name of Image.
     * @param height height of Image.
     * @param width width of Image.
     * @return 
     */
    public ImageIcon getScaledImageIcon(String imageName, int height, int width)
    {
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource("/Images/" + imageName)).getImage());
        Image image = imageIcon.getImage();
        Image newImage = image.getScaledInstance(height, width, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newImage);
        return imageIcon;
    }
    
    //----------------------------TIME FUNCTIONS-------------------------\\
    /**
     * Gets Current Local Time
     * 
     * @return Returns current local time in "{@code yyyy-MM-dd HH:mm:ss.SSS}" format.
     */
    public String getCurrentDateTime()
    {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }
    public String getCurrentDate()
    {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    public String getCurrentDateTime(String format)
    {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
    }
    
    //---------------------------- TABLE FUNCTIONS------------------------\\
    public void resizeColumnWidth(JTable table) 
    {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) 
        {
            int width = 15; // Min width
            for (int row = 0; row < table.getRowCount(); row++) 
            {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width +1 , width);
            }
            if(width > 300)
                width=300;
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }
    public void setHoverableTable(JTable table)
    {        
        table.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int y = p.y / table.getRowHeight();
                if(y < table.getModel().getRowCount())
                    table.setRowSelectionInterval(0, y);
            }
        });
    }
    public void setSelectionToZero(JTable table, boolean toZero)
    {
        if(toZero && table.getRowCount() > 0) table.setRowSelectionInterval(0, 0);
    }
    public void setCustomSelection(JTable table, int selection)
    {
        if(table.getRowCount() > 0) table.setRowSelectionInterval(0, selection);
    }
    //---------------------------- FILE FUNCTIONS ------------------------\\
    public String getSingleStringFromFile(File file)
    {
        return getFile(file);
    }
    public String getSingleStringFromFile(String fileName)
    {
        return getFile(new File(fileName));
    }
    private String getFile(File file)
    {        
        String singleString = null;
        try {
            Scanner reader = new Scanner(file);
            while(reader.hasNextLine())
                singleString = reader.nextLine();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MyConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return singleString;
    }
}
