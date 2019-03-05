package tk.pathfinder.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

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
        String name, addr, img;
        try{
            index = json.getInt("id");
            name = json.getString("name");
            addr = json.getString("address");
            img = json.getString("map_image");
        }
        catch(JSONException e){
            throw new IOException("Invalid JSON data type received: " + e.getMessage());
        }

        InputStream is = new ByteArrayInputStream(img.getBytes());
        Bitmap image = BitmapFactory.decodeStream(is);

        return new Map(index, name, addr, image, new Edge[0]);
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
}
