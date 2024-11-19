/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WordNet {
    private final static int LIMIT = 2;
    // word2num 将单词映射到所在的集合
    private Map<String, Integer> word2num;
    // 每个集合所对应的单词用一个链表存储
    private List<String>[] syns;
    // 存储语义图
    private Digraph digraph;
    // 图的根节点
    private int root;
    // The distance from A(defined in distance(String nounA, String nounB)) to root
    private int[] disToRoot;
    // ancestor of two node
    private int ancestor;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("The synsets and hypernyms cannot be null");
        }
        int Synlength = RowNumber(synsets);
        word2num = new HashMap<String, Integer>();
        syns = new ArrayList[Synlength];
        for (int i = 0; i < Synlength; i++) {
            syns[i] = new ArrayList();
        }

        // 建图
        GraphGenerator(hypernyms);
        StdOut.print(digraph.toString());

        // 提取单词
        WordExtract(synsets);
        for (int i = 0; i < syns.length; i++) {
            StdOut.print(i + ": ");
            for (String word : syns[i]) {
                StdOut.print(word + " ");
            }
            StdOut.println();
        }
    }

    private int RowNumber(String sets) {
        In in = new In(sets);
        int lineCount = 0;
        while (in.hasNextLine()) {
            in.readLine();
            lineCount++;
        }
        return lineCount;
    }

    private void WordExtract(String sets) {
        int Synlength = RowNumber(sets);
        In in = new In(sets);
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] fields = line.split(",");

            int index = Integer.parseInt(fields[0]);

            String[] synwords = fields[1].split(" ");
            for (String word : synwords) {
                word2num.put(word, index);
                syns[index].add(word);
            }
        }
        in.close();
    }

    private void GraphGenerator(String sets) {
        int length = RowNumber(sets);
        digraph = new Digraph(syns.length);
        In in = new In(sets);
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] fields = line.split(",");
            int from = Integer.parseInt(fields[0]);

            for (int i = 1, len = fields.length; i < len; i++) {
                int to = Integer.parseInt(fields[i]);
                digraph.addEdge(from, to);
            }
        }

        // 判断是否是一个DAG
        DirectedCycle dc = new DirectedCycle(digraph);
        if (dc.hasCycle()) {
            throw new IllegalArgumentException("Cycle found");
        }

        // 判断是否只有一个根
        int count = 0;
        for (int i = 0, len = syns.length; i < len; i++) {
            if (digraph.outdegree(i) == 0) {
                count++;
                root = i;
                if (count > 1) {
                    throw new IllegalArgumentException("More than one root");
                }
            }
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        List<String> allNouns = new ArrayList<>();
        for (List<String> syn : syns) {
            allNouns.addAll(syn);
        }
        return allNouns;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("The word cannot be null");
        }

        return word2num.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("The noun and noun b cannot be null");
        }
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("The nounA or nounB is not a valid noun");
        }

        int Anum = word2num.get(nounA);
        int BNum = word2num.get(nounB);

        PathA2Root(Anum);

        return dist(BNum);
    }

    // record the path from A to root(bfs)
    private void PathA2Root(int node) {
        disToRoot = new int[digraph.V()];
        for (int i = 0; i < digraph.V(); i++) {
            disToRoot[i] = -1;
        }
        Queue<Integer> q = new Queue<>();
        disToRoot[node] = 0;
        q.enqueue(node);

        while (!q.isEmpty()) {
            int cur = q.dequeue();
            for (int v : digraph.adj(cur)) {
                if (disToRoot[v] == -1) {
                    disToRoot[v] = disToRoot[cur] + 1;
                    q.enqueue(v);
                }
            }
        }
    }

    // record the path from B to root(bfs).
    // The first node found that both A and B pass is the ancestor.
    private int dist(int node) {
        if (disToRoot[node] != -1) {
            return disToRoot[node];
        }
        int[] disTo = new int[digraph.V()];
        for (int i = 0; i < digraph.V(); i++) {
            disTo[i] = -1;
        }
        Queue<Integer> q = new Queue<>();
        disTo[node] = 0;
        q.enqueue(node);

        while (!q.isEmpty()) {
            int cur = q.dequeue();
            if (disToRoot[cur] != -1) {
                ancestor = cur;
                return disToRoot[cur] + disTo[cur];
            }
            for (int v : digraph.adj(cur)) {
                if (disTo[v] == -1) {
                    disTo[v] = disTo[cur] + 1;
                    q.enqueue(v);
                }
            }
        }

        return disTo[root] + disToRoot[root];
    }

    // // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("The noun and noun b cannot be null");
        }
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("The nounA or nounB is not a valid noun");
        }
        boolean[] markedA = new boolean[digraph.V()];
        int Anum = word2num.get(nounA);

        // Mark all A's ancestors
        Queue<Integer> q = new Queue<>();
        q.enqueue(Anum);
        while (!q.isEmpty()) {
            int cur = q.dequeue();
            for (int v : digraph.adj(cur)) {
                if (!markedA[v]) {
                    markedA[v] = true;
                    q.enqueue(v);
                }
            }
            // for (int i = 0; i < digraph.V(); i++) {
            //     if (markedA[i]) {
            //         StdOut.print("1 ");
            //     }
            //     else {
            //         StdOut.print("0 ");
            //     }
            // }
            // StdOut.println();
        }

        int ancestor = commonAncestor(nounB, markedA);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < syns[ancestor].size() - 1; i++) {
            result.append(syns[ancestor].get(i)).append(" ");
        }
        result.append(syns[ancestor].get(syns[ancestor].size() - 1));
        return result.toString();
    }

    private int commonAncestor(String nounB, boolean[] markedA) {
        int BNum = word2num.get(nounB);
        boolean[] markedB = new boolean[digraph.V()];

        Queue<Integer> q = new Queue<>();
        q.enqueue(BNum);
        while (!q.isEmpty()) {
            int cur = q.dequeue();
            for (int v : digraph.adj(cur)) {
                if (!markedB[v]) {
                    markedB[v] = true;
                    if (markedA[v]) {
                        return v;
                    }
                    q.enqueue(v);
                }
            }
        }

        return root;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordNet = new WordNet(args[0], args[1]);
        StdOut.println(wordNet.distance("h", "e"));
        StdOut.println(wordNet.sap("h", "e"));
        StdOut.println(wordNet.distance("g", "e"));
        StdOut.println(wordNet.sap("g", "e"));
        StdOut.println(wordNet.distance("h", "f"));
        StdOut.println(wordNet.sap("h", "f"));
        StdOut.println(wordNet.distance("f", "h"));
        StdOut.println(wordNet.sap("f", "h"));
        StdOut.println(wordNet.distance("h", "c"));
        StdOut.println(wordNet.sap("h", "c"));

    }
}
