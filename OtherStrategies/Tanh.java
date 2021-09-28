package connectFour;

public class Tanh implements ActivationFunction {

    @Override
    public float activationFunction(Object[] inputs, Object[] weights) {
        float output = 0;
        for (int i = 0; i < inputs.length; i++) {
            float weight = (float) weights[i];
            int input = (int) inputs[i];
            output += weight * input;
        }
        return (float) Math.tanh(output);
    }
}
