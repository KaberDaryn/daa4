package graph.model;

public class Vertex {
    private final int id;
    private String label;
    private double duration; // For node duration model

    public Vertex(int id) {
        this.id = id;
        this.label = "v" + id;
    }

    public Vertex(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public Vertex(int id, String label, double duration) {
        this.id = id;
        this.label = label;
        this.duration = duration;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return id == vertex.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return label != null ? label : "v" + id;
    }
}