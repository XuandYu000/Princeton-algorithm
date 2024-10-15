import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int siz;
    private int topVir;
    private int bottomVir;
    private int openSite;
    private boolean[] grid;
    private WeightedQuickUnionUF uf;
    private WeightedQuickUnionUF ufForFull;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        siz = n;
        topVir = 0;
        bottomVir = siz * siz + 1;
        openSite = 0;
        grid = new boolean[siz * siz + 1];
        uf = new WeightedQuickUnionUF(siz * siz + 2);
        ufForFull = new WeightedQuickUnionUF(siz * siz + 1);
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (row < 1 || row > siz || col < 1 || col > siz) {
            throw new IllegalArgumentException("Invalid index");
        }
        int location = siz * (row - 1) + col;
        if (!grid[location]) {
            // Open the site.
            grid[location] = true;
            openSite++;

            // Fill the site if we open the site which is in row 1.
            if (location <= siz) {
                uf.union(topVir, location);
                ufForFull.union(topVir, location);
            }

            if (location > siz * (siz - 1)) {
                uf.union(bottomVir, location);
            }
            // Fill the neighbors which are also open
            int[] dx = { 1, 0, -1, 0 };
            int[] dy = { 0, 1, 0, -1 };
            for (int i = 0; i < 4; i++) {
                int nx = row + dx[i];
                int ny = col + dy[i];
                int nextloc = siz * (nx - 1) + ny;

                // out of index
                if (nx < 1 || nx > siz || ny < 1 || ny > siz) continue;
                // the site is not open
                if (!grid[nextloc]) continue;

                // union the site and the original site
                uf.union(location, nextloc);
                ufForFull.union(location, nextloc);
            }
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (row < 1 || row > siz || col < 1 || col > siz) {
            throw new IllegalArgumentException("Invalid index");
        }

        return grid[siz * (row - 1) + col];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (row < 1 || row > siz || col < 1 || col > siz) {
            throw new IllegalArgumentException("Invalid index");
        }
        return ufForFull.find(siz * (row - 1) + col) == ufForFull.find(topVir);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSite;
    }

    // does the system percolate?
    public boolean percolates() {
        return uf.find(topVir) == uf.find(bottomVir);
    }

    // test client (optional)
    public static void main(String[] args) {
        In in = new In(args[0]);
        int n = in.readInt();
        Percolation p = new Percolation(n);
        while (!in.isEmpty()) {
            int row = in.readInt();
            int col = in.readInt();
            p.open(row, col);
        }
    }
}
