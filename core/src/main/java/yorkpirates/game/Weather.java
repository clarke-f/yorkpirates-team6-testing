package yorkpirates.game;

import java.util.ArrayList;
import java.util.Timer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;


public class Weather {

    public int xpos, ypos, xoff, yoff;
    public WeatherType weatherType;
    private static Timer t;

    private static int disSpeed = 0;

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
        }else if(weatherType == WeatherType.MORTAR){
            ret = "A MORTAR has been spotted...";
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
   
    public static void DisadvantagePlayer(GameScreen gameScreen, Player player, WeatherType weatherType,ArrayList<Actor> disList) {
        //set players attributes so they have a disadvantage
        //we also need to draw some rectangles to represent rain/snow so they're
        //visibility is impeded.
        gameScreen.mortarable = false;
        
        if(weatherType == WeatherType.RAIN){
            disSpeed = 5;
        }else if (weatherType == WeatherType.SNOW){
            disSpeed = 10;
            player.projectileShootCooldown = 0.5f;
            
        }else if(weatherType == WeatherType.STORM){
            disSpeed = 20;
            player.projectileShootCooldown = 0.8f;
        }else if (weatherType == WeatherType.MORTAR){
            disSpeed = 30;
            // player.playerProjectileDamage = 200f;
            // t =  new Timer();
            // TimerTask tt = new TimerTask(){
            //     public void run(){
            //         try{
            //             Thread.sleep(1000);
            //         }catch(InterruptedException e){}
            //         player.takeDamage(gameScreen, 20, "ENEMY");
            //     }
            // };
            // t.scheduleAtFixedRate(tt, 0, 2000);

            //Long timer for player damage via mortar
            gameScreen.mortarable = true;
        
            player.projectileShootCooldown = 0.9f;
        }
        player.SPEED-=disSpeed;
        for(Actor r : disList){
            HUD.stage.addActor(r);
        }
    }
    public static void ResetPlayerDisadvantage(Player player){

        //remove timer
        if(t != null){
            t.cancel();
        }
       
        //disadvatange player attributes
        player.SPEED+=disSpeed;
        player.projectileShootCooldown = 0.1f;
        Array<Actor> actors = HUD.stage.getActors();
        for(int i = actors.size-1; i> 0;i--){
            Actor a = actors.get(i);
            
            if(a instanceof Rectangle || a instanceof RectangleColour){
                a.remove();
            }
        }
        disSpeed = 0;
    }
}

