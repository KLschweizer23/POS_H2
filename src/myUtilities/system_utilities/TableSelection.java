
package myUtilities.system_utilities;

import javax.swing.JTable;
import myUtilities.system_utilities.interfaces.TableSelectionInterface;

public class TableSelection implements TableSelectionInterface{
    
    public void setSelectionToZero(JTable table, boolean toZero){
        if(toZero && table.getRowCount() > 0) table.setRowSelectionInterval(0, 0);
    }
    
    public void setCustomSelection(JTable table, int selection){
        if(table.getRowCount() > 0) table.setRowSelectionInterval(0, selection);
    }
    
}
