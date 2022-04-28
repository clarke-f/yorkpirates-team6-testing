package yorkpirates.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Rectangle extends Actor {

    private Texture texture;

    public Rectangle(float x, float y, float width, float height, Texture texture) {
        this.texture = texture;
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
    }

 
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * 0.7f);
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }
}