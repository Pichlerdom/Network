package cargame;

import java.io.Serializable;
import java.util.Random;

public class Car implements Serializable {
    private static final float DELTA_A = 0.03f;
    private static final float DELTA_R = 3f;
    private static final float V_MAX = 0.5f;
    private float length;
    private float width;
    private float x,y;
    private float vx,vy;
    private float v;
    private float angle;
    private Random rand = new Random(System.nanoTime());
    public boolean hit_wall = false;
    public float dist = 0;
    public Car(float length,float width,float x,float y){
        this.length = length;
        this.width = width;
        this.x = x;
        this.y = y;
        v = 0;
        angle = rand.nextFloat() * 360;

    }
    //between 1 and -1
    public void update(float gas, float rotation, World world){
        if(hit_wall){return;}
        angle += (rotation)*DELTA_R;
        if(angle >= 360){angle = 0;}
        //angle = 180*rotation;
        float dx = gas * DELTA_A * (float) Math.cos(Math.toRadians(angle));
        float dy = gas * DELTA_A * (float) Math.sin(Math.toRadians(angle));
        vx += dx;
        if(vx >= 3) vx = 3;
        vy += dy;
        if(vy >= 3) vy =3;
        v = (float) Math.sqrt(Math.pow(vx,2) + Math.pow(vy,2));
        if(v > V_MAX){
            vx = (V_MAX/v) * vx;
            vy = (V_MAX/v) * vy;
        }
        if(world.canDriveHere((int)(x + dx),(int)y)) {
            x += vx;
            dist += vx;
        }else {
            hit_wall = true;
            vx *= -0.5;
            x += vx;
        }
        if(world.canDriveHere((int)(x),(int)(y + dy))) {
            y += vy;
            dist += vy;
        }else {
            hit_wall = true;
            vy *= -0.5;
            y += vy;
        }


    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getV() {
        return v;
    }

    public float getAngle() {
        return angle;
    }

    public float getLength() {
        return length;
    }

    public float getWidth() {
        return width;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public void reset(){
        this.v = 0;
        this.angle = rand.nextFloat() * 360;
        this.angle = 90;
        this.vx= 0;
        this.vy= 0;
        this.hit_wall = false;
    }

    public float getVx() {
        return vx;
    }

    public float getVy() {
        return vy;
    }
}
