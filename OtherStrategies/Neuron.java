package connectFour;

public class Neuron {
    private final Object[] weights;
    private final ActivationFunction function;

    public Neuron(Object[] weights, ActivationFunction function) {
        this.weights = weights;
        this.function = function;
    }

    public float process(Object[] inputs) {
        return function.activationFunction(inputs, this.weights);
    }
}
