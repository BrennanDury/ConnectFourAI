package connectFour;

import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

public class TensorModel {
    private Session.Runner player1;
    private Session.Runner player2;
    private static final String MODELFILE = "/Users/brennandury/IdeaProjects/ConnectFourAI/Model/src/random.Users.brennandury.IdeaProjects.ConnectFourAI.Model.src.AlphaZeroTraining.py";

    public TensorModel() {
        this.player1 = SavedModelBundle.loader(MODELFILE).load().session().runner();
        this.player2 = SavedModelBundle.loader(MODELFILE).load().session().runner();
    }

    public float process(Grid g, int player) {
        Tensor<Integer> board = Tensor.create(g.get1DBoard(), Integer.class);
        if (player == 1) {
            return this.player1
                    .feed("input_tensor", board)
                    .feed("dropout/keras_learning_phase", Tensor.create(Boolean.FALSE))
                    .fetch("output_tensor")
                    .run().get(0).floatValue();
        } else {
            return this.player2
                    .feed("input_tensor", board)
                    .feed("dropout/keras_learning_phase", Tensor.create(Boolean.FALSE))
                    .fetch("output_tensor")
                    .run().get(0).floatValue();
        }
    }

    public void retrain() {
        this.player1 = SavedModelBundle.loader(MODELFILE).load().session().runner();
        this.player2 = SavedModelBundle.loader(MODELFILE).load().session().runner();
    }
}
