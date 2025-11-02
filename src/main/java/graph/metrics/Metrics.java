package graph.metrics;

import java.util.HashMap;
import java.util.Map;

public class Metrics {
    private final String algorithmName;
    private final Map<String, Long> counters;
    private long startTime;
    private long endTime;

    public Metrics(String algorithmName) {
        this.algorithmName = algorithmName;
        this.counters = new HashMap<>();
    }

    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    public void stopTimer() {
        this.endTime = System.nanoTime();
    }

    public long getElapsedTime() {
        return endTime - startTime;
    }

    public void incrementCounter(String counterName) {
        counters.put(counterName, counters.getOrDefault(counterName, 0L) + 1);
    }

    public void setCounter(String counterName, long value) {
        counters.put(counterName, value);
    }

    public long getCounter(String counterName) {
        return counters.getOrDefault(counterName, 0L);
    }

    public void printMetrics() {
        System.out.println("=== Metrics for " + algorithmName + " ===");
        System.out.printf("Time: %.3f ms\n", getElapsedTime() / 1_000_000.0);
        for (Map.Entry<String, Long> entry : counters.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println();
    }

    public Map<String, Object> getMetricsMap() {
        Map<String, Object> metricsMap = new HashMap<>();
        metricsMap.put("algorithm", algorithmName);
        metricsMap.put("time_ms", getElapsedTime() / 1_000_000.0);
        metricsMap.putAll(counters);
        return metricsMap;
    }
}