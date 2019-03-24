package simplenetwork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Neuron implements Serializable {
    private List<Float> weights;
    private float bias;
    private final int numberOfInputs;
    private float output;
    private final boolean isInputLayer;
    private final boolean isReccuring;
    private float reccuringWeight;

    public Neuron(int prevLayerSize,boolean isInputLayer, float bias, boolean isReccuring){
        Random rand = new Random(System.nanoTime());
        weights = new ArrayList<Float>(prevLayerSize);
        this.bias = bias;
        this.isReccuring = isReccuring;

        if(isInputLayer){ //input layer
            for (int i = 0; i < prevLayerSize;i++) { weights.add(i ,1f); }
            this.bias = 0;
        }else {
            for (int i = 0; i < prevLayerSize;i++) { weights.add(i , (float) rand.nextGaussian()); }
        }
        if(isReccuring){
            reccuringWeight = (float)rand.nextGaussian();
        }
        this.numberOfInputs = prevLayerSize;
        this.isInputLayer = isInputLayer;
    }

    public void update(ArrayList<Float> input){
        if(input.size() != numberOfInputs){
            System.out.println("Something went wrong the input does not have the right size");
            return;
        }
        float sum = bias;
        for (int i = 0; i < numberOfInputs; i++) {
            sum += input.get(i) * weights.get(i);
        }
        if(isReccuring){
            sum += output*reccuringWeight;
        }
        output = sigmoid_function(sum);

    }
    public void combine(Neuron n1,Neuron n2, float prob){
        Random rand = new Random(System.nanoTime());
       // prob=1-prob;
        if(rand.nextDouble() > prob){
            bias = n1.bias;
        }else {
            bias = n2.bias;
        }

        for (int i = 0 ; i < weights.size(); i++){
            if(rand.nextDouble() >prob){
                weights.set(i,n1.weights.get(i));
            }else{
                weights.set(i,n2.weights.get(i));
            }
        }
        if(rand.nextDouble() > prob){
            reccuringWeight = n1.getReccuringWeight();
        }else {
            reccuringWeight = n2.getReccuringWeight();
        }
    }
    public void randomise_a_bit(){
        Random rand = new Random(System.nanoTime());
        if(rand.nextLong()%10 == 0){
            bias += (rand.nextDouble() - 0.5);
        }
        for (int i = 0; i < weights.size(); i++){
            if(rand.nextInt()%10 == 0)
                weights.set(i, (float) (weights.get(i) + (rand.nextGaussian() - 0.5) * 0.5));

            if(rand.nextInt()%100 == 0)
                weights.set(i, (float) (weights.get(i) * (rand.nextGaussian() - 0.5)*10));
        }
        if(rand.nextInt()% 10 == 0){
            reccuringWeight += (rand.nextFloat() - 0.5) * 0.1;
        }
    }
    public float getOutput(){return output;}

    public static float sigmoid_function(float x){
        return 1f/(1f+(float)Math.exp(-x));
    }

    public float getReccuringWeight() {
        return reccuringWeight;
    }
}
