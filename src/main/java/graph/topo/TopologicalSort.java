package graph.topo;

import graph.model.Graph;
import graph.metrics.Metrics;

import java.util.List;

public class TopologicalSort {
    private final KahnTopologicalSort kahn;
    private final DFSTopologicalSort dfs;

    public TopologicalSort() {
        this.kahn = new KahnTopologicalSort();
        this.dfs = new DFSTopologicalSort();
    }

    public List<Integer> topologicalSort(Graph graph) {
        // Use Kahn by default as it can detect cycles
        return kahn.topologicalSort(graph);
    }

    public List<Integer> topologicalSortDFS(Graph graph) {
        return dfs.topologicalSort(graph);
    }

    public List<Integer> topologicalSortKahn(Graph graph) {
        return kahn.topologicalSort(graph);
    }

    public Metrics getKahnMetrics() {
        return kahn.getMetrics();
    }

    public Metrics getDFSMetrics() {
        return dfs.getMetrics();
    }
}