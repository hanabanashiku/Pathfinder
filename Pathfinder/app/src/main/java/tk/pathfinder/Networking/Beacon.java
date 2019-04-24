package tk.pathfinder.Networking;

import java.util.Objects;

import tk.pathfinder.Map.Point;

/**
 * Represents a positioning beacon relating to a particular building map.
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class Beacon implements Comparable<Beacon> {
    private String ssid;
    private int index;
    private int building_index;
    private int strength;
    private Point location;

    /**
     * @param ssid The beacon's SSID identifier.
     * @param p The location of the beacon on the map.
     * @throws IllegalArgumentException if the SSID is invalid.
     */
    public Beacon(String ssid, Point p) throws IllegalArgumentException {
        this.ssid = ssid;
        this.location = p;
        this.strength = 0; // minimum strength for now.
        int[] ids = parseSsid(ssid);

        if(ids.length == 0)
            throw new IllegalArgumentException("Invalid SSID format for beacon");
        building_index = ids[0];
        index = ids[1];
    }

    /**
     * @return The Database index of the beacon.
     */
    public int getIndex(){
        return index;
    }

    /**
     * @return The index matching the building that the beacon belongs to.
     */
    public int getBuildingIndex(){
        return building_index;
    }

    /**
     * @return The beacon's hotspot SSID.
     */
    public String getSSID(){
        return ssid;
    }

    /**
     * @return The normalized signal strength over the interval [0, 100].
     */
    public int getLevel(){
        return strength;
    }

    /**
     * @param level The signal strength, normalized to 101 levels.
     * @throws IllegalArgumentException if the value is outside of the interval [0, 100].
     */
    public void setLevel(int level){
        if(level < 0 || level > 100)
            throw new IllegalArgumentException();
        strength = level;
    }

    /**
     * @return The location of the beacon on the map.
     */
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

    @Override
    public int hashCode() {
        return Objects.hash(ssid, index, building_index);
    }

    /**
     * Compare the two beacons based on signal strength.
     */
    @Override
    public int compareTo(Beacon o) {
        return Integer.compare(getLevel(), o.getLevel());
    }
}
