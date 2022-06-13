
package myUtilities.system_utilities;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JTable;
import myUtilities.system_utilities.interfaces.TableHoverInterface;

public class TableHover implements TableHoverInterface{
    
    @Override
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
    
}
