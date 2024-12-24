import org.junit.Test;
import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void readInputFileWithValidInput() throws IOException {
        String input = "[Stations]\n" +
                "1 101 102\n" +
                "2 201\n" +
                "[Charger Availability Reports]\n" +
                "101 0 1000 true\n" +
                "102 1000 2000 true\n" +
                "201 0 2000 false\n";

        Map<Integer, List<Main.ChargerReport>> chargerReports = new HashMap<>();
        Map<Integer, Set<Integer>> stationChargers = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new StringReader(input))) {
            Main.readInputFile(reader, chargerReports, stationChargers);
        }

        assertEquals(2, stationChargers.size());
        assertTrue(stationChargers.containsKey(1));
        assertTrue(stationChargers.containsKey(2));

        assertEquals(1, chargerReports.size());
        assertTrue(chargerReports.containsKey(101));
    }

    @Test(expected = NumberFormatException.class)
    public void readInputFileWithInvalidNumberFormat() throws IOException {
        String input = "[Stations]\n" +
                "1 101 102\n" +
                "[Charger Availability Reports]\n" +
                "101 0 invalid true\n"; // Invalid number format

        Map<Integer, List<Main.ChargerReport>> chargerReports = new HashMap<>();
        Map<Integer, Set<Integer>> stationChargers = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new StringReader(input))) {
            Main.readInputFile(reader, chargerReports, stationChargers);
        }
    }

    @Test
    public void testGetUptime() {
        Map<Integer, Set<Integer>> stationChargers = new HashMap<>();
        stationChargers.put(1, new HashSet<>(Arrays.asList(101, 102)));

        Map<Integer, List<Main.ChargerReport>> chargerReports = new HashMap<>();
        chargerReports.put(101, Arrays.asList(
                new Main.ChargerReport(0, 1000, true),
                new Main.ChargerReport(1000, 2000, false)
        ));
        chargerReports.put(102, Arrays.asList(
                new Main.ChargerReport(0, 1500, true),
                new Main.ChargerReport(1500, 3000, true)
        ));

        Map<Integer, Integer> uptimeResults = ChargingStationUptime.calculateUptime(stationChargers, chargerReports);

        assertEquals(1, uptimeResults.size());
        assertEquals(75, (int) uptimeResults.get(1)); // Expected uptime is (2500 / 3000) * 100 = 75%
    }

    @Test
    public void testPrintOutput() {
        Map<Integer, Integer> stationUptime = new TreeMap<>();
        stationUptime.put(1, 80);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Main.printOutput(stationUptime);
        String expectedOutput = "1 80\n";
        assertEquals(expectedOutput.trim(), outContent.toString().trim());
        System.setOut(System.out);
    }
}
