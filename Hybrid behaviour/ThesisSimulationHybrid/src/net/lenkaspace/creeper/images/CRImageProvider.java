package net.lenkaspace.creeper.images;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.imageio.ImageIO;

/**
 * Loads images from the same folder where it is placed.
 * Maintains a list of loaded images and provides images to other classes.
 * Needs to be subclassed and the subclass placed to a folder where
 * a project contains its own images.
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */
public class CRImageProvider {
	
	protected Dictionary <String, BufferedImage> bufferedImages;
	
	public CRImageProvider() {
		bufferedImages = new Hashtable <String, BufferedImage>();
	}
	
	/**
	 * Get an image from the folder where this class (or its sublclass) is located
	 * @param imageFileName String file name of the image, including its extension
	 * @return BufferedImage image
	 */
	public BufferedImage getImage(String imageFileName) {
		String imageAlias = this.getClass().getName() + ":" + imageFileName;
		BufferedImage image = bufferedImages.get(imageAlias);
		//-- check if buffered image already exists, if not create it
		if (image == null) {
			image = loadImage(imageFileName);
			if (image != null) {
				bufferedImages.put(imageAlias, image);
			}
		}
		//-- 
		return image;
	}
	
	/**
     * Load an image file into a BufferedImage
     * @param path String path of the file relative to project root, as it appears in the image directory specified in CRSettings. E.g. 'image.png'
     * @return BufferedImage
     */
    protected BufferedImage loadImage(String imageName) {
        BufferedImage img = null;
        try {
        	URL url =  this.getClass().getResource(imageName);
            img =  ImageIO.read(url);
        } catch (Exception e) {
        	System.err.println("Could not load image " +  this.getClass().getName() + ":" + imageName);
        }
        return img;
    }
    
}
