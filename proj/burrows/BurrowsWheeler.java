/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.Queue;

import java.util.Arrays;

public class BurrowsWheeler {
    private static final int R = 256;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        StringBuilder t = new StringBuilder();
        int first = -1;

        String s = BinaryStdIn.readString();
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray(s);
        for (int i = 0, len = circularSuffixArray.length(); i < len; i++) {
            if (circularSuffixArray.index(i) == 0) {
                first = i;
            }
            t.append(s.charAt((circularSuffixArray.index(i) - 1 + len) % len));
        }

        BinaryStdOut.write(first);
        BinaryStdOut.write(t.toString());
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        char[] t = BinaryStdIn.readString().toCharArray();
        int[] next = new int[t.length];

        Queue<Integer>[] queue = new Queue[R];
        for (int i = 0; i < R; i++) {
            queue[i] = new Queue<>();
        }

        for (int i = 0; i < t.length; i++) {
            queue[t[i]].enqueue(i);
        }
        Arrays.sort(t);
        for (int i = 0; i < t.length; i++) {
            next[i] = queue[t[i]].dequeue();
        }

        int pointer = first;
        for (int i = 0; i < t.length; i++) {
            BinaryStdOut.write(t[pointer]);
            pointer = next[pointer];
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else throw new IllegalArgumentException("Invalid command");
    }
}
