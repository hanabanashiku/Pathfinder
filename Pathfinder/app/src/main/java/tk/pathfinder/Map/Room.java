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
     * @param id The database index of the room.
     * @param p The location of the room in 3D space.
     * @param roomNumber The room's room number.
     * @param name The name of the room (leave null to set as roomNumber)
     * @param locked Whether or not the room requires authorization to enter.
     */
    public Room(int id, Point p, String roomNumber, String name, boolean locked){
        if(p == null)
            throw new NullPointerException("p");
        if(roomNumber == null && name == null)
            throw new NullPointerException("roomNumber");

        this.id = id;
        this.point = p;
        if(name == null)
            this.name = roomNumber;
        else this.name = name;
        this.roomNumber = roomNumber;
        this.auth = locked;
    }

    /**
     * @return The name of the room.
     */
    public String getName(){
        return this.name;
    }

    /**
     * @param value The new name.
     */
    public void setName(String value){
        if(value == null)
            this.name = this.roomNumber;
        this.name = value;
    }

    /**
     * @return The room number.
     */
    public String getRoomNumber(){
        return this.roomNumber;
    }


    /**
     * @return True if authorization is required to access the room.
     */
    public boolean requiresAuthorization(){
        return this.auth;
    }

    @Override
    public boolean isIntersection(){
        return false;
    }
}
