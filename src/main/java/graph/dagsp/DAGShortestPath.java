package graph.dagsp;

import graph.model.Graph;
import graph.model.Edge;
import graph.topo.TopologicalSort;
import graph.metrics.Metrics;

import java.util.*;

public class DAGShortestPath {
    private Metrics metrics;

    public DAGShortestPath() {
        this.metrics = new Metrics("DAGShortestPath");
    }

    public double[] findShortestPaths(Graph graph, int source) {
        metrics.startTimer();

        int n = graph.getN();
        double[] dist = new double[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        dist[source] = 0;

        // Get topological order
        TopologicalSort topoSort = new TopologicalSort();
        List<Integer> topoOrder = topoSort.topologicalSort(graph);

        // Process vertices in topological order
        for (int node : topoOrder) {
            metrics.incrementCounter("vertices_processed");
            if (dist[node] != Double.POSITIVE_INFINITY) {
                for (Edge edge : graph.getNeighbors(node)) {
                    metrics.incrementCounter("edges_relaxed");
                    double newDist = dist[node] + edge.getWeight();
                    if (newDist < dist[edge.getV()]) {
                        dist[edge.getV()] = newDist;
                        metrics.incrementCounter("distance_updates");
                    }
                }
            }
        }

        metrics.stopTimer();
        return dist;
    }

    public List<Integer> reconstructPath(Graph graph, int source, int target, double[] dist) {
        if (dist[target] == Double.POSITIVE_INFINITY) {
            return Collections.emptyList();
        }

        List<Integer> path = new ArrayList<>();
        path.add(target);

        int current = target;
        while (current != source) {
            boolean found = false;
            for (int i = 0; i < graph.getN(); i++) {
                for (Edge edge : graph.getNeighbors(i)) {
                    if (edge.getV() == current &&
                            Math.abs(dist[i] + edge.getWeight() - dist[current]) < 1e-9) {
                        path.add(0, i);
                        current = i;
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
            if (!found) break;
        }

        Collections.reverse(path);
        return path;
    }

    public Metrics getMetrics() {
        return metrics;
    }
}