package graph.scc;

import graph.model.Graph;
import graph.model.Edge;
import graph.metrics.Metrics;

import java.util.*;

public class TarjanSCC {
    private int index;
    private int[] ids;
    private int[] low;
    private boolean[] onStack;
    private Deque<Integer> stack;
    private List<List<Integer>> sccs;
    private Graph graph;
    private Metrics metrics;

    public TarjanSCC() {
        this.metrics = new Metrics("TarjanSCC");
    }

    public List<List<Integer>> findSCCs(Graph graph) {
        metrics.startTimer();
        this.graph = graph;
        int n = graph.getN();
        this.index = 0;
        this.ids = new int[n];
        this.low = new int[n];
        this.onStack = new boolean[n];
        this.stack = new ArrayDeque<>();
        this.sccs = new ArrayList<>();

        Arrays.fill(ids, -1);

        for (int i = 0; i < n; i++) {
            if (ids[i] == -1) {
                dfs(i);
            }
        }

        metrics.stopTimer();
        return sccs;
    }

    private void dfs(int at) {
        metrics.incrementCounter("dfs_calls");
        ids[at] = low[at] = index++;
        stack.push(at);
        onStack[at] = true;

        for (Edge edge : graph.getNeighbors(at)) {
            metrics.incrementCounter("edges_visited");
            int to = edge.getV();
            if (ids[to] == -1) {
                dfs(to);
                low[at] = Math.min(low[at], low[to]);
            } else if (onStack[to]) {
                low[at] = Math.min(low[at], ids[to]);
            }
        }

        if (ids[at] == low[at]) {
            List<Integer> scc = new ArrayList<>();
            while (!stack.isEmpty()) {
                int node = stack.pop();
                onStack[node] = false;
                scc.add(node);
                if (node == at) break;
            }
            sccs.add(scc);
            metrics.incrementCounter("sccs_found");
        }
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public Graph buildCondensationGraph(Graph originalGraph, List<List<Integer>> sccs) {
        int numComponents = sccs.size();
        Graph condensation = new Graph(numComponents, true);

        // Map each vertex to its component ID
        int[] componentId = new int[originalGraph.getN()];
        for (int i = 0; i < sccs.size(); i++) {
            for (int node : sccs.get(i)) {
                componentId[node] = i;
            }
        }

        // Add edges between components
        for (int u = 0; u < originalGraph.getN(); u++) {
            for (Edge edge : originalGraph.getNeighbors(u)) {
                int v = edge.getV();
                int compU = componentId[u];
                int compV = componentId[v];
                if (compU != compV) {
                    condensation.addEdge(compU, compV, edge.getWeight());
                }
            }
        }

        return condensation;
    }
}