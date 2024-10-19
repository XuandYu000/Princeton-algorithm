import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {
    private static final int BOTTOMBOUND = 3;
    private Point[] points;
    private ArrayList<LineSegment> segments;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        // corner case checking
        if (points == null) {
            throw new IllegalArgumentException("argument to constructor is null");
        }
        for (Point p : points) {
            if (p == null) {
                throw new IllegalArgumentException("one point is null");
            }
        }
        int len = points.length;
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                if (points[i].compareTo(points[j]) == 0) {
                    throw new IllegalArgumentException("repeated point");
                }
            }
        }
        if (len < 4) {
            return;
        }

        points = Arrays.copyOf(points, len);
        segments = new ArrayList<>();
        Point[] backup = Arrays.copyOf(points, len);

        for (Point p : points) {
            Arrays.sort(backup, p.slopeOrder());
            for (int i = 1; i < len; ) {
                int j = i + 1;
                while (j < len && p.slopeTo(backup[i]) == p.slopeTo(backup[j])) {
                    j++;
                }
                if (j - i >= BOTTOMBOUND && p.compareTo(min(backup, i, j - 1)) < 0) {
                    segments.add(new LineSegment(p, max(backup, i, j - 1)));
                }
                if (j == len) {
                    break;
                }
                i = j;
            }
        }

    }

    private Point min(Point[] s, int lo, int hi) {
        if (lo > hi || s == null) {
            throw new IllegalArgumentException();
        }
        Point ret = s[lo];
        for (int i = lo + 1; i <= hi; i++) {
            if (s[i].compareTo(ret) < 0) {
                ret = s[i];
            }
        }
        return ret;
    }

    private Point max(Point[] s, int lo, int hi) {
        if (lo > hi || s == null) {
            throw new IllegalArgumentException();
        }
        Point ret = s[lo];
        for (int i = lo + 1; i <= hi; i++) {
            if (s[i].compareTo(ret) > 0) {
                ret = s[i];
            }
        }
        return ret;
    }

    // the number of line segments
    public int numberOfSegments() {
        return segments.size();
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] ret = new LineSegment[segments.size()];
        segments.toArray(ret);
        return ret;
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
