package cargame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CarGame extends JFrame implements KeyListener {

    private static final int FPS = 60;
    private static final int NUMBER_OF_CARS = 100;
    public static final float CAR_WIDTH = 5;
    public static final float CAR_LENGTH = 9;
    public long TICKS_PER_ROUND = 43;
    public static final float NUMBER_OF_WORLDS = 3;
    private static final int SAVE_INT = 100;
    private BufferStrategy BS;
    private  World world;
    private List<CarWithNeuralNetwork> cars;
    private boolean running = true;
    private boolean training = true;
    private boolean display = false;
    private boolean timing = true;
    private int fps = 0;
    private long count = 0;
    private long generation = 0;
    private float avgFitness = 0;
    private float topFitness = 0;
    private Random rand = new Random(System.nanoTime());
    private final boolean carsSave = false;
    private final FitnessDisplay fitnessDisplay = new FitnessDisplay(700,100);

    private int worldNumber;
    private boolean updateWorld;

    public CarGame(String worldName){

        world = new World(worldName);
        initWindow();
        cars = new ArrayList<CarWithNeuralNetwork>();
        for (int i = 0; i < NUMBER_OF_CARS; i++)
            cars.add(new CarWithNeuralNetwork(CAR_LENGTH,CAR_WIDTH,world.getSpawnx(),world.getSpawny()));
    }
    public CarGame(String worldName, String carsToLoad){
        world = new World(worldName);
        initWindow();
        try {
            FileInputStream inStream = new FileInputStream("cars/" + carsToLoad + ".ser");
            ObjectInputStream objectInStream = new ObjectInputStream(inStream);
            cars = (ArrayList<CarWithNeuralNetwork>) objectInStream.readObject();
            if(cars == null){
                for (int i = 0; i < NUMBER_OF_CARS; i++)
                    this.cars.add(new CarWithNeuralNetwork(CAR_LENGTH,CAR_WIDTH,world.getSpawnx(),world.getSpawny()));
            }



        } catch (Exception e) {
            if(cars == null) {
                this.cars = new ArrayList<CarWithNeuralNetwork>();
            }
            for (int i = 0; i < NUMBER_OF_CARS; i++)
                this.cars.add(new CarWithNeuralNetwork(CAR_LENGTH,CAR_WIDTH,world.getSpawnx(),world.getSpawny()));
        }
    }
    private void initWindow(){

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        this.addKeyListener(this);
        this.setSize(world.getWidth(),world.getHeight());
        this.createBufferStrategy(2);
        BS = this.getBufferStrategy();
    }

    public void drawStuff(){
        Graphics2D g =  (Graphics2D) BS.getDrawGraphics();


        g.translate(0,20);
        if(display) {
            world.draw(g);
            cars.forEach(c -> c.draw(g));

            g.translate(0,world.getHeight());
        }

        Graphics2D gg = (Graphics2D) g.create();
        gg.setColor(Color.black);
        gg.fillRect(0,0,400,400);
        gg.setColor(Color.WHITE);
        gg.drawString(Integer.toString(fps),20,50);
        gg.drawString(count/100 + "/" + (TICKS_PER_ROUND),20,70);
        gg.drawString(Integer.toString(cars.size()),20,90);
        gg.drawString(training?"t":"",10,50);
        gg.drawString("g: " + generation,100,50);
        gg.drawString("f: " + (int)avgFitness,100,70);
        gg.drawString("f_top:" + topFitness, 100,90);
        gg.drawString("w: " + worldNumber,100,110);
        gg.dispose();
        gg = (Graphics2D) g.create();
        gg.translate(300,0);
        fitnessDisplay.draw(gg);
        gg.dispose();
        BS.show();

    }

    public void start(){

        long startTime;

        while(running){
            synchronized (this){
                startTime = System.currentTimeMillis();
                count ++;



                if(training){

                    if(count/100 > TICKS_PER_ROUND){
                        avgFitness = 0;
                        topFitness = 0;
                        cars.forEach(c->{
                            float dist = (float) Math.sqrt( Math.pow(world.getSpawnx() - c.getX(),2) +
                                    Math.pow(world.getSpawny() - c.getY(),2));

                            //FITNESS FUNKTION
                            c.setFitness(((c.dist*0.5f)+dist) * (c.hit_wall?1f:1f));
                            avgFitness += c.getFitness();
                            if(c.getFitness() > topFitness){
                                topFitness = c.getFitness();
                            }
                            c.reset();
                            c.setPosition(world.getSpawnx(),world.getSpawny());
                        });
                        avgFitness /= cars.size();
                        fitnessDisplay.addValue(avgFitness);
                        if(updateWorld){
                            updateWorld = false;
                            if(worldNumber > 0 && worldNumber <= NUMBER_OF_WORLDS)
                                world = new World("world" + worldNumber + ".txt");
                        }


                        float prob = 1f;
                        cars.sort((c2,c1)-> ((int)c1.getFitness() - (int)c2.getFitness()));
                        for(int i = 0;i<cars.size() ; i++){
                            if(i != 0) {
                                prob = 1f-(float)i/(float)NUMBER_OF_CARS;

                            }else{
                                prob = 1;
                            }

                             if(prob < rand.nextDouble()){
                                cars.remove(i);
                            }
                            if(cars.size()<=(NUMBER_OF_CARS/2)+1)break;
                        }
                        int j = 0;
                        int tempSize = cars.size();
                        for(int i = NUMBER_OF_CARS - cars.size(); i >= 0; i--){
                            cars.add(cars.get((i%10)%cars.size()).combine(cars.get(((Math.abs(rand.nextInt())))%tempSize)));
                            j++;
                        }


                        count = 0;
                        generation ++;

                    }

                }
                cars.stream().parallel().forEach(c -> {if(!c.hit_wall){c.update(world);}});


                drawStuff();


                int elapsed_time = (int) (System.currentTimeMillis() - startTime);
                fps = (int)(1000.0/(float)elapsed_time);
                if(!timing) {
                    try {

                        if (elapsed_time < 0) elapsed_time = 0;
                        if (elapsed_time > 1000/FPS) elapsed_time = 1000/FPS;
                        Thread.sleep((1000 / FPS) - elapsed_time);
                    } catch (InterruptedException ie) {

                    }
                }
            }

        }
    }


    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

        switch (keyEvent.getKeyChar()){
            case 't':
                training = !training;
                break;
            case 'f':
                timing = !timing;
                break;
            case 'd':
                display = !display;
                break;
            case 's':
                saveCars();
                break;
            case 'u':
                TICKS_PER_ROUND ++;
                break;
            case 'j':
                TICKS_PER_ROUND--;
                break;
            case 'i':
                worldNumber++;
                updateWorld = true;
                break;
            case 'k':
                worldNumber--;
                updateWorld = true;
                break;
        }
    }
    public void saveCars() {
        synchronized (this){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy:MM:dd_HH_mm");
            LocalDateTime now = LocalDateTime.now();

            FileOutputStream fileOut =
                    null;
            try {
                int count = 0;
                String  fileName = (new File(".").getAbsolutePath()) + "cars/cars"+count+"_g" + this.generation + ".ser";
                StringBuilder sb = new StringBuilder(fileName);
                sb.delete(sb.indexOf("."),sb.indexOf(".")+1);
                fileName = sb.toString();
                File file = new File(fileName);
                while(file.exists()){
                    count++;
                    fileName = (new File(".").getAbsolutePath()) + "cars/cars"+count+"_g" + this.generation + ".ser";
                    sb = new StringBuilder(fileName);
                    sb.delete(sb.indexOf("."),sb.indexOf(".")+1);
                    fileName = sb.toString();

                    file = new File(fileName);
                }
                System.out.println(fileName);

                if(file.createNewFile()){
                    fileOut = new FileOutputStream(file);
                }else{
                    System.out.println("could not create file!"
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ObjectOutputStream out = null;
            try {
                if(fileOut != null){
                out = new ObjectOutputStream(fileOut);
                out.writeObject(cars);
                out.close();
                fileOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
