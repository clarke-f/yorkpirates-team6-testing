package yorkpirates.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Rectangle extends Actor {

    private Texture texture;
    private float alpha;

    public Rectangle(float x, float y, float width, float height, Texture texture, float alpha) {
        this.texture = texture;
        this.alpha = alpha;
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
    }

 
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * alpha);
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }
}