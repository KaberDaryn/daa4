package graph.dagsp;

import graph.model.Graph;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DAGAlgorithmsTest {

    @Test
    public void testShortestPathInGeneratedDAG() {
        Graph graph = new Graph(20, true);
        // Create a simple DAG for testing
        for (int i = 0; i < 19; i++) {
            graph.addEdge(i, i + 1, 1);
        }

        DAGShortestPath shortestPath = new DAGShortestPath();
        double[] distances = shortestPath.findShortestPaths(graph, 0);

        assertNotNull(distances);
        assertEquals(20, distances.length);
        assertEquals(0.0, distances[0], 1e-9);

        // Check that distances increase correctly
        for (int i = 1; i < distances.length; i++) {
            assertEquals(i, distances[i], 1e-9);
        }
    }

    @Test
    public void testCriticalPathInGeneratedDAG() {
        Graph graph = new Graph(15, true);
        // Create a simple DAG structure
        for (int i = 0; i < 14; i++) {
            graph.addEdge(i, i + 1, 2);
        }
        // Add some alternative paths
        graph.addEdge(0, 5, 10);
        graph.addEdge(5, 10, 10);

        DAGLongestPath longestPath = new DAGLongestPath();
        DAGLongestPath.CriticalPathResult result = longestPath.findCriticalPath(graph);

        assertNotNull(result);
        // Critical path should be through the alternative paths: 0->5->10->11->12->13->14
        // Weight: 10 + 10 + 2 + 2 + 2 + 2 = 28
        assertEquals(28.0, result.getLength(), 1e-9);
        assertEquals(0, result.getStartVertex());
        assertEquals(14, result.getEndVertex());

        List<Integer> path = longestPath.reconstructPath(graph,
                result.getStartVertex(), result.getEndVertex());
        assertNotNull(path);
        assertFalse(path.isEmpty());
    }

    @Test
    public void testLongestPathInDAG() {
        Graph graph = new Graph(6, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 6);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 4, 4);
        graph.addEdge(2, 5, 2);
        graph.addEdge(2, 3, 7);
        graph.addEdge(3, 5, 1);
        graph.addEdge(3, 4, -1);
        graph.addEdge(4, 5, -2);

        DAGLongestPath longestPath = new DAGLongestPath();
        DAGLongestPath.CriticalPathResult result = longestPath.findCriticalPath(graph);

        // CORRECTED: The actual longest path is 0->1->2->3->5 = 5+2+7+1=15
        assertEquals(15.0, result.getLength(), 1e-9);
        assertEquals(0, result.getStartVertex());
        assertEquals(5, result.getEndVertex());

        List<Integer> path = longestPath.reconstructPath(graph, result.getStartVertex(), result.getEndVertex());
        // The path should be 0->1->2->3->5
        assertEquals(List.of(0, 1, 2, 3, 5), path);
    }

    @Test
    public void testSimpleDAG() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 4);
        graph.addEdge(1, 2, 2);
        graph.addEdge(1, 3, 6);
        graph.addEdge(2, 3, 3);

        DAGShortestPath shortestPath = new DAGShortestPath();
        double[] distances = shortestPath.findShortestPaths(graph, 0);

        assertEquals(0.0, distances[0], 1e-9);
        assertEquals(1.0, distances[1], 1e-9);
        assertEquals(3.0, distances[2], 1e-9);
        assertEquals(6.0, distances[3], 1e-9);
    }
}