
package myUtilities.system_utilities;

import java.awt.Image;
import javax.swing.ImageIcon;
import myUtilities.system_utilities.interfaces.ImageIconInterface;

public class ImageIconScaler implements ImageIconInterface{
    
    /**
     * Gets the image on a default resource path in "Images" folder/package
     * 
     * @param imageName Name of Image.
     * @param height height of Image.
     * @param width width of Image.
     * @return 
     */
    @Override
    public ImageIcon getScaledImageIcon(String imageName, int height, int width)
    {
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource("/Images/" + imageName)).getImage());
        Image image = imageIcon.getImage();
        Image newImage = image.getScaledInstance(height, width, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newImage);
        return imageIcon;
    }
    
}
