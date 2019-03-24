package cargame;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class FitnessDisplay implements Drawable {
    private final List<Float> fitnessValues;
    private final int length;
    private final float height;
    private float maxValue;

    public FitnessDisplay(int length,float height){
        fitnessValues = new LinkedList<>();
        this.length = length;
        this.height = height;
    }
    public void addValue(float value){
        fitnessValues.add(value);
        if(fitnessValues.size() > length){
            fitnessValues.remove(0);
        }
        if(value > maxValue){
            maxValue = value;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        Graphics2D gg = (Graphics2D) g.create();
gg.setColor(Color.BLACK);
        gg.fillRect(0,0,length,(int)height);
        gg.setColor(Color.GREEN);
        for(int i = 0; i < fitnessValues.size();i++){
            gg.translate(1,0);
            gg.drawLine(0,(int)(height * (fitnessValues.get(i)/maxValue)),0,0);
        }
    }
}
