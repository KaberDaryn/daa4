package graph.dagsp;

import graph.model.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DAGShortestPathTest {

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

    @Test
    public void testUnreachableVertices() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(3, 4, 1); // Disconnected component

        DAGShortestPath shortestPath = new DAGShortestPath();
        double[] distances = shortestPath.findShortestPaths(graph, 0);

        assertEquals(0.0, distances[0], 1e-9);
        assertEquals(1.0, distances[1], 1e-9);
        assertEquals(2.0, distances[2], 1e-9);
        assertEquals(Double.POSITIVE_INFINITY, distances[3]);
        assertEquals(Double.POSITIVE_INFINITY, distances[4]);
    }

    @Test
    public void testSingleVertex() {
        Graph graph = new Graph(1, true);

        DAGShortestPath shortestPath = new DAGShortestPath();
        double[] distances = shortestPath.findShortestPaths(graph, 0);

        assertEquals(0.0, distances[0], 1e-9);
    }
}