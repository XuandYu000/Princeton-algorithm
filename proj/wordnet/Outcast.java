/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int[] distances = new int[nouns.length];
        for (int i = 0; i < nouns.length; i++) {
            for (int j = 0; j < nouns.length; j++) {
                if (j == i) continue;
                distances[i] += wordnet.distance(nouns[i], nouns[j]);
            }
            // StdOut.println(nouns[i] + ": " + distances[i]);
        }
        int idx = 0;
        int maximum = distances[0];
        for (int i = 1; i < nouns.length; i++) {
            if (distances[i] > maximum) {
                idx = i;
                maximum = distances[i];
            }
        }
        return nouns[idx];
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
