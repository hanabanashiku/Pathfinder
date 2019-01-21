package com.pathfinder;

/**
 * Represents a link between two nodes.
 */
public class Edge {
    private Node node1;
    private Node node2;
    private int weight;

    /**
     * @param node1 The first node
     * @param node2 The node to connect to
     * @param weight The edge's weight (usually the distance)
     * @throws IllegalArgumentException If the two nodes are the same, or if the weight is not positive.
     */
    public Edge(Node node1, Node node2, int weight) throws IllegalArgumentException {
        if(node1 == null)
            throw new NullPointerException("node1 cannot be null!");
        if(node2 == null)
            throw new NullPointerException("node2 cannot be null!");
        if(node1 == node2)
            throw new IllegalArgumentException("The two nodes cannot be the same node!");
        if(weight <= 0)
            throw new IllegalArgumentException("The weight must be a positive value!");
        this.node1 = node1;
        this.node2 = node2;
        this.weight = weight;
    }

    public Node getNode1() {
        return node1;
    }

    public Node getNode2() {
        return node2;
    }

    public int getWeight() {
        return weight;
    }
}
