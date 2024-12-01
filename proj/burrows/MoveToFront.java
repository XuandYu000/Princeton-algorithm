/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;


public class MoveToFront {
    private static final int R = 256;

    public static void encode() {
        char[] moveToFront = new char[R];
        for (int i = 0; i < R; i++) {
            moveToFront[i] = (char) i;
        }

        while (!BinaryStdIn.isEmpty()) {
            char ch = BinaryStdIn.readChar();
            for (int j = 0; j < R; j++) {
                if (ch == moveToFront[j]) {
                    BinaryStdOut.write(j, 8);

                    // move to front
                    for (int k = j; k > 0; k--) {
                        moveToFront[k] = moveToFront[k - 1];
                    }
                    moveToFront[0] = ch;
                    break;
                }
            }
        }

        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] moveToFront = new char[R];
        for (int i = 0; i < R; i++) {
            moveToFront[i] = (char) i;
        }

        while (!BinaryStdIn.isEmpty()) {
            char ch = BinaryStdIn.readChar();
            BinaryStdOut.write(moveToFront[ch]);
            char t = moveToFront[ch];
            for (int i = ch; i > 0; i--) {
                moveToFront[i] = moveToFront[i - 1];
            }
            moveToFront[0] = t;
        }

        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Invalid command");
    }
}
