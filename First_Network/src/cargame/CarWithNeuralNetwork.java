package cargame;


import simplenetwork.SimpleNetwork;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CarWithNeuralNetwork extends Car implements Drawable , Serializable{
    private static final int NUMBER_OF_SENSORS = 5;
    private static final int SENSOR_ANGLE = 90;
    private static final float SENSOR_DIST = 50;
    private static final int[] NET_LAYERS = {NUMBER_OF_SENSORS + 5,50,40,10 ,2};
    private static final boolean[] NET_RECCURING_LAYERS = {false,true,false,false,false};
    private final SimpleNetwork nn;
    private final List<CarSensor> sensors;
    private float fitness= 0;


    public CarWithNeuralNetwork(float length,float width,float x,float y){
        super(length, width, x, y);
        nn= new SimpleNetwork(NET_LAYERS,NET_RECCURING_LAYERS);
        sensors = new ArrayList<>(NUMBER_OF_SENSORS);
        for(int i = 0; i < NUMBER_OF_SENSORS; i++)
            sensors.add(i, new CarSensor(SENSOR_DIST, (i * (SENSOR_ANGLE/(NUMBER_OF_SENSORS-1))+315)));
    }
    public CarWithNeuralNetwork(float length,float width,float x,float y,SimpleNetwork nn){
        super(length, width, x, y);
        this.nn = nn;
        sensors = new ArrayList<>(NUMBER_OF_SENSORS);
        for(int i = 0; i < NUMBER_OF_SENSORS; i++)
            sensors.add(i, new CarSensor(SENSOR_DIST, (i * (SENSOR_ANGLE/(NUMBER_OF_SENSORS-1))+315)));
    }


    public void update(World world){
        //get sensor values;
        ArrayList<Float> sensorValues = (ArrayList<Float>) sensors.stream().parallel().map(s -> s.getOutput(this.getX(),this.getY(),this.getAngle(),world) - 0.5f).collect(Collectors.toList());

        sensorValues.add(getV());
        sensorValues.add(getAngle());
        sensorValues.add((this.hit_wall?0f:1f));
        sensorValues.add(this.getVx());
        sensorValues.add(this.getVy());
        ArrayList<Float> networkOutput = nn.propagate(sensorValues);
        super.update(2f * (networkOutput.get(0) - 0.5f), 2f *(networkOutput.get(1) - 0.5f), world);
}
    @Override
    public void draw(Graphics2D g) {
        Graphics2D gg =(Graphics2D) g.create();

        float x = getX();
        float y = getY();
        gg.translate(x - getWidth()/2,y - getLength()/2);
        gg.rotate(Math.toRadians(getAngle()+90));

        gg.setColor(Color.BLUE);
        gg.fillRect((int)0,(int)0,(int) getWidth(), (int)getLength());

        sensors.forEach(s->s.draw(gg));
        gg.dispose();
    }
    public void setFitness(float fitness){
        this.fitness = fitness;
        nn.setFitness(fitness);
    }
    public float getFitness(){
        return fitness;

    }
    public CarWithNeuralNetwork combine(CarWithNeuralNetwork c){
        return new CarWithNeuralNetwork(this.getLength(),this.getWidth(),0,0,this.nn.combine(c.getNetwork()));
    }

    public SimpleNetwork getNetwork(){
        return nn;
    }
}
