package tk.pathfinder.Map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import tk.pathfinder.Networking.Beacon;

/***
 * Represents a graph for a building map.
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class Map {

    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private ArrayList<Beacon> beacons;

    private Integer id;
    private String name;

    /**
     * @param id The database index of the map.
     * @param name The name of the map.
     * @param edges The set of edges.
     * @param beacons The set of beacons.
     */
    public Map(Integer id, String name, Edge[] edges, Beacon[] beacons){
        if(edges == null)
            throw new NullPointerException("edges");

        this.id = id;
        this.name = name;

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

        this.beacons = new ArrayList<>();
        this.beacons.addAll(Arrays.asList(beacons));
    }

    /**
     * @return an iterator pointing to all map nodes.
     */
    Iterator<Node> getNodes(){
        return nodes.iterator();
    }

    /***
     * @return an iterator pointing to all edges.
     */
    public Iterator<Edge> getEdges(){
        return edges.iterator();
    }

    public Iterator<Beacon> getBeacons() { return beacons.iterator(); }

    /***
     * @return an iterator pointing to all rooms.
     */
    public Iterator<Room> getRooms(){
        List<Room> rooms = new ArrayList<>();
        for(Iterator<Node> i = getNodes(); i.hasNext(); ){
            Node n = i.next();
            if(n instanceof Room)
                rooms.add((Room)n);
        }

        return rooms.iterator();
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

    /**
     * @param type A type of floor connector
     * @return An iterator pointing to all floor connectors of the given type.
     */
    Iterator<FloorConnector> getFloorConnectors(FloorConnector.FloorConnectorTypes type){
        List<FloorConnector> connectors = new ArrayList<>();
        for(Iterator<Node> i = getNodes(); i.hasNext(); ){
            Node n = i.next();
            if(n instanceof FloorConnector && ((FloorConnector)n).getType() == type)
                connectors.add((FloorConnector)n);
        }
        return connectors.iterator();
    }

    /***
     * @param n The current edge
     * @return an iterator pointing to all edges connected to the current, sorted by weight.
     */
    Iterator<Edge> getNextEdges(Node n){
        final ArrayList<Edge> resultEdges = new ArrayList<>();

        for(Edge e : edges)
            if(e.contains(n))
                resultEdges.add(e);

            Collections.sort(resultEdges);

        return resultEdges.iterator();
    }

    /**
     * @return An array with [0] being the lowest floor, and [1] being the highest.
     */
    public int[] getFloorRange(){
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;

        if(nodes.size() == 0)
            return new int[] {0, 0};
        for(Node n : nodes){
            int floor = n.getPoint().getY();
            if(floor < lowest)
                lowest = floor;
            if(floor > highest)
                highest = floor;
        }

        return new int[] {lowest, highest};
    }


    /**
     * Get the edge that connects the two nodes.
     * @param a A node.
     * @param b A node.
     * @return The edge, or null if it does not exist.
     */
    Edge getEdge(Node a, Node b){
        for(Edge e : edges)
            if(e.contains(a) && e.contains(b))
                return e;
        return null;
    }

    /***
     * Get the closest node to a given location
     * @param p The point to use as a reference
     * @return the closest node.
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

    /**
     * Search for a destination by name.
     * @param keywords The search terms to use.
     * @return A list of destinations matching the given keywords.
     */
    public List<Room> findDestination(String keywords){
        List<Room> ret = new ArrayList<>();
        String[] parts = keywords.split(" ");

        for(Iterator<Room> rooms = getRooms(); rooms.hasNext(); ){
            Room i = rooms.next();
            for(String j : parts) {
                j = j.toLowerCase();
                if (i.getRoomNumber() != null && i.getRoomNumber().toLowerCase().contains(j)
                        || i.getName() != null && i.getName().toLowerCase().contains(j)) {
                    ret.add(i);
                    break;
                }
            }
        }

        return ret;
    }

    // get the closest floor connector to a point.
    private FloorConnector getClosestFloorConnector(Point p) throws IllegalStateException{
        FloorConnector result = null;
        double min = Double.POSITIVE_INFINITY; // minimum distance

        for(Iterator<FloorConnector> fcs = getFloorConnectors(); fcs.hasNext(); ){
            FloorConnector i = fcs.next();
            double dist = p.distance(i.getPoint());
            if(dist < min){
                min = dist;
                result = i;
            }
        }

        return result;
    }

    /**
     * Approximate the distance to a node.
     * @param a The starting location
     * @param b The destination node
     * @return The distance, or a negative number if there was an error.
     */
    public int getNodeDistance(Point a, Node b){
        if(a == null || b == null)
            return -2;
        if(!nodes.contains(b))
            return -1;

        // different floor!
        if(a.getY() != b.getPoint().getY()){
            FloorConnector fc = getClosestFloorConnector(a);
            if(fc == null)
                return -2;
            // get distance to the floor, and then to the destination.
            return (int)(a.distance(new Point(fc.getPoint().getX(), a.getY(), fc.getPoint().getZ())) +
                    b.getPoint().distance(new Point(fc.getPoint().getX(), b.getPoint().getY(), fc.getPoint().getZ())));
        }
        else
            return (int)a.distance(b.getPoint());
    }

    /**
     * @return The name of the map.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The database index of the map.
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param ssid A beacon SSID.
     * @return The beacon matching the given SSID, or null.
     */
    public Beacon getBeacon(String ssid){
        for(Beacon i : beacons)
            if(i.getSSID().equals(ssid))
                return i;
        return null;
    }
}
