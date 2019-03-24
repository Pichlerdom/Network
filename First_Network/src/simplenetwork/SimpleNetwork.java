package simplenetwork;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SimpleNetwork implements Serializable {
    private final List<Integer> layerSizes;
    private final boolean[] isLayerRecurring;
    private int layerNumber;
    public List<List<Neuron>> layers;
    private float fitness;


    public SimpleNetwork(int[] layersizes, boolean[] isLayerRecurring){
        layerSizes = new ArrayList<Integer>();
        Arrays.stream(layersizes).forEach(i -> layerSizes.add(i)); //Network layer sizes

        this.isLayerRecurring = isLayerRecurring.clone();

        this.layerNumber = layersizes.length;   //number of layers
        //make layers
        layers = new ArrayList<>();
        ((ArrayList<List<Neuron>>) layers).ensureCapacity(layerNumber);
        layers.add(0,new ArrayList<>(layerSizes.get(0)));
        for(int i = 0; i < layerSizes.get(0); i++)layers.get(0)
                                                        .add(i,new Neuron(layerSizes.get(0),true,0,this.isLayerRecurring[0]));
        for(int i = 1; i < layerNumber; i++){
            layers.add(i,new ArrayList<>());
            for(int j = 0; j < layerSizes.get(i); j++)layers.get(i)
                                                            .add(j,new Neuron(layerSizes.get(i-1),false,0, this.isLayerRecurring[i]));
        }
    }

    public ArrayList<Float> propagate(ArrayList<Float> input){
        layers.get(0).forEach(neuron -> neuron.update(input));
        ArrayList<Float> temp;
        for(int i = 1; i< layerNumber; i++){
            //get input data for next layer
            temp = (ArrayList<Float>) layers.get(i-1).stream()
                                                    .map(n -> n.getOutput())
                                                    .collect(Collectors.toList());
            for(Neuron n: layers.get(i)){
                n.update(temp);
            }
        }
        return (ArrayList<Float>) layers.get(layerNumber-1).stream()
                                                .map(n->n.getOutput())
                                                .collect(Collectors.toList());
    }
    public SimpleNetwork combine(SimpleNetwork that){
        Random rand = new Random(System.nanoTime());
        int[] layersizes = new int[layerNumber];
        for (int i = 0; i < layerNumber; i++) layersizes[i] = this.layerSizes.get(i).intValue();
        boolean[] isLayerRecurring;
        isLayerRecurring = this.isLayerRecurring.clone();
        SimpleNetwork nn = new SimpleNetwork(layersizes,isLayerRecurring);
        float prob = this.fitness/that.fitness;

        for(int i = 0; i < layers.size(); i++){
            for(int j = 0; j < layers.get(i).size(); j++){
                nn.layers.get(i).get(j).combine(that.layers.get(i).get(j),this.layers.get(i).get(j),prob);
                nn.layers.get(i).get(j).randomise_a_bit();
            }
        }
        return nn;
    }

    public float getFitness(){return fitness;}
    public void setFitness(float fitness){this.fitness = fitness;}
}
