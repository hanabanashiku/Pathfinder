package tk.pathfinder.tests;

import org.junit.*;

import java.io.IOException;
import java.util.List;

import tk.pathfinder.Map.Map;
import tk.pathfinder.Map.Node;
import tk.pathfinder.Map.Point;
import tk.pathfinder.Map.Room;
import tk.pathfinder.Networking.Api;


public class ApiTests {
    @Test
    public void findMapsTest(){
        try{
            Api.findMaps(null);
            Assert.fail();
        }
        catch(IllegalArgumentException ignored){}
        catch(IOException ignored) { Assert.fail(); }

        Api.MapQueryResult[] results = null;
        try{
            results = Api.findMaps("oakland");
        }
        catch(IOException e){
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        Assert.assertNotEquals(results.length, 0);

        Api.MapQueryResult r = results[0];

        Assert.assertNotNull(results[0].getName());
    }

    @Test
    public void getMapTest(){
        try{
            Api.getMap(0);
            Assert.fail();
        }
        catch(IOException ignored) {}

        Map map = null;

        try{
            map = Api.getMap(73); // Rez de chausse
        }
        catch(IOException e){
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        Assert.assertNotNull(map);

        Assert.assertNotNull(map.getName());

        Assert.assertEquals(73, (int)map.getId());

        Assert.assertEquals(new int[] {1, 1}, map.getFloorRange());

        Assert.assertEquals("Rez de Chausse", map.getName());
    }

    @Test
    public void findDestination(){
        Map map = null;
        try{
            map = Api.getMap(73); // Rez de chausse
        }
        catch(IOException e){
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        try{
            map.findDestination(null);
            Assert.fail();
        }
        catch(IllegalArgumentException ignored){ }

        List<Room> results = map.findDestination("blah");

        Assert.assertEquals(0, results.size());

        results = map.findDestination("garage");
        Assert.assertNotEquals(0, results.size());
        results = map.findDestination("Garage");
        Assert.assertNotEquals(0, results.size());
        results = map.findDestination("GARAGE");
        Assert.assertNotEquals(0, results.size());
        results = map.findDestination(" garage");
        Assert.assertNotEquals(0, results.size());
        results = map.findDestination("garage ");
        Assert.assertNotEquals(0, results.size());
        results = map.findDestination("the garage");
        Assert.assertNotEquals(0, results.size());
    }

    @Test
    public void closestNode(){
        Map map = null;
        try{
            map = Api.getMap(73); // Rez de chausse
        }
        catch(IOException e){
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        try{
            map.closestNode(null);
            Assert.fail();
        }
        catch(IllegalArgumentException ignored) {}

        Point p = new Point(630, 1, 630);
        Node n = map.closestNode(p);
        Assert.assertEquals(158, n.getId());

        p = new Point(430, 1, 670);
        n = map.closestNode(p);
        Assert.assertEquals(163, n.getId());
    }
}
