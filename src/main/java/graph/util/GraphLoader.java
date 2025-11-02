package graph.util;

import graph.model.Graph;
import graph.model.Edge;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.*;

public class GraphLoader {
    private static final Gson gson = new Gson();

    public static Graph loadFromJson(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filename);
        }

        try (Reader reader = new FileReader(filename)) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            boolean directed = json.get("directed").getAsBoolean();
            int n = json.get("n").getAsInt();
            Graph graph = new Graph(n, directed);

            if (json.has("weight_model")) {
                graph.setProperty("weight_model", json.get("weight_model").getAsString());
            }
            if (json.has("source")) {
                graph.setProperty("source", json.get("source").getAsInt());
            }

            JsonArray edges = json.getAsJsonArray("edges");
            for (JsonElement edgeElement : edges) {
                JsonObject edge = edgeElement.getAsJsonObject();
                int u = edge.get("u").getAsInt();
                int v = edge.get("v").getAsInt();
                double w = edge.get("w").getAsDouble();
                graph.addEdge(u, v, w);
            }

            return graph;
        }
    }

    public static void saveToJson(Graph graph, String filename) throws IOException {
        // Create parent directories if they don't exist
        File file = new File(filename);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        JsonObject json = new JsonObject();
        json.addProperty("directed", graph.isDirected());
        json.addProperty("n", graph.getN());

        JsonArray edges = new JsonArray();
        for (int u = 0; u < graph.getN(); u++) {
            for (Edge edge : graph.getNeighbors(u)) {
                JsonObject edgeObj = new JsonObject();
                edgeObj.addProperty("u", u);
                edgeObj.addProperty("v", edge.getV());
                edgeObj.addProperty("w", edge.getWeight());
                edges.add(edgeObj);
            }
        }
        json.add("edges", edges);

        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(json, writer);
        }
    }
}