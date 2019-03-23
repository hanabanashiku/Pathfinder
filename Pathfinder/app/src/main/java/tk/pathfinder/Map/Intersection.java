package tk.pathfinder.Map;

/***
 * Represents an intersection between two or more edges.
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class Intersection extends Node {

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
