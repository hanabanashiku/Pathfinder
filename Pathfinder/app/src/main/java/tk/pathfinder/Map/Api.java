package tk.pathfinder.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import tk.pathfinder.Networking.Beacon;

/**
 * Contains API calls for map data.
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class Api {

    public static Map GetMap(Integer id) throws IOException{
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
        String name, addr;
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        List<Beacon> beacons = new ArrayList<>();
        try{
            index = json.getInt("id");
            name = json.getString("name");
            addr = json.getString("address");

            // parse map nodes
            JSONArray nArr = json.getJSONArray("nodes");
            for(int i = 0; i < nArr.length(); i++){
                JSONObject j = nArr.getJSONObject(i);
                int n_id = j.getInt("id");
                JSONObject n_corr = j.getJSONObject("coordinate");
                Point p = new Point((int)(n_corr.getDouble("x")*100),
                        (int)(n_corr.getDouble("y")*100), (int)(n_corr.getDouble("z")*100));

                switch(j.getString("type")){
                    case "room":
                        String room_num = j.getString("room_number");
                        String room_name = j.getString("name");
                        boolean auth = j.getBoolean("requires_auth");
                        nodes.add(new Room(n_id, p, room_num, room_name, auth));
                        break;

                    case "floor_connector":
                        String fc_name = j.getString("name");
                        FloorConnector.FloorConnectorTypes fc_type = FloorConnector.FloorConnectorTypes.values()[j.getInt("connector_type")];
                        boolean fc_auth = j.getBoolean("requires_auth");
                        boolean operating = j.getBoolean("is_operational");
                        nodes.add(new FloorConnector(id, p, fc_name, fc_type, new int[0], operating, fc_auth));
                        break;

                    case "intersection":
                        nodes.add(new Intersection(id, p));
                        break;
                }
            }

            // parse map edges
            JSONArray eArr = json.getJSONArray("edges");
            for(int i = 0; i < eArr.length(); i++){
                JSONObject j = eArr.getJSONObject(i);
                int n1_id = j.getInt("node1");
                int n2_id = j.getInt("node2");
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
                Point p = new Point((int)(b_corr.getDouble("x")*100),
                        (int)(b_corr.getDouble("y")*100), (int)(b_corr.getDouble("z")*100));
                beacons.add(new Beacon(ssid, p));
            }
        }
        catch(JSONException e){
            throw new IOException("Invalid JSON data type received: " + e.getMessage());
        }

        /*InputStream is = new ByteArrayInputStream(img.getBytes());
        Bitmap image = BitmapFactory.decodeStream(is);*/

        return new Map(index, name, addr, (Edge[])edges.toArray(),(Beacon[])beacons.toArray());
    }

    /**
     * Get a list of maps from the database.
     * @return A JSONObject containing the map data.
     */
    public static JSONObject GetMaps() throws IOException {
        HttpsURLConnection con;
        try{
            URL url = new URL("https://path-finder.tk/api/maps?list");
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
            throw new IOException("Could not parse the JSON data");
        }

        if(con.getResponseCode() != 200)
            try{
                throw new IOException("Error getting maps: " + json.getString("details"));
            }
            catch(JSONException e){
                throw new IOException("Error getting maps; additionally, a JSON error was encountered while parsing the error.");
            }

        return json;
    }

    private static String getReader(HttpsURLConnection con) throws IOException {
        con.setUseCaches(false);
        con.connect();

        BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
        return r.readLine();
    }

    private static Node findNode(List<Node> nodes, int id){
        for(Node n : nodes)
            if(n.id == id)
                return n;
        return null;
    }
}
