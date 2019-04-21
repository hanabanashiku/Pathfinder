package tk.pathfinder.Map;

import java.util.Iterator;

import androidx.annotation.NonNull;

/**
 * Represents a sequences of edges on a map leading to a destination.
 */
public class Path implements Iterable<Edge> {
    private Edge[] edges;

    /**
     * @param e A list of edges.
     */
    Path(Edge[] e){
        if(e == null)
            throw new IllegalArgumentException();
        edges = e;
    }

    /**
     * @return The number of edges on the path.
     */
    public int length(){
        return edges.length;
    }

    /**
     * @return The total distance traveled by the path.
     */
    public int distance(){
        int distance = 0;
        for (Edge e : this) {
            distance += e.getWeight();
        }
        return distance;
    }

    public Edge get(int i) {
        if(i < 0 || i >= edges.length)
            throw new IndexOutOfBoundsException();
        return edges[i];
    }

    /**
     * Combine two paths together.
     * @param path The path to append.
     * @return A new path as a combination of this path and the one provided.
     * @throws IllegalArgumentException if the end node of the first path does not equal the start node of the second path.
     */
    Path append(Path path){
        if(edges[edges.length - 1] != path.edges[path.length() - 1]) // TODO probably a bug
            throw new IllegalArgumentException("The end node of the first path must equal the start node of the second.");

        Edge[] arr = new Edge[edges.length + path.length()];
        System.arraycopy(edges, 0, arr, 0, edges.length);
        System.arraycopy(path.edges, 0, arr, edges.length, path.length());
        return new Path(arr);
    }

    /**
     * Check if there exists an edge connecting the two nodes.
     * @param a Node 1
     * @param b Ndde 2
     * @return True if there is an edge connecting the two nodes.
     */
    public boolean contains(Node a, Node b){
        for(Edge e : edges){
            if(e.getOther(a) == b)
                return true;
        }
        return false;
    }

    /**
     * Check if a node exists on the current path.
     * @param a The node to check.
     * @return True if the node lies on the path.
     */
    public boolean contains(Node a){
        for(Edge e : edges)
            if(e.contains(a))
                return true;
        return false;
    }


    @NonNull
    @Override
    public Iterator<Edge> iterator() {
        return new Iterator<Edge>() {
            private int i = -1;

            @Override
            public boolean hasNext() {
                return i < edges.length - 1;
            }

            @Override
            public Edge next() {
                i++;
                return edges[i];
            }
        };
    }
}
