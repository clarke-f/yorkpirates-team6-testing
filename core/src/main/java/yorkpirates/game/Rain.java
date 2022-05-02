package yorkpirates.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;


public class Rain extends Rectangle {
    private int dropSpeed;

    public Rain(float x, float y, float width, float height, Texture texture, float alpha) {
        super(x, y, width, height, texture,alpha);
        this.dropSpeed = (int) Math.floor(Math.random() * (4 - 1 + 1) + 1);
    } 

    @Override
    public void act(float delta) {
        if(getY() <= (Gdx.graphics.getHeight()/2)-200){
            setPosition(getX(),(Gdx.graphics.getHeight()/2)+200);
        }
        setPosition(getX(), getY() - dropSpeed);
    }
}