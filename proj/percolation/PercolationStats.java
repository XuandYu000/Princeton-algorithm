/* *****************************************************************************
 *  Name:              Alan Turing
 *  Coursera User ID:  123456
 *  Last modified:     1/1/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private int n;
    private int trials;
    private double[] x;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException();
        }
        this.n = n;
        this.trials = trials;
        this.x = new double[trials];

        for (int k = 0; k < trials; k++) {
            Percolation percolation = new Percolation(n);
            int threshold = 0;
            while (!percolation.percolates()) {
                threshold++;
                int openx = StdRandom.uniformInt(1, n + 1);
                int openy = StdRandom.uniformInt(1, n + 1);
                while (percolation.isOpen(openx, openy)) {
                    openx = StdRandom.uniformInt(1, n + 1);
                    openy = StdRandom.uniformInt(1, n + 1);
                }
                percolation.open(openx, openy);
            }
            x[k] = (double) threshold / (n * n);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(x);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(x);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - 1.96 * stddev() / Math.sqrt(trials);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + 1.96 * stddev() / Math.sqrt(trials);
    }

    // test client (see below)
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        PercolationStats percolationStats = new PercolationStats(n, trials);
        StdOut.println("mean                    = " + percolationStats.mean());
        StdOut.println("stddev                  = " + percolationStats.stddev());
        StdOut.printf("95%% confidence interval = [%f, %f]\n",
                      percolationStats.confidenceLo(), percolationStats.confidenceHi());

    }
}
