package yorkpirates.game;

import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;


public class Rain extends Rectangle{

    private Rain drop;
    private int dropSpeed;
    public Rain(float x, float y, float width, float height, Texture texture) {
        super(x, y, width, height, texture);
        drop = this;
        dropSpeed = (int)Math.floor(Math.random()*(50- 10+ 1) +10);
        Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            public void run(){
                if(drop.getY() <= -300){
                    drop.setPosition(drop.getX(), Gdx.graphics.getHeight());
                }
                drop.setPosition(drop.getX(), drop.getY() - dropSpeed);
            }
        };
        t.scheduleAtFixedRate(tt, 1000, 50);
    }
    
}