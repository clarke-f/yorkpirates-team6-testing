package yorkpirates.game;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;

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


    public String getWeatherLabelText(){
        String ret = "";
        
        if(this.weatherType == WeatherType.RAIN){
            ret = "RAINING TODAY";
        }else if(this.weatherType == WeatherType.SNOW){
            ret = "IT IS SNOWING here";
        }else if(this.weatherType == WeatherType.STORM){
            ret = "STORMY DAYS";
        }else if(this.weatherType == WeatherType.JAMESFURY){
            ret = "james is coming...";
        }else if(this.weatherType == WeatherType.NONE){
            ret = "";
        }
        return ret;
    }

    public static Weather WhichWeather(int x, int y, ArrayList<Weather> weathers){
        for(int i =0; i < weathers.size();i++){
            if(x >= weathers.get(i).xpos -weathers.get(i).xoff  && x <=weathers.get(i).xpos + weathers.get(i).xoff && y >= weathers.get(i).ypos - weathers.get(i).yoff && y <= weathers.get(i).ypos + weathers.get(i).yoff){
                return weathers.get(i);
            }
        }
        return new Weather(0,0, 0, 0, WeatherType.NONE);
    }

    //will be easier to add lists later, just testing with these for now
    Rectangle snow1 = new Rectangle(100, 100, 400, 200, new Color(255, 255, 255, 1));
    Rectangle snow2 = new Rectangle(500, 300, 300, 300, new Color(255, 255, 255, 1));
    Rectangle snow3 = new Rectangle(900, 900, 100, 500, new Color(255, 255, 255, 1));
    Rectangle snow4 = new Rectangle(1500, 100, 700, 400, new Color(255, 255, 255, 1));
    Rectangle snow5 = new Rectangle(1200, 1000, 300, 50, new Color(255, 255, 255, 1));
    Rectangle snow6 = new Rectangle(1700, 300, 200, 200, new Color(255, 255, 255, 1));
    Rectangle snow7 = new Rectangle(1400, 400, 300, 400, new Color(255, 255, 255, 1));

    Rectangle rain1 = new Rectangle(150, 500, 100, 100, new Color(44, 186, 242, 0.4f));
    Rectangle rain2 = new Rectangle(700, 200, 200, 150, new Color(44, 186, 242, 0.4f));
    Rectangle rain3 = new Rectangle(200, 100, 400, 200, new Color(44, 186, 242, 0.4f));
    Rectangle rain4 = new Rectangle(1100, 900, 100, 100, new Color(44, 186, 242, 0.4f));
    Rectangle rain5 = new Rectangle(300, 1000, 50, 150, new Color(44, 186, 242, 0.4f));
    Rectangle rain6 = new Rectangle(1700, 700, 100, 100, new Color(44, 186, 242, 0.4f));
    Rectangle rain7 = new Rectangle(1000, 500, 150, 400, new Color(44, 186, 242, 0.4f));
    public void DisadvantagePlayer(Player player){
        //set players attributes so they have a disadvantage
        //we also need to draw some rectangles to represent rain/snow so they're
        //visibility is impeded.
        
        if(weatherType == WeatherType.RAIN){
            player.SPEED = 50f;
            //will make these lists later
            HUD.stage.addActor(rain1);
            HUD.stage.addActor(rain2);
            HUD.stage.addActor(rain3);
            HUD.stage.addActor(rain4);
            HUD.stage.addActor(rain5);
            HUD.stage.addActor(rain6);
            HUD.stage.addActor(rain7);
            
        }else if (weatherType == WeatherType.SNOW){
            player.SPEED = 40f;
            player.projectileShootCooldown = 0.5f;
            //will make these lists later
            HUD.stage.addActor(snow1);
            HUD.stage.addActor(snow2);
            HUD.stage.addActor(snow3);
            HUD.stage.addActor(snow4);
            HUD.stage.addActor(snow5);
            HUD.stage.addActor(snow6);
            HUD.stage.addActor(snow7);
        }else if (weatherType == WeatherType.JAMESFURY){
            player.SPEED = 10f;
            player.playerProjectileDamage = 200f;
            player.projectileShootCooldown = 1f;
        }
    }
    public void ResetPlayerDisadvantage(Player player){
        player.SPEED = 70f;
        player.playerProjectileDamage = 20;
        for(Actor a : HUD.stage.getActors()){
            
            if(a instanceof Rectangle){
                a.remove();
            }
        }
    }
}

