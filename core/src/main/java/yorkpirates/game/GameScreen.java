package yorkpirates.game;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.FitViewport;


import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Arrays;



public class GameScreen extends ScreenAdapter {
    // Team name constants
    public static final String playerTeam = "PLAYER";
    public static final String enemyTeam = "ENEMY";


    //weathers and positions
   
    public static ArrayList<Weather> weathers = new ArrayList<Weather> ();

    // Score managers
    public ScoreManager points;
    public ScoreManager loot;

    // Colleges
    public Set<College> colleges;
    public Set<Projectile> projectiles;

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
    private boolean canFire = true;
    private float lastPause = 0;

    public static ArrayList<Actor> rains,snows,storms,mortars;
    public static ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    
    

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
        Label l = HUD.AddWeatherLabel("");
        //initialise sound
        music = Gdx.audio.newMusic(Gdx.files.internal("Pirate1_Theme1.ogg"));
        music.setLooping(true);
        music.setVolume(0);
        music.play();

        // Initialise sprites array to be used generating GameObjects
        Array<Texture> sprites = new Array<>();

        // Initialise player
        sprites.add(new Texture("ship1.png"), new Texture("ship2.png"), new Texture("ship3.png"));
        player = new Player(sprites, 2, 821, 489, 32, 16, playerTeam,l,this);
        sprites.clear();
        followPos = new Vector3(player.x, player.y, 0f);
        game.camera.position.lerp(new Vector3(760, 510, 0f), 1f);

        // Initialise tilemap
        tiledMap = new TmxMapLoader().load("FINAL_MAP.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // Initialise colleges
        College.capturedCount = 0;
        colleges = new HashSet<>();
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
        projectiles = new HashSet<>();

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
        newShop = new Shop(shopImages, 1790, 1999, 0.58f, false, "Derwent");
        shops.add(newShop);
        //langwith
        newShop  = new Shop(shopImages, 1500, 1522, 0.45f, false, "Langwith");
        shops.add(newShop);

        //Weather and obstacles

        //init weather events
        initWeatherEvents();
       
        Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            public void run(){
                player.checkForWeather();
            }
        };
        t.scheduleAtFixedRate(tt, 500, 700);

        //textures
        //barrels
        Array<Texture> barrel_brown = new Array<Texture>();
        barrel_brown.add(new Texture(Gdx.files.internal("barrel.png")));

        Array<Texture> barrel_gold = new Array<Texture>();
        barrel_gold.add(new Texture(Gdx.files.internal("barrel_gold.png")));

        //icebergs
        Array<Texture> iceberg = new Array<Texture>();
        iceberg.add(new Texture(Gdx.files.internal("iceberg.png")));

        //objects
        //barrels 
        Barrel b1 = new Barrel(barrel_brown, 0f, 821f, 608f, 20f, 20f, "ENEMY",40,BarrelType.BROWN);
        Barrel b2 = new Barrel(barrel_gold, 0f,1086f, 787f, 20f, 20f, "ENEMY",0,BarrelType.GOLD);
        Barrel b3 = new Barrel(barrel_brown, 0f,1299f, 605f, 20f, 20f, "ENEMY",40,BarrelType.BROWN);
        Barrel b4 = new Barrel(barrel_brown, 0f,1619f, 524f, 20f, 20f, "ENEMY",40,BarrelType.BROWN);
        Barrel b5 = new Barrel(barrel_gold, 0f,1936f, 801f, 20f, 20f, "ENEMY",0,BarrelType.GOLD);
        Barrel b6 = new Barrel(barrel_brown, 0f,1700f, 1532f, 20f, 20f, "ENEMY",40,BarrelType.BROWN);
        Barrel b7 = new Barrel(barrel_brown, 0f,1815f, 1222f, 10f, 10f, "ENEMY",40,BarrelType.BROWN);
        Barrel b8 = new Barrel(barrel_brown, 0f,2215f, 1614f, 20f, 20f, "ENEMY",40,BarrelType.BROWN);
        Barrel b9 = new Barrel(barrel_brown, 0f,2093f, 2038f, 20f, 20f, "ENEMY",40,BarrelType.BROWN);
        Barrel b10 = new Barrel(barrel_brown, 0f,2137f, 1877f, 15f, 15f, "ENEMY",40,BarrelType.BROWN);
        Barrel b11 = new Barrel(barrel_brown, 0f,1892f, 1543f, 20f, 20f, "ENEMY",40,BarrelType.BROWN);
        //icebergs
        Obstacle ice1 = new Obstacle(iceberg,0, 950, 600, 50, 40, "ENEMY",100);
        Obstacle ice2 = new Obstacle(iceberg,0, 1530, 500, 50, 40, "ENEMY",100);
        Obstacle ice3 = new Obstacle(iceberg,0, 565, 1075, 50, 40, "ENEMY",100);
        Obstacle ice4 = new Obstacle(iceberg,0, 1111, 1729, 50, 40, "ENEMY",100);
        Obstacle ice5 = new Obstacle(iceberg,0, 656, 1840, 50, 40, "ENEMY",100);
        Obstacle ice6 = new Obstacle(iceberg,0, 1770, 1658, 50, 40, "ENEMY",100);
        
        obstacles.add(b1);
        obstacles.add(b2);
        obstacles.add(b3);
        obstacles.add(b4);
        obstacles.add(b5);
        obstacles.add(b6);
        obstacles.add(b7);
        obstacles.add(b8);
        obstacles.add(b9);
        obstacles.add(b10);
        obstacles.add(b11);

        obstacles.add(ice1);
        obstacles.add(ice2);
        obstacles.add(ice3);
        obstacles.add(ice4);
        obstacles.add(ice5);
        obstacles.add(ice6);

        //generate the weather animations
        generateRain();
        generateSnow();
        generateStorm();

    }
    private void generateRain(){
        Texture rain = new Texture(Gdx.files.internal("rain.png"));

        int numOfDrops = (int)Math.floor(Math.random()*(10-6+1)+6);

        for(int i =0;i<numOfDrops;i++){
            int x = (int)Math.floor(Math.random()*(((Gdx.graphics.getWidth()/2)+ 200) - ((Gdx.graphics.getWidth()/2)-200)+1) + (Gdx.graphics.getWidth()/2)-200);
            int y = (int)Math.floor(Math.random()*(((Gdx.graphics.getHeight()/2)+200) - ((Gdx.graphics.getHeight()/2)-200)+1) + (Gdx.graphics.getHeight()/2)-100);
            int size = (int)Math.floor(Math.random()*(80-40+1)+40);

            Rain rrain = new Rain(x, y, size,size,rain,0.6f);
            rains.add(rrain);
        }
    }
    private void generateSnow(){
        Texture snow = new Texture(Gdx.files.internal("snow.png"));

        int numOfFlakes = (int)Math.floor(Math.random()*(10-6+1)+6);
        
        for(int i =0;i<numOfFlakes;i++){
            int x = (int)Math.floor(Math.random()*(((Gdx.graphics.getWidth()/2)+ 200) - ((Gdx.graphics.getWidth()/2)-200)+1) + (Gdx.graphics.getWidth()/2)-200);
            int y = (int)Math.floor(Math.random()*(((Gdx.graphics.getHeight()/2)+200) - ((Gdx.graphics.getHeight()/2)-200)+1) + (Gdx.graphics.getHeight()/2)-200);
            int size = (int)Math.floor(Math.random()*(80-40+1)+40);

            Snow rsnow = new Snow(x, y, size,size,snow,0.7f);
            snows.add(rsnow);
        }
    }

    private void generateStorm(){
        Texture rain = new Texture(Gdx.files.internal("rain.png"));
        Texture snow = new Texture(Gdx.files.internal("snow.png"));
        for(int i =0;i<6;i++){
            int x = (int)Math.floor(Math.random()*(((Gdx.graphics.getWidth()/2)+ 200) - ((Gdx.graphics.getWidth()/2)-200)+1) + (Gdx.graphics.getWidth()/2)-200);
            int y = (int)Math.floor(Math.random()*(((Gdx.graphics.getHeight()/2)+200) - ((Gdx.graphics.getHeight()/2)-200)+1) + (Gdx.graphics.getHeight()/2)-200);
            int size = (int)Math.floor(Math.random()*(80-40+1)+40);

            Snow rsnow = new Snow(x, y, size,size,snow,0.7f);
            storms.add(rsnow);
        }
        
        for(int i =0;i<6;i++){
            int x = (int)Math.floor(Math.random()*(((Gdx.graphics.getWidth()/2)+ 200) - ((Gdx.graphics.getWidth()/2)-200)+1) + (Gdx.graphics.getWidth()/2)-200);
            int y = (int)Math.floor(Math.random()*(((Gdx.graphics.getHeight()/2)-200) - ((Gdx.graphics.getHeight()/2)+200)+1) + (Gdx.graphics.getHeight()/2)+200);
            int size = (int)Math.floor(Math.random()*(80-40+1)+40);
         
            Rain rrain = new Rain(x, y, size,size,rain,0.6f);
            storms.add(rrain);
            
        }

        //make screen darker
        RectangleColour stormback = new RectangleColour(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),new Color(0,0,0,0.3f));
        storms.add(stormback);
    }
    private void initWeatherEvents(){
        //animations
        rains = new ArrayList<Actor>();
        snows = new ArrayList<Actor>();
        storms = new ArrayList<Actor>();
        mortars = new ArrayList<Actor>((Arrays.asList(new RectangleColour(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new Color(255, 0, 0, 0.5f)))));

        //postion of weathers on map
        Weather rain =  new Weather(820, 980, 1000,100, WeatherType.RAIN);
        Weather rain2 =  new Weather(1770, 2300, 150,150, WeatherType.RAIN);
        Weather snow =  new Weather(1190, 911, 100,100, WeatherType.SNOW);
        Weather snow2 =  new Weather(940,1341, 100,100, WeatherType.SNOW);
        Weather snow3 =  new Weather(2072,2145, 100,100, WeatherType.SNOW);
        Weather storm =  new Weather(1700, 678, 100,100, WeatherType.STORM);
        Weather storm2 =  new Weather(670, 700, 150,150, WeatherType.STORM);
        Weather storm3 =  new Weather(400,1000, 200,150, WeatherType.STORM);
        Weather mortar =  new Weather(1380, 1770, 180,150, WeatherType.MORTAR);
        Weather mortar2 = new Weather(1435,741,180,150,WeatherType.MORTAR);
        
        weathers.add(rain);
        weathers.add(rain2);
        weathers.add(snow);
        weathers.add(snow2);
        weathers.add(snow3);
        weathers.add(storm);
        weathers.add(storm2);
        weathers.add(storm3);
        weathers.add(mortar);
        weathers.add(mortar2);
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
        for (Projectile p : projectiles) {
            p.draw(game.batch, 0);
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
        for (College c : colleges) {
            c.draw(game.batch, 0);
        }
        
        // Draw Shops
        for (int i = 0; i < shops.size; i++){
            if (shops.get(i).activated){
                shops.get(i).draw(game.batch, 0);
            }
        }

        // Draw Obstacles
        for(Obstacle o : obstacles){
            o.draw(game.batch, 0);
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
    private void update() {

        // Call updates for all relevant objects
        player.update(this, game.camera);
        
        Iterator<College> cIterator = colleges.iterator();

        while (cIterator.hasNext()) {
            College c = cIterator.next();
            c.update(this);
        }
        
        

        // for (College c : colleges) {
        //     c.update(this);
        // }

        // Check for projectile creation, then call projectile update
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            if(canFire){
                Vector3 mouseVector = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
                Vector3 mousePos = game.camera.unproject(mouseVector);
    
                Array<Texture> sprites = new Array<>();
                sprites.add(new Texture("tempProjectile.png"));
                projectiles.add(new Projectile(sprites, 0, player, mousePos.x, mousePos.y, playerTeam));
                gameHUD.endTutorial();
                canFire = false;
                Thread t = new Thread(){
                    public void run(){
                        try{
                            Thread.sleep((int) (1000 * player.projectileShootCooldown));
                        }catch(InterruptedException e){}
                        canFire = true;
                    }
                };
                t.start();
            }
           
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
            if(canFire){
                Array<Texture> sprites = new Array<>();
                sprites.add(new Texture("tempProjectile.png"));
                projectiles.add(new Projectile(sprites, 0, player, 0, player.y, playerTeam));
                gameHUD.endTutorial();
                canFire = false;
                Thread t = new Thread(){
                    public void run(){
                        try{
                            Thread.sleep((int) (1000 * player.projectileShootCooldown));
                        }catch(InterruptedException e){}
                        canFire = true;
                    }
                };
                t.start();
            }
        }
        
        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
            if(canFire){
                Array<Texture> sprites = new Array<>();
                sprites.add(new Texture("tempProjectile.png"));
                projectiles.add(new Projectile(sprites, 0, player, Gdx.graphics.getWidth(), player.y, playerTeam));
                gameHUD.endTutorial();
                canFire = false;
                Thread t = new Thread(){
                    public void run(){
                        try{
                            Thread.sleep((int) (1000 * player.projectileShootCooldown));
                        }catch(InterruptedException e){}
                        canFire = true;
                    }
                };
                t.start();
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            if(canFire){
                Array<Texture> sprites = new Array<>();
                sprites.add(new Texture("tempProjectile.png"));
                projectiles.add(new Projectile(sprites, 0, player, player.x, Gdx.graphics.getHeight(), playerTeam));
                gameHUD.endTutorial();
                canFire = false;
                Thread t = new Thread(){
                    public void run(){
                        try{
                            Thread.sleep((int) (1000 * player.projectileShootCooldown));
                        }catch(InterruptedException e){}
                        canFire = true;
                    }
                };
                t.start();
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            if(canFire){
                Array<Texture> sprites = new Array<>();
                sprites.add(new Texture("tempProjectile.png"));
                projectiles.add(new Projectile(sprites, 0, player, player.x, 0, playerTeam));
                gameHUD.endTutorial();
                canFire = false;
                Thread t = new Thread(){
                    public void run(){
                        try{
                            Thread.sleep((int) (1000 * player.projectileShootCooldown));
                        }catch(InterruptedException e){}
                        canFire = true;
                    }
                };
                t.start();
            }
        }
        Iterator<Projectile> pIterator = projectiles.iterator();

        while (pIterator.hasNext()) {
            Projectile p = pIterator.next();
            if (p.update(this) == 0) {
                pIterator.remove();
            }
        }

        // for (int i = projectiles.size - 1; i >= 0; i--) {
        //     projectiles.get(i).update(this);
        // }

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
