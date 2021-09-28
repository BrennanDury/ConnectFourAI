import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;

import java.util.ArrayList;
import java.util.List;

public class TensorModel {
    private SavedModelBundle player;
    private static final String MODELFILE = "/Users/brennandury/IdeaProjects/AlphaZero/py/src/player";

    public TensorModel() {
        this.player = SavedModelBundle.loader(MODELFILE).withTags("serve").load();
    }

    public List<float[]> inference(Board g) {
        int lastPlayer = g.getNextPlayer() * -1;
        List<Short> state = g.get1DBoard();
        float[][] arr = new float[1][42];
        for (int i  = 0; i < arr[0].length; i++) {
            int playerAt = state.get(i);
            playerAt *= lastPlayer;
            arr[0][i] = playerAt;
        }
        Tensor<Float> board = Tensor.create(arr, Float.class);
        float[][] copy1 = new float[1][1];
        float[][] copy2 = new float[1][Board.COLS];

        List<Tensor<?>> outputs = this.player.session().runner()
                .feed("serving_default_input_1:0", board)
                .fetch("StatefulPartitionedCall:0")
                .fetch("StatefulPartitionedCall:1")
                .run();
        outputs.get(0).copyTo(copy1);
        outputs.get(1).copyTo(copy2);

        List<float[]> result = new ArrayList<>();
        result.add(copy1[0]);

        float[] policies = new float[Board.COLS];
        for (int i = 0; i < Board.COLS; i++) {
            policies[i] = copy2[0][i];
        }
        result.add(policies);
        return result;
    }

    public void retrain() {
        this.player = SavedModelBundle.loader(MODELFILE).withTags("serve").load();
    }
}
