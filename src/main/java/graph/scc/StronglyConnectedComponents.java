package graph.scc;

import graph.model.Graph;
import java.util.List;

public interface StronglyConnectedComponents {
    List<List<Integer>> findSCCs(Graph graph);
    Graph buildCondensationGraph(Graph originalGraph, List<List<Integer>> sccs);
}