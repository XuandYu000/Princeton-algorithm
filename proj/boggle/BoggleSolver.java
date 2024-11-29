/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;

public class BoggleSolver {
    private static final int R = 26;
    private static final int LENGTHLIMIT = 3;
    private static final int[] dx = { -1, -1, -1, 0, 0, 1, 1, 1 };
    private static final int[] dy = { -1, 0, 1, -1, 1, -1, 0, 1 };

    private Node root;

    private HashSet<String> words;
    private boolean[][] marked;

    private static class Node {
        private Node[] next = new Node[R];
        private boolean isString = false;
    }

    private void add(String key) {
        root = add(root, key, 0);
    }

    private Node add(Node x, String key, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            x.isString = true;
        }
        else {
            char c = key.charAt(d);
            x.next[c - 'A'] = add(x.next[c - 'A'], key, d + 1);
        }
        return x;
    }

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        for (String word : dictionary) {
            add(word);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        int rows = board.rows();
        int cols = board.cols();
        words = new HashSet<>();

        if (root == null) return words;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (root.next[board.getLetter(i, j) - 'A'] != null) {
                    marked = new boolean[rows][cols];
                    dfs(i, j, rows, cols, "", root.next[board.getLetter(i, j) - 'A'], board);
                }
            }
        }

        return words;
    }

    private void dfs(int row, int col, int rows, int cols, String word, Node node,
                     BoggleBoard board) {
        marked[row][col] = true;
        char c = board.getLetter(row, col);
        String curword = word + c;

        if (c == 'Q') {
            curword += 'U';
            node = node.next['U' - 'A'];
        }

        if (node == null) {
            marked[row][col] = false;
            return;
        }

        if (curword.length() >= LENGTHLIMIT && node.isString) words.add(curword);

        for (int i = 0; i < 8; i++) {
            int nextRow = row + dx[i];
            int nextCol = col + dy[i];
            if (nextRow < 0 || nextRow >= rows || nextCol < 0 || nextCol >= cols) continue;
            if (marked[nextRow][nextCol]) continue;

            char nextc = board.getLetter(nextRow, nextCol);
            if (node.next[nextc - 'A'] != null) {
                dfs(nextRow, nextCol, rows, cols, curword, node.next[nextc - 'A'], board);
            }
        }

        marked[row][col] = false;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!contains(word)) return 0;

        int length = word.length();
        if (length < 3) return 0;
        else if (length == 3 || length == 4) return 1;
        else if (length == 5) return 2;
        else if (length == 6) return 3;
        else if (length == 7) return 5;
        else return 11;
    }

    private boolean contains(String key) {
        Node x = get(root, key, 0);
        if (x == null) return false;
        return x.isString;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.next[c - 'A'], key, d + 1);
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word + " " + solver.scoreOf(word));
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
