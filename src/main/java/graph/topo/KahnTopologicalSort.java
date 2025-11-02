package graph.topo;

import graph.model.Graph;
import graph.model.Edge;
import graph.metrics.Metrics;

import java.util.*;

public class KahnTopologicalSort {
    private Metrics metrics;

    public KahnTopologicalSort() {
        this.metrics = new Metrics("KahnTopologicalSort");
    }

    public List<Integer> topologicalSort(Graph graph) {
        metrics.startTimer();

        int n = graph.getN();
        int[] inDegree = new int[n];

        // Calculate in-degree for each vertex
        for (int i = 0; i < n; i++) {
            for (Edge edge : graph.getNeighbors(i)) {
                inDegree[edge.getV()]++;
                metrics.incrementCounter("edges_processed");
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
                metrics.incrementCounter("queue_operations");
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int node = queue.poll();
            metrics.incrementCounter("queue_operations");
            result.add(node);

            for (Edge edge : graph.getNeighbors(node)) {
                int neighbor = edge.getV();
                inDegree[neighbor]--;
                if (inDegree[neighbor] == 0) {
                    queue.offer(neighbor);
                    metrics.incrementCounter("queue_operations");
                }
            }
        }

        // Check for cycles
        if (result.size() != n) {
            throw new IllegalArgumentException("Graph has cycles, cannot perform topological sort");
        }

        metrics.stopTimer();
        return result;
    }

    public Metrics getMetrics() {
        return metrics;
    }
}