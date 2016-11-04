package geometry;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import superposition.SuperPositionQCP;

/**
 * 
 * @author antonin
 * 
 *         Class for superposing sets of points.
 *
 */

public class Transformer {

	private SuperPositionQCP qcp = new SuperPositionQCP();

	public void set(Point3d[] a, Point3d[] b) {
		qcp.set(a, b);
		qcp.getRmsd();
	}

	public double getRmsd() {
		return qcp.getRmsd();
	}

	public Point3d[] transform() {
		return qcp.getTransformedCoordinates();
	}

	public Matrix4d getMatrix() {
		Matrix4d m = qcp.getTransformationMatrix();
		return m;
	}

	public Matrix3d getRotationMatrix() {
		return qcp.calcRotationMatrix();
	}

	public static void main(String[] args) {
		double angle1 = -0.2;
		double angle2 = 0.3;
		double[][] ac = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
		double[][] bc = { { Math.cos(angle1), Math.sin(angle1), 0 }, { -Math.sin(angle1), Math.cos(angle1), 0 },
				{ 0, 0, 1 } };
		double[][] cc = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
		double[][] dc = { { Math.cos(angle2), Math.sin(angle2), 0 }, { -Math.sin(angle2), Math.cos(angle2), 0 },
				{ 0, 0, 1 } };
		Transformer ta = new Transformer();
		ta.set(PointConversion.getPoints3d(ac), PointConversion.getPoints3d(bc));
		Transformer tb = new Transformer();
		tb.set(PointConversion.getPoints3d(cc), PointConversion.getPoints3d(dc));

		ta.getRmsd();
		Matrix3d ma = ta.getRotationMatrix();
		tb.getRmsd();
		Matrix3d mb = tb.getRotationMatrix();

		//System.out.println(compareRotations(ma, mb));
	}
}