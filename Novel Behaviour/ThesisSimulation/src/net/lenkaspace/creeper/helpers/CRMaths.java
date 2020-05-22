package net.lenkaspace.creeper.helpers;

import java.util.Random;

/**
 * Provides static methods with mathematical functions
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */
public class CRMaths {

	
	/**
	 * Get distance between points in a 2D space
	 */
	public static double getDistanceOfPoints(double x1_, double y1_, double x2_, double y2_) {
		return Math.hypot(x1_ - x2_, y1_ - y2_);
	}
	
	
	/**
	 * Format a double to return format X.YYYY, where the number of Y's is the number of decimal points
	 * @param number_ double number to format
	 * @param numOfdecimalPoints_ int number of decimal points
	 * @return String formatted string
	 */
	public static String formatDouble(double number_, int numOfdecimalPoints_) {
		String returnStr = "";
		returnStr = "" + Math.round(number_ * Math.pow(10, numOfdecimalPoints_)) / (double) Math.pow(10, numOfdecimalPoints_);
		if (returnStr.indexOf(".") < 0) {
			returnStr += ".";
		}
		for (int i = returnStr.length(); i<numOfdecimalPoints_+2; i++) {
			returnStr += "0";
		}
		return returnStr;
	}
	
	
	/**
	 * Return a random double between 0 and 1, including or excluding 0
	 * @param inclZero_ boolean is true, 0 can also be a result
	 */
	public static double getRandomDouble(boolean inclZero_) {
	    double aStart = 0.0;
	   
	    double aEnd = 10.0;
		if ( aStart > aEnd ) {
	      throw new IllegalArgumentException("Start cannot exceed End.");
	    }
	    //get the range, casting to long to avoid overflow problems
	    long range = (long)aEnd - (long)aStart + 1;
	    // compute a fraction of the range, 0 <= frac < range
	    long fraction = (long)(range * Math.random());
	    int randomNumber =  (int)(fraction + aStart); 
	    if (inclZero_) {
	    	return (double)randomNumber/10;
	    } else {
	    	return (double)randomNumber/10 + 0.0001;
	    }  
	}
	
	/**
	 * Return a random integer between <start;end>
	 * @param start_ int start of the interval, included
	 * @param end_ int end of the interval, included
	 */
	public static int getRandomInteger(int start_, int end_) {
		return getRandomInteger(start_, end_, false);
	}
	/**
	 * Return a random integer between <start;end>
	 * @param start_ int start of the interval, included
	 * @param end_ int end of the interval, included
	 * @param canBePositiveOrNegative_ the function can also return numbers from <-end;-start>
	 */
	public static int getRandomInteger(int start_, int end_, boolean canBePositiveOrNegative_) {
		if ( start_ > end_ ) {
	      throw new IllegalArgumentException("Start cannot exceed End.");
	    }
	    //get the range, casting to long to avoid overflow problems
	    long range = (long)end_ - (long)start_ + 1;
	    // compute a fraction of the range, 0 <= frac < range
	    long fraction = (long)(range * Math.random());
	    int randomNumber =  (int)((fraction + start_)); 
	    
	    if (canBePositiveOrNegative_ && Math.random() < 0.5) {
	    	randomNumber = -randomNumber;
		}
	    
	    return randomNumber;
	    
	}
	
	/**
	 * Return a random Gaussian number
	 */
	public static double getRandomGaussian (double mean_, double variance_) {
		Random rand = new Random();
		return (mean_ + rand.nextGaussian()*variance_);
	}
	
	
	//==================================== FUNCTIONS ====================================
	
	/**
	 * computeBrokenLine
	 * 	- line with a break middle, x range 0;1 y range -1;1
	 * 	- an example graph for threshold = 0.2:
	 * 
	 * 1
	 *                   /
	 *                /
	 *             /
	 *          /
	 * 0     /           1 
	 *      /
	 *     /
	 *    /
	 *   /
	 * -1	 
	 */
	public static double computeBrokenLine(double middle, double in) {
		if (in < middle)  {
	       return in/middle - 1;
		} else {
	       return (in-middle)/(1-middle);
		}
	}
	
	
	/**
	 * computeVshape
	 * 	 - returns value from <-1;1> based on a v-shaped graph for x=<0;1>
	 *	 - example: middle = 0.3, maxLeft = 0.4, maxRIght = 0.8
	 *
	 *  1
	 *  
	 *                             / 
	 *                           /
	 *   \                     /
	 *    \                  /
	 *  0  \    0.3        /        1
	 *      \            /
	 *       \         /
	 *        \      /
	 *         \   /
	 * -1       \/
	 *      
	 */
	public static double computeVShapeImbalanced(double in, double middle, double maxLeft, double maxRight) {
		if (in < middle) {
			return maxLeft - ((1+maxLeft)/middle * in );
		} else {
			return (1+maxRight)*((in-middle)/(1-middle))-1;
		}
	}
	
	
	
	/**
	 * computeSigmoid
	 * 	 - returns sigmoid from <0;1> value for x=<0;1> 
	 */
	public static double computeSigmoid (double in) {
		double returnVal = (1/ (1 + Math.exp(7 - 13*in)));
		if (in == 0) {
			returnVal = 0;
		} else if (in == 1) {
			returnVal = 1;
		}
		return (returnVal);
	}
	
	/**
	 * computeSigmoid
	 * 	 - returns a value from <-1;1> for x=<-1;1> based on two sigmoid with half of the Y range.
	 *     The sigmoids mirrored along the Y-axis
	 */
	public static double computeSigmoidWithParams (double in, double slope, double xAxisThreshold) {
		if (in == -1) {
			return -1;
		} else if (in < 0) {
			return -(1/ (1 + Math.exp(7 + slope*(in+xAxisThreshold))));
		} else if (in == 0) {
			return 0;
		} else if (in > 0 && in < 1) {
			return (1/ (1 + Math.exp(7 - slope*(in-xAxisThreshold))));
		} else {
			return 1;
		}
	}
	
	
	/**
	 * computeLinearBeforeThreshold
	 * 	- for x=<0;1>, y=<0;1>. An example graph for threshold = 0.5:
	 * 
	 * 1  	   ________
	 *        |
	 *        | 
	 *      /   
	 *    /   
	 *  /    
	 * 0	 0.5	  1
	 */
	public static double computeLinearBeforeThreshold(double in, double threshold) {
		if (in < threshold) {
			return in;
		} else {
			return 1;
		}
	}
	
	/**
	 * computeInverseBeforeThreshold
	 * 	- for x=<0;1>, y=<0;1>. An example graph for threshold = 0.5:
	 * 
	 * 1  \
	 *     \
	 *      \
	 *       |
	 *       |
	 *       |________
	 * 0	 0.5	  1
	 */
	public static double computeLinearInverseBeforeThreshold(double in, double threshold) {
		if (in < threshold) {
			return 1-in;
		} else {
			return 0;
		}
	}
	
	/**
	 * computeLinearNegativeInverseBeforeThreshold
	 * 	- for x=<0;1>, y=<-1;0>. An example graph for threshold = 0.5:
	 * 
	 * 0      _________ 1
	 *        |
	 *        |
	 *       /
	 *      / 
	 *     / 
	 *    /   
	 * -1/	 
	 */
	public static double computeLinearNegativeInverseBeforeThreshold(double in, double threshold) {
		if (in < threshold) {
			return -(1-in);
		} else {
			return 0;
		}
	}
	
	/**
	 * computeNegativeHillBeforeThreshold
	 * 	- extension of computeLinearNegativeInverseBeforeThreshold, for x=<-1;1>, y=<-1;1>
	 * 	- an example graph for threshold = 0.2:
	 * 
	 *          -0.2      0.2
	 * -1 ________     0    _________ 1
	 *            |        |
	 *            |        |
	 *            \       /
	 *             \     / 
	 *              \   / 
	 *               \ /   
	 * -1	 
	 */
	public static double computeNegativeHillBeforeThreshold(double in, double threshold) {
		if (in <=0) {
			if (in < -threshold) {
				return 0;			
			} else {
				return -(1+in);
			}
		} else {
			if (in < threshold) {
				return -(1-in);
			} else {
				return 0;
			}
		}
	}
	
	/**
	 * computeNegativeValleyBeforeThreshold
	 * 	- extension of computeLinearNegativeInverseBeforeThreshold, for range in = <-1;1>
	 * 	- an example graph for threshold = 0.2:
	 *        
	 *        -0.2        0.2
	 * -1        ____0____      1
	 *          |         |
	 *          |         |
	 *         /           \
	 *        /             \
	 *       /               \
	 *      /                 \
	 *     /                   \
	 *    /                     \  
	 * -1	 
	 */
	public static double computeNegativeValleyBeforeThreshold(double in, double threshold) {
		if (in <=0) {
			if (in < -threshold) {
				return in;		
			} else {
				return 0;
			}
		} else {
			if (in > threshold) {
				return -in;
			} else {
				return 0;
			}
		}
	}
	
	
	
	/**
	 * computeStepFunctionFirstPositive
	 * 	- for x=<0;1>, y=<-1;1>. An example graph for threshold = 0.4:
	 * 
	 * 1 _______
	 *         |
	 *         |
	 * 0       |0.4       1
	 *         | 
	 *         |__________
	 * -1	 
	 */
	public static double computeStepFunctionFirstPositive(double in, double threshold) {
		if (in <= threshold) {
			return 1;
		} else {
			return -1;
		}
	}
	
}
