import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.*;

public class MainIntegrationTest {

    @Test
    public void testIntegration_ValidInputFile() throws IOException {
        File sampleLocalFile = File.createTempFile("testInput", ".txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("[Stations]\n");
            writer.write("1 101 102\n");
            writer.write("2 201\n");
            writer.write("[Charger Availability Reports]\n");
            writer.write("101 0 1000 true\n");
            writer.write("102 1000 2000 true\n");
            writer.write("201 0 2000 false\n");
        }
        String[] args = {sampleLocalFile.getAbsolutePath()};
        Main.main(args);
        sampleLocalFile.deleteOnExit();
    }
}
