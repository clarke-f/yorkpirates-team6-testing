package yorkpirates.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

import java.util.Objects;

import static java.lang.Math.*;

public class Projectile extends GameObject{

    private final float maxDistance; // Projectile max range.
    private float distanceTravelled;
    private final GameObject origin;

    private final float dx;
    private final float dy;
    private final float projectileSpeed; // Projectile movement speed.

    private static final float projectileDamage = 20f; // Projectile damage.

    /**
     * Generates a projectile object within the game with animated frame(s) and a hit-box.
     * @param frames    The animation frames, or a single sprite.
     * @param fps       The number of frames to be displayed per second.
     * @param origin    The object which the projectile originates from.
     * @param goal_x    The x coordinate within the map the object is moving towards.
     * @param goal_y    The y coordinate within the map the object is moving towards.
     * @param team      The team of the projectile.
     */
    public Projectile(Array<Texture> frames, float fps, GameObject origin, float goal_x, float goal_y, String team) {
        super(frames, fps, origin.x, origin.y, 5f,5f,team);
        this.origin = origin;

        // Speed calculations
        if(Objects.equals(team, GameScreen.playerTeam)){
            projectileSpeed = 150f;
        }else{
            projectileSpeed = 50f;
        }

        // Movement calculations
        float changeInX = goal_x - origin.x;
        float changeInY = goal_y - origin.y;
        float scaleFactor = max(abs(changeInX), abs(changeInY));
        dx = changeInX / scaleFactor;
        dy = changeInY / scaleFactor;

        distanceTravelled = 0;
        float rangeModifier = min(origin.hitBox.width, origin.hitBox.height);
        maxDistance = rangeModifier * projectileSpeed;
    }

    /**
     * Called once per frame. Used to perform calculations such as projectile movement and collision detection.
     * @param screen    The main game screen.
     */
    public int update(GameScreen screen){

        // Movement Calculations
        float xMove = projectileSpeed * dx;
        float yMove = projectileSpeed * dy;
        distanceTravelled += projectileSpeed;
        move(xMove, yMove);

        // Hit calculations
        if (origin == screen.getPlayer()) {
            for (College c : screen.colleges) {
                if (overlaps(c.hitBox)){
                    if(!Objects.equals(team, c.team)){ // Checks if projectile and college are on the same time
                        c.takeDamage(screen, projectileDamage, team);
                    }
                    return 0;
                }
            }
        } else {
            if (overlaps(screen.getPlayer().hitBox)) {
                if(!Objects.equals(team, GameScreen.playerTeam)) { // Checks if projectile and player are on the same time
                    screen.getPlayer().takeDamage(screen, projectileDamage, team);
                }
                return 0;
            }
        }

        // Destroys after max travel distance
        if(distanceTravelled > maxDistance) return 0;
        return 1;
    }
}
