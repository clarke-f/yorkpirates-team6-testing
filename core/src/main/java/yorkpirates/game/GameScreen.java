package yorkpirates.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import static java.lang.Math.abs;

public class GameScreen extends ScreenAdapter {
    // Team name constants
    public static final String playerTeam = "PLAYER";
    public static final String enemyTeam = "ENEMY";

    // Score managers
    public ScoreManager points;
    public ScoreManager loot;

    // Colleges
    public Array<College> colleges;
    public Array<Projectile> projectiles;

    // Shops
    public Array<Shop> shops;
    public boolean shopOpened;

    // Sound
    public Music music;

    // Main classes
    private final YorkPirates game;

    // Player
    private final Player player;
    private String playerName;
    private Vector3 followPos;
    private boolean followPlayer = false;

    // UI & Camera
    private final HUD gameHUD;
    private final SpriteBatch HUDBatch;
    private final OrthographicCamera HUDCam;
    private final FitViewport viewport;

    // Tilemap
    private final TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer tiledMapRenderer;

    // Trackers
    private float elapsedTime = 0;
    private boolean isPaused = false;
    private float lastPause = 0;

    /**
     * Initialises the main game screen, as well as relevant entities and data.
     * @param game  Passes in the base game class for reference.
     */
    public GameScreen(YorkPirates game){
        this.game = game;
        playerName = "Player";

        // Initialise points and loot managers
        points = new ScoreManager();
        loot = new ScoreManager();

        // Initialise HUD
        HUDBatch = new SpriteBatch();
        HUDCam = new OrthographicCamera();
        HUDCam.setToOrtho(false, game.camera.viewportWidth, game.camera.viewportHeight);
        viewport = new FitViewport( Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), HUDCam);
        gameHUD =  new HUD(this);

        //initialise sound
        music = Gdx.audio.newMusic(Gdx.files.internal("Pirate1_Theme1.ogg"));
        music.setLooping(true);
        music.setVolume(0);
        music.play();

        // Initialise sprites array to be used generating GameObjects
        Array<Texture> sprites = new Array<>();

        // Initialise player
        sprites.add(new Texture("ship1.png"), new Texture("ship2.png"), new Texture("ship3.png"));
        player = new Player(sprites, 2, 821, 489, 32, 16, playerTeam);
        sprites.clear();
        followPos = new Vector3(player.x, player.y, 0f);
        game.camera.position.lerp(new Vector3(760, 510, 0f), 1f);

        // Initialise tilemap
        tiledMap = new TmxMapLoader().load("FINAL_MAP.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // Initialise colleges
        College.capturedCount = 0;
        colleges = new Array<>();
        College newCollege;
        Array<Texture> collegeSprites = new Array<>();

        // Add alcuin
        collegeSprites.add( new Texture("alcuin.png"),
                            new Texture("alcuin_2.png"));
        newCollege = new College(collegeSprites, 1492, 672, 0.4f,"Alcuin", enemyTeam, player, "alcuin_boat.png");
        newCollege.addBoat(30, -20, -60);
        newCollege.addBoat(-50, -40, -150);
        newCollege.addBoat(-40, -70, 0);
        colleges.add(newCollege);
        collegeSprites.clear();

        // Add derwent
        collegeSprites.add( new Texture("derwent.png"),
                            new Texture("derwent_2.png"));
        newCollege = (new College(collegeSprites, 1815, 2105, 0.8f,"Derwent", enemyTeam, player, "derwent_boat.png"));
        newCollege.addBoat(-70, -20, 60);
        newCollege.addBoat(-70, -60, 70);
        colleges.add(newCollege);
        collegeSprites.clear();

        // Add langwith
        collegeSprites.add( new Texture("langwith.png"),
                            new Texture("langwith_2.png"));
        newCollege = (new College(collegeSprites, 1300, 1530, 1.0f,"Langwith", enemyTeam, player, "langwith_boat.png"));
        newCollege.addBoat(-150, -50, 60);
        newCollege.addBoat(-120, -10, -60);
        newCollege.addBoat(-10, -40, 230);
        newCollege.addBoat(140, 10, 300);
        newCollege.addBoat(200, 35, 135);
        colleges.add(newCollege);
        collegeSprites.clear();

        // Add goodricke
        collegeSprites.add( new Texture("goodricke.png"));
        colleges.add(new College(collegeSprites, 700, 525, 0.7f,"Home",playerTeam,player, "ship1.png"));

        // Initialise projectiles array to be used storing live projectiles
        projectiles = new Array<>();

        // Initialise shops
        Array<Texture> shopImages = new Array<>();
        shops = new Array<>();
        Shop newShop;
        shopOpened = false;

        // alcuin
        shopImages.add(new Texture("shop.png"));
        newShop = new Shop(shopImages, 1510, 620, 0.35f, false, "Alcuin");
        shops.add(newShop);
        // derwent
        newShop = new Shop(shopImages, 1750, 1955, 0.6f, false, "Derwent");
        shops.add(newShop);
        //langwith
        newShop  = new Shop(shopImages, 1290, 1400, 0.8f, false, "Langwith");
        shops.add(newShop);
    }

    /**
     * Is called once every frame. Runs update(), renders the game and then the HUD.
     * @param delta The time passed since the previously rendered frame.
     */
    @Override
    public void render(float delta){
        // Only update if not paused
        if(!isPaused) {
            elapsedTime += delta;
            update();
        }
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
        ScreenUtils.clear(0.1f, 0.6f, 0.6f, 1.0f);

        // Gameplay drawing batch
        game.batch.begin();
        tiledMapRenderer.setView(game.camera); // Draw map first so behind everything
        tiledMapRenderer.render();

        // Draw Projectiles
        for(int i = 0; i < projectiles.size; i++) {
            projectiles.get(i).draw(game.batch, 0);
        }

        // Draw Player, Player Health and Player Name
        if(!isPaused) {
            player.drawHealthBar(game.batch);
            player.draw(game.batch, elapsedTime);
            HUDBatch.begin();
            Vector3 pos = game.camera.project(new Vector3(player.x, player.y, 0f));
            game.font.draw(HUDBatch, playerName, pos.x, pos.y + 170f, 1f, Align.center, true);
            HUDBatch.end();
        }

        // Draw Colleges
        for(int i = 0; i < colleges.size; i++) {
            colleges.get(i).draw(game.batch, 0);
        }

        // Draw Shops
        for (int i = 0; i < shops.size; i++){
            if (shops.get(i).activated){
                shops.get(i).draw(game.batch, 0);
            }
        }
        game.batch.end();

        // Draw HUD
        HUDBatch.setProjectionMatrix(HUDCam.combined);
        if(!isPaused) {
            // Draw UI
            gameHUD.renderStage(this);
            HUDCam.update();
        }
    }

    /**
     * Is called once every frame. Used for game calculations that take place before rendering.
     */
    private void update(){
        // Call updates for all relevant objects
        player.update(this, game.camera);
        for(int i = 0; i < colleges.size; i++) {
            colleges.get(i).update(this);
        }

        // Check for projectile creation, then call projectile update
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
            Vector3 mouseVector = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
            Vector3 mousePos = game.camera.unproject(mouseVector);

            Array<Texture> sprites = new Array<>();
            sprites.add(new Texture("tempProjectile.png"));
            projectiles.add(new Projectile(sprites, 0, player, mousePos.x, mousePos.y, playerTeam));
            gameHUD.endTutorial();
        } for(int i = projectiles.size - 1; i >= 0; i--) {
            projectiles.get(i).update(this);
        }

        // Camera calculations based on player movement
        if(followPlayer) followPos = new Vector3(player.x, player.y, 0);
        if(Math.abs(game.camera.position.x - followPos.x) > 1f || Math.abs(game.camera.position.y - followPos.y) > 1f){
            game.camera.position.slerp(followPos, 0.1f);
        }

        // Call to open shop window
        for(int i=0; i < shops.size; i++){
            if(Gdx.input.isKeyJustPressed(Input.Keys.E)
            && (abs(shops.get(i).x - player.x)) < (Gdx.graphics.getWidth()/15f)
            && (abs(shops.get(i).y - player.y)) < (Gdx.graphics.getHeight()/10f)
            && shops.get(i).activated){
                if (shopOpened){
                    shopOpened = false;
                }
                else{
                    shopOpened = true;
                }
            }
        }

        // Player upgrades
        if (shopOpened){
            if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
                shops.get(0).upgrade(player, loot, "damage");
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)){
                shops.get(0).upgrade(player, loot, "speed");
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)){
                shops.get(0).upgrade(player, loot, "armour");
            }
        }

        // Call to pause the game
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && elapsedTime - lastPause > 0.1f){
            gamePause();
        }
    }

    /**
     * Called to switch from the current screen to the pause screen, while retaining the current screen's information.
     */
    public void gamePause(){
        isPaused = true;
        game.setScreen(new PauseScreen(game,this));
    }

    /**
     * Called to switch from the current screen to the end screen.
     * @param win   The boolean determining the win state of the game.
     */
    public void gameEnd(boolean win){
        game.setScreen(new EndScreen(game, this, win));
    }

    /**
     * Called to switch from the current screen to the title screen.
     */
    public void gameReset(){
        game.setScreen(new TitleScreen(game));
    }

    /**
     * Used to encapsulate elapsedTime.
     * @return  Time since the current session started.
     */
    public float getElapsedTime() { return elapsedTime; }

    /**
     * Used to toggle whether the camera follows the player.
     * @param follow  Whether the camera will follow the player.
     */
    public void toggleFollowPlayer(boolean follow) { this.followPlayer = follow; }

    /**
     * Get the player's name for the current session.
     * @return  Player's name.
     */
    public String getPlayerName() { return playerName; }

    /**
     * Set the player's name.
     * @param playerName    Chosen player name.
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        gameHUD.updateName(this);
    }

    /**
     * Get the player.
     * @return  The player.
     */
    public Player getPlayer() { return player; }

    /**
     * Get the main game class.
     * @return  The main game class.
     */
    public YorkPirates getMain() { return game; }

    /**
     * Get the game's HUD.
     * @return  The HUD.
     */
    public HUD getHUD() { return gameHUD; }

    /**
     * Set whether the game is paused or not.
     * @param paused    Whether the game is paused.
     */
    public void setPaused(boolean paused) {
        if (!paused && isPaused) lastPause = elapsedTime;
        isPaused = paused;
    }

    /**
     * Gets whether the game is paused.
     * @return  True if the game is paused.
     */
    public boolean isPaused() { return  isPaused; }

    /**
     * Get the viewport.
     * @return  The viewport.
     */
    public FitViewport getViewport() { return viewport; }

    /**
     * Disposes of disposables when game finishes execution.
     */
    @Override
    public void dispose(){
        HUDBatch.dispose();
        tiledMap.dispose();
        music.dispose();
    }
}
