package yorkpirates.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

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

    public void activate(){
        this.activated = true;
        /* Array<Texture> activatedSprite = new Array<>();
        activatedSprite.add(shopImage.get(1));
        changeImage(activatedSprite, 0); */
    }

    public void upgrade(Player player, ScoreManager loot, String upgradeSelected){
        if (upgradeSelected == "damage"){
            if(loot.Sub(10)){
                player.DAMAGE += 5f;
            }
        }
        if (upgradeSelected == "speed"){
            // upgrade speed
            if(loot.Sub(10)){
                player.SPEED += 5f;
            }
        }
        if (upgradeSelected == "armour"){
            if (player.ARMOUR < 17){
                if(loot.Sub(10)){
                    player.ARMOUR += 3;
                }
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch, float elapsedTime){
        batch.draw(anim.getKeyFrame(elapsedTime, true), x-width/2, y-width/2, width, height);
    }
}