/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final int[][] tiles;
    private final int n;


    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        this.n = tiles.length;
        this.tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.tiles[i][j] = tiles[i][j];
            }
        }
    }

    // string representation of this board
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(n).append("\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sb.append(" ").append(tiles[i][j]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        int hamming = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int ideal = i * n + j + 1;
                if (ideal != tiles[i][j] && tiles[i][j] != 0) {
                    hamming++;
                }
            }
        }
        return hamming;
    }

    // Returns the Manhattan distance (sum of distances of tiles from their goal positions)
    public int manhattan() {
        int manhattanDistance = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != 0) {
                    int targetRow = (tiles[i][j] - 1) / n;
                    int targetCol = (tiles[i][j] - 1) % n;
                    manhattanDistance += Math.abs(i - targetRow) + Math.abs(j - targetCol);
                }
            }
        }
        return manhattanDistance;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (this == y) return true;
        if (y == null || getClass() != y.getClass()) return false;
        Board board = (Board) y;
        if (n != board.n) return false;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != board.tiles[i][j]) return false;
            }
        }
        return true;
    }

    // Helper to create a new board by swapping tiles
    private int[][] copyTiles(int[][] original) {
        int[][] copy = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, n);
        }
        return copy;
    }

    // Swap tiles and generate new board
    private Board addNeighbor(int zeroRow, int zeroCol, int nx, int ny) {
        int newRow = zeroRow + nx;
        int newCol = zeroCol + ny;
        int[][] newTiles = copyTiles(tiles);
        newTiles[zeroRow][zeroCol] = newTiles[newRow][newCol];
        newTiles[newRow][newCol] = 0;
        return new Board(newTiles);
    }

    // Return a list of all neighboring boards (sliding tiles)
    public Iterable<Board> neighbors() {
        List<Board> neighbors = new ArrayList<>();
        int zeroRow = 0, zeroCol = 0;

        // Find the empty space (denoted by 0)
        outer:
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] == 0) {
                    zeroRow = i;
                    zeroCol = j;
                    break outer;
                }
            }
        }

        // Possible moves: up, down, left, right
        if (zeroRow > 0) neighbors.add(addNeighbor(zeroRow, zeroCol, -1, 0)); // Up
        if (zeroRow < n - 1) neighbors.add(addNeighbor(zeroRow, zeroCol, 1, 0)); // Down
        if (zeroCol > 0) neighbors.add(addNeighbor(zeroRow, zeroCol, 0, -1)); // Left
        if (zeroCol < n - 1) neighbors.add(addNeighbor(zeroRow, zeroCol, 0, 1)); // Right

        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] newTiles = copyTiles(tiles);

        int zeroRow = 0, zeroCol = 0;

        // Find the empty space (denoted by 0)
        outer:
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] == 0) {
                    zeroRow = i;
                    zeroCol = j;
                    break outer;
                }
            }
        }

        int[] indices = new int[4];
        int k = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != zeroRow && j != zeroCol) {
                    indices[k++] = i;
                    indices[k++] = j;
                    if (k == indices.length) break;
                }
            }
            if (k == indices.length) break;
        }

        int t = newTiles[indices[0]][indices[1]];
        newTiles[indices[0]][indices[1]] = newTiles[indices[2]][indices[3]];
        newTiles[indices[2]][indices[3]] = t;
        return new Board(newTiles);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        int[][] tiles = {
                { 8, 1, 3 },
                { 4, 0, 2 },
                { 7, 6, 5 }
        };
        Board board = new Board(tiles);
        System.out.println("Board:\n" + board);
        System.out.println("Dimension: " + board.dimension());
        System.out.println("Hamming: " + board.hamming());
        System.out.println("Manhattan: " + board.manhattan());
        System.out.println("Is goal: " + board.isGoal());
        System.out.println("Neighbors:");
        for (Board neighbor : board.neighbors()) {
            System.out.println(neighbor);
        }
    }

}