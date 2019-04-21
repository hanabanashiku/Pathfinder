package tk.pathfinder.Networking;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import tk.pathfinder.Map.*;
/**
 * Contains API calls for map data.
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class Api {

    /**
     * Get a map from the database
     * @param id The map id.
     * @return The map.
     * @throws IOException If there was an error returned by the HTTP connection or the endpoint.
     */
    public static Map getMap(Integer id) throws IOException{
        HttpsURLConnection con;
        try{
            URL url = new URL("https://path-finder.tk/api/maps?id=" + id.toString());
            con = (HttpsURLConnection)url.openConnection();
        }
        catch(MalformedURLException e){
            throw new RuntimeException("Invalid URL encountered");
        }
        String response = getReader(con);
        JSONObject json;
        try{
            json = new JSONObject(response);
        }
        catch(JSONException e){
            throw new IOException("Could not parse JSON result");
        }

        if(con.getResponseCode() == 500){
            try{
                throw new IOException("Server error encountered: " + json.getString("details"));
            }
            catch(JSONException e){
                throw new IOException("Server error encountered; additionally, a JSON error was encountered while parsing the error.");
            }
        }

        if(con.getResponseCode() == 204 || con.getResponseCode() == 404){
            throw new IOException("Empty response received.");
        }

        Integer index;
        String name;
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        List<Beacon> beacons = new ArrayList<>();
        try{
            index = json.getInt("id");
            name = json.getString("name");

            // parse map nodes
            JSONArray nArr = json.getJSONArray("nodes");
            for(int i = 0; i < nArr.length(); i++){
                JSONObject j = nArr.getJSONObject(i);
                int n_id = j.getInt("id");
                JSONObject n_corr = j.getJSONObject("coordinate");
                Point p = new Point((int)(n_corr.getDouble("x")*1000),
                        (int)(n_corr.getDouble("y")), (int)(n_corr.getDouble("z")*1000));
                switch(j.getString("type")){
                    case "room":
                        String room_num = j.getString("room_number");
                        if(room_num.equals("null"))
                            room_num = null;
                        String room_name = j.getString("name");
                        if(room_name.equals("null"))
                            room_name = null;
                        boolean auth = j.getBoolean("requires_auth");
                        nodes.add(new Room(n_id, p, room_num, room_name, auth));
                        break;

                    case "floor_connector":
                        String fc_name = j.getString("name");
                        FloorConnector.FloorConnectorTypes fc_type = FloorConnector.FloorConnectorTypes.values()[j.getInt("connector_type")];
                        boolean fc_auth = j.getBoolean("requires_auth");
                        boolean operating = j.getBoolean("is_operational");
                        nodes.add(new FloorConnector(n_id, p, fc_name, fc_type, new int[0], operating, fc_auth));
                        break;

                    case "intersection":
                        nodes.add(new Intersection(n_id, p));
                        break;
                }
            }

            // parse map edges
            JSONArray eArr = json.getJSONArray("edges");
            for(int i = 0; i < eArr.length(); i++){
                JSONArray j = eArr.getJSONArray(i);
                int n1_id = j.getInt(0);
                int n2_id = j.getInt(1);
                Node node1 = findNode(nodes, n1_id);
                Node node2 = findNode(nodes, n2_id);
                if(node1 == null || node2 == null)
                    continue;
                edges.add(new Edge(node1, node2));
            }

            // parse map beacons
            JSONArray bArr = json.getJSONArray("beacons");
            for(int i = 0; i < bArr.length(); i++){
                JSONObject j = bArr.getJSONObject(i);
                String ssid = j.getString("ssid");
                JSONObject b_corr = j.getJSONObject("coordinate");
                Point p = new Point((int)(b_corr.getDouble("x")*1000),
                        (int)(b_corr.getDouble("y")), (int)(b_corr.getDouble("z")*1000));
                beacons.add(new Beacon(ssid, p));
            }
        }
        catch(JSONException e){
            throw new IOException("Invalid JSON data type received: " + e.getMessage());
        }

        Edge[] eArr = new Edge[edges.size()];
        Beacon[] bArr = new Beacon[beacons.size()];
        eArr = edges.toArray(eArr);
        bArr = beacons.toArray(bArr);

        return new Map(index, name, eArr, bArr);
    }


    /**
     * Get a list of maps from the database whose names match a search term.
     * @param keywords The search terms to return
     * @return A list of map information
     * @throws IOException on API failure.
     */
    public static MapQueryResult[] findMaps(String keywords) throws IOException {
        HttpsURLConnection con;

        try{
            keywords = URLEncoder.encode(keywords, "UTF-8");
            URL url = new URL("https://path-finder.tk/api/maps?q=" + keywords);
            con = (HttpsURLConnection)url.openConnection();
        }
        catch(MalformedURLException e){
            throw new RuntimeException("Invalid URL encountered");
        }

        String response = getReader(con);
        JSONObject json;

        // empty result!
        if(con.getResponseCode() == 204){
            return new MapQueryResult[0];
        }

        if(con.getResponseCode() != 200)
            try{
                throw new IOException("Error getting maps: " + new JSONObject(response).getString("details"));
            }
            catch(NullPointerException e){
                throw new IOException("Error retrieving maps.");
            }
            catch(JSONException e){
                throw new IOException("Error getting maps; additionally, a JSON error was encountered while parsing the error.");
            }

        try{
            json = new JSONObject(response);
            MapQueryResult[] results = new MapQueryResult[json.getInt("total")];
            JSONArray arr = json.getJSONArray("buildings");

            for(int i = 0; i < arr.length(); i++){
                JSONObject o = arr.getJSONObject(i);
                results[i] = new MapQueryResult(o.getInt("id"), o.getString("name"));
            }
            return results;
        }
        catch(JSONException e){
            throw new IOException("Could not parse the JSON data");
        }
    }

    private static String getReader(HttpsURLConnection con) throws IOException {
        con.setUseCaches(false);
        con.connect();

        BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
        return r.readLine();
    }

    // find a node matching a specific id
    private static Node findNode(List<Node> nodes, int id){
        for(Node n : nodes)
            if(n.getId() == id)
                return n;
        return null;
    }

    /**
     * Represents a result from trying to find a map using a set of keywords.
     */
    public static class MapQueryResult{
        private final int id;
        private final String name;

        public int getId() { return id; }
        public String getName() { return name;}

        /**
         * @param id The map index.
         * @param name The map name.
         */
        MapQueryResult(int id, String name){
            this.id = id;
            this.name = name;
        }
    }
}
