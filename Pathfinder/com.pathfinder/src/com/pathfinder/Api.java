package com.pathfinder;

import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

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
        JSONObject json = new JSONObject(response);

        if(con.getResponseCode() == 500){
            throw new IOException("Server error encountered: " + json.getString("details"));
        }

        if(con.getResponseCode() == 204 || con.getResponseCode() == 404){
            throw new IOException("Empty response received.");
        }

        Integer index = json.getInt("id");
        String name = json.getString("name");
        String addr = json.getString("address");
        String img = json.getString("map_image");
        InputStream is = new ByteArrayInputStream(img.getBytes());
        BufferedImage image = ImageIO.read(is);

        return new Map(index, name, addr, image, new Edge[0]);
    }

    /**
     * Get a list of maps from the database.
     * @return A JSONObject containing the map data.
     * @throws IOException If the maps could not be retrieved.
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

        JSONObject json = new JSONObject(response);

        if(con.getResponseCode() != 200)
            throw new IOException("Error getting maps: " + json.getString("details"));

        return json;
    }

    private static String getReader(HttpsURLConnection con) throws IOException {
        con.setUseCaches(false);
        con.connect();

        BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
        return r.readLine();
    }
}
