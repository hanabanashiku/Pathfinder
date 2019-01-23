package com.pathfinder;

import java.util.HashMap;
import java.util.stream.IntStream;

/***
 * Represents a node that allows a user to move between floors, such as an elevator.
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class FloorConnector extends Node {

    public enum FloorConnectorTypes{
        STAIRCASE, ELEVATOR, ESCALATOR
    }

    private String name;
    private boolean auth;
    private HashMap<Integer, Boolean> floors;
    private boolean operational;
    private FloorConnectorTypes type;

    /***
     * @param p The location of the node in 3D space.
     * @param name The name of the node (set to null for a generic name).
     * @param type The type of connector.
     * @param floors An array containing all floors accessible from the connector.
     * @param requiresAuthorization if true, the connector requires authorization to use.
     */
    public FloorConnector(Point p, String name, FloorConnectorTypes type, int[] floors, boolean requiresAuthorization){
        if(floors == null || floors.length == 0)
            throw new NullPointerException("floors must be non-empty");
        processConstructor(p, name, type, requiresAuthorization);

        this.floors = new HashMap<>();
        for (int floor : floors) this.floors.put(floor, true);
    }

    /***
     * @param p The location of the node in 3D space.
     * @param name The name of the node (set to null for a generic name).
     * @param type The type of connector.
     * @param firstFloor The most bottom floor number that the connector can reach.
     * @param lastFloor The most highest floor number that the connector can reach.
     * @param requiresAuthorization if true, the connector requires authorization to use.
     * @throws IllegalArgumentException on invalid firstFloor or lastFloor arguments.
     */
    public FloorConnector(Point p, String name, FloorConnectorTypes type, int firstFloor, int lastFloor,
                          boolean requiresAuthorization) throws IllegalArgumentException {
        processConstructor(p, name, type, requiresAuthorization);

        if(firstFloor >= lastFloor)
            throw new IllegalArgumentException("The last floor must be greater than the first floor!");
        IntStream.rangeClosed(firstFloor, lastFloor).forEach(floor -> this.floors.put(floor, true));
    }

    private void processConstructor(Point p, String name, FloorConnectorTypes type, boolean requiresAuthorization){
        if(p == null)
            throw new NullPointerException("p");

        this.point = p;
        this.type = type;
        this.auth = requiresAuthorization;
        setName(name);
        operational = true;
    }

    /***
     * @return The name of the node.
     */
    public String getName() {
        return name;
    }

    /***
     * @param value The name of the node.
     */
    public void setName(String value) {
        if(value == null)
            switch(type){
                case STAIRCASE:
                    this.name = "Staircase";
                    break;
                case ELEVATOR:
                    this.name = "Elevator";
                    break;
                case ESCALATOR:
                    this.name = "Escalator";
                    break;
            }
        else this.name = value;
    }

    /***
     * @return The type of connector
     */
    public FloorConnectorTypes getType() {
        return type;
    }

    /***
     * @param type The connector type
     */
    public void setType(FloorConnectorTypes type) {
        this.type = type;
    }

    /***
     * @return If true, the node requires special access, like a pinpad or keycard.
     */
    public boolean requiresAuthorization(){
        return this.auth;
    }

    /***
     * @param value whether the node requires authorization to use
     */
    public void setAuthorization(boolean value){
        this.auth = value;
    }

    /***
     * @return true if the node is available for use.
     */
    public boolean isOperational(){
        return this.operational;
    }

    /***
     * Mark the node as available for use.
     */
    public void open(){
        this.operational = true;
    }

    /***
     * Mark the node as not operational.
     */
    public void close(){
        this.operational = false;
    }

    /***
     * @param floor The floor to check.
     * @return true if the node may be used to access the floor.
     */
    public boolean isFloorAccessible(int floor){
        if(!isOperational())
            return false;
        if(this.floors.containsKey(floor))
            return this.floors.get(floor);
        return false;
    }

    /***
     * Set whether or not a floor is accessible from the node.
     * @param floor The floor number.
     * @param isAccessible true if the floor is accessible.
     */
    public void setFloorAccessibility(int floor, boolean isAccessible){
        this.floors.put(floor, isAccessible);
    }

    public boolean isIntersection() { return false; }
}
