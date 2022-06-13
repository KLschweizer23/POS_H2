
package myUtilities.system_utilities.interfaces;

import javax.swing.JTable;

public interface TableSelectionInterface {
    
    void setSelectionToZero(JTable table, boolean toZero);
    
    void setCustomSelection(JTable table, int selection);
    
}
