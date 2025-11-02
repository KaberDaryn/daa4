package graph.dagsp;

import graph.model.Graph;
import graph.model.Edge;
import graph.topo.TopologicalSort;
import graph.metrics.Metrics;

import java.util.*;

public class DAGLongestPath {
    private Metrics metrics;

    public DAGLongestPath() {
        this.metrics = new Metrics("DAGLongestPath");
    }

    public CriticalPathResult findCriticalPath(Graph graph) {
        metrics.startTimer();

        int n = graph.getN();
        double maxLength = Double.NEGATIVE_INFINITY;
        int startVertex = -1;
        int endVertex = -1;

        // Get topological order
        TopologicalSort topoSort = new TopologicalSort();
        List<Integer> topoOrder;
        try {
            topoOrder = topoSort.topologicalSort(graph);
        } catch (IllegalArgumentException e) {
            // Graph has cycles, cannot find critical path
            metrics.stopTimer();
            return new CriticalPathResult(Double.NEGATIVE_INFINITY, -1, -1);
        }

        // Try each vertex as potential start of critical path
        for (int source = 0; source < n; source++) {
            double[] dist = new double[n];
            Arrays.fill(dist, Double.NEGATIVE_INFINITY);
            dist[source] = 0;
            int[] prev = new int[n];
            Arrays.fill(prev, -1);

            // Process vertices in topological order
            for (int node : topoOrder) {
                if (dist[node] != Double.NEGATIVE_INFINITY) {
                    for (Edge edge : graph.getNeighbors(node)) {
                        metrics.incrementCounter("edges_relaxed");
                        double newDist = dist[node] + edge.getWeight();
                        if (newDist > dist[edge.getV()]) {
                            dist[edge.getV()] = newDist;
                            prev[edge.getV()] = node;
                            metrics.incrementCounter("distance_updates");
                        }
                    }
                }
                metrics.incrementCounter("vertices_processed");
            }

            // Find maximum distance from this source
            for (int i = 0; i < n; i++) {
                if (dist[i] > maxLength && dist[i] != Double.NEGATIVE_INFINITY) {
                    maxLength = dist[i];
                    endVertex = i;
                    startVertex = source;
                }
            }
        }

        metrics.stopTimer();
        return new CriticalPathResult(maxLength, startVertex, endVertex);
    }

    public List<Integer> reconstructPath(Graph graph, int startVertex, int endVertex) {
        if (startVertex == -1 || endVertex == -1) {
            return Collections.emptyList();
        }

        TopologicalSort topoSort = new TopologicalSort();
        List<Integer> topoOrder;
        try {
            topoOrder = topoSort.topologicalSort(graph);
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }

        double[] dist = new double[graph.getN()];
        Arrays.fill(dist, Double.NEGATIVE_INFINITY);
        dist[startVertex] = 0;
        int[] prev = new int[graph.getN()];
        Arrays.fill(prev, -1);

        // Compute longest paths
        for (int node : topoOrder) {
            if (dist[node] != Double.NEGATIVE_INFINITY) {
                for (Edge edge : graph.getNeighbors(node)) {
                    double newDist = dist[node] + edge.getWeight();
                    if (newDist > dist[edge.getV()]) {
                        dist[edge.getV()] = newDist;
                        prev[edge.getV()] = node;
                    }
                }
            }
        }

        // Reconstruct path
        List<Integer> path = new ArrayList<>();
        for (int at = endVertex; at != -1; at = prev[at]) {
            path.add(at);
        }
        Collections.reverse(path);

        // Verify the path is correct
        if (path.isEmpty() || path.get(0) != startVertex) {
            return Collections.emptyList();
        }

        return path;
    }

    public static class CriticalPathResult {
        private final double length;
        private final int startVertex;
        private final int endVertex;

        public CriticalPathResult(double length, int startVertex, int endVertex) {
            this.length = length;
            this.startVertex = startVertex;
            this.endVertex = endVertex;
        }

        public double getLength() { return length; }
        public int getStartVertex() { return startVertex; }
        public int getEndVertex() { return endVertex; }
    }

    public Metrics getMetrics() {
        return metrics;
    }
}