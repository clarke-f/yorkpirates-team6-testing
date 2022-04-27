package yorkpirates;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import yorkpirates.game.GameScreen;
import yorkpirates.game.HUD;
import yorkpirates.game.Player;

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

    public void DisadvantagePlayer(Player player){
        if(weatherType == WeatherType.RAIN){
            player.SPEED = 50;
            
        }else if (weatherType == WeatherType.SNOW){
            player.SPEED = 30;
            player.playerProjectileDamage = 155;
        }
    }
    public void ResetPlayerDisadvantage(Player player){
        player.SPEED = 70f;
        player.playerProjectileDamage = 20;
    }
}

