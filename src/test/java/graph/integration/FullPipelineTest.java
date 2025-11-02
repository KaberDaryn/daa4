package graph.integration;

import graph.model.Graph;
import graph.scc.TarjanSCC;
import graph.topo.KahnTopologicalSort;
import graph.dagsp.DAGShortestPath;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FullPipelineTest {

    @Test
    public void testFullPipeline() {
        // Create a complex graph with cycles
        Graph graph = new Graph(8, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1); // Cycle 1
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 5, 1);
        graph.addEdge(5, 3, 1); // Cycle 2
        graph.addEdge(5, 6, 1);
        graph.addEdge(6, 7, 1);

        // Step 1: Find SCCs
        TarjanSCC tarjan = new TarjanSCC();
        List<List<Integer>> sccs = tarjan.findSCCs(graph);
        assertTrue(sccs.size() >= 2);

        // Step 2: Build condensation graph
        Graph condensation = tarjan.buildCondensationGraph(graph, sccs);

        // Step 3: Topological sort on condensation
        KahnTopologicalSort kahn = new KahnTopologicalSort();
        List<Integer> topoOrder = kahn.topologicalSort(condensation);
        assertEquals(condensation.getN(), topoOrder.size());

        // Step 4: Shortest paths on condensation
        DAGShortestPath shortestPath = new DAGShortestPath();
        double[] distances = shortestPath.findShortestPaths(condensation, 0);

        assertNotNull(distances);
        assertEquals(condensation.getN(), distances.length);
    }
}