package vectorization;

import geometry.primitives.AxisAngle;
import geometry.primitives.AxisAngleFactory;
import geometry.primitives.Point;
import geometry.superposition.Superposer;
import language.Pair;

/**
 *
 * @author Antonin Pavelka
 */
public class AdvancedObjectPairVectorizer implements ObjectPairVectorizer {

	@Override
	public float[] vectorize(RigidBody b1, RigidBody b2) {
		Superposer transformer = getTransformer(b1, b2);
		Point[] averaged = average(getSuperposed(transformer));
		return null;
	}

	private Pair<Point[]> getSuperposed(Superposer transformer) {
		Point[] x = transformer.getXPoints();
		Point[] y = transformer.getTransformedYPoints();
		return new Pair(x, y);
	}

	private AxisAngle getEigenvector(Superposer transformer) {
		AxisAngle axisAngle = AxisAngleFactory.toAxisAngle(transformer.getRotationMatrix());
		return axisAngle;
	}

	/* Superposes the second on the first. */
	private Superposer getTransformer(RigidBody b1, RigidBody b2) {
		Superposer transformer = new Superposer();
		transformer.set(b1.getAllPoints(), b2.getAllPoints());
		return transformer;
	}

	private Point[] average(Pair<Point[]> aligned) {
		Point[] x = aligned._1;
		Point[] y = aligned._2;
		Point[] averaged = new Point[x.length];
		for (int i = 0; i < averaged.length; i++) {
			averaged[i] = x[i].plus(y[i]).divide(2);
		}
		return averaged;
	}

	private void print(Point[] a) {
		for (Point p : a) {
			System.out.println(p);
		}
	}

}
