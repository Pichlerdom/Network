package cargame;

import java.awt.*;
import java.io.Serializable;

public class CarSensor  implements Drawable, Serializable {
    private final float maxDist;
    private final float angle;
    private static final float STEP_SIZE = 3f;
    private float last_dist = 0;


    public CarSensor(float maxDist, float angle){
        this.maxDist = maxDist;
        if(angle < 0){
            angle = 360 - angle;
        }
        this.angle = angle;
    }


    public float getOutput(float x, float y, float carAngle, World world) {
        float rayx = x;
        float rayy = y;
        float drayx = (float) (STEP_SIZE * Math.cos(Math.toRadians(angle + carAngle)));
        float drayy = (float) (STEP_SIZE * Math.sin(Math.toRadians(angle + carAngle)));
        float dist;
        for ( dist = 0;
                rayx < world.getWidth() && rayx > 0 &&
                rayy < world.getHeight() && rayy > 0 &&
                dist <= maxDist;
                dist += STEP_SIZE) {
            rayx += drayx;
            rayy += drayy;
            if(!world.canDriveHere((int)rayx,(int)rayy)){
                break;
            }
        }
        last_dist = dist;
        return (dist/maxDist);
    }

    @Override
    public void draw(Graphics2D g) {
        Graphics2D gg = (Graphics2D) g.create();

        gg.translate(CarGame.CAR_WIDTH/2,CarGame.CAR_LENGTH/2);
        gg.rotate(Math.toRadians(angle +270));

            gg.setColor(Color.RED);
            gg.drawLine(0, 0, (int) last_dist, 0);


        gg.dispose();
    }
}
