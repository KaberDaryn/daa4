package graph.scc;

import graph.model.Graph;
import graph.model.Edge;
import graph.metrics.Metrics;

import java.util.*;

public class KosarajuSCC {
    private Metrics metrics;

    public KosarajuSCC() {
        this.metrics = new Metrics("KosarajuSCC");
    }

    public List<List<Integer>> findSCCs(Graph graph) {
        metrics.startTimer();

        int n = graph.getN();
        boolean[] visited = new boolean[n];
        Stack<Integer> stack = new Stack<>();
        List<List<Integer>> sccs = new ArrayList<>();

        // First DFS: fill stack with finishing times
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfsFirstPass(graph, i, visited, stack);
            }
        }

        // Get transpose graph
        Graph transpose = graph.getTranspose();
        Arrays.fill(visited, false);

        // Second DFS: process in reverse order
        while (!stack.isEmpty()) {
            int node = stack.pop();
            if (!visited[node]) {
                List<Integer> scc = new ArrayList<>();
                dfsSecondPass(transpose, node, visited, scc);
                sccs.add(scc);
                metrics.incrementCounter("sccs_found");
            }
        }

        metrics.stopTimer();
        return sccs;
    }

    private void dfsFirstPass(Graph graph, int node, boolean[] visited, Stack<Integer> stack) {
        metrics.incrementCounter("dfs_calls");
        visited[node] = true;

        for (Edge edge : graph.getNeighbors(node)) {
            metrics.incrementCounter("edges_visited");
            int neighbor = edge.getV();
            if (!visited[neighbor]) {
                dfsFirstPass(graph, neighbor, visited, stack);
            }
        }

        stack.push(node);
    }

    private void dfsSecondPass(Graph graph, int node, boolean[] visited, List<Integer> scc) {
        metrics.incrementCounter("dfs_calls");
        visited[node] = true;
        scc.add(node);

        for (Edge edge : graph.getNeighbors(node)) {
            metrics.incrementCounter("edges_visited");
            int neighbor = edge.getV();
            if (!visited[neighbor]) {
                dfsSecondPass(graph, neighbor, visited, scc);
            }
        }
    }

    public Metrics getMetrics() {
        return metrics;
    }
}