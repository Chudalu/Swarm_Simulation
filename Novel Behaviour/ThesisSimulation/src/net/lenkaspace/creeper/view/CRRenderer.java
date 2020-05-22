package net.lenkaspace.creeper.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import net.lenkaspace.creeper.CRController;
import net.lenkaspace.creeper.images.CRImageProvider;
import net.lenkaspace.creeper.model.CRBaseSituatedModel;
import net.lenkaspace.creeper.model.CRSettings;
import net.lenkaspace.creeper.model.CRWorld;
import net.lenkaspace.creeper.vo.CRVector3d;

/**
 * Uses Java's native rendering techniques to display CRBaseSituatedModel objects in the world.
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */
@SuppressWarnings("serial")
public class CRRenderer extends Component implements ActionListener {
	
	//-- image name constants
	public static String CR_BLACK_DOT 		= "crBlackDot.png";
	public static String CR_BLUE_DOT 		= "crBlueDot.png";
	public static String CR_GRAY_DOT 		= "crGrayDot.png";
	public static String CR_GREEN_DOT 		= "crGreenDot.png";
	public static String CR_PURPLE_DOT 		= "crPurpleDot.png";
	public static String CR_RED_DOT 		= "crRedDot.png";
	public static String CR_BLANK_CIRCLE 	= "crBlankCircle.png";
	public static String CR_BLUE_CIRCLE 	= "crBlueCircle.png";
	public static String CR_GRAY_CIRCLE 	= "crGrayCircle.png";
	public static String CR_GREEN_CIRCLE 	= "crGreenCircle.png";
	public static String CR_PURPLE_CIRCLE 	= "crPurpleCircle.png";
	public static String CR_RED_CIRCLE 		= "crRedCircle.png";
	
	//--
	
	public static enum DIRECTION { UP, RIGHT, DOWN, LEFT }
	
	protected boolean shouldOverrideNoPaint = false; //useful when CRRendered needs re-painting even though it normally shouldn't
    protected CRWorld world;    
    protected Dimension size;
    protected CRImageProvider imageProvider;
    protected CRImageProvider crImageProvider;
    protected CRVector3d	viewPosition; //location of view's top left corner in world coordinates
    
    protected CRController controller;
    
    
    public CRRenderer(CRController controller_) {
    	this.setPreferredSize(new Dimension(700,650));
    	controller = controller_;
    	crImageProvider = new CRImageProvider();
    	viewPosition = new CRVector3d();
    	this.setFocusable(true);
    }
    
    
    /**
     * Called each time interval. Calls repaint.
     */
    public void actionPerformed(ActionEvent e) {
    	this.repaint();
	}
    
    /**
     * Main rendering function
     */
    public void paint(Graphics g) {
    	
    	Dimension size = getSize();
    	//-------- draw background
 	    g.setColor(Color.WHITE);   
 	    g.fillRect(0, 0, size.width, size.height);
  
	    CRSettings settings = CRSettings.getSingleton();
	    if ((settings.getIsInitDone() && settings.getShouldDraw() && settings.isTimeRunning()) || shouldOverrideNoPaint) {
		    if (shouldOverrideNoPaint) {
		    	//make sure only paints once..
		    	shouldOverrideNoPaint = false;
		    }
	    	//-- draw all renderable objects in the world
		    for (CRBaseSituatedModel situatedModel : world.getSituatedModels() ) {
		    	String imageFileName = situatedModel.getImageFileName();
		    	if (situatedModel.isVisible() && imageFileName != null && imageFileName != "") {
		    		//-- get the image either from Creeper native CRImageProvider or from a specified image provider
		    		BufferedImage image;
		    		if (imageFileName == CR_BLACK_DOT || imageFileName == CR_BLUE_DOT || imageFileName == CR_GRAY_DOT || imageFileName == CR_GREEN_DOT || imageFileName == CR_PURPLE_DOT || imageFileName == CR_RED_DOT ||
		    		imageFileName == CR_BLANK_CIRCLE || imageFileName == CR_BLUE_CIRCLE || imageFileName == CR_GRAY_CIRCLE || imageFileName == CR_GREEN_CIRCLE || imageFileName == CR_PURPLE_CIRCLE || imageFileName == CR_RED_CIRCLE) {
		    			image = crImageProvider.getImage(imageFileName);
		    		} else {
		    			image = imageProvider.getImage(imageFileName);
		    		}
		    		CRVector3d positionOnScreen = new CRVector3d(situatedModel.getPosition());
	    			positionOnScreen.x -= viewPosition.x;
	    			positionOnScreen.y -= viewPosition.y;
		    		if (image != null) {
		    			drawImageAt(image, positionOnScreen, situatedModel.getSize(), Math.toRadians(situatedModel.getRotation()), situatedModel.getAlpha(), g);
		    		}
		    		
		    		if (situatedModel.getToolTip().length() > 0) {
			    		drawTextAt(positionOnScreen, situatedModel.getToolTip(), Color.BLACK, g);
			    	}
		    	}
		    	
		    	
		    }
	    }
	    
 	    //-------- draw border
 	    g.setColor(new Color(0,0,0 ));   
 	    g.drawRect(0, 0, size.width-1, size.height-2); 
 
    }
    
    //==================================== MOVING VIEW ===================================
    public void moveView(DIRECTION direction_, int amount_) {
    	switch (direction_) {
    	case UP:
    		viewPosition.y -= amount_;
    		break;
    	case DOWN:
    		viewPosition.y += amount_;
    		break;
    	case LEFT:
    		viewPosition.x -= amount_;
    		break;
    	case RIGHT:
    		viewPosition.x += amount_;
    		break;
    	}
    	checkAndRedisplayViewPosition();
    }
    
    //==================================== IMAGES ========================================
        
    /**
     * Draw an image
     * @param img_ BufferedImage to draw
     * @param pos_ CRVector3d position [x,y,z]
     * @param rot_ double rotation in radians
     * @param g Graphics object
     */
    public void drawImageAt(BufferedImage img_, CRVector3d pos_, CRVector3d size_, double rot_, double alpha_, Graphics g ) {
    	if (img_ != null) {
	    	Graphics2D g2d = (Graphics2D)g;
	        AffineTransform origXform = g2d.getTransform();
	        //--- rotate about image's origin:
	        g2d.rotate(rot_, pos_.x , pos_.y );
	        //-- adjust x and y based on image size:
	        int imgX = (int) (pos_.x - size_.x/2);
	    	int imgY = (int) (pos_.y - size_.y/2);
	    	AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)alpha_);
	    	g2d.setComposite(ac);
	        g2d.drawImage(img_, imgX, imgY, (int)size_.x, (int)size_.y, this);
	        //---- prevent the transformation from affecting other images:
	        g2d.setTransform(origXform);	
    	}
    }
    
    /**
     * Draw text of a specified color
     * @param pos_  CRVector3d position [x,y,z]
     * @param string_ String text to display
     * @param color_ Color color of text
     * @param g Graphics object
     */
    public void drawTextAt(CRVector3d pos_, String string_, Color color_, Graphics g) {
    	g.setColor(color_); 
		g.drawString(string_, (int)pos_.x, (int)pos_.y);
    }
       
    //==================================== GETTERS / SETTERS =================================
    
    public void setShouldOverrideNoPaint(boolean value_) { shouldOverrideNoPaint = value_; }
    public void setWorld(CRWorld world_) { world = world_; }
    
    public CRImageProvider getImageProvider() { return imageProvider; }
    public void setImageProvider(CRImageProvider imageProvider_) { imageProvider = imageProvider_; }
    
    public double getViewPositionX() { return viewPosition.x; } 
    public double getViewPositionY() { return viewPosition.y; }
    
    public void setViewPosition(CRVector3d viewPosition_) {
    	viewPosition.copyFrom(viewPosition_);
    	checkAndRedisplayViewPosition();
    }
    
    public void centerAtViewPosition(CRVector3d viewPosition_) {
    	viewPosition.copyFrom(viewPosition_);
    	viewPosition.x -= this.getSize().getWidth()/2;
    	viewPosition.y -= this.getSize().getHeight()/2;
    }
    
    private void checkAndRedisplayViewPosition() {
    	if (viewPosition.x < 0) { viewPosition.x = 0; }
    	if (viewPosition.x > controller.getWorld().getSize().x - this.getSize().width) { viewPosition.x  = controller.getWorld().getSize().x - this.getSize().width; }
    	if (viewPosition.y < 0) { viewPosition.y = 0; }
    	if (viewPosition.y > controller.getWorld().getSize().y - this.getSize().height) { viewPosition.y  = controller.getWorld().getSize().y - this.getSize().height; }
    	controller.getControlPanel().redisplayCurrentViewPosition();
    }
	
}
