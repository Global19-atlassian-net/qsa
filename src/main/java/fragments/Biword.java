package fragments;

import geometry.Coordinates;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point3d;
import geometry.Point;
import pdb.Residue;
import spark.clustering.Clusterable;
import vectorization.SmartVectorizer;

/**
 *
 * @author Antonin Pavelka
 */
public class Biword implements Clusterable<Biword>, Coordinates {

    private static final long serialVersionUID = 1L;
    private WordImpl a_, b_;
    private double[] features_;
    private Point3d[] ps3d;
    private Point[] centeredPoints;
    private double wordDistance;
    private static double maxWdd = Parameters.create().getMaxWordDistDiff();
    private static double maxWr = Parameters.create().getMaxWordRmsd();
    private double[] coords = new double[6];

    public Biword(WordImpl a, WordImpl b) {
        a_ = a;
        b_ = b;
        computeFeatures(a, b);
        wordDistance = a.getCenter().distance(b.getCenter());
        
        SmartVectorizer av = new SmartVectorizer(a_);
        SmartVectorizer bv = new SmartVectorizer(b_);
        coords[0] = av.firstHalf().distance(bv.firstHalf());
        coords[1] = av.secondHalf().distance(bv.secondHalf());
        coords[2] = av.firstHalf().distance(bv.secondHalf());
        coords[3] = av.secondHalf().distance(bv.firstHalf());
        coords[4] = av.getStraightness();
        coords[5] = bv.getStraightness();
    }

    public Biword switchWords() {
        return new Biword(b_, a_);
    }

    public WordImpl[] getWords() {
        WordImpl[] w = {a_, b_};
        return w;
    }

    @Override
    public double[] getCoords() {
        return coords;
    }

    public double[] coordDiff(Biword other) {
        double[] diff = new double[coords.length];
        for (int i = 0; i < coords.length; i++) {
            diff[i] = Math.abs(coords[i] - other.coords[i]);
        }
        return diff;
    }

    public boolean isSimilar(Biword other, WordMatcher wm) {
        if (Math.abs(wordDistance - other.wordDistance) <= maxWdd) {
            if (wm.getRmsd(a_.getId(), other.a_.getId()) <= maxWr) {
                if (wm.getRmsd(b_.getId(), other.b_.getId()) <= maxWr) {
                    return true;
                }
            }

        }
        return false;
    }

    public Point getCenter() {
        return a_.getCenter().plus(b_.getCenter()).divide(2);
    }

    public Point3d getCenter3d() {
        Point p = getCenter();
        return new Point3d(p.getCoords());
    }

    public double distance(Biword other) {
        double sum = 0;
        for (int i = 0; i < features_.length; i++) {
            sum += Math.abs(features_[i] - other.features_[i]);
        }
        return sum / features_.length;
    }

    private void computeFeatures(WordImpl a, WordImpl b) {
        List<Double> features = new ArrayList<>();
        Point[] aps = a.getPoints();
        Point[] bps = b.getPoints();
        for (int x = 0; x < aps.length; x++) {
            for (int y = 0; y < x; y++) {
                double d = aps[x].distance(bps[y]);
                features.add(d);
            }
        }
        features_ = new double[features.size()];
        for (int i = 0; i < features_.length; i++) {
            features_[i] = features.get(i);
        }
    }

    public Point[] getPoints() {
        Point[] aps = a_.getPoints();
        Point[] bps = b_.getPoints();
        Point[] ps = new Point[aps.length + bps.length];
        System.arraycopy(aps, 0, ps, 0, aps.length);
        System.arraycopy(bps, 0, ps, aps.length, bps.length);
        return ps;
    }

    public Point3d[] getPoints3d() {
        if (ps3d == null) {
            Point[] ps = getPoints();
            ps3d = new Point3d[ps.length];
            for (int i = 0; i < ps.length; i++) {
                ps3d[i] = new Point3d(ps[i].getCoords());
            }
        }
        return ps3d;
    }

    public Point[] getCenteredPoints() {
        if (centeredPoints == null) {
            Point[] a = a_.getPoints();
            Point[] b = b_.getPoints();
            centeredPoints = new Point[a.length + b.length];
            Point c = getCenter();
            for (int i = 0; i < a.length; i++) {
                centeredPoints[i] = a[i].minus(c);
            }
            for (int i = 0; i < b.length; i++) {
                centeredPoints[a.length + i] = b[i].minus(c);
            }
        }
        return centeredPoints;
    }

    public Residue[] getResidues() {
        Residue[] a = a_.getResidues();
        Residue[] b = b_.getResidues();
        return Residue.merge(a, b);
    }
}