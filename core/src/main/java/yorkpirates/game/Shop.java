package yorkpirates.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.crashinvaders.vfx.effects.WaterDistortionEffect;

import java.util.Objects;

public class Shop extends GameObject {
    public String name;
    public boolean activated;
    private Array<Texture> shopImage;
    
    

    public Shop(Array<Texture> shopImage, float x, float y, float scale, Boolean activated, String name){
        super(shopImage, 0f, x, y, shopImage.get(0).getWidth()*scale, shopImage.get(0).getHeight()*scale, "neutral");
        this.shopImage = shopImage;
        this.activated = activated;
        this.name = name;
    }

    public void activate(GameScreen screen, int index){
        activated = true;
        /* Array<Texture> activatedSprite = new Array<>();
        activatedSprite.add(shopImage.get(1));
        changeImage(activatedSprite, 0); */
    }

    public void upgrade(String upgradeSelected){
        if (upgradeSelected == "1"){
            // upgrade damage
        }
        if (upgradeSelected == "2"){
            // upgrade speed
        }
        if (upgradeSelected == "3"){
            // upgrade armour
        }
    }

    @Override
    public void draw(SpriteBatch batch, float elapsedTime){
        batch.draw(anim.getKeyFrame(elapsedTime, true), x-width/2, y-width/2, width, height);
    }
}