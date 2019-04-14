package tk.pathfinder.Map;

/***
 * Represents an intersection between two or more edges.
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class Intersection extends Node {

    /**
     * @param id The database index of the node.
     * @param p The location of the node on its map.
     */
    public Intersection(int id, Point p){
        this.id = id;
        point = p;
    }


    /***
     * @return true if the node represents an intersection between two edges.
     */
    @Override
    public boolean isIntersection() {
        return true;
    }
}
