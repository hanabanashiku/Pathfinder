package tk.pathfinder.Map;

/***
 * Represents a destination node
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class Room extends Node {

    private String name;
    private String roomNumber;
    private boolean auth;

    /***
     * @param p The location of the room in 3D space.
     * @param roomNumber The room's room number.
     * @param name The name of the room (leave null to set as roomNumber)
     * @param locked Whether or not the room requires authorization to enter.
     */
    public Room(Point p, String roomNumber, String name, boolean locked){
        if(point == null)
            throw new NullPointerException("p");
        if(roomNumber == null)
            throw new NullPointerException("roomNumber");

        this.point = p;
        this.roomNumber = roomNumber;
        if(name == null)
            this.name = roomNumber;
        else this.name = name;
        this.auth = locked;
    }

    public Room(Point p, String roomNumber){
        if(point == null)
            throw new NullPointerException("p");
        if(roomNumber == null)
            throw new NullPointerException("roomNumber");
        this.point = p;
        this.roomNumber = roomNumber;
        this.name = this.roomNumber;
        this.auth = false;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String value){
        if(value == null)
            this.name = this.roomNumber;
        this.name = value;
    }

    public String getRoomNumber(){
        return this.roomNumber;
    }

    public void setRoomNumber(String value){
        if(value == null)
            throw new NullPointerException("value");
        this.roomNumber = value;
    }

    public boolean requiresAuthorization(){
        return this.auth;
    }

    public void setAuthorization(boolean value){
        this.auth = value;
    }

    @Override
    public boolean isIntersection(){
        return false;
    }
}
