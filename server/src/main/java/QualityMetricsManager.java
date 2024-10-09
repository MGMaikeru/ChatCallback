import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QualityMetricsManager {
    private static QualityMetricsManager instance;
    private Map<String, CommandMetrics> metricsMap;
    private CommandMetrics generalMetrics;

    private static class CommandMetrics {
        String commandType;
        List<Long> responseTimes;
        List<Long> processingTimes;
        int totalRequests;
        int missedEvents;
        int unprocessedEvents;
        long startTime;
        long deadline;

        CommandMetrics(String commandType, long deadline) {
            this.commandType = commandType;
            this.responseTimes = new ArrayList<>();
            this.processingTimes = new ArrayList<>();
            this.totalRequests = 0;
            this.missedEvents = 0;
            this.unprocessedEvents = 0;
            this.startTime = System.currentTimeMillis();
            this.deadline = deadline;
        }
    }

    private QualityMetricsManager() {
        metricsMap = new HashMap<>();
        initializeMetrics("fibonacci", 1000);    // 1 segundo
        initializeMetrics("listifs", 500);       // 500ms
        initializeMetrics("listports", 5000);    // 5 segundos
        initializeMetrics("command", 2000);      // 2 segundos

        // Metricas generales con un deadline promedio 2 segundos
        generalMetrics = new CommandMetrics("General (All Commands)", 2000);
    }

    private void initializeMetrics(String commandType, long deadline) {
        metricsMap.put(commandType, new CommandMetrics(commandType, deadline));
    }

    public static QualityMetricsManager getInstance() {
        if (instance == null) {
            instance = new QualityMetricsManager();
        }
        return instance;
    }

    public void recordMetric(String commandType, long responseTime, long processingTime,
                             boolean processed, boolean missed) {
        CommandMetrics metrics = metricsMap.get(commandType);
        if (metrics != null) {
            recordMetricForCommand(metrics, responseTime, processingTime, processed, missed);
        }
        recordMetricForCommand(generalMetrics, responseTime, processingTime, processed, missed);
    }

    private void recordMetricForCommand(CommandMetrics metrics, long responseTime, long processingTime,
                                        boolean processed, boolean missed) {
        metrics.responseTimes.add(responseTime);
        metrics.processingTimes.add(processingTime);
        metrics.totalRequests++;
        if (!processed) metrics.unprocessedEvents++;
        if (missed) metrics.missedEvents++;
    }

    private double calculateAverage(List<Long> times) {
        return times.stream().mapToLong(Long::longValue).average().orElse(0.0);
    }

    private double calculateJitter(List<Long> times) {
        if (times.size() < 2) return 0;
        double sumDifferences = 0;
        for (int i = 1; i < times.size(); i++) {
            sumDifferences += Math.abs(times.get(i) - times.get(i - 1));
        }
        return sumDifferences / (times.size() - 1);
    }

    private String getMetricsReport(CommandMetrics metrics, boolean isGeneral) {
        long elapsedTimeSeconds = (System.currentTimeMillis() - metrics.startTime) / 1000;
        if (elapsedTimeSeconds == 0) elapsedTimeSeconds = 1;

        double throughput = (double) metrics.totalRequests / elapsedTimeSeconds;
        double avgResponseTime = calculateAverage(metrics.responseTimes);
        double avgProcessingTime = calculateAverage(metrics.processingTimes);
        double jitter = calculateJitter(metrics.responseTimes);
        double missingRate = (double) metrics.missedEvents / elapsedTimeSeconds;
        double unprocessRate = (double) metrics.unprocessedEvents / elapsedTimeSeconds;

        StringBuilder report = new StringBuilder();

        if (isGeneral) {
            report.append("\n- REPORTE ATRIBUTOS DE CALIDAD -\n");
        } else {
            report.append("\n- Atributos de calidad para ").append(metrics.commandType).append(" -\n");
        }

        report.append(String.format("Throughput: %.2f requests/s\n", throughput));
        report.append(String.format("Average Response Time: %.2f ms\n", avgResponseTime));
        report.append(String.format("Processing Time (avg): %.2f ms\n", avgProcessingTime));
        report.append(String.format("Deadline: %d ms\n", metrics.deadline));
        report.append(String.format("Jitter: %.2f ms\n", jitter));
        report.append(String.format("Missing Rate: %.2f events/s\n", missingRate));
        report.append(String.format("Unprocess Rate: %.2f events/s\n", unprocessRate));
        report.append(String.format("Total Requests: %d\n", metrics.totalRequests));

        if (isGeneral) {
            report.append("---------------------------------------------\n");
        }

        return report.toString();
    }

    public String generateFullReport() {
        StringBuilder fullReport = new StringBuilder();

        if (generalMetrics.totalRequests > 0) {
            fullReport.append(getMetricsReport(generalMetrics, true));
        }

        fullReport.append("\n- REPORTE COMANDOS ESPECIFICOS -\n");
        for (CommandMetrics metrics : metricsMap.values()) {
            if (metrics.totalRequests > 0) {
                fullReport.append(getMetricsReport(metrics, false));
            }
        }

        fullReport.append("---------------------------------------------\n");
        return fullReport.toString();
    }
}