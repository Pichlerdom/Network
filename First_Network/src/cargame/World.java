package cargame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

public class World  implements Drawable{
    private boolean[][] world;
    private float spawnx;
    private float spawny;
    private int worldBlockSize;
    private BufferedImage worldImg;

    public World(String worldName){

        BufferedReader in;
        String[] spawn = null;
        try {
            URL path = World.class.getResource("worlds/"+ worldName);
            System.out.println("loading World " + path.toString());
            in = new BufferedReader(new FileReader(path.getFile()));
            String worldImgName = in.readLine();
            String spawnPoints = in .readLine();
            this.worldBlockSize = Integer.valueOf(in.readLine());
            spawn = spawnPoints.split(",");

            //load bitmap
            URL url = World.class.getResource("worlds/" + worldImgName);
            worldImg = ImageIO.read(url);

            world = new boolean[worldImg.getWidth()][worldImg.getHeight()];
            for(int x = 0; x < world.length; x++){
                for (int y = 0; y < world[0].length;y++){
                    world[x][y] = (worldImg.getRGB(x,y) & 0xff0000) > 128?true:false;
                }
            }
        }catch (Exception e){
            System.out.println("World not found!");
            world = null;
        }
        if(spawn == null) {
            spawnx = 0;
            spawny = 0;
        }
        else{
            spawnx = Integer.valueOf(spawn[0]);
            spawny = Integer.valueOf(spawn[1]);
        }
    }


    public float getSpawnx() { return spawnx * worldBlockSize;
    }

    public float getSpawny() {
        return spawny * worldBlockSize;
    }

    public float getWorldBlockSize() {
        return worldBlockSize;
    }

    public int getHeight(){return world[0].length * worldBlockSize;}
    public int getWidth(){return world.length * worldBlockSize;}
    public boolean canDriveHere(int x, int y){
        if(x/worldBlockSize >= world.length || y/worldBlockSize >= world[0].length || x < 0 || y < 0) return false;
        return world[x/worldBlockSize][y/worldBlockSize];
    }


    @Override
    public void draw(Graphics2D g) {
        Graphics2D gg =(Graphics2D) g.create();
        if(worldImg != null){
            gg.scale(worldBlockSize,worldBlockSize);
            gg.drawImage(worldImg,null,0,0);
        }
  /*      for(int x = 0; x < world.length; x++){
            for(int y = 0; y < world[0].length;y++){

                if(!world[x][y]){g.setColor(Color.BLACK);}
                else{g.setColor(Color.GREEN);}
                g.fillRect(x* worldBlockSize,
                            y * worldBlockSize,
                        x * worldBlockSize+worldBlockSize,
                        y * worldBlockSize+worldBlockSize);
            }
        }*/
        gg.dispose();
    }
}
