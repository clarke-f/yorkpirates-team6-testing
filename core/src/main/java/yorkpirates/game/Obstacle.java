package yorkpirates.game;

import com.badlogic.gdx.graphics.Texture;

public class Obstacle extends GameObject{
    public int damage;
    public Obstacle(Texture texture, float x, float y, float width, float height, String team, int damage) {
        super(texture, x, y, width, height, team);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.damage = damage;
    }
}
