package com.pathfinder;

/***
 *  Represents a node on a graph
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public abstract class Node {
    protected Point point;

    /***
     * @return the location of the node in 3D space
     */
    public Point getPoint(){
        return point;
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
}
