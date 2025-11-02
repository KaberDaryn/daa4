package graph.model;

public class Edge {
    private final int v;
    private final double weight;

    public Edge(int v, double weight) {
        this.v = v;
        this.weight = weight;
    }

    public int getV() { return v; }
    public double getWeight() { return weight; }

    @Override
    public String toString() {
        return "->" + v + "(" + weight + ")";
    }
}