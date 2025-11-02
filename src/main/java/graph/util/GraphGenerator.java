package graph.util;

import graph.model.Graph;
import java.io.IOException;
import java.util.Random;

public class GraphGenerator {
    private final Random random;

    public GraphGenerator() {
        this.random = new Random(42); // Fixed seed for reproducibility
    }

    public void generateAllDatasets() throws IOException {
        System.out.println("Generating small datasets...");
        generateSmallDatasets();

        System.out.println("Generating medium datasets...");
        generateMediumDatasets();

        System.out.println("Generating large datasets...");
        generateLargeDatasets();

        System.out.println("All datasets generated successfully!");
    }

    private void generateSmallDatasets() throws IOException {
        // Small 1: Assignment example with clear cycles and DAG condensation
        Graph small1 = createAssignmentExample();
        GraphLoader.saveToJson(small1, "data/small_1.json");
        System.out.println("Created small_1.json with " + small1.getN() + " vertices");

        // Small 2: Pure DAG for topological sort and shortest path testing
        Graph small2 = createPureDAG(10, 0.4);
        GraphLoader.saveToJson(small2, "data/small_2.json");
        System.out.println("Created small_2.json with " + small2.getN() + " vertices");

        // Small 3: Graph with multiple SCCs
        Graph small3 = createGraphWithMultipleSCCs(12, 3);
        GraphLoader.saveToJson(small3, "data/small_3.json");
        System.out.println("Created small_3.json with " + small3.getN() + " vertices");
    }

    private void generateMediumDatasets() throws IOException {
        // Medium 1: Complex SCC structure
        Graph medium1 = createGraphWithMultipleSCCs(20, 5);
        GraphLoader.saveToJson(medium1, "data/medium_1.json");
        System.out.println("Created medium_1.json with " + medium1.getN() + " vertices");

        // Medium 2: Large DAG
        Graph medium2 = createPureDAG(25, 0.3);
        GraphLoader.saveToJson(medium2, "data/medium_2.json");
        System.out.println("Created medium_2.json with " + medium2.getN() + " vertices");

        // Medium 3: Mixed graph with some cycles
        Graph medium3 = createMixedGraphWithCycles(30, 0.25, 2);
        GraphLoader.saveToJson(medium3, "data/medium_3.json");
        System.out.println("Created medium_3.json with " + medium3.getN() + " vertices");
    }

    private void generateLargeDatasets() throws IOException {
        // Large 1: Large DAG for performance testing
        Graph large1 = createPureDAG(50, 0.2);
        GraphLoader.saveToJson(large1, "data/large_1.json");
        System.out.println("Created large_1.json with " + large1.getN() + " vertices");

        // Large 2: Complex SCC structure
        Graph large2 = createGraphWithMultipleSCCs(60, 8);
        GraphLoader.saveToJson(large2, "data/large_2.json");
        System.out.println("Created large_2.json with " + large2.getN() + " vertices");

        // Large 3: Random mixed graph
        Graph large3 = createMixedGraph(70, 0.15);
        GraphLoader.saveToJson(large3, "data/large_3.json");
        System.out.println("Created large_3.json with " + large3.getN() + " vertices");
    }

    private Graph createAssignmentExample() {
        Graph graph = new Graph(8, true);
        // Original assignment edges - creates a cycle 1-2-3
        graph.addEdge(0, 1, 3);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 3, 4);
        graph.addEdge(3, 1, 1);  // This creates a cycle 1-2-3

        // Additional DAG structure
        graph.addEdge(4, 5, 2);
        graph.addEdge(5, 6, 5);
        graph.addEdge(6, 7, 1);

        // Connections between components
        graph.addEdge(0, 4, 2);
        graph.addEdge(2, 5, 3);
        graph.addEdge(3, 6, 4);

        graph.setProperty("source", 0);
        graph.setProperty("weight_model", "edge");
        return graph;
    }

    private Graph createPureDAG(int n, double density) {
        Graph graph = new Graph(n, true);

        // Create a proper DAG with no cycles
        for (int u = 0; u < n; u++) {
            for (int v = u + 1; v < n; v++) {
                if (random.nextDouble() < density) {
                    double weight = 1 + random.nextInt(10);
                    graph.addEdge(u, v, weight);
                }
            }
        }

        graph.setProperty("source", 0);
        graph.setProperty("weight_model", "edge");
        return graph;
    }

    private Graph createGraphWithMultipleSCCs(int n, int sccCount) {
        Graph graph = new Graph(n, true);

        int verticesPerSCC = n / sccCount;

        for (int scc = 0; scc < sccCount; scc++) {
            int start = scc * verticesPerSCC;
            int end = (scc == sccCount - 1) ? n : start + verticesPerSCC;

            // Create strong connectivity within SCC
            for (int i = start; i < end; i++) {
                for (int j = start; j < end; j++) {
                    if (i != j && random.nextDouble() < 0.7) {
                        graph.addEdge(i, j, 1 + random.nextInt(5));
                    }
                }
            }
        }

        // Add edges between SCCs to create DAG structure between components
        for (int scc1 = 0; scc1 < sccCount; scc1++) {
            for (int scc2 = scc1 + 1; scc2 < sccCount; scc2++) {
                if (random.nextDouble() < 0.5) {
                    int u = random.nextInt(verticesPerSCC) + scc1 * verticesPerSCC;
                    int v = random.nextInt(verticesPerSCC) + scc2 * verticesPerSCC;
                    graph.addEdge(u, v, 1 + random.nextInt(5));
                }
            }
        }

        graph.setProperty("source", 0);
        graph.setProperty("weight_model", "edge");
        return graph;
    }

    private Graph createMixedGraphWithCycles(int n, double density, int cycleCount) {
        Graph graph = new Graph(n, true);

        // Add random edges
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (u != v && random.nextDouble() < density) {
                    double weight = 1 + random.nextInt(10);
                    graph.addEdge(u, v, weight);
                }
            }
        }

        // Ensure some cycles
        for (int i = 0; i < cycleCount; i++) {
            int cycleSize = 3 + random.nextInt(3);
            int start = random.nextInt(n - cycleSize);
            for (int j = 0; j < cycleSize; j++) {
                int u = start + j;
                int v = start + (j + 1) % cycleSize;
                graph.addEdge(u, v, 1 + random.nextInt(5));
            }
        }

        graph.setProperty("source", 0);
        graph.setProperty("weight_model", "edge");
        return graph;
    }

    private Graph createMixedGraph(int n, double density) {
        Graph graph = new Graph(n, true);

        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (u != v && random.nextDouble() < density) {
                    double weight = 1 + random.nextInt(10);
                    graph.addEdge(u, v, weight);
                }
            }
        }

        graph.setProperty("source", 0);
        graph.setProperty("weight_model", "edge");
        return graph;
    }
}