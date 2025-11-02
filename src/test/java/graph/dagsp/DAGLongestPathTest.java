package graph.dagsp;

import graph.model.Graph;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DAGLongestPathTest {

    @Test
    public void testSimpleLongestPath() {
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

        // Updated expectation: The actual longest path is 0->1->2->3->5 = 5+2+7+1=15
        // Or 0->1->3->5 = 5+6+1=12, but 15 is longer
        assertEquals(15.0, result.getLength(), 1e-9);
        assertEquals(0, result.getStartVertex());
        assertEquals(5, result.getEndVertex());

        List<Integer> path = longestPath.reconstructPath(graph, result.getStartVertex(), result.getEndVertex());
        // The path should be 0->1->2->3->5
        assertEquals(List.of(0, 1, 2, 3, 5), path);
    }

    @Test
    public void testCriticalPath() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 3, 4);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 5);

        DAGLongestPath longestPath = new DAGLongestPath();
        DAGLongestPath.CriticalPathResult result = longestPath.findCriticalPath(graph);

        // Critical path should be 0->1->3->4 with total weight 3+4+5=12
        assertEquals(12.0, result.getLength(), 1e-9);
        assertEquals(0, result.getStartVertex());
        assertEquals(4, result.getEndVertex());

        List<Integer> path = longestPath.reconstructPath(graph, result.getStartVertex(), result.getEndVertex());
        assertEquals(List.of(0, 1, 3, 4), path);
    }

    @Test
    public void testNegativeWeightsLongestPath() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, -1);
        graph.addEdge(0, 2, 4);
        graph.addEdge(1, 2, 3);
        graph.addEdge(1, 3, 2);
        graph.addEdge(2, 3, -5);

        DAGLongestPath longestPath = new DAGLongestPath();
        DAGLongestPath.CriticalPathResult result = longestPath.findCriticalPath(graph);

        // Longest path from 0: 0->2 with weight 4
        assertEquals(4.0, result.getLength(), 1e-9);
        assertEquals(0, result.getStartVertex());
        assertEquals(2, result.getEndVertex());
    }

    @Test
    public void testDisconnectedGraph() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 2);
        graph.addEdge(2, 3, 3);
        // Two disconnected components

        DAGLongestPath longestPath = new DAGLongestPath();
        DAGLongestPath.CriticalPathResult result = longestPath.findCriticalPath(graph);

        // Should find path in one of the components
        assertTrue(result.getLength() >= 0);
    }

    @Test
    public void testSimpleDAGLongestPath() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 3, 3);
        graph.addEdge(2, 3, 1);

        DAGLongestPath longestPath = new DAGLongestPath();
        DAGLongestPath.CriticalPathResult result = longestPath.findCriticalPath(graph);

        // Longest path: 0->2->3 = 2+1=3 OR 0->1->3 = 1+3=4
        // 0->1->3 is longer
        assertEquals(4.0, result.getLength(), 1e-9);
        assertEquals(0, result.getStartVertex());
        assertEquals(3, result.getEndVertex());
    }
}