package connectFour;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Retrain implements Runnable {
    private static final String DATA1FILE = "data1.txt";
    private static final String DATA2FILE = "data2.txt";
    private List<List<Integer>> data1;
    private List<List<Integer>> data2;
    private int sinceLast;
    private TensorModel network;

    public Retrain(List<List<Integer>> data1, List<List<Integer>> data2, int sinceLast, TensorModel network) {
        this.data1 = data1;
        this.data2 = data2;
        this.sinceLast = sinceLast;
        this.network = network;
    }
    @Override
    public void run() {
        int trim = (int) (this.sinceLast * 0.9);
        if (this.sinceLast > 0) {
            this.data1 = this.data1.subList(trim, this.data1.size());
            this.data2 = this.data2.subList(trim, this.data2.size());
            try {
                FileWriter writer1 = new FileWriter(DATA1FILE);
                writer1.write(this.data1.toString());
                writer1.flush();
                writer1.close();
                FileWriter writer2 = new FileWriter(DATA2FILE);
                writer2.write(this.data2.toString());
                writer2.flush();
                writer2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.network.retrain();
            this.network.retrain();
        }
    }
}
