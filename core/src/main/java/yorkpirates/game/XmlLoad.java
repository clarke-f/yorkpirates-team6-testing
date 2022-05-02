package yorkpirates.game;
import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.io.FileWriter;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.XmlWriter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.Set;


public class XmlLoad {

    //creates strings showing where files should be stored and loaded. should point to same file. trying to work out how to store in assets atm bear with me.
    static String LoadPath = "assets/save.xml";
    static String StorePath = "assets/save.xml";

    //loads xml file into a root element
    private static Element LoadFile (){
        FileHandle LoadHandle = Gdx.files.internal(LoadPath);
        XmlReader reader = new XmlReader();
        Element root = reader.parse(LoadHandle);
        return root;
    }

    //returns a player object loaded from xml file
    public static Player LoadPlayer (Texture texture){
        Element root = LoadFile();
        Element playerElem = root.getChildByName("player");
        Float playerX = Float.parseFloat(playerElem.get("x"));
        Float playerY = Float.parseFloat(playerElem.get("y"));
        Label playerWeather = HUD.AddWeatherLabel(playerElem.get("weather"));
        return new Player(texture, playerX, playerY, 32, 16, "PLAYER", playerWeather);
    }

    //returns position of college with collegeName in form [x,y]
    public static Float[] LoadCollegePosition (String collegeName){
        Element root = LoadFile();
        Element colleges = root.getChildByName("colleges");
        Element thisCollege = colleges.getChildByName(collegeName);
        Float[] toReturn = {Float.parseFloat(thisCollege.get("x")), Float.parseFloat(thisCollege.get("y"))};
        return toReturn;
    }

    //returns team of college with collegeName
    public static String LoadCollegeTeam (String collegeName){
        Element root = LoadFile();
        Element colleges = root.getChildByName("colleges");
        Element thisCollege = colleges.getChildByName(collegeName);
        return thisCollege.get("team");
    }

    //returns 2d array containing coods of boats attacked to collegename
    public static Float[][] LoadCollegeBoats (String collegeName){
        System.out.println(collegeName);
        Element root = LoadFile();
        Element colleges = root.getChildByName("colleges");
        Element thisCollege = colleges.getChildByName(collegeName);
        Element thisCollegeBoats = thisCollege.getChildByName("boats");
        if (thisCollegeBoats != null){
            int toRetLength = 0;
            for (Element boat : thisCollegeBoats.getChildrenByName("boat")){
                toRetLength = toRetLength + 1;
            }
            Float[][] toReturn = new Float[toRetLength][2];
            int loop = 0;
            for (Element boat : thisCollegeBoats.getChildrenByName("boat")){
                System.out.println(String.valueOf(loop));
                toReturn[loop][0] = Float.parseFloat(boat.get("x"));
                System.out.println(String.valueOf(toReturn[loop][0]));
                toReturn[loop][1] = Float.parseFloat(boat.get("y"));
                loop = loop + 1;
            }
            return toReturn;
        } else {
            Float[][] toReturn = {{}};
            return toReturn;
        }
    }

    //writes to xml file
    public static void Save (Player player, Set<College> colleges){

        try{
        //creates file writer
        FileWriter fw = new FileWriter(StorePath);
        XmlWriter writer = new XmlWriter(fw);

        //saves player
        writer.element("all");
            writer.element("player");
                writer.element("x");
                    writer.text(String.valueOf(player.x));
                writer.pop();
                writer.element("y");
                    writer.text(String.valueOf(player.y));
                writer.pop();
                writer.element("weather");
                    writer.text("None");
                writer.pop();
            writer.pop();

            //writes colleges
            writer.element("colleges");
            for (College xmlCollege : colleges){
                writer.element(xmlCollege.collegeName);
                    writer.element("x");
                        writer.text(String.valueOf(xmlCollege.x));
                    writer.pop();
                    writer.element("y");
                        writer.text(String.valueOf(xmlCollege.y));
                    writer.pop();
                    writer.element("team");
                        writer.text(xmlCollege.team);
                    writer.pop();
                writer.element("boats");
                    for (GameObject xmlBoat : xmlCollege.boats) {
                        writer.element("boat");
                            writer.element("x");
                                writer.text(String.valueOf(xmlBoat.x));
                            writer.pop();
                            writer.element("y");
                                writer.text(String.valueOf(xmlBoat.y));
                            writer.pop();
                        writer.pop();
                    }
                    writer.pop();
                writer.pop();
            }
            writer.pop();
        
        //closes all
        writer.pop();

        //closes writer and saves file
        writer.close();
        }
        catch(IOException e){
            System.out.println(e);
        }
    }
}
