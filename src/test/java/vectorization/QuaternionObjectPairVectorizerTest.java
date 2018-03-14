package vectorization;

import geometry.metric.Metric;
import geometry.primitives.Point;
import geometry.superposition.Superposer;
import geometry.test.RandomBodies;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import junit.framework.TestCase;
import language.MathUtil;
import language.Pair;
import structure.VectorizationException;
import testing.TestResources;

/**
 *
 * @author Antonin Pavelka
 */
public class QuaternionObjectPairVectorizerTest extends TestCase {

	private RandomBodies randomBodies = new RandomBodies();
	private QuaternionObjectPairVectorizer vectorizer = new QuaternionObjectPairVectorizer();
	//private DualQuaternionObjectPairVectorizer vectorizer = new DualQuaternionObjectPairVectorizer();
	private TestResources resources = new TestResources();
	private final int cycles = 20000;
	//private final int cycles = 1;
	private double[] xs = new double[cycles];
	private double[] ys = new double[cycles];

	public QuaternionObjectPairVectorizerTest(String testName) {
		super(testName);
	}

	public void testVectorize() throws IOException, VectorizationException {
		File file = resources.getDirectoris().getQuaternionGraph();
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			bw.write("rmsd,euclidean,chebyshev\n");
			for (int i = 0; i < cycles; i++) {
				compare(bw, i);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		double correlation = MathUtil.Correlation(xs, ys);
		//if (correlation < 0.2) {
		System.out.println("");
		System.out.println("correlation: " + correlation);
		System.out.println("");
		//throw new RuntimeException("correlation: " + correlation);
		//}
	}

	private void compare(BufferedWriter bw, int index) throws IOException, VectorizationException {
		Point[][] x = randomBodies.createRandomOctahedronPair();
		Point[][] y = randomBodies.createRandomOctahedronPair();

		Pair<RigidBody> a = new Pair(RigidBody.create(x[0]), RigidBody.create(x[1]));
		Pair<RigidBody> b = new Pair(RigidBody.create(y[0]), RigidBody.create(y[1]));

		float[] vx = vectorizer.vectorize(a._1, a._2, 0);
		float[][] vy = {
			vectorizer.vectorize(b._1, b._2, 0),
			vectorizer.vectorize(b._1, b._2, 1)};
		//float[] vy2 = vectorizer.vectorize(by1, by2, 2);
		//float[] vy3 = vectorizer.vectorize(by1, by2, 3);

		double rmsd = rmsd(x, y);

		double euclideanDistance1 = Metric.euclidean(vx, vy[0]);
		double euclideanDistance2 = Metric.euclidean(vx, vy[1]);
		//double euclideanDistance3 = Metric.euclidean(vx, vy2);
		//double euclideanDistance4 = Metric.euclidean(vx, vy3);
//		double euclideanDistance = Math.min(Math.min(Math.min(euclideanDistance1, euclideanDistance2), euclideanDistance3), euclideanDistance4);
		double euclideanDistance = Math.min(euclideanDistance1, euclideanDistance2);

		double chebyshevDistance1 = Metric.chebyshev(vx, vy[0]);
		double chebyshevDistance2 = Metric.chebyshev(vx, vy[1]);
		//double chebyshevDistance3 = Metric.chebyshev(vx, vy0);
		//double chebyshevDistance4 = Metric.chebyshev(vx, vy0);
//		double chebyshevDistance = Math.min(Math.min(Math.min(chebyshevDistance1, chebyshevDistance2), chebyshevDistance3), chebyshevDistance4);
		double chebyshevDistance = Math.min(chebyshevDistance1, chebyshevDistance2);

		bw.write(rmsd + "," + euclideanDistance + "," + chebyshevDistance + "\n");
		xs[index] = rmsd;
		ys[index] = euclideanDistance;
	}

	private double computeObjectDistancePrimitive(Point[][] x, Point[][] y) {
		double sum = 0;
		int n = 0;
		for (int i = 0; i < 2; i++) {
			for (int k = 0; k < 2; k++) {
				sum += x[i][k].distance(y[i][k]);
				n++;
			}
		}
		return sum / n;
	}

	private double rmsd(Point[][] x, Point[][] y) {

		Superposer superposer = new Superposer();

		Point[] xf = flat(x);
		Point[] yf = flat(y);

		//System.out.println("vvv");
		//print(xf);
		//System.out.println("-");
		//print(yf);
		//System.out.println("---");
		superposer.set(xf, yf);
		return superposer.getRmsd();
	}

	private Point[] flat(Point[][] points) {
		Point[] flat = new Point[points[0].length + points[1].length];
		for (int i = 0; i < points[0].length; i++) {
			flat[i] = points[0][i];
		}
		for (int i = 0; i < points[1].length; i++) {
			flat[i + points[0].length] = points[1][i];
		}
		return flat;
	}

	private float[] internalDistances(Point[] points) {
		float[] a = new float[points.length * (points.length - 1) / 2];
		int i = 0;
		for (int x = 0; x < points.length; x++) {
			for (int y = 0; y < x; y++) {
				a[i++] = (float) points[x].distance(points[y]);
			}
		}
		return a;
	}

	private void printVector(float[] vector) {
		for (float v : vector) {
			System.out.print(v + " ");
		}
		System.out.println("");
	}

	private void print(Point[] points) {
		for (Point p : points) {
			System.out.println(p);
		}
	}

}
