package tk.pathfinder.Map;

import java.lang.*;


class shortestPath {

    static final int N = Integer.MAX_VALUE;

    //for finding the node with minimum distance from the nodes not yet included
    int minDistanceNode(int dist[], Boolean spt[]) {
        // Initialize min value
        int min = Integer.MAX_VALUE, min_index = -1;

        for (int n = 0; n < N; n++)
            //shortest path tree will keep track of nodes included in the shortest path tree
            if (spt[n] == false && dist[n] <= min) {
                min = dist[n];
                min_index = n;
            }

        return min_index;
    }



    void dijkstra(int map[][], int start) {

        //dist will store the shortest distance from start to i
        int dist[] = new int[N];

        //will be true if node i is included in shortest path tree
        Boolean spt[] = new Boolean[N];

        // Initialize all distances as infinite
        for (int i = 0; i < N; i++) {
            dist[i] = Integer.MAX_VALUE;
            spt[i] = false;
        }

        // Distance of start node from itself is always 0
        dist[start] = 0;

        //finding shortest path for all nodes
        for (int count = 0; count < N - 1; count++) {

            //picking minimum dist node from the set of nodes that have not been looked at yet
            int minNode = minDistanceNode(dist, spt);

            //notes the node is looked at
            spt[minNode] = true;


            for (int n = 0; n < N; n++)

                //updates dist if its not in the spt yet
                if (!spt[n] && map[minNode][n] != 0 &&
                        dist[minNode] != Integer.MAX_VALUE && dist[minNode] + map[minNode][n] < dist[n])
                        dist[n] = dist[minNode] + map[minNode][n];
        }

    }
}