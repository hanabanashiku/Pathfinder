package tk.pathfinder.Map;

import java.util.Objects;

/***
 *  Represents a node on a graph
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public abstract class Node {
    protected int id;
    protected Point point;

    /***
     * @return the location of the node in 3D space
     */
    public Point getPoint(){
        return point;
    }

    /**
     * @return The database index of the node.
     */
    public int getId() { return id; }

    /***
     * @return the floor the node is located on.
     */
    public int getFloor(){
        return point.getY();
    }


    /***
     * @param value The location of the node in 3D space
     */
    public void setPoint(Point value){
        point = value;
    }

    /***
     * @return true if the node represents an intersection between two edges.
     */
    public abstract boolean isIntersection();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return Objects.equals(point, node.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point);
    }
}
