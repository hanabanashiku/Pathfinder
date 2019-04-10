package tk.pathfinder.Map;

import java.util.Comparator;
import java.util.Objects;

/**
 * Represents a link between two nodes.
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class Edge implements Comparator<Edge>, Comparable<Edge> {
    private Node node1;
    private Node node2;
    private double weight = -1;

    /**
     * @param node1 The first node
     * @param node2 The node to connect to
     * @throws IllegalArgumentException If the two nodes are the same, or if the weight is not positive.
     */
    public Edge(Node node1, Node node2) throws IllegalArgumentException {
        if(node1 == null)
            throw new NullPointerException("node1 cannot be null!");
        if(node2 == null)
            throw new NullPointerException("node2 cannot be null!");
        if(node1 == node2)
            throw new IllegalArgumentException("The two nodes cannot be the same node!");
        this.node1 = node1;
        this.node2 = node2;
    }

    public Node getNode1() {
        return node1;
    }

    public Node getNode2() {
        return node2;
    }

    /**
     * Get the node that is connected to the given node.
     * @param n The known node
     * @return The unknown node, or null.
     */
    Node getOther(Node n){
        if(n.equals(node1))
            return node2;
        if(n.equals(node2))
            return node1;
        return null;
    }

    /***
     * @return The distance spanned by the edge.
     */
    double getWeight(){
        if(weight == -1)
            weight = node1.getPoint().distance(node2.getPoint());
        return weight;
    }


    /**
     * Check if the node is one of the two nodes the edge connects.
     * @param n The node to check.
     * @return True if the node is contained in the edge.
     */
    boolean contains(Node n){
        return node1.equals(n) || node2.equals(n);
    }

    @Override
    public int compareTo(Edge edge) {
        return this.compare(this, edge);
    }

    @Override
    public int compare(Edge edge, Edge t1) {
        return Double.compare(edge.getWeight(), t1.getWeight());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return (node1.equals(edge.node1) &&
                node2.equals(edge.node2)) ||
                (node1.equals(edge.node2) &&
                node2.equals(edge.node1));
    }

    @Override
    public int hashCode() {
        return Objects.hash(node1, node2);
    }
}
