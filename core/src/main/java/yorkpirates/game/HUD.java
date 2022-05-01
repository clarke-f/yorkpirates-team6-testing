package yorkpirates.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import static java.lang.Math.abs;

public class HUD {

    // Stage
    public Stage stage;
    private final Table mainTable;

    // Tutorial
    private final Table tutorial;
    private final Cell<Image> tutorialImg;
    private final Label tutorialLabel;
    private boolean tutorialComplete = false;
    private boolean canEndGame = false;

    // Shop
    private final Table shop;
    private final Label health;
    private final Label damage;
    private final Label speed;
    private final Label openShop;

    // Player counters
    private final Label score;
    private final Label loot;

    // Player tasks
    private final Label tasksTitle;
    private final CheckBox collegesTask;
    private final CheckBox movementTask;
    private final CheckBox pointsTask;

    private final int DISTANCE_GOAL = MathUtils.random(55,65)*10;
    private final int POINT_GOAL = MathUtils.random(13,18)*10;

    private final int DISTANCE_REWARD = MathUtils.random(17,23);
    private final int POINT_REWARD = MathUtils.random(13,17);

    /**
     * Generates a HUD object within the game that controls elements of the UI.
     * @param screen    The game screen which this is attached to.
     */
    public HUD(GameScreen screen){
        // Generate skin
        TextureAtlas atlas;
        atlas = new TextureAtlas(Gdx.files.internal("Skin/YorkPiratesSkin.atlas"));
        Skin skin = new Skin(Gdx.files.internal("Skin/YorkPiratesSkin.json"), new TextureAtlas(Gdx.files.internal("Skin/YorkPiratesSkin.atlas")));
        skin.addRegions(atlas);

        // Generate stage and table
        stage = new Stage(screen.getViewport());
        Gdx.input.setInputProcessor(stage);
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setTouchable(Touchable.enabled);
        if(YorkPirates.DEBUG_ON) mainTable.setDebug(true);

        // Create menu button
        ImageButton menuButton = new ImageButton(skin, "Menu");

        menuButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                screen.gamePause();
            }
        });

        // Create tutorial actors
        Image tutorialImg = new Image(screen.getMain().keyboard.getKeyFrame(0f));
        tutorialImg.setScaling(Scaling.fit);
        tutorialLabel = new Label("WASD or Arrow Keys\n to Move.", skin);

        // Create score related actors
        Image coin = new Image(new Texture(Gdx.files.internal("loot.png")));
        Image star = new Image(new Texture(Gdx.files.internal("points.png")));
        coin.setScaling(Scaling.fit);
        star.setScaling(Scaling.fit);
        loot = new Label(screen.loot.GetString(), skin);
        score = new Label(screen.points.GetString(), skin);
        loot.setFontScale(1.2f);
        score.setFontScale(1.2f);

        // Create task related actors
        tasksTitle = new Label(screen.getPlayerName() + "'s Tasks:", skin);
        tasksTitle.setFontScale(0.5f, 0.5f);
        collegesTask = new CheckBox("", skin);
        movementTask = new CheckBox("", skin);
        pointsTask = new CheckBox("", skin);
        collegesTask.setChecked(true);
        movementTask.setChecked(true);
        pointsTask.setChecked(true);
        collegesTask.setDisabled(true);
        movementTask.setDisabled(true);
        pointsTask.setDisabled(true);

        // Create player tracker
        Table tracker = new Table();
        tracker.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("transparent.png"))));
        if(YorkPirates.DEBUG_ON) tracker.debug();

        // Add score to player tracker
        Table scores = new Table();
        scores.add(star).padRight(20);
        scores.add(score).padRight(20);
        scores.add(coin).padRight(20);
        scores.add(loot).padRight(20);
        if(YorkPirates.DEBUG_ON) scores.setDebug(true);
        tracker.add(scores);

        // Add tasks to player tracker
        tracker.row();
        tracker.add(tasksTitle).pad(1);
        tracker.row();
        tracker.add(collegesTask).left().pad(5);
        tracker.row();
        tracker.add(movementTask).left().pad(5);
        tracker.row();
        tracker.add(pointsTask).left().pad(5);

        // Create tutorial placeholder
        tutorial = new Table();
        tutorial.setBackground(tracker.getBackground());
        this.tutorialImg = tutorial.add(tutorialImg).expand().fill().minSize(200f).maxSize(500f);
        tutorial.row();
        tutorial.add(tutorialLabel);
        if(YorkPirates.DEBUG_ON) tutorial.setDebug(true);

        // Create shop prompt
        openShop = new Label("", skin);
        openShop.setBounds(10, 35, 16, 9);


        // Create shop table 
        shop = new Table();
        shop.setFillParent(true);
        damage = new Label("Press 1 to upgrade damage", skin);
        shop.add(damage).center().padBottom(25);
        shop.row();
        speed = new Label("Press 2 to upgrade speed", skin);
        shop.add(speed).center();
        shop.row();
        health = new Label("Press 3 to upgrade health", skin);
        shop.add(health).center().padTop(25);

        // Start main table

        // Add menu button to table
        mainTable.row();
        mainTable.add(menuButton).size(150).left().top().pad(25);

        // Add tutorial to table
        mainTable.row();
        mainTable.add(tutorial.pad(100f));

        // Add tracker to table
        mainTable.add().expand();
        mainTable.add(tracker);

        // Add actors to the stage
        stage.addActor(mainTable);
        stage.addActor(shop);
        stage.addActor(openShop);

        shop.setVisible(false);
    }

    /**
     * Called to render the HUD elements
     * @param screen    The game screen which this is attached to.
     */
    public void renderStage(GameScreen screen){
        Gdx.input.setInputProcessor(stage);
        stage.draw();

        // Update the score and loot
        score.setText(screen.points.GetString());
        loot.setText(screen.loot.GetString());

        // Calculate which part of the tutorial to show
        if(screen.getPlayer().getDistance() < 2){
            // Movement tutorial
            Image newimg = new Image(screen.getMain().keyboard.getKeyFrame(screen.getElapsedTime(), true));
            newimg.setScaling(Scaling.fit);
            tutorialImg.setActor(newimg);
            tutorialComplete = false;
        } else if(!tutorialComplete){
            // Shooting tutorial
            Image newimg = new Image(screen.getMain().mouse.getKeyFrame(screen.getElapsedTime(), true));
            newimg.setScaling(Scaling.fit);
            tutorialImg.setActor(newimg);
            tutorialLabel.setText("Click to shoot.");
        } else if(canEndGame) {
            // Able to end the game
            tutorial.setVisible(true);
            Image newimg = new Image(screen.getMain().enter.getKeyFrame(screen.getElapsedTime(), true));
            newimg.setScaling(Scaling.fit);
            tutorialImg.setActor(newimg);
            tutorialLabel.setText("Press Enter to end game.");
            canEndGame = false;
        } else {
            // Tutorial complete
            tutorial.setVisible(false);
        }

        // Prompt the player to open the shop
        Player player = screen.getPlayer();
        for(int i=0; i < screen.shops.size; i++){
            if((abs(screen.shops.get(i).x - player.x)) < (Gdx.graphics.getWidth()/15f)
            && (abs(screen.shops.get(i).y - player.y)) < (Gdx.graphics.getHeight()/10f)
            && screen.shops.get(i).activated){
                openShop.setText("Press e to open the shop");
            }
        }

        // Check if the player is outside any shop range
        for(int i=0; i < screen.shops.size; i++){
            if((abs(screen.shops.get(i).x - player.x)) < (Gdx.graphics.getWidth()/15f)
            && (abs(screen.shops.get(i).y - player.y)) < (Gdx.graphics.getHeight()/10f)){
                if(screen.shops.get(i).activated){
                    break;
                }
            }    
            else if(i == screen.shops.size-1){
                openShop.setText("");
                shop.setVisible(false);
                screen.shopOpened = false;
            }
        }
        // Check if the player has opened or closed the shop
        if (screen.shopOpened){
            mainTable.setVisible(false);
            shop.setVisible(true);
            openShop.setText("Press e to close the shop");
        }
        else{
            mainTable.setVisible(true);
            shop.setVisible(false);
        }


        // Decide on and then display main player goal
        if(College.capturedCount >= screen.colleges.size-1){
            collegesTask.setText("Return home to win.");
        } else {
            collegesTask.setText("Capture all colleges:  "+Math.min(College.capturedCount, screen.colleges.size-1)+"/"+(screen.colleges.size-1)+"  ");
        }

        // Distance related task calculations
        if(screen.getPlayer().getDistance() > DISTANCE_GOAL && movementTask.isChecked()) { screen.loot.Add(DISTANCE_REWARD); }
        movementTask.setChecked(screen.getPlayer().getDistance() < DISTANCE_GOAL);
        movementTask.setText("Move "+DISTANCE_GOAL+"m:  "+Math.min((int)(screen.getPlayer().getDistance()), DISTANCE_GOAL)+"/"+DISTANCE_GOAL+"  ");

        // Points related task calculations
        if(screen.points.Get() > POINT_GOAL && pointsTask.isChecked()) { screen.loot.Add(POINT_REWARD); }
        pointsTask.setChecked(screen.points.Get() < POINT_GOAL);
        pointsTask.setText("Get "+POINT_GOAL+" points:  "+Math.min(screen.points.Get(), POINT_GOAL)+"/"+POINT_GOAL+"  ");
    }

    public void updateName(GameScreen screen) { tasksTitle.setText(screen.getPlayerName() +"'s Tasks:"); }

    public void endTutorial() { tutorialComplete = true; }

    public void setGameEndable() {canEndGame = true; }
}
