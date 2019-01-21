package com.pathfinder;

/***
 * Represents an intersection between two or more edges.
 */
public class Intersection extends Node {

    public Intersection(Point p){
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
