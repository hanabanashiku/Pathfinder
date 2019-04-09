package tk.pathfinder.Networking;

import android.os.Build;

import java.util.Objects;

import androidx.annotation.RequiresApi;
import tk.pathfinder.Map.Point;

public class Beacon implements Comparable<Beacon> {
    private String ssid;
    private int index;
    private int building_index;
    private int strength;
    private int frequency;
    private Point location;

    public Beacon(String ssid, Point p) throws IllegalArgumentException {
        this.ssid = ssid;
        this.location = p;
        this.strength = -128; // minimum strength for now.
        int[] ids = parseSsid(ssid);

        if(ids.length == 0)
            throw new IllegalArgumentException("Invalid SSID format for beacon");
        building_index = ids[0];
        index = ids[1];
    }

    public int getIndex(){
        return index;
    }

    public int getBuildingIndex(){
        return building_index;
    }

    public String getSSID(){
        return ssid;
    }

    public int getLevel(){
        return strength;
    }

    public void setLevel(int level){
        if(level < -128 || level > 128)
            throw new IllegalArgumentException();
        strength = level;
    }

    public int getFrequency() { return frequency; }

    public void setFrequency(int value){
        frequency = value;
    }

    public Point getLocation(){
        return location;
    }

    /**
     * Parse an SSID into a building id and a node id.
     * @param ssid the ssid string to parse
     * @return an array of the form {building_id, node_id} or {} on failure.
     */
    static int[] parseSsid(String ssid){
        String[] parts = ssid.split("_");

        if(!parts[0].equals("PF") || parts.length != 3)
            return new int[]{};

        int[] ret = new int[2];

        try{
            ret[0] = Integer.parseInt(parts[1]);
            ret[1] = Integer.parseInt(parts[2]);
        }
        catch(NumberFormatException ignored){
            return new int[]{};
        }

        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Beacon beacon = (Beacon) o;
        return index == beacon.index &&
                building_index == beacon.building_index &&
                Objects.equals(ssid, beacon.ssid);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(ssid, index, building_index);
    }

    @Override
    public int compareTo(Beacon o) {
        return Integer.compare(getLevel(), o.getLevel());
    }
}
