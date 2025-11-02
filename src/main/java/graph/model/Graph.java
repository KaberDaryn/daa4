package graph.model;

import java.util.*;

public class Graph {
    private final int n;
    private final List<List<Edge>> adjList;
    private final boolean directed;
    private final Map<String, Object> properties;

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adjList = new ArrayList<>();
        this.properties = new HashMap<>();

        for (int i = 0; i < n; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, double weight) {
        validateVertex(u);
        validateVertex(v);
        adjList.get(u).add(new Edge(v, weight));
        if (!directed) {
            adjList.get(v).add(new Edge(u, weight));
        }
    }

    public List<Edge> getNeighbors(int u) {
        validateVertex(u);
        return Collections.unmodifiableList(adjList.get(u));
    }

    public Graph getTranspose() {
        if (!directed) return this;

        Graph transpose = new Graph(n, true);
        for (int u = 0; u < n; u++) {
            for (Edge edge : adjList.get(u)) {
                transpose.addEdge(edge.getV(), u, edge.getWeight());
            }
        }
        return transpose;
    }

    public int getN() { return n; }
    public boolean isDirected() { return directed; }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    private void validateVertex(int u) {
        if (u < 0 || u >= n) {
            throw new IllegalArgumentException("Vertex " + u + " is not in graph [0," + (n-1) + "]");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph(n=").append(n).append(", directed=").append(directed).append(")\n");
        for (int i = 0; i < n; i++) {
            sb.append(i).append(": ").append(adjList.get(i)).append("\n");
        }
        return sb.toString();
    }
}