package graph;

import graph.model.Graph;
import graph.scc.TarjanSCC;
import graph.scc.KosarajuSCC;
import graph.topo.KahnTopologicalSort;
import graph.dagsp.DAGShortestPath;
import graph.dagsp.DAGLongestPath;
import graph.util.GraphLoader;
import graph.util.GraphGenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Create data directory if it doesn't exist
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                System.out.println("Created data directory");
            }

            // Generate test datasets
            System.out.println("Generating datasets...");
            GraphGenerator generator = new GraphGenerator();
            generator.generateAllDatasets();
            System.out.println("Generated all datasets");

            // Test with different examples
            testSCCAlgorithms();
            testWithExample();
            testPerformance();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testSCCAlgorithms() throws IOException {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("COMPARING SCC ALGORITHMS");
        System.out.println("=".repeat(50));

        Graph graph = GraphLoader.loadFromJson("data/small_2.json");
        System.out.println("Testing on graph with " + graph.getN() + " vertices and potential cycles");

        // Test Tarjan
        TarjanSCC tarjan = new TarjanSCC();
        List<List<Integer>> tarjanSCCs = tarjan.findSCCs(graph);
        System.out.println("\nTarjan SCCs: " + tarjanSCCs.size() + " components");
        for (int i = 0; i < tarjanSCCs.size(); i++) {
            System.out.println("  Component " + i + ": " + tarjanSCCs.get(i));
        }
        tarjan.getMetrics().printMetrics();

        // Test Kosaraju
        KosarajuSCC kosaraju = new KosarajuSCC();
        List<List<Integer>> kosarajuSCCs = kosaraju.findSCCs(graph);
        System.out.println("Kosaraju SCCs: " + kosarajuSCCs.size() + " components");
        kosaraju.getMetrics().printMetrics();

        // Build condensation graph
        Graph condensation = tarjan.buildCondensationGraph(graph, tarjanSCCs);
        System.out.println("Condensation graph has " + condensation.getN() + " components");
    }

    private static void testWithExample() throws IOException {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("FULL PIPELINE TEST");
        System.out.println("=".repeat(50));

        // Load the provided graph
        Graph graph = GraphLoader.loadFromJson("data/small_1.json");
        System.out.println("Graph loaded: " + graph.getN() + " vertices");
        System.out.println("Graph structure:");
        System.out.println(graph);

        // Test SCC
        System.out.println("\n--- Strongly Connected Components ---");
        TarjanSCC tarjan = new TarjanSCC();
        List<List<Integer>> sccs = tarjan.findSCCs(graph);
        System.out.println("SCCs found: " + sccs.size());
        for (int i = 0; i < sccs.size(); i++) {
            System.out.println("SCC " + i + ": " + sccs.get(i));
        }
        tarjan.getMetrics().printMetrics();

        // Build condensation graph
        Graph condensation = tarjan.buildCondensationGraph(graph, sccs);
        System.out.println("Condensation graph: " + condensation.getN() + " components");
        System.out.println("Condensation structure:");
        System.out.println(condensation);

        // Test Topological Sort on condensation graph
        System.out.println("\n--- Topological Sort on Condensation ---");
        KahnTopologicalSort kahn = new KahnTopologicalSort();
        try {
            List<Integer> topoOrder = kahn.topologicalSort(condensation);
            System.out.println("Topological order of components: " + topoOrder);
            kahn.getMetrics().printMetrics();

            // Map back to original vertices
            System.out.println("Derived order of original tasks:");
            for (int compId : topoOrder) {
                System.out.println("  Component " + compId + ": " + sccs.get(compId));
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Cannot perform topological sort: " + e.getMessage());
        }

        // Test Shortest Paths on CONDENSATION GRAPH
        System.out.println("\n--- Shortest Paths in Condensation DAG ---");
        DAGShortestPath shortestPath = new DAGShortestPath();
        int sourceComponent = findComponentContainingVertex(sccs, 0);
        if (sourceComponent != -1) {
            double[] distances = shortestPath.findShortestPaths(condensation, sourceComponent);
            System.out.println("Shortest distances from component " + sourceComponent + " (contains vertex 0):");
            for (int i = 0; i < distances.length; i++) {
                System.out.println("  to component " + i + " (" + sccs.get(i) + "): " +
                        (distances[i] == Double.POSITIVE_INFINITY ? "∞" : String.format("%.1f", distances[i])));
            }
            shortestPath.getMetrics().printMetrics();
        } else {
            System.out.println("Source vertex 0 not found in any component");
        }

        // Test Longest Paths on CONDENSATION GRAPH
        System.out.println("\n--- Longest Paths (Critical Path) in Condensation DAG ---");
        DAGLongestPath longestPath = new DAGLongestPath();
        DAGLongestPath.CriticalPathResult criticalPath = longestPath.findCriticalPath(condensation);
        System.out.println("Critical path length: " + criticalPath.getLength());
        System.out.println("Start component: " + criticalPath.getStartVertex() + " (" + sccs.get(criticalPath.getStartVertex()) + ")");
        System.out.println("End component: " + criticalPath.getEndVertex() + " (" + sccs.get(criticalPath.getEndVertex()) + ")");

        List<Integer> criticalPathComponents = longestPath.reconstructPath(condensation,
                criticalPath.getStartVertex(), criticalPath.getEndVertex());
        System.out.println("Critical path through components: " + criticalPathComponents);

        // Map critical path back to original vertices
        System.out.println("Critical path in original graph:");
        for (int compId : criticalPathComponents) {
            System.out.println("  Component " + compId + ": " + sccs.get(compId));
        }
        longestPath.getMetrics().printMetrics();
    }

    private static void testPerformance() throws IOException {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PERFORMANCE TEST ON LARGE DATASETS");
        System.out.println("=".repeat(50));

        testDatasetPerformance("data/medium_1.json");
        testDatasetPerformance("data/large_1.json");
    }

    private static void testDatasetPerformance(String filename) throws IOException {
        System.out.println("\nTesting: " + filename);
        Graph graph = GraphLoader.loadFromJson(filename);
        System.out.println("Graph: " + graph.getN() + " vertices");

        // SCC Performance
        TarjanSCC tarjan = new TarjanSCC();
        long startTime = System.nanoTime();
        List<List<Integer>> sccs = tarjan.findSCCs(graph);
        long sccTime = System.nanoTime() - startTime;
        System.out.printf("SCC: %d components, Time: %.3f ms%n",
                sccs.size(), sccTime / 1_000_000.0);

        // Build condensation graph
        Graph condensation = tarjan.buildCondensationGraph(graph, sccs);

        // Topological Sort Performance on condensation
        KahnTopologicalSort kahn = new KahnTopologicalSort();
        startTime = System.nanoTime();
        List<Integer> topoOrder = kahn.topologicalSort(condensation);
        long topoTime = System.nanoTime() - startTime;
        System.out.printf("Topological Sort: %d components, Time: %.3f ms%n",
                topoOrder.size(), topoTime / 1_000_000.0);

        // Shortest Path Performance on condensation
        DAGShortestPath shortestPath = new DAGShortestPath();
        startTime = System.nanoTime();
        double[] distances = shortestPath.findShortestPaths(condensation, 0);
        long spTime = System.nanoTime() - startTime;
        System.out.printf("Shortest Path in Condensation: Time: %.3f ms%n", spTime / 1_000_000.0);

        // Count reachable components
        int reachable = 0;
        for (double dist : distances) {
            if (dist != Double.POSITIVE_INFINITY) {
                reachable++;
            }
        }
        System.out.printf("Reachable components from source: %d/%d%n", reachable, condensation.getN());
    }

    private static int findComponentContainingVertex(List<List<Integer>> sccs, int vertex) {
        for (int i = 0; i < sccs.size(); i++) {
            if (sccs.get(i).contains(vertex)) {
                return i;
            }
        }
        return -1;
    }
}