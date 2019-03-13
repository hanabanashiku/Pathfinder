package tk.pathfinder.Networking;

import tk.pathfinder.Map.Point;

public class Beacon {
    private String ssid;
    private int index;
    private int building_index;
    private int strength;
    private Point location;

    public Beacon(String ssid, int strength){
        this.ssid = ssid;
        this.strength = strength;
    }

    public int getBuildingIndex(){
        return building_index;
    }

    public String getSsid(){
        return ssid;
    }

    public int getLevel(){
        return strength;
    }

    public void setLevel(int level){
        if(level < -127 || level > 128)
            throw new IllegalArgumentException();
        strength = level;
    }
}
