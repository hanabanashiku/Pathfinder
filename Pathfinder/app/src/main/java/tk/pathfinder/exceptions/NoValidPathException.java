package tk.pathfinder.exceptions;

import tk.pathfinder.Map.Node;
import tk.pathfinder.Map.Room;

public class NoValidPathException extends Exception {

    private Node startNode;
    private Room destNode;

    public NoValidPathException(Node start, Room dest){
        super("There was no valid path found to the given destination, Room " + dest.getRoomNumber());
        startNode = start;
        destNode = dest;
    }

    public Node getStart() {
        return startNode;
    }

    public Room getDestination(){
        return destNode;
    }
}
