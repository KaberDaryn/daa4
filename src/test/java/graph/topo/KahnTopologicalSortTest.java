package graph.topo;

import graph.model.Graph;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class KahnTopologicalSortTest {

    @Test
    public void testSimpleDAG() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);

        KahnTopologicalSort kahn = new KahnTopologicalSort();
        List<Integer> order = kahn.topologicalSort(graph);

        assertEquals(4, order.size());
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(0) < order.indexOf(2));
        assertTrue(order.indexOf(1) < order.indexOf(3));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }

    @Test
    public void testLinearOrder() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);

        KahnTopologicalSort kahn = new KahnTopologicalSort();
        List<Integer> order = kahn.topologicalSort(graph);

        assertEquals(5, order.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(i, order.get(i).intValue());
        }
    }

    @Test
    public void testCycleDetection() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1); // Cycle

        KahnTopologicalSort kahn = new KahnTopologicalSort();

        assertThrows(IllegalArgumentException.class, () -> {
            kahn.topologicalSort(graph);
        });
    }
}