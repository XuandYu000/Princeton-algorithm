/* *****************************************************************************
 *  Name: Xu Zhiyu
 *  Date: 11/22/24
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {
    private static final int BORDER_ENERGY = 1000;
    private static final int[] DIR = { -1, 0, 1 };
    private int width;
    private int height;
    private double[][] energies;
    private final Picture pic;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Picture cannot be null");
        }

        this.pic = new Picture(picture);
        width = picture.width();
        height = picture.height();
        energies = new double[width][height];
    }

    // current picture
    public Picture picture() {
        Picture newpic = new Picture(width, height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newpic.setRGB(i, j, pic.getRGB(i, j));
            }
        }
        return newpic;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException("Invalid x or y");
        }

        if (energies[x][y] != 0.0) {
            return energies[x][y];
        }

        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
            energies[x][y] = BORDER_ENERGY;
            return energies[x][y];
        }

        energies[x][y] = Math.sqrt(gradient(pic.get(x - 1, y), pic.get(x + 1, y))
                                           + gradient(pic.get(x, y - 1), pic.get(x, y + 1)));

        return energies[x][y];
    }

    private double gradient(Color a, Color b) {
        int diffR = a.getRed() - b.getRed();
        int diffG = a.getGreen() - b.getGreen();
        int diffB = a.getBlue() - b.getBlue();

        return Math.pow(diffR, 2) + Math.pow(diffG, 2) + Math.pow(diffB, 2);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int[] seam = new int[width];
        double[][] disTo = new double[width][height];
        // edgeTo[i][j] 表示到节点i,j，要从x=i-1,y=edgeTo[i][j]出发路径最短
        int[][] endTo = new int[width][height];
        double sp = Double.POSITIVE_INFINITY;
        int endRow = -1;

        // 初始化路径， x轴除x = 0外其余路径隔断
        Arrays.fill(disTo[0], 0.0);
        for (int i = 1; i < width; i++) {
            Arrays.fill(disTo[i], Double.POSITIVE_INFINITY);
        }

        for (int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height; j++) {
                for (int dir : DIR) {
                    int nextj = j + dir;

                    if (nextj < 0 || nextj >= height) continue;

                    // relax
                    if (disTo[i + 1][nextj] > disTo[i][j] + energy(i + 1, nextj)) {
                        disTo[i + 1][nextj] = disTo[i][j] + energy(i + 1, nextj);
                        endTo[i + 1][nextj] = j;
                    }
                }
            }
        }
        // 确定回溯的起点
        for (int i = 0; i < height; i++) {
            if (sp > disTo[width - 1][i]) {
                sp = disTo[width - 1][i];
                endRow = i;
            }
        }

        // 从最后一行开始回溯
        for (int col = width - 1, row = endRow; col >= 0; row = endTo[col][row], col--)
            seam[col] = row;
        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] seam = new int[height];
        double[][] disTo = new double[height][width];
        // edgeTo[i][j] 表示到节点i,j，要从x=i-1,y=edgeTo[i][j]出发路径最短
        int[][] endTo = new int[height][width];
        double sp = Double.POSITIVE_INFINITY;
        int endCol = -1;

        // 初始化路径， y轴除y = 0外其余路径隔断
        Arrays.fill(disTo[0], 0.0);
        for (int i = 1; i < height; i++) {
            Arrays.fill(disTo[i], Double.POSITIVE_INFINITY);
        }

        for (int row = 0; row < height - 1; row++) {
            for (int col = 0; col < width; col++) {
                for (int dir : DIR) {
                    int nextCol = col + dir;

                    if (nextCol < 0 || nextCol >= width) continue;

                    // relax
                    if (disTo[row + 1][nextCol] > disTo[row][col] + energy(nextCol, row + 1)) {
                        disTo[row + 1][nextCol] = disTo[row][col] + energy(nextCol, row + 1);
                        endTo[row + 1][nextCol] = col;
                    }
                }
            }
        }
        // 确定回溯起点
        for (int col = 0; col < width; col++) {
            if (sp > disTo[height - 1][col]) {
                sp = disTo[height - 1][col];
                endCol = col;
            }
        }

        // 从最后一行开始回溯
        for (int row = height - 1, col = endCol; row >= 0; col = endTo[row][col], row--)
            seam[row] = col;
        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validSeam(seam, width, height);
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height - 1; row++) {
                if (row >= seam[col]) {
                    pic.setRGB(col, row, pic.getRGB(col, row + 1));
                    energies[col][row] = energies[col][row + 1];
                }
            }
        }

        // reset energy
        for (int col = 0; col < width; col++) {
            for (int offset : DIR) {
                int row = seam[col] + offset;
                if (row >= 0 && row < height)
                    energies[col][row] = 0;
            }
        }
        height--;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validSeam(seam, height, width);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width - 1; col++) {
                if (col >= seam[row]) {
                    pic.setRGB(col, row, pic.getRGB(col + 1, row));
                    energies[col][row] = energies[col + 1][row];
                }
            }
        }
        // reset energy
        for (int row = 0; row < height; row++) {
            for (int offset : DIR) {
                int col = seam[row] + offset;
                if (col >= 0 && col < width)
                    energies[col][row] = 0;
            }
        }
        width--;
    }

    private void validSeam(int[] seam, int length, int valueLimit) {
        if (seam == null || seam.length != length || valueLimit < 1)
            throw new IllegalArgumentException("Invalid seam");
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= valueLimit)
                throw new IllegalArgumentException("Invalid seam");
        }
        for (int i = 0, j = 1; j < seam.length; i++, j++) {
            if (Math.abs(seam[i] - seam[j]) > 1)
                throw new IllegalArgumentException("Invalid seam");
        }
    }

    //  unit testing (optional)
    public static void main(String[] args) {

    }

}