package tk.pathfinder.tests;

import com.google.android.material.internal.NavigationMenuItemView;

import org.junit.Assert;
import org.junit.Test;

import tk.pathfinder.Map.Point;
import tk.pathfinder.Networking.Beacon;
import tk.pathfinder.Map.Navigation;

public class BeaconTests {

    @Test
    public void ssidParseTest(){
        try{
            new Beacon(null, Point.getDefault());
            Assert.fail();
        }
        catch(IllegalArgumentException ignored) {}

        try{
            new Beacon("SSID", Point.getDefault());
            Assert.fail();
        }
        catch(IllegalArgumentException ignored) {}

        Beacon b = new Beacon("PF_5_20", Point.getDefault());
        Assert.assertEquals(Point.getDefault(), b.getLocation());
        Assert.assertEquals(5, b.getBuildingIndex());
        Assert.assertEquals(20, b.getIndex());
    }

    @Test
    public void strenghTest(){
        Beacon b = new Beacon("PF_1_2", Point.getDefault());

        try{
            b.setLevel(101);
            Assert.fail();
        }
        catch(IllegalArgumentException ignored) {}
        try{
            b.setLevel(-1);
            Assert.fail();
        }
        catch(IllegalArgumentException ignored) {}

        Assert.assertEquals(0, b.getLevel());

        b.setLevel(100);
        Assert.assertEquals(100, b.getLevel());
        b.setLevel(50);
        Assert.assertEquals(50, b.getLevel());
    }

    @Test
    public void triangulateTest(){
        try{
            Navigation.triangulate(null, null, null);
            Assert.fail();
        }
        catch(IllegalArgumentException ignored) {}

        Beacon b1; Beacon b2 = null; Beacon b3 = null;

        b1 = new Beacon("PF_1_1", new Point(10, 1, 10));

        b1.setLevel(100);

        Assert.assertEquals(new Point(10, 1, 10), Navigation.triangulate(b1, null, null));
        b1.setLevel(50);

        Assert.assertEquals(new Point(10, 1, 10), Navigation.triangulate(b1, null, null));

        b2 = new Beacon("PF_1_2", new Point(20, 1, 10));
        b1.setLevel(100);
        b2.setLevel(0);

        Assert.assertEquals(new Point(10, 1, 10), Navigation.triangulate(b1, b2, null));

        b1.setLevel(0);
        b2.setLevel(100);
        Assert.assertEquals(new Point(20, 1, 10), Navigation.triangulate(b1, b2, null));

        b1.setLevel(100);
        Assert.assertEquals(new Point(15, 1, 10), Navigation.triangulate(b1, b2, null));

        b1.setLevel(50);
        b2.setLevel(50);
        Assert.assertEquals(new Point(15, 1, 10), Navigation.triangulate(b1, b2, null));

        b1.setLevel(25);
        b2.setLevel(75);
        Assert.assertEquals(new Point(18, 1, 10), Navigation.triangulate(b1, b2, null));

        b1.setLevel(75);
        b2.setLevel(25);
        Assert.assertEquals(new Point(12, 1, 10), Navigation.triangulate(b1, b2, null));

        b3 = new Beacon("PF_1_3", new Point(15, 1, 0));
        b1.setLevel(50);
        b2.setLevel(50);
        b3.setLevel(50);
        Assert.assertEquals(new Point(15, 1, 5), Navigation.triangulate(b1, b2, b3));

        b1.setLevel(100);
        b2.setLevel(0);
        b3.setLevel(0);
        Assert.assertEquals(new Point(15, 1, 0), Navigation.triangulate(b1, b2, b3));

        b1.setLevel(0);
        b2.setLevel(100);
        b3.setLevel(0);
        Assert.assertEquals(new Point(2, 1, 10), Navigation.triangulate(b1, b2, b3));

        b1.setLevel(0);
        b2.setLevel(0);
        b3.setLevel(100);
        Assert.assertEquals(new Point(20, 1, 10), Navigation.triangulate(b1, b2, b3));


        b1.setLevel(0);
        b2.setLevel(50);
        b3.setLevel(50);
        Assert.assertEquals(new Point(17, 1, 5), Navigation.triangulate(b1, b2, b3));

        b1.setLevel(25);
        Assert.assertEquals(new Point(15, 1, 5), Navigation.triangulate(b1, b2, b3);
    }
}
