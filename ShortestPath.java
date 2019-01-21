package shortestpath;

import java.util.*; 
import java.lang.*; 
import java.io.*; 

/**
 *
 * @author Zack
 */
public class ShortestPath {

    static final int NODE=5;
    
        //find node with minimum distance value not already in tree
        int minDistance(int dist[], Boolean shortTree[]) 
    { 
        
        int min = Integer.MAX_VALUE, min_index=-1; 
  
        for (int v = 0; v < NODE; v++) 
            if (shortTree[v] == false && dist[v] <= min) 
            { 
                min = dist[v]; 
                min_index = v; 
            } 
  
        return min_index; 
    }
    
    void algorithm(int graph[][], int source)
    {
        int dist[] = new int [NODE];
        
        //returns true if node is included in shortest tree
        Boolean shortTree[] = new Boolean[NODE];
        
        for (int i = 0; i < NODE; i++)
        {
            dist[i] = Integer.MAX_VALUE;
            shortTree[i] = false;
        }
        
        //distance from self is 0
        dist[source] = 0;
        
        
        //finding shortest path for nodes
        for (int count = 0; count < NODE-1; count++)
        {
            
            int u = minDistance(dist, shortTree);
            
            shortTree[u] = true;
            
            for (int v = 0; v < NODE; v++)
            {
                if (!shortTree[v] && graph[u][v]!=0 && 
                        dist[u] != Integer.MAX_VALUE && 
                        dist[u]+graph[u][v] < dist[v]) 
                {
                    dist[v] = dist[u] + graph[u][v];
                }
            }
        }
        
    }
    
}
