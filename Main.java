import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    static class ChargerReport {
        long startTime;
        long endTime;
        boolean isUp;

        ChargerReport(long startTime, long endTime, boolean isUp) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.isUp = isUp;
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("provide the correct path to the input file.");
            return;
        }

        String inputFilePath = args[0];
        Map<Integer, List<ChargerReport>> chargerReports = new HashMap<>();
        Map<Integer, Set<Integer>> stationChargers = new HashMap<>();

        try {
            readInputFile(inputFilePath, chargerReports, stationChargers);
        } catch (IOException e) {
            System.err.println("Input file is not readable: " + e.getMessage());
            return;
        } catch (NumberFormatException e) {
            System.err.println("Input file has invalid number format:  " + e.getMessage());
            return;
        }

        // Calculate uptime for each station
        Map<Integer, Integer> mapForStationUpTime = getUpTime(stationChargers, chargerReports);

        // Print output
        printOutput(mapForStationUpTime);
    }

    private static void readInputFile(String filePath,
                                      Map<Integer, List<ChargerReport>> chargerReports,
                                      Map<Integer, Set<Integer>> stationChargers) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean readingReports = false;

            while ((line = br.readLine()) != null) {
//                line = line.trim();
                line = line.replaceAll("[^\\x20-\\x7E]", "").trim();
                if (line.isEmpty()) continue;

                // Skip section headers
                if (line.startsWith("[") && line.endsWith("]")) {
                    if (line.equals("[Charger Availability Reports]")) {
                        readingReports = true;
                    }
                    continue; // Skip headers
                }

                if (!readingReports) {
                    // Reading station and charger IDs
                    String[] parts = line.split("\\s+");
                    int stationId = Integer.parseInt(parts[0]);
                    Set<Integer> chargers = Arrays.stream(parts)
                            .skip(1)
                            .map(Integer::parseInt)
                            .collect(Collectors.toSet());
                    stationChargers.put(stationId, chargers);
                } else {
                    // Reading charger availability reports
                    String[] reportsParts = line.split("\\s+");
                    int chargerId = Integer.parseInt(reportsParts[0]);
                    long startTime = Long.parseLong(reportsParts[1]);
                    long endTime = Long.parseLong(reportsParts[2]);
                    boolean isUp = Boolean.parseBoolean(reportsParts[3]);

                    chargerReports.computeIfAbsent(chargerId, k -> new ArrayList<>())
                            .add(new ChargerReport(startTime, endTime, isUp));
                }
            }
        }
    }

    private static Map<Integer, Integer> getUpTime(Map<Integer, Set<Integer>> stationChargers,
                                                         Map<Integer, List<ChargerReport>> chargerReports) {
        Map<Integer, Integer> stationUptime = new TreeMap<>();

        stationChargers.forEach((stationId, chargers) -> {
            long totalDuration = 0;
            long totalUpDuration = 0;

            for (int chargerId : chargers) {
                List<ChargerReport> reports = chargerReports.get(chargerId);
                if (reports != null) {
                    for (ChargerReport report : reports) {
                        long duration = report.endTime - report.startTime;
                        totalDuration += duration;
                        if (report.isUp) {
                            totalUpDuration += duration;
                        }
                    }
                }
            }

            int uptimePercentage = totalDuration > 0 ? (int) ((totalUpDuration * 100) / totalDuration) : 0;
            stationUptime.put(stationId, uptimePercentage);
        });

        return stationUptime;
    }

    private static void printOutput(Map<Integer, Integer> stationUptime) {
        stationUptime.forEach((stationId, uptime) ->
                System.out.println(stationId + " " + uptime)
        );
    }
}
