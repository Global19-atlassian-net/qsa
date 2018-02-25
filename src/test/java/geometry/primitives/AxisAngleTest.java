package geometry.primitives;

import geometry.superposition.Superposer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import junit.framework.TestCase;
import language.Pair;
import testing.TestResources;

/**
 *
 * @author Antonin Pavelka
 */
public class AxisAngleTest extends TestCase {

	private Random random = new Random(1);
	private Point[] sphere = createSphereSurface();
	private TestResources resources = new TestResources();

	int cycles = 10000;

	public AxisAngleTest(String testName) {
		super(testName);
	}

	public void testSphereToCube() {
		Matrix3d matrix = new Matrix3d();
		matrix.rotX(50);
		AxisAngle axisAngle = AxisAngleFactory.toAxisAngle(matrix);
		Point sphere = new Point(1, 2, 3).normalize().divide(2);
		Point cube = axisAngle.sphereToCube(sphere);
		assert cube.close(new Point(0.16666666666666666, 0.3333333333333333, 0.5));
		double colinearity = Math.abs(1 - sphere.dot(cube) / sphere.size() / cube.size());
		assert colinearity < 0.000001;

	}

	public void testGetVectorRepresentation() {
		File file = resources.getDirectoris().getAxisAngleGraph();
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			for (int i = 0; i < cycles; i++) {
				compare(bw);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void compare(BufferedWriter bw) throws IOException {
		Point[] x = rotateRandomly(sphere);
		Point[] y = rotateRandomly(sphere);

		Point vectorX = getVectorizedRotation(new Pair(sphere, x));
		Point vectorY = getVectorizedRotation(new Pair(sphere, y));

		AxisAngle aaX = AxisAngleFactory.toAxisAngle(getRotationMatrix(new Pair(sphere, x)));
		AxisAngle aaY = AxisAngleFactory.toAxisAngle(getRotationMatrix(new Pair(sphere, y)));

		double vectorDistance = vectorDistanceEuclidean(new Pair(vectorX, vectorY)); // diff of vectors for both sphere pairs
		//double vectorDistance = vectorDistanceAvg(new Pair(vectorX, vectorY)); // diff of vectors for both sphere pairs
		//double vectorDistance = vectorDistanceChebyshev(new Pair(vectorX, vectorY));
		double objectDistance = getObjectDistance(new Pair(x, y)); // how different the second, rotated, spheres are

		if (vectorDistance > 0.5 && objectDistance < 40) {
			System.out.println(vectorDistance + "," + objectDistance + " ***");
			System.out.println();
			System.out.println(vectorX);
			System.out.println(vectorY);
			System.out.println("aa " + aaX);
			System.out.println("aa " + aaY);
			System.out.println("---");
			for (Point p : x) {
				System.out.println(p);
			}
			System.out.println("---");
			for (Point p : y) {
				System.out.println(p);
			}
			System.out.println("------");
		}
		bw.write(vectorDistance + "," + objectDistance + "\n");
	}

	private Point getVectorizedRotation(Pair<Point[]> objects) {
		AxisAngle axisAngle = AxisAngleFactory.toAxisAngle(getRotationMatrix(objects));
		Point vector = axisAngle.getVectorRepresentation();
		return vector;
	}

	private double getObjectDistance(Pair<Point[]> objects) {
		Matrix3d rotation = getRotationMatrix(objects);
		AxisAngle axisAngle = AxisAngleFactory.toAxisAngle(rotation);
		return axisAngle.getAngleInDegrees();
		/*		double max = 0;
		for (int i = 0; i < objects._1.length; i++) {
			double d = objects._1[i].distance(objects._2[i]);
			if (d > max) {
				max = d;
			}
		}
		return max;*/
	}

	private double vectorDistanceEuclidean(Pair<Point> vectors) {
		double sum = 0;
		for (int i = 0; i < 3; i++) {
			double d = closedDistance(vectors._1.getCoords()[i], vectors._2.getCoords()[i]);
			sum += d * d;
		}
		return Math.sqrt(sum);
	}
	
	private double vectorDistanceAvg(Pair<Point> vectors) {
		double sum = 0;
		for (int i = 0; i < 3; i++) {
			double d = closedDistance(vectors._1.getCoords()[i], vectors._2.getCoords()[i]);
			sum += d;
		}
		return sum / 3;
	}

	private double vectorDistanceChebyshev(Pair<Point> vectors) {
		double max = 0;
		for (int i = 0; i < 3; i++) {
			double d = closedDistance(vectors._1.getCoords()[i], vectors._2.getCoords()[i]);
			if (d > max) {
				max = d;
			}
		}
		return max;
	}

	private double closedDistance(double a, double b) {
		if (a > b) {
			double pom = a;
			a = b;
			b = pom;
		}
		double dif = b - a;
		if (b - a > 1) {
			dif = 2 - dif;
		}
		assert dif >= 0;
		assert dif <= 1 : a + " " + b + " " + dif;
		return dif;
	}

	private Matrix3d getRotationMatrix(Pair<Point[]> objects) {
		Superposer transformer = new Superposer();
		transformer.set(objects._1, objects._2);
		return transformer.getRotationMatrix();
	}

	private Point[] createSphereSurface() {
		Point[] sphere = {
			new Point(1, 0, 0),
			new Point(-1, 0, 0),
			new Point(0, 1, 0),
			new Point(0, -1, 0),
			new Point(0, 0, 1),
			new Point(0, 0, -1)
		};
		return sphere;
	}

	private Point createRandomUnit() {
		Point point = new Point(1, 0, 0);
		Matrix3d rotation = randomRotation();
		Point3d randomUnit = point.toPoint3d();
		rotation.transform(randomUnit);
		return new Point(randomUnit);
	}

	private Point[] rotateRandomly(Point[] points) {
		Matrix3d rotation = randomRotation();
		Point[] rotated = new Point[points.length];
		for (int i = 0; i < points.length; i++) {
			rotated[i] = points[i].transform(rotation);
		}
		return rotated;
	}

	private Matrix3d randomRotation() {
		Matrix3d x = new Matrix3d();
		x.rotX(randomAngle());
		Matrix3d y = new Matrix3d();
		y.rotY(randomAngle());
		Matrix3d z = new Matrix3d();
		z.rotZ(randomAngle());
		x.mul(y);
		x.mul(z);
		return x;
	}

	private double randomAngle() {
		return random.nextDouble() * Math.PI * 2;
	}

}
