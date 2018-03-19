package vectorization.force;

import geometry.metric.LpSpace;
import geometry.primitives.Point;
import geometry.superposition.Superposer;
import javax.vecmath.Point3d;
import structure.VectorizationException;
import vectorization.BallsDistanceVectorizer;
import vectorization.RigidBody;

/**
 *
 * @author Antonin Pavelka
 */
public class RigidBodyPair {

	public final RigidBody body1, body2;
	private final float[] vector;
	private final Point3d[] points;
	private static BallsDistanceVectorizer vectorizer = new BallsDistanceVectorizer();
	private static LpSpace metric = new LpSpace(vectorizer.getDimensions());
	private static Superposer superposer = new Superposer(false);

	public RigidBodyPair(RigidBody body1, RigidBody body2) {
		try {
			this.body1 = body1;
			this.body2 = body2;
			vector = new float[vectorizer.getDimensions().number() + 1];
			points = getPoints(body1, body2);
			float[] known = vectorizer.vectorize(body1, body2, 0);
			for (int i = 0; i < known.length; i++) {
				vector[i] = known[i];
			}
		} catch (VectorizationException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void setDihedral(float d) {
		vector[vector.length - 1] = d;
	}

	public double vectorDistance(RigidBodyPair other) {
		return metric.euclidean(this.vector, other.vector);
	}

	public double rmsdDistance(RigidBodyPair other) {
		superposer.set(this.points, other.points);
		return superposer.getRmsd();
	}

	private Point3d[] getPoints(RigidBody a, RigidBody b) {
		Point[] x = a.getAllPoints();
		Point[] y = b.getAllPoints();
		Point3d[] p3d = new Point3d[x.length + y.length];
		for (int i = 0; i < x.length; i++) {
			p3d[i] = x[i].toPoint3d();
		}
		for (int i = 0; i < y.length; i++) {
			p3d[i + x.length] = y[i].toPoint3d();
		}
		return p3d;
	}

}
