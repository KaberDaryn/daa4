package graph.integration;

import graph.model.Graph;
import graph.scc.TarjanSCC;
import graph.scc.KosarajuSCC;
import graph.topo.KahnTopologicalSort;
import graph.topo.DFSTopologicalSort;
import graph.dagsp.DAGShortestPath;
import graph.util.TestGraphFactory;
import org.junit.jupiter.api.Test;
import java.util.List;

public class PerformanceTest {

    @Test
    public void compareSCCAlgorithms() {
        // Generate test graph with multiple SCCs
        Graph graph = TestGraphFactory.createTestGraphWithSCCs(100, 10);

        TarjanSCC tarjan = new TarjanSCC();
        long startTime = System.nanoTime();
        List<List<Integer>> tarjanSCCs = tarjan.findSCCs(graph);
        long tarjanTime = System.nanoTime() - startTime;

        KosarajuSCC kosaraju = new KosarajuSCC();
        startTime = System.nanoTime();
        List<List<Integer>> kosarajuSCCs = kosaraju.findSCCs(graph);
        long kosarajuTime = System.nanoTime() - startTime;

        System.out.printf("Tarjan: %d ms, Kosaraju: %d ms%n",
                tarjanTime / 1_000_000, kosarajuTime / 1_000_000);
        System.out.printf("Tarjan SCCs: %d, Kosaraju SCCs: %d%n",
                tarjanSCCs.size(), kosarajuSCCs.size());

        // Ensure both algorithms find the same number of SCCs
        assert tarjanSCCs.size() == kosarajuSCCs.size();
    }

    @Test
    public void compareTopoAlgorithms() {
        // Generate a DAG for topological sort comparison
        Graph graph = TestGraphFactory.createTestDAG(1000, 0.3);

        KahnTopologicalSort kahn = new KahnTopologicalSort();
        long startTime = System.nanoTime();
        List<Integer> kahnOrder = kahn.topologicalSort(graph);
        long kahnTime = System.nanoTime() - startTime;

        DFSTopologicalSort dfsTopo = new DFSTopologicalSort();
        startTime = System.nanoTime();
        List<Integer> dfsOrder = dfsTopo.topologicalSort(graph);
        long dfsTime = System.nanoTime() - startTime;

        System.out.printf("Kahn: %d ms, DFS: %d ms%n",
                kahnTime / 1_000_000, dfsTime / 1_000_000);
        System.out.printf("Kahn order size: %d, DFS order size: %d%n",
                kahnOrder.size(), dfsOrder.size());

        // Both should produce a topological order of the same size
        assert kahnOrder.size() == dfsOrder.size();
    }

    @Test
    public void testLargeGraphPerformance() {
        // Generate a large mixed graph for performance testing
        Graph graph = TestGraphFactory.createTestMixedGraph(500, 0.2);

        // Test SCC performance on large graph
        TarjanSCC tarjan = new TarjanSCC();
        long startTime = System.nanoTime();
        List<List<Integer>> sccs = tarjan.findSCCs(graph);
        long sccTime = System.nanoTime() - startTime;

        System.out.printf("SCC found %d components in %d ms%n",
                sccs.size(), sccTime / 1_000_000);

        // Build condensation graph
        Graph condensation = tarjan.buildCondensationGraph(graph, sccs);

        // Test topological sort on condensation
        KahnTopologicalSort kahn = new KahnTopologicalSort();
        startTime = System.nanoTime();
        List<Integer> order = kahn.topologicalSort(condensation);
        long topoTime = System.nanoTime() - startTime;

        System.out.printf("Topological sort on condensation (%d components) completed in %d ms%n",
                condensation.getN(), topoTime / 1_000_000);

        // Test shortest paths on condensation
        if (condensation.getN() > 0) {
            DAGShortestPath shortestPath = new DAGShortestPath();
            startTime = System.nanoTime();
            double[] distances = shortestPath.findShortestPaths(condensation, 0);
            long spTime = System.nanoTime() - startTime;

            System.out.printf("Shortest paths in condensation completed in %d ms%n",
                    spTime / 1_000_000);
        }
    }

    @Test
    public void testGraphWithCyclesPerformance() {
        // Test performance on graphs with cycles using SCC graph which has cycles by design
        Graph graph = TestGraphFactory.createTestGraphWithSCCs(200, 8);

        System.out.println("Testing performance on graph with cycles:");
        System.out.println("Vertices: " + graph.getN() + ", SCCs: 8");

        // SCC performance
        TarjanSCC tarjan = new TarjanSCC();
        long startTime = System.nanoTime();
        List<List<Integer>> sccs = tarjan.findSCCs(graph);
        long sccTime = System.nanoTime() - startTime;

        System.out.printf("SCC: %d components, Time: %.3f ms%n",
                sccs.size(), sccTime / 1_000_000.0);

        // Build and test condensation graph
        Graph condensation = tarjan.buildCondensationGraph(graph, sccs);
        KahnTopologicalSort kahn = new KahnTopologicalSort();
        startTime = System.nanoTime();
        List<Integer> topoOrder = kahn.topologicalSort(condensation);
        long topoTime = System.nanoTime() - startTime;

        System.out.printf("Condensation & Topo Sort: %d components, Time: %.3f ms%n",
                condensation.getN(), topoTime / 1_000_000.0);
    }

    @Test
    public void testScalability() {
        System.out.println("Scalability Test:");
        System.out.println("=================");

        int[] sizes = {50, 100, 200, 500};

        for (int size : sizes) {
            Graph graph = TestGraphFactory.createTestMixedGraph(size, 0.15);

            long startTime = System.nanoTime();
            TarjanSCC tarjan = new TarjanSCC();
            List<List<Integer>> sccs = tarjan.findSCCs(graph);
            long sccTime = System.nanoTime() - startTime;

            Graph condensation = tarjan.buildCondensationGraph(graph, sccs);

            startTime = System.nanoTime();
            KahnTopologicalSort kahn = new KahnTopologicalSort();
            List<Integer> topoOrder = kahn.topologicalSort(condensation);
            long topoTime = System.nanoTime() - startTime;

            System.out.printf("Size: %d, SCC: %.3f ms, Topo: %.3f ms, Components: %d%n",
                    size, sccTime / 1_000_000.0, topoTime / 1_000_000.0, condensation.getN());
        }
    }

    @Test
    public void testDenseVsSparseGraphs() {
        System.out.println("Dense vs Sparse Graphs Performance:");
        System.out.println("===================================");

        double[] densities = {0.1, 0.3, 0.5, 0.7};
        int size = 100;

        for (double density : densities) {
            Graph graph = TestGraphFactory.createTestMixedGraph(size, density);

            long startTime = System.nanoTime();
            TarjanSCC tarjan = new TarjanSCC();
            List<List<Integer>> sccs = tarjan.findSCCs(graph);
            long sccTime = System.nanoTime() - startTime;

            System.out.printf("Density: %.1f, Vertices: %d, SCC Time: %.3f ms, Components: %d%n",
                    density, size, sccTime / 1_000_000.0, sccs.size());
        }
    }
}