package tk.pathfinder.Map;

import android.support.annotation.NonNull;

import java.util.Iterator;

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
