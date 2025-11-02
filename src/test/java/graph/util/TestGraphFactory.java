package graph.util;

import graph.model.Graph;
import java.util.Random;

public class TestGraphFactory {
    private static final Random random = new Random(42);

    public static Graph createTestGraphWithSCCs(int n, int sccCount) {
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

        return graph;
    }

    public static Graph createTestDAG(int n, double density) {
        Graph graph = new Graph(n, true);

        for (int u = 0; u < n; u++) {
            for (int v = u + 1; v < n; v++) {
                if (random.nextDouble() < density) {
                    double weight = 1 + random.nextInt(10);
                    graph.addEdge(u, v, weight);
                }
            }
        }

        return graph;
    }

    public static Graph createTestMixedGraph(int n, double density) {
        Graph graph = new Graph(n, true);

        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (u != v && random.nextDouble() < density) {
                    double weight = 1 + random.nextInt(10);
                    graph.addEdge(u, v, weight);
                }
            }
        }

        return graph;
    }
}