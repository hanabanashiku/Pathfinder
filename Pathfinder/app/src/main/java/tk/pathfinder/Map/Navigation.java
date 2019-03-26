package tk.pathfinder.Map;

import tk.pathfinder.Networking.Beacon;
import tk.pathfinder.exceptions.NoValidPathException;
import java.lang.UnsupportedOperationException;

public class Navigation {

    /**
     * Find the shortest path between two locations.
     * @param map The map to navigate.
     * @param current The closest node to the user's current position.
     * @param goal The user's desired destination.
     * @return A list of edges, in order from current to goal.
     * @throws NoValidPathException if there is no possible way to get from the current location to the destination.
     */
    public static Path NavigatePath(Map map, Node current, Room goal) throws NoValidPathException {
        return NavigatePath(map, current, goal, FloorConnector.FloorConnectorTypes.ELEVATOR);
    }

       /**
     * Find the shortest path between two locations.
     * @param map The map to navigate.
     * @param current The closest node to the user's current position.
     * @param goal The user's desired destination.
     * @param connector_preference The preferred method of ascending floors.
     * @return A list of edges, in order from current to goal.
     * @throws NoValidPathException if there is no possible way to get from the current location to the destination.
     */
    public static Path NavigatePath(Map map, Node current, Room goal, FloorConnector.FloorConnectorTypes connector_preference) throws NoValidPathException {
        throw new UnsupportedOperationException();
    }

    //converts signal strength (in Mhz) to distance (in meters)
    public double strengthToDistance(Beacon strength, Beacon frequency){


        double exp = (27.55 - (20 * Math.log10((double)frequency.getFrequency())) + Math.abs((double)strength.getLevel())) / 20.0;
        return Math.pow(10.0, exp);
    }

}
