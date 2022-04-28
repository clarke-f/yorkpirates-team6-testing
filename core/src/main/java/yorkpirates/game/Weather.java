package yorkpirates.game;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;


public class Weather {

    public int xpos, ypos, xoff, yoff;
    public WeatherType weatherType;

    public Weather(int xpos, int ypos, int xoff, int yoff, WeatherType weatherType){
        this.xpos = xpos;
        this.ypos = ypos;
        this.xoff = xoff;
        this.yoff = yoff;
        this.weatherType = weatherType;
        
    }

    public static String getWeatherLabelText(WeatherType weatherType){
        String ret = "";
        
        if(weatherType == WeatherType.RAIN){
            ret = "";
        }else if(weatherType == WeatherType.SNOW){
            ret = "";
        }else if(weatherType == WeatherType.STORM){
            ret = "";
        }else if(weatherType == WeatherType.JAMESFURY){
            ret = "james is coming...";
        }else if(weatherType == WeatherType.NONE){
            ret = "";
        }
        return ret;
    }

    public static WeatherType WhichWeather(int x, int y, ArrayList<Weather> weathers){
        for(int i =0; i < weathers.size();i++){
            if(x >= weathers.get(i).xpos -weathers.get(i).xoff  && x <=weathers.get(i).xpos + weathers.get(i).xoff && y >= weathers.get(i).ypos - weathers.get(i).yoff && y <= weathers.get(i).ypos + weathers.get(i).yoff){
                return weathers.get(i).weatherType;
            }
        }
        return WeatherType.NONE;
    }
   
    //will be easier to add lists later, just testing with these for now
  
    
    // static Rectangle stormback = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new Color(0, 0, 0, 0.6f));
    // static Rectangle storm1 = new Rectangle(300, 100, 100, 120, rain);
    // static Rectangle storm2 = new Rectangle(780, 900, 110, 300, rain);
    
    // static RectangleColour james = new RectangleColour(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new Color(255, 0, 0, 0.5f));

    // static Texture rain = 
    
    // static ArrayList<Rectangle> storm = new ArrayList<Rectangle>(Arrays.asList(stormback,storm1,storm2));


    public static void DisadvantagePlayer(Player player, WeatherType weatherType,ArrayList<Actor> disList ){
        //set players attributes so they have a disadvantage
        //we also need to draw some rectangles to represent rain/snow so they're
        //visibility is impeded.
        
        if(weatherType == WeatherType.RAIN){
            player.SPEED = 60f;
            //will make these lists later
            
        
        }else if (weatherType == WeatherType.SNOW){
            player.SPEED = 50f;
            player.projectileShootCooldown = 0.5f;
            
        }else if(weatherType == WeatherType.STORM){
            player.SPEED = 30f;
            player.projectileShootCooldown = 0.8f;
            
            
        }else if (weatherType == WeatherType.JAMESFURY){
            player.SPEED = 10f;
            player.playerProjectileDamage = 200f;
            player.projectileShootCooldown = 1f;
            
        }
        for(Actor r : disList){
            HUD.stage.addActor(r);
        }
    }
    public static void ResetPlayerDisadvantage(Player player){
        player.SPEED = 70f;
        player.playerProjectileDamage = 20;
        player.projectileShootCooldown = 0.1f;
        Array<Actor> actors = HUD.stage.getActors();
        for(int i = actors.size-1; i> 0;i--){
            Actor a = actors.get(i);
            
            if(a instanceof Rectangle || a instanceof RectangleColour){
                a.remove();
            }
        }
    }
}

