package yorkpirates.game;

import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;


public class Snow extends Rectangle{
    private Snow flake;
    private Boolean scaleUp = true;
    public Snow(float x, float y, float width, float height, Texture texture){
        super(x, y, width, height,texture);
        flake = this;
       
        Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            public void run(){
                System.out.println(flake.getWidth());
                
                if(flake.getWidth() >= 600){
                    scaleUp = false;
                }
                if(flake.getWidth() <= 200){
                    scaleUp = true;
                }

                if(scaleUp == true){
                    flake.setSize(flake.getWidth() * 1.05f,flake.getHeight() * 1.05f);
                }else{
                    flake.setSize(flake.getWidth() * 0.95f,flake.getHeight() * 0.95f);
                }
                
            }
        };
        t.scheduleAtFixedRate(tt, 1000, 200);
    }
}
