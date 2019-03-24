import cargame.CarGame;


public class NetworkApp{


    public static void main(String[] args){
        CarGame game;
        if(args.length == 1){
            game = new CarGame("world3.txt",args[0]);
        }else {
             game = new CarGame("world3.txt");
        }
        game.start();

    }
}
