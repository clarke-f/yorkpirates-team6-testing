package yorkpirates.game;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
            ret = "RAINING TODAY";
        }else if(weatherType == WeatherType.SNOW){
            ret = "IT IS SNOWING here";
        }else if(weatherType == WeatherType.STORM){
            ret = "STORMY DAYS";
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
    
    static Rectangle snow1 = new Rectangle(100, 100, 400, 200, new Color(255, 255, 255, 1));
    static Rectangle snow2 = new Rectangle(500, 300, 300, 300, new Color(255, 255, 255, 1));
    static Rectangle snow3 = new Rectangle(900, 900, 100, 500, new Color(255, 255, 255, 1));
    static Rectangle snow4 = new Rectangle(1500, 100, 700, 400, new Color(255, 255, 255, 1));
    static Rectangle snow5 = new Rectangle(1200, 1000, 300, 50, new Color(255, 255, 255, 1));
    static Rectangle snow6 = new Rectangle(1700, 300, 200, 200, new Color(255, 255, 255, 1));
    static Rectangle snow7 = new Rectangle(1400, 400, 300, 400, new Color(255, 255, 255, 1));

    static Rectangle rain1 = new Rectangle(150, 500, 100, 100, new Color(44, 186, 242, 0.4f));
    static Rectangle rain2 = new Rectangle(700, 200, 200, 150, new Color(44, 186, 242, 0.4f));
    static Rectangle rain3 = new Rectangle(200, 100, 400, 200, new Color(44, 186, 242, 0.4f));
    static Rectangle rain4 = new Rectangle(1100, 900, 100, 100, new Color(44, 186, 242, 0.4f));
    static Rectangle rain5 = new Rectangle(300, 1000, 50, 150, new Color(44, 186, 242, 0.4f));
    static Rectangle rain6 = new Rectangle(1700, 700, 100, 100, new Color(44, 186, 242, 0.4f));
    static Rectangle rain7 = new Rectangle(1000, 500, 150, 400, new Color(44, 186, 242, 0.4f));

    static Rectangle stormback = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new Color(0, 0, 0, 0.6f));
    static Rectangle storm1 = new Rectangle(300, 100, 100, 120, new Color(44, 186, 242, 0.4f));
    static Rectangle storm2 = new Rectangle(780, 900, 110, 300, new Color(44, 186, 242, 0.4f));
    
    static Rectangle james = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new Color(255, 0, 0, 0.5f));

    static ArrayList<Rectangle> rains = new ArrayList<Rectangle>(Arrays.asList(rain1,rain2,rain3,rain4,rain5,rain6,rain7));
    static ArrayList<Rectangle> snows = new ArrayList<Rectangle>(Arrays.asList(snow1,snow2,snow3,snow4,snow5,snow6,snow7));
    static ArrayList<Rectangle> storm = new ArrayList<Rectangle>(Arrays.asList(stormback,storm1,storm2));
    
    public static void DisadvantagePlayer(Player player, WeatherType weatherType){
        //set players attributes so they have a disadvantage
        //we also need to draw some rectangles to represent rain/snow so they're
        //visibility is impeded.
        
        if(weatherType == WeatherType.RAIN){
            player.SPEED = 60f;
            //will make these lists later
            for(Rectangle r : rains){
                HUD.stage.addActor(r);
            }
            
        }else if (weatherType == WeatherType.SNOW){
            player.SPEED = 50f;
            player.projectileShootCooldown = 0.5f;
            //will make these lists later
            for(Rectangle r : snows){
                HUD.stage.addActor(r);
            }
        }else if(weatherType == WeatherType.STORM){
            player.SPEED = 30f;
            player.projectileShootCooldown = 0.8f;
            for(Rectangle r : storm){
                HUD.stage.addActor(r);
            }
            for(Rectangle r : rains){
                HUD.stage.addActor(r);
            }
        }else if (weatherType == WeatherType.JAMESFURY){
            player.SPEED = 10f;
            player.playerProjectileDamage = 200f;
            player.projectileShootCooldown = 1f;
            HUD.stage.addActor(james);
        }
    }
    public static void ResetPlayerDisadvantage(Player player){
        player.SPEED = 70f;
        player.playerProjectileDamage = 20;
        Array<Actor> actors = HUD.stage.getActors();
        for(int i = actors.size-1; i> 0;i--){
            Actor a = actors.get(i);
            
            if(a instanceof Rectangle){
                a.remove();
            }
        }
    }
}

