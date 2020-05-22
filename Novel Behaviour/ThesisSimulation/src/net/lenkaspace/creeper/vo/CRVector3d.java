
package net.lenkaspace.creeper.vo;

/**
 * Represents a vector in a 3D space. Provides vector calculations.
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */
public class CRVector3d implements Comparable<CRVector3d>  {

	public static final int INVALID_COMPONENT = -99999;
	public double x;
	public double y;
	public double z;
	
	//==================================== STATIC OPERATIONS ===============================
	public static CRVector3d subtractVectors(CRVector3d  vec1, CRVector3d  vec2) {		
		return new CRVector3d (vec1.x-vec2.x, vec1.y-vec2.y, vec1.z-vec2.z);
	}
	
	public static CRVector3d addVectors(CRVector3d vec1, CRVector3d vec2) {
		return new CRVector3d (vec1.x+vec2.x, vec1.y+vec2.y, vec1.z+vec2.z);
	};
	
	public static double  dotProduct (CRVector3d  vec1, CRVector3d vec2) {
		return (vec1.x*vec2.x + vec1.y*vec2.y + vec1.z*vec2.z);
	}

	public static CRVector3d crossProduct(CRVector3d  vec1, CRVector3d vec2) {
		return new CRVector3d ( (vec1.y*vec2.z) - (vec1.z*vec2.y),
							(vec1.z*vec2.x) - (vec1.x*vec2.z),
							(vec1.x*vec2.y) - (vec1.y*vec2.x));
	}
	
	public static CRVector3d invalidVector() {
		return new CRVector3d(INVALID_COMPONENT, INVALID_COMPONENT, INVALID_COMPONENT);
	}
	
	//==================================== CONSTRUCTORS ====================================
	public CRVector3d(double x_,double y_,double z_) {
		x=x_;
		y=y_;
		z=z_;
	}
	
	public CRVector3d(CRVector3d anotherVec_) {
		x=anotherVec_.x;
		y=anotherVec_.y;
		z=anotherVec_.z;
	}
	
	public CRVector3d() {
		x= 0;
		y= 0;
		z= 0;
	} 
	
	public CRVector3d getInverted() {
		return new CRVector3d(-x,-y,-z);
	}
	
	public void copyFrom(CRVector3d anotherVec_) {
		x=anotherVec_.x;
		y=anotherVec_.y;
		z=anotherVec_.z;
	}
	
	//==================================== OPERATIONS =====================================
	
	public boolean isValid() {
		return (x!=INVALID_COMPONENT && y!=INVALID_COMPONENT && z!=INVALID_COMPONENT);
	}
	
	public double getMagnitude(){
		return (double) Math.sqrt (x*x + y*y + z*z );
	}
	
	public void invert() {
		x = -x;
		y = -y;
		z = -z;
	}
	
	public void normalize() {
		double tol = 0.0001;
		double m = Math.sqrt((x* x + (y*y) + (z*z)));
		if (m <= tol) { m=1; }
		x /= m;
		y /=m;
		z /=m;

		if (Math.abs(x) < tol){x = 0;}
		if (Math.abs(y) < tol){y = 0;}
		if (Math.abs(z) < tol){z = 0;}
	}
	
	public void addVector(CRVector3d vector_) {
		x += vector_.x;
		y += vector_.y;
		z += vector_.z;
	}
	
	public void subtratVector(CRVector3d vector_) {
		x -= vector_.x;
		y -= vector_.y;
		z -= vector_.z;
	}
	
	public void multiplyBy(double number) {
		this.x *= number;
		this.y *= number;
		this.z *= number;
	}
	
	
	
	public boolean equals(CRVector3d anotherVec) {
		if ((int)anotherVec.x == (int)this.x && (int)anotherVec.y == (int)this.y && (int)anotherVec.z == (int)this.z) {
			return true;
		}
		return false;
	}

	public int compareTo(CRVector3d o) {
		if (this.equals(o)) {
			return 0;
			
		} else {
			return 1;
		}
	}
	
	public String toString() {
		return "[" + x + "; " + y + "; " + z + "]";
	}
	
}
