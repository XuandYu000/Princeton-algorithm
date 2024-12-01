/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class CircularSuffixArray {
    private final int size;
    private final int[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("The given string cannot be null!");
        }
        size = s.length();
        index = new int[size];

        String duplicate = s + s;
        CircularSuffix[] array = new CircularSuffix[size];
        for (int i = 0; i < size; i++) {
            array[i] = new CircularSuffix(duplicate, i);
        }
        Arrays.sort(array);

        for (int i = 0; i < size; i++) {
            index[i] = array[i].index;
        }
    }

    private static class CircularSuffix implements Comparable<CircularSuffix> {
        private final String s;
        private final int index;

        public CircularSuffix(String dup, int index) {
            this.s = dup;
            this.index = index;
        }

        private int length() {
            return s.length() >> 1;
        }

        private char charAt(int i) {
            return s.charAt(index + i);
        }

        public int compareTo(CircularSuffix other) {
            if (this == other) return 0;
            int len = Math.min(length(), other.length());
            for (int i = 0; i < len; i++) {
                if (charAt(i) != other.charAt(i)) return charAt(i) - other.charAt(i);
            }
            return length() - other.length();
        }
    }

    // length of s
    public int length() {
        return size;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= size) {
            throw new IllegalArgumentException("index out of range");
        }
        return index[i];
    }

    // unit testing (required)
    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray CSA = new CircularSuffixArray("ABRACADABRA!");
        StdOut.println("length of s : " + CSA.length());
        for (int i = 0; i < CSA.length(); i++) {
            StdOut.println(CSA.index(i));
        }
    }
}
