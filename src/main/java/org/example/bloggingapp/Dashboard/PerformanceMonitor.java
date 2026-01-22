package org.example.bloggingapp.Dashboard;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Performance monitoring service for tracking cache metrics and system performance
 */
public class PerformanceMonitor {
    
    private static PerformanceMonitor instance;
    private final ConcurrentHashMap<String, MetricData> metrics;
    private final AtomicLong totalRequests;
    private final AtomicLong cacheHits;
    private final AtomicLong cacheMisses;
    private final AtomicLong totalResponseTime;
    private final List<PerformanceSnapshot> performanceHistory;
    
    private PerformanceMonitor() {
        this.metrics = new ConcurrentHashMap<>();
        this.totalRequests = new AtomicLong(0);
        this.cacheHits = new AtomicLong(0);
        this.cacheMisses = new AtomicLong(0);
        this.totalResponseTime = new AtomicLong(0);
        this.performanceHistory = new ArrayList<>();
    }
    
    public static synchronized PerformanceMonitor getInstance() {
        if (instance == null) {
            instance = new PerformanceMonitor();
        }
        return instance;
    }
    
    /**
     * Records a cache hit
     */
    public void recordCacheHit(String cacheName) {
        cacheHits.incrementAndGet();
        totalRequests.incrementAndGet();
        updateMetric(cacheName, "hits", 1);
    }
    
    /**
     * Records a cache miss
     */
    public void recordCacheMiss(String cacheName) {
        cacheMisses.incrementAndGet();
        totalRequests.incrementAndGet();
        updateMetric(cacheName, "misses", 1);
    }
    
    /**
     * Records a response time
     */
    public void recordResponseTime(String operation, long responseTimeMs) {
        totalResponseTime.addAndGet(responseTimeMs);
        updateMetric(operation, "responseTime", responseTimeMs);
    }
    
    /**
     * Records a cache eviction
     */
    public void recordCacheEviction(String cacheName) {
        updateMetric(cacheName, "evictions", 1);
    }
    
    /**
     * Records cache size
     */
    public void recordCacheSize(String cacheName, int size) {
        updateMetric(cacheName, "size", size);
    }
    
    /**
     * Updates a specific metric
     */
    private void updateMetric(String cacheName, String metricType, long value) {
        String key = cacheName + "." + metricType;
        MetricData data = metrics.computeIfAbsent(key, k -> new MetricData());
        data.addValue(value);
    }
    
    /**
     * Gets current cache hit rate
     */
    public double getHitRate() {
        long total = totalRequests.get();
        return total == 0 ? 0.0 : (double) cacheHits.get() / total * 100.0;
    }
    
    /**
     * Gets average response time
     */
    public double getAverageResponseTime() {
        long requests = totalRequests.get();
        return requests == 0 ? 0.0 : (double) totalResponseTime.get() / requests;
    }
    
    /**
     * Gets total number of requests
     */
    public long getTotalRequests() {
        return totalRequests.get();
    }
    
    /**
     * Gets total cache hits
     */
    public long getCacheHits() {
        return cacheHits.get();
    }
    
    /**
     * Gets total cache misses
     */
    public long getCacheMisses() {
        return cacheMisses.get();
    }
    
    /**
     * Gets metric data for a specific cache and metric type
     */
    public MetricData getMetric(String cacheName, String metricType) {
        String key = cacheName + "." + metricType;
        return metrics.get(key);
    }
    
    /**
     * Creates a performance snapshot
     */
    public PerformanceSnapshot createSnapshot() {
        return new PerformanceSnapshot(
            LocalDateTime.now(),
            getHitRate(),
            getAverageResponseTime(),
            getTotalRequests(),
            getCacheHits(),
            getCacheMisses(),
            new ConcurrentHashMap<>(metrics)
        );
    }
    
    /**
     * Adds a performance snapshot to history
     */
    public void addSnapshotToHistory() {
        PerformanceSnapshot snapshot = createSnapshot();
        performanceHistory.add(snapshot);
        
        // Keep only last 100 snapshots
        if (performanceHistory.size() > 100) {
            performanceHistory.remove(0);
        }
    }
    
    /**
     * Gets performance history
     */
    public List<PerformanceSnapshot> getPerformanceHistory() {
        return new ArrayList<>(performanceHistory);
    }
    
    /**
     * Resets all metrics
     */
    public void reset() {
        totalRequests.set(0);
        cacheHits.set(0);
        cacheMisses.set(0);
        totalResponseTime.set(0);
        metrics.clear();
        performanceHistory.clear();
    }
    
    /**
     * Generates performance report
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("PERFORMANCE REPORT\n");
        report.append("==================\n");
        report.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");
        
        report.append("Overall Metrics:\n");
        report.append("  Total Requests: ").append(getTotalRequests()).append("\n");
        report.append("  Cache Hits: ").append(getCacheHits()).append("\n");
        report.append("  Cache Misses: ").append(getCacheMisses()).append("\n");
        report.append("  Hit Rate: ").append(String.format("%.2f%%", getHitRate())).append("\n");
        report.append("  Avg Response Time: ").append(String.format("%.2f ms", getAverageResponseTime())).append("\n\n");
        
        report.append("Cache-Specific Metrics:\n");
        for (String key : metrics.keySet()) {
            MetricData data = metrics.get(key);
            report.append("  ").append(key).append(": ").append(data).append("\n");
        }
        
        return report.toString();
    }
    
    /**
     * Data class for storing metric information
     */
    public static class MetricData {
        private long totalValue;
        private long count;
        private long minValue;
        private long maxValue;
        private long lastUpdated;
        
        public MetricData() {
            this.minValue = Long.MAX_VALUE;
            this.maxValue = Long.MIN_VALUE;
            this.lastUpdated = System.currentTimeMillis();
        }
        
        public void addValue(long value) {
            totalValue += value;
            count++;
            minValue = Math.min(minValue, value);
            maxValue = Math.max(maxValue, value);
            lastUpdated = System.currentTimeMillis();
        }
        
        public long getTotalValue() { return totalValue; }
        public long getCount() { return count; }
        public double getAverage() { return count == 0 ? 0.0 : (double) totalValue / count; }
        public long getMinValue() { return minValue == Long.MAX_VALUE ? 0 : minValue; }
        public long getMaxValue() { return maxValue == Long.MIN_VALUE ? 0 : maxValue; }
        public long getLastUpdated() { return lastUpdated; }
        
        @Override
        public String toString() {
            return String.format("avg=%.2f, min=%d, max=%d, count=%d", getAverage(), getMinValue(), getMaxValue(), getCount());
        }
    }
    
    /**
     * Performance snapshot for historical tracking
     */
    public static class PerformanceSnapshot {
        private final LocalDateTime timestamp;
        private final double hitRate;
        private final double averageResponseTime;
        private final long totalRequests;
        private final long cacheHits;
        private final long cacheMisses;
        private final ConcurrentHashMap<String, MetricData> metrics;
        
        public PerformanceSnapshot(LocalDateTime timestamp, double hitRate, double averageResponseTime,
                                 long totalRequests, long cacheHits, long cacheMisses,
                                 ConcurrentHashMap<String, MetricData> metrics) {
            this.timestamp = timestamp;
            this.hitRate = hitRate;
            this.averageResponseTime = averageResponseTime;
            this.totalRequests = totalRequests;
            this.cacheHits = cacheHits;
            this.cacheMisses = cacheMisses;
            this.metrics = new ConcurrentHashMap<>(metrics);
        }
        
        // Getters
        public LocalDateTime getTimestamp() { return timestamp; }
        public double getHitRate() { return hitRate; }
        public double getAverageResponseTime() { return averageResponseTime; }
        public long getTotalRequests() { return totalRequests; }
        public long getCacheHits() { return cacheHits; }
        public long getCacheMisses() { return cacheMisses; }
        public ConcurrentHashMap<String, MetricData> getMetrics() { return metrics; }
        
        @Override
        public String toString() {
            return String.format("Snapshot[%s: hitRate=%.2f%%, avgTime=%.2fms, requests=%d]",
                timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                hitRate, averageResponseTime, totalRequests);
        }
    }
}
