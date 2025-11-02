package graph.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.*;

public class JsonUtil {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Исправлено: setPrettyPrinting

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(File file, Class<T> classOfT) throws IOException {
        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, classOfT);
        }
    }

    public static void toJsonFile(Object object, String filename) throws IOException {
        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(object, writer);
        }
    }

    public static String formatJson(String json) {
        JsonElement jsonElement = JsonParser.parseString(json);
        return gson.toJson(jsonElement);
    }

    public static String formatJson(File file) throws IOException {
        try (Reader reader = new FileReader(file)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            return gson.toJson(jsonElement);
        }
    }
}