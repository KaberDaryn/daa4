package graph.scc;

import graph.model.Graph;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class KosarajuSCCTest {

    @Test
    public void testSimpleCycleKosaraju() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        KosarajuSCC kosaraju = new KosarajuSCC();
        List<List<Integer>> sccs = kosaraju.findSCCs(graph);

        assertEquals(1, sccs.size());
        assertEquals(3, sccs.get(0).size());
        assertTrue(sccs.get(0).contains(0));
        assertTrue(sccs.get(0).contains(1));
        assertTrue(sccs.get(0).contains(2));
    }

    @Test
    public void testDisconnectedGraphKosaraju() {
        Graph graph = new Graph(6, true);
        // First component: 0-1-2 cycle
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        // Second component: 3-4-5 cycle
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 5, 1);
        graph.addEdge(5, 3, 1);

        KosarajuSCC kosaraju = new KosarajuSCC();
        List<List<Integer>> sccs = kosaraju.findSCCs(graph);

        assertEquals(2, sccs.size());

        // Check that each SCC has 3 vertices
        for (List<Integer> scc : sccs) {
            assertEquals(3, scc.size());
        }
    }

    @Test
    public void testSingleVertexSCCsKosaraju() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        // Linear chain - each vertex is its own SCC

        KosarajuSCC kosaraju = new KosarajuSCC();
        List<List<Integer>> sccs = kosaraju.findSCCs(graph);

        assertEquals(4, sccs.size());
        for (List<Integer> scc : sccs) {
            assertEquals(1, scc.size());
        }
    }
}