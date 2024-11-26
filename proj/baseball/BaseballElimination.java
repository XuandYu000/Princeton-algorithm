/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

/*
 * Data format:
 * 4
 * Atlanta       83 71  8  0 1 6 1
 * Philadelphia  80 79  3  1 0 0 2
 * New_York      78 78  6  6 0 0 0
 * Montreal      77 82  3  1 2 0 0
 * */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BaseballElimination {
    private final int numberofTeams;
    private final Map<String, Integer> team2num;
    private final Map<Integer, String> num2team;
    private final int[] wins;
    private final int[] losses;
    private final int[] remains;
    private final int[][] g;
    private final boolean[] solved;
    private final boolean[] isOut;
    private final ArrayList<Set<String>> certificates;

    // create a baseball division from given filenames in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        numberofTeams = in.readInt();

        team2num = new HashMap<>();
        num2team = new HashMap<>();
        wins = new int[numberofTeams];
        losses = new int[numberofTeams];
        remains = new int[numberofTeams];
        g = new int[numberofTeams][numberofTeams];
        solved = new boolean[numberofTeams];
        isOut = new boolean[numberofTeams];
        certificates = new ArrayList<Set<String>>();

        for (int i = 0; i < numberofTeams; i++) {
            certificates.add(null);

            String team = in.readString();
            int win = in.readInt();
            int loss = in.readInt();
            int remain = in.readInt();

            team2num.put(team, i);
            num2team.put(i, team);
            wins[i] = win;
            losses[i] = loss;
            remains[i] = remain;

            for (int j = 0; j < numberofTeams; j++) {
                g[i][j] = in.readInt();
            }
        }
    }

    // number of teams
    public int numberOfTeams() {
        return numberofTeams;
    }

    // all teams
    public Iterable<String> teams() {
        return team2num.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        validTeam(team);
        return wins[team2num.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        validTeam(team);
        return losses[team2num.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        validTeam(team);
        return remains[team2num.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        validTeam(team1);
        validTeam(team2);
        return g[team2num.get(team1)][team2num.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        validTeam(team);

        if (!solved[team2num.get(team)]) slove(team);
        return isOut[team2num.get(team)];
    }

    public Iterable<String> certificateOfElimination(String team) {
        validTeam(team);
        if (!solved[team2num.get(team)]) slove(team);
        return certificates.get(team2num.get(team));
    }

    private void slove(String team) {
        int x = team2num.get(team);
        solved[x] = true;
        // trivial elimination
        for (int i = 0; i < numberofTeams; i++) {
            if (wins[x] + remains[x] < wins[i]) {
                isOut[x] = true;
                certificates.set(x, new HashSet<>());
                certificates.get(x).add(num2team.get(i));
                return;
            }
        }

        // Nontrivial elimination
        int involveTeams = numberofTeams - 1;
        int numberofGames = ((involveTeams - 1) * involveTeams) >> 1;
        int bias = numberofGames + 1;
        int s = 0;
        int t = numberofGames + involveTeams + 2;

        FlowNetwork flowNetwork = flowNetworkGenerate(x);
        FordFulkerson FF = new FordFulkerson(flowNetwork, s, t);
        // StdOut.println(flowNetwork.toString());
        for (int i = 1; i < bias; i++) {
            if (FF.inCut(i)) {
                isOut[x] = true;
                certificates.set(x, new HashSet<>());
                for (int j = 0; j < numberofTeams; j++) {
                    if (FF.inCut(j + bias)) {
                        certificates.get(x).add(num2team.get(j));
                    }
                }
                return;
            }
        }
        isOut[x] = false;
    }

    private FlowNetwork flowNetworkGenerate(int x) {
        int involveTeams = numberofTeams - 1;
        int numberofGames = ((involveTeams - 1) * involveTeams) >> 1;
        int bias = numberofGames + 1;
        int s = 0;
        int t = numberofGames + involveTeams + 2;

        int index = 1;
        FlowNetwork flowNetwork = new FlowNetwork(t + 1);
        for (int i = 0; i < numberofTeams; i++) {
            if (i == x) continue;
            for (int j = i + 1; j < numberofTeams; j++) {
                if (j == x) continue;

                // edge from s to game vertices i->j
                flowNetwork.addEdge(new FlowEdge(s, index, g[i][j]));

                // edges from game vertices i->j to i and to j;
                flowNetwork.addEdge(
                        new FlowEdge(index, i + bias, Double.POSITIVE_INFINITY));
                flowNetwork.addEdge(
                        new FlowEdge(index, j + bias, Double.POSITIVE_INFINITY));
                index++;
            }
        }

        // edge from team vertices to t
        for (int i = 0; i < numberofTeams; i++) {
            if (i == x) continue;
            flowNetwork.addEdge(new FlowEdge(i + bias, t, wins[x] + remains[x] - wins[i]));
        }

        // StdOut.println(flowNetwork.toString());
        return flowNetwork;
    }

    private void validTeam(String team) {
        if (!team2num.containsKey(team)) {
            throw new IllegalArgumentException("Team " + team + " does not exist");
        }
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }

        // Test construct function
        // StdOut.println(division.numberofTeams());
        // for (String team : division.teams()) {
        //     StdOut.print(
        //             division.team2num.get(team) + ": " + team + ":\nwins: " + division.wins(team)
        //                     + " losses: " + division.losses(team)
        //                     + " remaining: " + division.remaining(team));
        //     for (String team2 : division.teams()) {
        //         StdOut.print(" " + team2 + "->" + division.against(team, team2));
        //     }
        //     StdOut.println();
        // }
    }
}
