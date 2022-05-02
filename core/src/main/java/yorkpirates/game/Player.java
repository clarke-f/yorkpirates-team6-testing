package yorkpirates.game;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class Player extends GameObject {

    // Player constants
    public float SPEED = 70f;
    public float playerProjectileDamage = 20;
    public float projectileShootCooldown = 0.1f;
    private static final int POINT_FREQUENCY = 1000; // How often the player gains points by moving.
    private static final float CAMERA_SLACK = 0.1f; // What percentage of the screen the player can move in before the camera follows.
     // Player movement speed.
    private static final int HEALTH = 200;

    // Movement calculation values
    private int previousDirectionX;
    private int previousDirectionY;
    private float distance;
    private long lastMovementScore;

    private HealthBar playerHealth;
    private float splashTime;
    private long timeLastHit;
    private boolean doBloodSplash = false;

    //weather
    public Label weatherLabel;
    private WeatherType currentWeatherType= WeatherType.NONE;

    /**
     * Generates a generic object within the game with animated frame(s) and a hit-box.
     * @param frames    The animation frames, or a single sprite.
     * @param fps       The number of frames to be displayed per second.
     * @param x         The x coordinate within the map to initialise the object at.
     * @param y         The y coordinate within the map to initialise the object at.
     * @param width     The size of the object in the x-axis.
     * @param height    The size of the object in the y-axis.
     * @param team      The team the player is on.
     */
    public Player(Array<Texture> frames, float fps, float x, float y, float width, float height, String team,Label weatherLabel){
        super(frames, fps, x, y, width, height, team);
        lastMovementScore = 0;
        splashTime = 0;
        this.weatherLabel = weatherLabel;
        // Generate health
        Array<Texture> sprites = new Array<>();
        sprites.add(new Texture("allyHealthBar.png"));
        setMaxHealth(HEALTH);
        playerHealth = new HealthBar(this,sprites);
    }

    /**
     * Called once per frame. Used to perform calculations such as player/camera movement.
     * @param screen    The main game screen.
     * @param camera    The player camera.
     */
    public void update(GameScreen screen, OrthographicCamera camera){
        Vector2 oldPos = new Vector2(x,y); // Stored for next-frame calculations

        // Get input movement
        int horizontal = ((Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) ? 1 : 0)
                - ((Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) ? 1 : 0);
        int vertical = ((Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) ? 1 : 0)
                - ((Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) ? 1 : 0);

        // Calculate collision && movement
        if (horizontal != 0 || vertical != 0){
            move(SPEED *horizontal, SPEED *vertical);
            previousDirectionX = horizontal;
            previousDirectionY = vertical;
            if (safeMove(screen.getMain().edges)) {
                if (TimeUtils.timeSinceMillis(lastMovementScore) > POINT_FREQUENCY) {
                    lastMovementScore = TimeUtils.millis();
                    screen.points.Add(1);
                }
            } else {    // Collision
                Vector2 newPos = new Vector2(x, y);
                x = oldPos.x;
                if (!safeMove(screen.getMain().edges)) {
                    x = newPos.x;
                    y = oldPos.y;
                    if (!safeMove(screen.getMain().edges)) {
                        x = oldPos.x;
                    }
                }
            }
        }else{
            HUD.speedLbl.setText("0mph");
        }
        updateHitboxPos();
        // Track distance travelled
        distance += Math.pow((Math.pow((x - oldPos.x),2f) + Math.pow((y - oldPos.y),2f)),0.5f)/10f;

        // Camera Calculations
        ProcessCamera(screen, camera);

        // Blood splash calculations
        if(doBloodSplash){
            if(splashTime > 1){
                doBloodSplash = false;
                splashTime = 0;
            }else{
                splashTime += 1;
            }
        }

        if (TimeUtils.timeSinceMillis(timeLastHit) > 10000){
            currentHealth += 0.03;
            if(currentHealth > maxHealth) currentHealth = maxHealth;
            playerHealth.resize(currentHealth);
        }

        //collide with obstacle
        for(Iterator<Obstacle> it = GameScreen.obstacles.iterator();it.hasNext();){
            Obstacle o = it.next();
            if(overlaps(o.hitBox)){
                
                if(o instanceof Barrel){
                    Barrel b = (Barrel)o;
                    if(b.type == BarrelType.BROWN){
                        takeDamage(screen, b.damage, "ENEMY");
                    }else{
                        int randMoney = (int)Math.floor(6 - 2 + 1) + 2;
                        screen.loot.Add(randMoney);
                    }
                    it.remove();
                }else{
                    takeDamage(screen, o.damage, "ENEMY");
                    move(-500, -500);
                }
            }
        }
        
    }

    /**
     *  Calculate if the current player position is safe to be in.
     * @param edges A 2d array containing safe/unsafe positions to be in.
     * @return      If the current position is safe.
     */
    private Boolean safeMove(Array<Array<Boolean>> edges){
        return (
                        edges.get((int)((y+height/2)/16)).get((int)((x+width/2)/16)) &&
                        edges.get((int)((y+height/2)/16)).get((int)((x-width/2)/16)) &&
                        edges.get((int)((y-height/2)/16)).get((int)((x+width/2)/16)) &&
                        edges.get((int)((y-height/2)/16)).get((int)((x-width/2)/16))
        );
    }

    /**
     * Moves the player within the x and y-axis of the game world.
     * @param x     The amount to move the object within the x-axis.
     * @param y     The amount to move the object within the y-axis.
     */
    @Override
    public void move(float x, float y){
        this.x += x * Gdx.graphics.getDeltaTime();
        this.y += y * Gdx.graphics.getDeltaTime();
        HUD.speedLbl.setText(SPEED + "mph");
        playerHealth.move(this.x, this.y + height/2 + 2f); // Healthbar moves with player
    }
    public void checkForWeather(){
        // System.out.println("checking..");

        WeatherType type = Weather.WhichWeather((int)this.x, (int)this.y, GameScreen.weathers);
        // HUD.UpdateWeatherLabel(this.x + " | " + this.y,weatherLabel);
        //only check if its different weather
        if(currentWeatherType != type){
            Weather.ResetPlayerDisadvantage(this);
            if(type == WeatherType.NONE){
                //this is to test the position
                
                HUD.UpdateWeatherLabel("",weatherLabel);
                
            }else{
                //update weather label to show user which weather event they're in 
                HUD.UpdateWeatherLabel(Weather.getWeatherLabelText(type),weatherLabel);
                //need to disadvantage the player in some way
                if(type == WeatherType.RAIN){
                    Weather.DisadvantagePlayer(this,type,GameScreen.rains);
                }else if (type == WeatherType.SNOW){
                    Weather.DisadvantagePlayer(this,type,GameScreen.snows);
                }else if (type == WeatherType.STORM){
                    Weather.DisadvantagePlayer(this,type,GameScreen.storms);
                }else if(type == WeatherType.JAMESFURY){
                    Weather.DisadvantagePlayer(this, type, GameScreen.jamesa);
                }
            }
        }
        currentWeatherType = type;
    }
    
    /**
     * Called when a projectile hits the college.
     * @param screen            The main game screen.
     * @param damage            The damage dealt by the projectile.
     * @param projectileTeam    The team of the projectile.
     */
    @Override
    public void takeDamage(GameScreen screen, float damage, String projectileTeam){
        timeLastHit = TimeUtils.millis();
        currentHealth -= damage;
        doBloodSplash = true;

        // Health-bar reduction
        playerHealth.resize(currentHealth);
        if(currentHealth <= 0){
           screen.gameEnd(false);
        }
    }

    /**
     * Called after update(), calculates whether the camera should follow the player and passes it to the game screen.
     * @param screen    The main game screen.
     * @param camera    The player camera.
     */
    private void ProcessCamera(GameScreen screen, OrthographicCamera camera) {
        Vector2 camDiff = new Vector2(x - camera.position.x, y - camera.position.y);
        screen.toggleFollowPlayer(Math.abs(camDiff.x) > camera.viewportWidth / 2 * CAMERA_SLACK || Math.abs(camDiff.y) > camera.viewportWidth / 2 * CAMERA_SLACK);
    }

    /**
     * Called when drawing the player.
     * @param batch         The batch to draw the player within.
     * @param elapsedTime   The current time the game has been running for.
     */
    @Override
    public void draw(SpriteBatch batch, float elapsedTime){
        // Generates the sprite
        Texture frame = anim.getKeyFrame((currentHealth/maxHealth > 0.66f) ? 0 : ((currentHealth/maxHealth > 0.33f) ? 2 : 1), true);
        if(doBloodSplash){
            // batch.setShader(shader); // Set our grey-out shader to the batch
        } float rotation = (float) Math.toDegrees(Math.atan2(previousDirectionY, previousDirectionX));

        // Draws sprite and health-bar
        batch.draw(frame, x - width/2, y - height/2, width/2, height/2, width, height, 1f, 1f, rotation, 0, 0, frame.getWidth(), frame.getHeight(), false, false);
        batch.setShader(null);
    }

    public void drawHealthBar(SpriteBatch batch){
        if(!(playerHealth == null)) playerHealth.draw(batch, 0);
    }

    public float getDistance() {
        return distance;
    }
}
