package yorkpirates.game;

import com.badlogic.gdx.graphics.Texture;

public class Snow extends Rectangle {
    private Boolean scaleUp = true;

    public Snow(float x, float y, float width, float height, Texture texture,float alpha){
        super(x, y, width, height,texture,alpha);
    }

    @Override
    public void act(float delta) {
        if(getWidth() >= 200){
            scaleUp = false;
        }
        if(getWidth() <= 40){
            scaleUp = true;
        }

        if(scaleUp == true){
            setSize(getWidth() * 1.05f, getHeight() * 1.05f);
            // flake.setPosition(flake.getX() * 1.05f,flake.getY() * 1.05f);
        }else{
            setSize(getWidth() * 0.95f, getHeight() * 0.95f);
            // flake.setPosition(flake.getX() * 0.95f,flake.getY() * 0.95f);
        }
    }
}
