package tk.pathfinder.Map;

import java.util.ArrayList;
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
    private boolean visited;
    private ArrayList<Edge> edges = new ArrayList<Edge> ();

    /***
     * @return the location of the node in 3D space
     */
    public Point getPoint(){
        return point;
    }

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

    public boolean isVisited(){
        return visited;
    }
    
    public void setVisited(boolean visited){
        this.visited = visited;
    }
    
    public ArrayList<Edge> getEdges(){
        return edges;
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
        return visited == node.visited &&
                Objects.equals(point, node.point) &&
                Objects.equals(edges, node.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, visited, edges);
    }
}
