package graph.integration;

import graph.model.Graph;
import graph.scc.TarjanSCC;
import graph.topo.KahnTopologicalSort;
import graph.dagsp.DAGShortestPath;
import graph.dagsp.DAGLongestPath;
import graph.util.GraphLoader;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ComprehensiveTest {

    @Test
    public void testCompletePipeline() throws Exception {
        // Load a test graph that we know is a DAG
        Graph graph = GraphLoader.loadFromJson("data/small_3.json"); // Use pure DAG dataset

        // Step 1: SCC decomposition - should find each vertex as its own component in DAG
        TarjanSCC tarjan = new TarjanSCC();
        List<List<Integer>> sccs = tarjan.findSCCs(graph);
        assertFalse(sccs.isEmpty());

        // Step 2: Build condensation graph
        Graph condensation = tarjan.buildCondensationGraph(graph, sccs);
        assertTrue(condensation.getN() > 0);

        // Step 3: Topological sort on condensation
        KahnTopologicalSort kahn = new KahnTopologicalSort();
        List<Integer> topoOrder = kahn.topologicalSort(condensation);
        assertEquals(condensation.getN(), topoOrder.size());

        // Step 4: Shortest paths on condensation
        DAGShortestPath shortestPath = new DAGShortestPath();
        double[] distances = shortestPath.findShortestPaths(condensation, 0);
        assertNotNull(distances);
        assertEquals(condensation.getN(), distances.length);

        // Step 5: Longest paths on condensation
        DAGLongestPath longestPath = new DAGLongestPath();
        DAGLongestPath.CriticalPathResult criticalPath = longestPath.findCriticalPath(condensation);
        assertNotNull(criticalPath);
    }

    @Test
    public void testGraphWithKnownStructure() {
        // Create a graph with known SCC structure
        Graph graph = new Graph(6, true);
        // SCC 1: 0-1-2
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        // SCC 2: 3-4
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 3, 1);
        // Vertex 5 is standalone
        graph.addEdge(0, 3, 1);
        graph.addEdge(3, 5, 1);

        TarjanSCC tarjan = new TarjanSCC();
        List<List<Integer>> sccs = tarjan.findSCCs(graph);

        // Should find 3 SCCs: [0,1,2], [3,4], [5]
        assertEquals(3, sccs.size());

        // Check sizes
        boolean foundSize3 = false, foundSize2 = false, foundSize1 = false;
        for (List<Integer> scc : sccs) {
            if (scc.size() == 3) foundSize3 = true;
            if (scc.size() == 2) foundSize2 = true;
            if (scc.size() == 1) foundSize1 = true;
        }
        assertTrue(foundSize3 && foundSize2 && foundSize1);
    }

    @Test
    public void testDAGPipeline() {
        // Test with a known DAG
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 4);
        graph.addEdge(3, 4, 5);

        // This should work without SCC since it's already a DAG
        KahnTopologicalSort kahn = new KahnTopologicalSort();
        List<Integer> topoOrder = kahn.topologicalSort(graph);
        assertEquals(5, topoOrder.size());

        DAGShortestPath shortestPath = new DAGShortestPath();
        double[] distances = shortestPath.findShortestPaths(graph, 0);
        assertNotNull(distances);
        assertEquals(5, distances.length);
    }
}