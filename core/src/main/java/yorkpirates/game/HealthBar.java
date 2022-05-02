package yorkpirates.game;

import com.badlogic.gdx.graphics.Texture;

public class HealthBar extends GameObject {

    private final float startWidth;

    /**
     * Generates a health bar object within the game.
     * @param entity    The college which this bar is attached to.
     * @param frames    The animation frames, or a single sprite.
     */
    public HealthBar(GameObject entity, Texture texture) {
        super(texture, entity.x, entity.y + entity.height/2 + 2f, entity.width, 2f, entity.team);
        startWidth = entity.width;
        setMaxHealth(entity.maxHealth);
    }

    /**
     * Resizes the bar to match the new health value.
     * @param currentValue  The current health value of the attached object
     */
    public void resize(float currentValue){
        currentHealth = currentValue;
        this.width = startWidth * (currentValue/maxHealth);
    }

    /**
     * Moves the object within the x and y-axis of the game world.
     * @param x     The value to set the object to within the x-axis.
     * @param y     The value to set the object to within the y-axis.
     */
    @Override
    public void move(float x, float y){
        this.x = x;
        this.y = y;
    }
}
