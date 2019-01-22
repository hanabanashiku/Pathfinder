package com.pathfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/***
 * Represents a graph for a building map.
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class Map {

    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    public Map(Edge[] edges){
        if(edges == null)
            throw new NullPointerException("edges");

        nodes = new ArrayList<>();
        this.edges = new ArrayList<>();

        for (Edge edge : edges) {
            if (edge != null && !this.edges.contains(edge))
                this.edges.add(edge);

            if (edge != null && edge.getNode1() != null && !nodes.contains(edge.getNode1()))
                nodes.add(edge.getNode1());
            if (edge != null && edge.getNode2() != null && !nodes.contains(edge.getNode2()))
                nodes.add(edge.getNode2());
        }
    }

    /**
     * @return an iterator pointing to all map nodes.
     */
    public Iterator<Node> getNodes(){
        return nodes.iterator();
    }

    /***
     * @return an iterator point to all edges.
     */
    public Iterator<Edge> getEdges(){
        return edges.iterator();
    }

    /***
     * @return an iterator pointing to all nodes that function as elevators, staircases, or escalators.
     */
    public Iterator<FloorConnector> getFloorConnectors(){
        ArrayList<FloorConnector> elevators = new ArrayList<>();

        for(Iterator<Node> i = getNodes(); i.hasNext(); ){
            Node n = i.next();
            if(n instanceof FloorConnector)
                elevators.add((FloorConnector)n);
        }
        return elevators.iterator();
    }

    /***
     * @param n The current edge
     * @return an iterator pointing to all edges connected to the current, sorted by weight.
     */
    public Iterator<Edge> getNextEdges(Node n){
        final ArrayList<Edge> resultEdges = new ArrayList<>();

        for(Edge e : edges)
            if(e.getNode1() == n || e.getNode2() == n)
                resultEdges.add(e);

            Collections.sort(resultEdges);

        return resultEdges.iterator();
    }

    /***
     * Add an edge to the map, and any associated nodes.
     * @param e The edge ot add.
     */
    public void addEdge(Edge e){
        if(e == null || edges.contains(e))
            return;

        edges.add(e);
        if(nodes.contains(e.getNode1()))
            nodes.add(e.getNode1());
        if(nodes.contains(e.getNode2()))
            nodes.add(e.getNode2());
    }

    /***
     * Remove an edge from the map.
     * @param e The edge to remove.
     * @return true on success.
     */
    public boolean removeEdge(Edge e){
        if(e == null || !edges.contains(e))
            return false;

        edges.remove(e);
        if(!getNextEdges(e.getNode1()).hasNext())
            nodes.remove(e.getNode1());
        if(!getNextEdges(e.getNode2()).hasNext())
            nodes.remove(e.getNode2());
        return true;
    }

    /***
     * Get the closest node to a given location
     * @param p The point to use as a reference
     * @return the closest node.
     * @throws IllegalStateException if there are no nodes at all
     */
    public Node closestNode(Point p) throws IllegalStateException {
        Node n = null; // result
        double dist = -1; // minimum distance
        Iterator<Node> i = getNodes();

        if(!i.hasNext())
            throw new IllegalStateException("There are no nodes in the map!");

        // search each node and compare the distances
        while(i.hasNext()){
            Node current = i.next();
            // The user can't be closest to this point if the floor is different!
            if(p.getY() != current.point.getY())
                continue;
            double currentDistance = p.distance(current.getPoint());
            if(dist == -1 || currentDistance < dist){
                dist = currentDistance;
                n = current;
            }
        }
        return n;
    }

    /***
     * Get the closest floor connector to a given location
     * @param p The point to use as a reference
     * @return the closest node
     * @throws IllegalStateException if there are no nodes at all
     */
    public FloorConnector closestFloorConnector(Point p) throws IllegalStateException {
        FloorConnector n = null; // result
        double dist = -1; // minimum distance
        Iterator<FloorConnector> i = getFloorConnectors();

        if(!i.hasNext())
            throw new IllegalStateException("There are no nodes in the map!");

        // search each node and compare the distances
        while(i.hasNext()){
            FloorConnector current = i.next();
            // The user can't be closest to this point if the floor is different!
            if(p.getY() != current.point.getY())
                continue;
            double currentDistance = p.distance(current.getPoint());
            if(dist == -1 || currentDistance < dist){
                dist = currentDistance;
                n = current;
            }
        }
        return n;
    }

}
