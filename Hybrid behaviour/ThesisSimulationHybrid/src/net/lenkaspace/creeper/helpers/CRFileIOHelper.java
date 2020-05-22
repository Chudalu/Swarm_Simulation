package net.lenkaspace.creeper.helpers;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import org.apache.commons.io.FileUtils;
import javax.imageio.ImageIO;
//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * Provides static method for saving Java component images and text into files.
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */
public class CRFileIOHelper {

	private static BufferedImage bi;
	private static ByteArrayOutputStream boutstream;
	//private static JPEGImageEncoder encoder;
	
	/**
	 * Print a Java Component into an image file
	 * @param component_ Component to print
	 * @param fileName String filename without an extension
	 */
	public static void componentToJpeg(Component component_ , String fileName){ 
		if (fileName.length() > 0) {
			//-- add .jpg to file name:
			fileName += ".jpg";
			try { 
				if (boutstream == null) {
					boutstream = new ByteArrayOutputStream() ;  
				}
				
				Rectangle rect = component_.getBounds() ; 
				if (bi == null || bi.getWidth() != rect.width || bi.getHeight() != rect.getHeight()) {
					bi = new BufferedImage( rect.width,rect.height, BufferedImage.TYPE_USHORT_565_RGB) ; 
					//encoder = JPEGCodec.createJPEGEncoder(boutstream) ; 
				}
				component_.paint(bi.getGraphics() ) ; 
				ImageIO.write(bi, "jpg", new File(fileName+".jpg"));
				/*encoder.encode(bi) ; 
				FileOutputStream fimage = new FileOutputStream(new File(fileName) ) ; 
				boutstream.writeTo(fimage) ; 
				fimage.close() ;
				//-- clear the output stream:
				boutstream.reset();*/
				
			} catch (Exception e){
				System.err.println("Error in writing " + fileName +":" +e.getMessage()) ;
			} 
		}
	}
	
	/**
	 * Put a string into a new txt file
	 * @param stringToPrint_ String contents of the file
	 * @param fileName_ String filename without an extension
	 */
	public static void stringToFile (String stringToPrint_, String fileName_) {
		CRFileIOHelper.stringToFile(stringToPrint_, fileName_, "txt");
	}
	
	/**
	 * Put a string into a new file
	 * @param stringToPrint_ String contents of the file
	 * @param fileName_ String filename without an extension
	 * @param extension_ String extension without a dot. Set to 'txt' by default.
	 */
	public static void stringToFile (String stringToPrint_, String fileName_, String extension_) {
		if (fileName_.length() > 0) {
			//-- check extension
			if (extension_.length() == 0) {
				extension_ = "txt";
			}
			//-- add extension to file name:
			fileName_ += "." + extension_;
			try {
				//System.out.println("PRINTING "+fileName_);
				File file = new File(fileName_);
				FileUtils.writeStringToFile(file, stringToPrint_);
			} catch (Exception e) {
				System.err.println("Error in writing " + fileName_ +":" +e.getMessage()) ;
			}
		}
	}
}
