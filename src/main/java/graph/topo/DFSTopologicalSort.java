package graph.topo;

import graph.model.Graph;
import graph.model.Edge;
import graph.metrics.Metrics;

import java.util.*;

public class DFSTopologicalSort {
    private Metrics metrics;

    public DFSTopologicalSort() {
        this.metrics = new Metrics("DFSTopologicalSort");
    }

    public List<Integer> topologicalSort(Graph graph) {
        metrics.startTimer();

        int n = graph.getN();
        boolean[] visited = new boolean[n];
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs(graph, i, visited, stack);
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }

        metrics.stopTimer();
        return result;
    }

    private void dfs(Graph graph, int node, boolean[] visited, Stack<Integer> stack) {
        metrics.incrementCounter("dfs_calls");
        visited[node] = true;

        for (Edge edge : graph.getNeighbors(node)) {
            metrics.incrementCounter("edges_visited");
            int neighbor = edge.getV();
            if (!visited[neighbor]) {
                dfs(graph, neighbor, visited, stack);
            }
        }

        stack.push(node);
        metrics.incrementCounter("stack_operations");
    }

    public Metrics getMetrics() {
        return metrics;
    }
}