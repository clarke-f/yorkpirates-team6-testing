package yorkpirates.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Boat extends GameObject {
    public float rotation;

    public Boat(Texture texture, float x, float y, float width, float height, float rotation, String team) {
        super(texture, x, y, width, height, team);
        this.rotation = rotation;
    }

    @Override
    public void move(float x, float y) {
       this.rotation = (float) Math.atan2(y - this.y, x - this.x);

       if (x == -50) {
           System.out.println(rotation);
       }
    }

    @Override
    public void draw(SpriteBatch batch, float elapsedTime) {
        batch.draw(texture, x, y, 0, 0, width, height, 1f, 1f, rotation, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }
}  