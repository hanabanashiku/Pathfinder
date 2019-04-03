package tk.pathfinder.Map;

import java.util.Iterator;

import androidx.annotation.NonNull;

public class Path implements Iterable<Edge> {
    private Edge[] edges;

    public Path(Edge[] e){
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
     * @return The total distance travelled by the path.
     */
    public int distance(){
        int distance = 0;
        for(Iterator<Edge> i = iterator(); i.hasNext(); ){
            Edge e = i.next();
            distance += e.getWeight();
        }
        return distance;
    }

    public Edge get(int i) {
        if(i < 0 || i >= edges.length)
            throw new IndexOutOfBoundsException();
        return edges[i];
    }

    public Path append(Path path){
        if(edges[edges.length - 1] != path.edges[path.length() - 1])
            throw new IllegalArgumentException("The end node of the first path must equal the start node of the second.");

        Edge[] arr = new Edge[edges.length + path.length()];
        System.arraycopy(edges, 0, arr, 0, edges.length);
        System.arraycopy(path.edges, 0, arr, edges.length, path.length());
        return new Path(arr);
    }


    @NonNull
    @Override
    public Iterator<Edge> iterator() {
        return new Iterator<Edge>() {
            private int i = -1;

            @Override
            public boolean hasNext() {
                return i < edges.length;
            }

            @Override
            public Edge next() {
                i++;
                return edges[i];
            }
        };
    }
}
