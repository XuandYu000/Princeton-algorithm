import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class BruteCollinearPoints {
    private static final int POINTSONLINE = 3;
    private Point[] points;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException("The given points is null");
        }
        this.points = points;
        Arrays.sort(points);
    }

    // the number of line segments
    public int numberOfSegments() {
        int n = 0;
        for (int i = 0; i < points.length - POINTSONLINE; i++) {
            for (int j = i + 1; j < points.length - POINTSONLINE + 1; j++) {
                for (int k = j + 1; k < points.length - POINTSONLINE + 2; k++) {
                    for (int r = k + 1; r < points.length - POINTSONLINE + 3; r++) {
                        double slope1 = points[i].slopeTo(points[j]);
                        double slope2 = points[i].slopeTo(points[k]);
                        double slope3 = points[i].slopeTo(points[r]);
                        if (slope1 == slope2 && slope2 == slope3) {
                            n++;
                        }
                    }
                }
            }
        }
        return n;
    }

    // the line segments
    public LineSegment[] segments() {
        int n = numberOfSegments();
        int cnt = 0;
        LineSegment[] segments = new LineSegment[n];
        for (int i = 0; i < points.length - POINTSONLINE; i++) {
            for (int j = i + 1; j < points.length - POINTSONLINE + 1; j++) {
                for (int k = j + 1; k < points.length - POINTSONLINE + 2; k++) {
                    for (int r = k + 1; r < points.length - POINTSONLINE + 3; r++) {
                        double slope1 = points[i].slopeTo(points[j]);
                        double slope2 = points[i].slopeTo(points[k]);
                        double slope3 = points[i].slopeTo(points[r]);
                        if (slope1 == slope2 && slope2 == slope3) {
                            segments[cnt++] = new LineSegment(points[i], points[r]);
                        }
                    }
                }
            }
        }
        return segments;
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
