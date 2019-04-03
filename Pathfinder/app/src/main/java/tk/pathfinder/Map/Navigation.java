package tk.pathfinder.Map;

import tk.pathfinder.Networking.Beacon;
import tk.pathfinder.exceptions.NoValidPathException;
import java.lang.UnsupportedOperationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
        return NavigatePath(map, current, goal, null);
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
        // on the same floor
        if(current.getFloor() == goal.getFloor()){
            Path p = aStar(map, current, goal);
            if(p == null)
                throw new NoValidPathException(current, goal);
            return p;
        }

        // go to a floor connector first
        List<FloorConnector> connectors = new ArrayList<>();
        Iterator<FloorConnector> i;
        int goalFloor = goal.getFloor();
        if(connector_preference == null)
            i = map.getFloorConnectors();
        else i = map.getFloorConnectors(connector_preference);

        while(i.hasNext()){
            FloorConnector c = i.next();
            if(!c.isFloorAccessible(goalFloor) || !c.isOperational())
                continue;
            connectors.add(i.next());
        }

        // TODO taking multiple connectors, taking others if the preference is not available.
        if(connectors.size() == 0){
            throw new NoValidPathException(current, goal);
        }
        Collections.sort(connectors, (o1, o2) ->
                Double.compare(o1.getPoint().distance(current.getPoint()),
                o2.getPoint().distance(current.getPoint())));

        // navigate to the floor connector, then navigate to the goal
        FloorConnector c = connectors.get(0);
        return aStar(map, current, c).append(aStar(map, c, goal));
    }

    private static Path aStar(Map map, Node start, Node goal){
        // evaluated nodes
        List<Node> closed = new ArrayList<>();

        // discovered nodes
        List<Node> open = new ArrayList<>();
        open.add(start);

        // each node can be most efficiently reached from the previous node.
        HashMap<Node, Node> cameFrom = new HashMap<>();

        // the cost of getting from the start node to the given node.
        HashMap<Node, Double> score = new HashMap<>();
        // the cost of getting from the start node to the goal by passing the node.
        HashMap<Node, Double> f = new HashMap<>();
        for(Iterator<Node> i = map.getNodes(); i.hasNext(); ){
            Node n = i.next();
            score.put(n, Double.MAX_VALUE);
            f.put(n, Double.MAX_VALUE);
        }
        score.put(start, 0.0);
        f.put(start, h(start, goal));

        while(!open.isEmpty()){
            Node current = minScore(open, f);

            // we've struck gold
            if(current == goal)
                return getPathResult(map, cameFrom, goal);

            open.remove(current);
            closed.add(current);

            // check all the neighbors
            for(Iterator<Edge> i = map.getNextEdges(current); i.hasNext(); ){
                Edge e = i.next();
                Node neighbor = e.getOther(current);

                // ignore if already evaluated
                if(closed.contains(neighbor))
                    continue;

                // calculate the new score
                double g = score.get(current) + e.getWeight();

                // new node
                if(!open.contains(neighbor))
                    open.add(neighbor);
                // not the best we've seen
                else if(g >= score.get(neighbor))
                    continue;

                // this is the best node so far
                cameFrom.put(neighbor, current);
                score.put(neighbor, g);
                f.put(neighbor, g + h(neighbor, goal));
            }
        }

        return null;
    }

    // get the node with the lowest f-score.
    private static Node minScore(List<Node> open, HashMap<Node, Double> f){
        double min = Double.MAX_VALUE;
        Node res = open.get(0);

        if(open.size() > 1)
            for(int i = 1; i < open.size(); i++){
                Node n = open.get(i);
                double score = f.get(n);
                if(score < min){
                    min = score;
                    res = n;
                }
            }
        return res;
    }

    private static double h(Node node, Node goal){
        return node.getPoint().distance(goal.getPoint());
    }

    private static Path getPathResult(Map map, HashMap<Node, Node> cameFrom, Node current){
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        nodes.add(current);

        while(cameFrom.keySet().contains(current)){
            current = cameFrom.get(current);
            nodes.add(current);
        }

        for(int i = nodes.size() - 1; i > 0; i--){
            Edge e = map.getEdge(nodes.get(i), nodes.get(i-1));
            if(e == null)
                throw new RuntimeException("An invalid path was encountered.");
            edges.add(e);
        }

        return new Path((Edge[])edges.toArray());
    }

    //converts signal strength (in Mhz) to distance (in meters)
    public double strengthToDistance(Beacon strength, Beacon frequency){


        double exp = (27.55 - (20 * Math.log10((double)frequency.getFrequency())) + Math.abs((double)strength.getLevel())) / 20.0;
        return Math.pow(10.0, exp);
    }

}
