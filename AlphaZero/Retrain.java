import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class Retrain implements Runnable {
    private static final String DATAFILE = "/Users/brennandury/ConnectFourData/Data" + Math.random() + ".txt";
    private final List<List<Number>> data;

    public Retrain(List<List<Number>> data) {
        this.data = data;
    }

    @Override
    public void run() {
        Writer writer = null;
        try {
            writer = new FileWriter(DATAFILE);
            writer.write(this.data.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (IOException ignore) {}
        }
    }
}
