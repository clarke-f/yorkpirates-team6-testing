package yorkpirates.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Objects;

import static java.lang.Math.abs;


public class College extends GameObject {

    public static int capturedCount = 0;

    private HealthBar collegeBar;
    private Indicator direction;

    private float splashTime;
    private long lastShotFired;

    public final String collegeName;
    public float scale;

    public Texture boatTexture, capturedTexture;
    public Array<GameObject> boats;

    private boolean doBloodSplash = false;

    /**
     * Generates a college object within the game with animated frame(s) and a hit-box.
     * @param x         The x coordinate within the map to initialise the object at.
     * @param y         The y coordinate within the map to initialise the object at.
     * @param name      The name of the college.
     * @param team      The team the college is on.
     */
    public College(Texture texture, float x, float y, float scale, String name, String team, Player player, Texture boatTexture, Texture capturedTexture) {
        super(texture, x, y, texture.getWidth()*scale, texture.getHeight()*scale, team);

        this.boatTexture = boatTexture;
        this.capturedTexture = capturedTexture;
        this.boats = new Array<>();
        this.scale = scale;

        splashTime = 0;
        setMaxHealth(50);
        lastShotFired = 0;
        collegeName = name;

        if(Objects.equals(team, GameScreen.playerTeam)){
            if(Objects.equals(name, "Home")){
                direction = new Indicator(this, player, new Texture("homeArrow.png"));
            }else{
                direction = new Indicator(this, player, new Texture("allyArrow.png"));
            }
            collegeBar = new HealthBar(this, new Texture("allyHealthBar.png"));

        }else{
            collegeBar = new HealthBar(this, new Texture("enemyHealthBar.png"));
            direction = new Indicator(this, player, new Texture("questArrow.png"));
        }
    }

    /**
     * Called once per frame. Used to perform calculations such as collision.
     * @param screen    The main game screen.
     */
    public int update(GameScreen screen){
        direction.move();
        float playerX = screen.getPlayer().x;
        float playerY = screen.getPlayer().y;
        boolean nearPlayer = abs(this.x - playerX) < (Gdx.graphics.getWidth()/15f) && abs(this.y - playerY) < (Gdx.graphics.getHeight()/10f);

        if(nearPlayer || screen.isPaused()){
            direction.setVisible(false);

            if(!Objects.equals(team, GameScreen.playerTeam)) { // Checks if the college is an enemy of the player
                // How often the college can shoot.
                int shootFrequency = 1000;
                if (TimeUtils.timeSinceMillis(lastShotFired) > shootFrequency){
                    lastShotFired = TimeUtils.millis();
                    screen.projectiles.add(new Projectile(new Texture("tempProjectile.png"), this, playerX, playerY, team));
                }
            }else if(Objects.equals(collegeName, "Home")){
                boolean victory = true;
                for (College c : screen.colleges) {
                    if(!Objects.equals(c.team, GameScreen.playerTeam)){
                        victory = false;
                    }
                }
                if(victory){
                    screen.getHUD().setGameEndable();
                    if(Gdx.input.isKeyPressed(Input.Keys.ENTER)) screen.gameEnd(true);
                }
            }
        }else{
            direction.setVisible(true);
        }

        if(doBloodSplash){
            if(splashTime > 1){
                doBloodSplash = false;
                splashTime = 0;
            }else{
                splashTime += 1;
            }
        }

        return 0;
    }

    /**
     * Called when a projectile hits the college.
     * @param screen            The main game screen.
     * @param damage            The damage dealt by the projectile.
     * @param projectileTeam    The team of the projectile.
     */
    @Override
    public void takeDamage(GameScreen screen, float damage, String projectileTeam){
        currentHealth -= damage;
        doBloodSplash = true;

        if(currentHealth > 0){
            collegeBar.resize(currentHealth);
        }else{
            if(!Objects.equals(team, GameScreen.playerTeam)) { // Checks if the college is an enemy of the player
                // College taken over
                int pointsGained = 50;
                screen.points.Add(pointsGained);
                int lootGained = 50;
                screen.loot.Add(lootGained);
                
                for (int i=0; i < screen.shops.size; i++){
                    if(screen.shops.get(i).name == collegeName){
                        screen.shops.get(i).activate();
                    }
                }

                changeImage(capturedTexture);
                collegeBar.changeImage(new Texture("allyHealthBar.png"));
                direction.changeImage(new Texture("allyArrow.png"));

                currentHealth = maxHealth;
                collegeBar.resize(currentHealth);
                College.capturedCount++;

                //remove mortars
                if(team == "Langwith"){
                    for(Weather w : GameScreen.weathers){
                        if(w.xpos == 1380){
                            GameScreen.weathers.remove(w);
                            break;
                        }
                    }
                }else if(team == "Alcuin"){
                    for(Weather w : GameScreen.weathers){
                        if(w.xpos == 1435){
                            GameScreen.weathers.remove(w);
                            break;
                        }
                    }
                }

                //set to players team
                team = GameScreen.playerTeam;

                
            }
        }
    }

    /**
     * Called when drawing the object.
     * @param batch         The batch to draw the object within.
     * @param elapsedTime   The current time the game has been running for.
     */
    @Override
    public void draw(SpriteBatch batch, float elapsedTime){
        // if(doBloodSplash)   batch.setShader(null); // Set red shader to the batch
        // else                batch.setShader(null);

        // Draw college
        batch.setShader(null);
        batch.draw(texture, x - width/2, y - height/2, width, height);

        // TODO something about boat rotations
        // Draw boats before college so under
        for(int i = 0; i < boats.size; i++){
            GameObject boat = boats.get(i);
            batch.draw(boatTexture, boat.x+boat.height, boat.y, 0,0, boat.width, boat.height, 1f, 1f, 0, 0, 0, boatTexture.getWidth(), boatTexture.getHeight(), false, false);
        }

        collegeBar.draw(batch, 0);
        direction.draw(batch,0);
    }

    /**
     * Add a boat to this college.
     * @param x The x position of the new boat relative to the college.
     * @param y The y position of the new boat relative to the college.
     */
    public void addBoat(float x, float y, float rotation){
        boats.add(new GameObject(boatTexture, this.x+x, this.y+y, 25, 12, team));
        // boatRotations.add(rotation);
    }

}