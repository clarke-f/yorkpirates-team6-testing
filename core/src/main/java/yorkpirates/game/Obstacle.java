package yorkpirates.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

public class Obstacle extends GameObject{
    public int damage;
    public Obstacle(Array<Texture> frames, float fps, float x, float y, float width, float height, String team, int damage) {
        super(frames, fps, x, y, width, height, team);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.damage = damage;
    }
}
