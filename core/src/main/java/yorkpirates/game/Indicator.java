package yorkpirates.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import static java.lang.Math.*;

public class Indicator extends GameObject{

    private final Player player;
    private final College college;

    private Vector2 gradient;
    private boolean visible;

    public Indicator(College college, Player player, Texture texture) {
        super(texture, player.x, player.y, texture.getWidth()/50f, texture.getHeight()/50f, college.team);
        this.player = player;
        this.college = college;
        gradient = updateGradient();
        visible = false;
        move();
    }

    /**
     * Called when drawing the Indicator.
     * @param batch         The batch to draw the player within.
     * @param elapsedTime   The current time the game has been running for.
     */
    @Override
    public void draw(SpriteBatch batch, float elapsedTime){
        // Draw the indicator if visible
        if(visible){
            float rotation = (float) Math.toDegrees(Math.atan2(gradient.y,gradient.x));
            batch.draw(texture, x - width/2, y - height/2, width/2, height/2, width, height, 1f, 1f, rotation, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
        }
    }

    /**
     * Called when updating the gradient of the Indicator.
     */
    public Vector2 updateGradient(){
        // Calculate the gradient to draw the indicator at.
        float changeInX = college.x - player.x;
        float changeInY = college.y - player.y;
        float scaleFactor = max(abs(changeInX),abs(changeInY));
        float dx = changeInX / scaleFactor;
        float dy = changeInY / scaleFactor;

        return (new Vector2(dx, dy));
    }

    /**
     * Moves the object within the x and y-axis of the game world.
     */
    void move(){
        gradient = updateGradient();
        this.x = player.x + (50 * gradient.x);
        this.y = player.y + (50 * gradient.y);
    }

    /**
     * Set whether the indicator is visible.
     * @param visible   Whether the indicator is visible.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
