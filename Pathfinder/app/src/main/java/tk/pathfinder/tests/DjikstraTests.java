package tk.pathfinder.tests;

import org.junit.*;
import org.junit.rules.ExpectedException;
import tk.pathfinder.Map.*;
import tk.pathfinder.Map.Map;
import tk.pathfinder.exceptions.NoValidPathException;

import java.util.*;

public class DjikstraTests {

    private Map map;
    private List<Node> nodes;
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    // In order to test our algorithm, we create a map of a mock two-story building.
    @BeforeClass
    public void SetUp(){
        nodes = new ArrayList<>();
        // build our map
        List<Edge> edges = new ArrayList<>();

        nodes.add(new Room(new Point(1, 1, 4), "1", null, false)); // 0, ROOM 1
        nodes.add(new Intersection(new Point(1, 1, 3))); // 1
        edges.add(new Edge(
                new Room(new Point(1, 1, 5), "1", null, false),
                new Intersection(new Point(1, 1, 3))
        ));

        nodes.add(new Intersection(new Point(3, 1, 3))); // 2
        edges.add(new Edge(nodes.get(1), nodes.get(2)));

        nodes.add(new Room(new Point(3, 1, 2), "2", null, false)); // 3, ROOM 2
        edges.add(new Edge(nodes.get(2), nodes.get(3)));

        nodes.add(new Intersection(new Point(7, 1, 3))); // 4
        edges.add(new Edge(nodes.get(2), nodes.get(4)));

        nodes.add(new Room(new Point(7, 1, 2), "3", null, false)); // 5, ROOM 3
        edges.add(new Edge(nodes.get(4), nodes.get(5)));

        nodes.add(new Room(new Point(7, 1, 4), "4", null, false)); // 6, ROOM 4
        edges.add(new Edge(nodes.get(4), nodes.get(6)));

        nodes.add(new Intersection(new Point(10, 1, 3))); // 7
        edges.add(new Edge(nodes.get(4), nodes.get(7)));

        // we should be able to ignore the y component for this for the algorithm.. avoid making n copies of the same staircase.
        // 8, Stairs
        nodes.add(new FloorConnector(new Point(10, 1, 5), "Stairs", FloorConnector.FloorConnectorTypes.STAIRCASE, new int[] {1, 2}, false));
        edges.add(new Edge(nodes.get(7), nodes.get(8)));

        ////////// FLOOR 2 //////////
        nodes.add(new Intersection(new Point(10, 2, 3))); // 9
        edges.add(new Edge(nodes.get(8), nodes.get(9)));

        nodes.add(new Room(new Point(2, 2, 3), "5", null, false)); // 10, ROOM 5
        edges.add(new Edge(nodes.get(9), nodes.get(10)));

        map = new Map(-1, "St. Davinceberg", "6969 E. Mtcalm, Pontiac, MI.", null, (Edge[])edges.toArray());

        Assert.assertNotNull(map);
    }

    @Test
    public void NullArgumentTest1() throws IllegalArgumentException, NoValidPathException {
        exception.expect(IllegalArgumentException.class);
        Navigation.NavigatePath(map, nodes.get(1), null);
    }
    @Test
    public void NullArgumentTest2() throws IllegalArgumentException, NoValidPathException {
        exception.expect(IllegalArgumentException.class);
        Navigation.NavigatePath(map, null, (Room)nodes.get(1));
    }

    @Test
    public void NoPathTest() throws NoValidPathException {
        Room r = new Room(new Point(0, 0, 0), "50", "The Room That Should Not Be", true);
        exception.expect(NoValidPathException.class);
        Navigation.NavigatePath(map, nodes.get(1), r);
    }

    @Test
    public void PathTest() throws NoValidPathException {
        Path r = Navigation.NavigatePath(map, nodes.get(1), (Room)nodes.get(6));
        Assert.assertNotNull(r);
        Assert.assertEquals(3, r.length());

        Assert.assertEquals(r.get(0), new Edge(nodes.get(1), nodes.get(2)));
        Assert.assertEquals(r.get(1), new Edge(nodes.get(2), nodes.get(4)));
        Assert.assertEquals(r.get(2), new Edge(nodes.get(4), nodes.get(6)));
    }

    @Test
    public void FloorTest() throws NoValidPathException {
        Path r = Navigation.NavigatePath(map, nodes.get(1), (Room)nodes.get(10));
        Assert.assertNotNull(r);
        Assert.assertEquals(6, r.length());

        Assert.assertEquals(r.get(0), new Edge(nodes.get(1), nodes.get(2)));
        Assert.assertEquals(r.get(1), new Edge(nodes.get(2), nodes.get(4)));
        Assert.assertEquals(r.get(2), new Edge(nodes.get(4), nodes.get(7)));
        Assert.assertEquals(r.get(3), new Edge(nodes.get(7), nodes.get(8)));
        Assert.assertEquals(r.get(4), new Edge(nodes.get(8), nodes.get(9)));
        Assert.assertEquals(r.get(5), new Edge(nodes.get(9), nodes.get(10)));
    }
}
