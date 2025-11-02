package graph.topo;

import graph.model.Graph;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DFSTopologicalSortTest {

    @Test
    public void testSimpleDAGDFS() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);

        DFSTopologicalSort dfsTopo = new DFSTopologicalSort();
        List<Integer> order = dfsTopo.topologicalSort(graph);

        assertEquals(4, order.size());
        // In DFS topological sort, 0 should come before its descendants
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(0) < order.indexOf(2));
        assertTrue(order.indexOf(1) < order.indexOf(3));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }

    @Test
    public void testMultiplePathsDFS() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);

        DFSTopologicalSort dfsTopo = new DFSTopologicalSort();
        List<Integer> order = dfsTopo.topologicalSort(graph);

        assertEquals(5, order.size());
        assertTrue(order.indexOf(0) < order.indexOf(3));
        assertTrue(order.indexOf(1) < order.indexOf(3));
        assertTrue(order.indexOf(2) < order.indexOf(3));
        assertTrue(order.indexOf(3) < order.indexOf(4));
    }
}