package tk.pathfinder.Map;

/***
 * Represents a point in 3D space.
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public final class Point {

    private Integer x;
    private Integer y;
    private Integer z;

    private static Point nullPoint = new Point(-1, -1, -1);

    /**
     * @return The default point, defined by (-1, -1, -1).
     */
    public static Point getDefault() { return nullPoint; }


    /**
     * @param x The x coordinate
     * @param y The y (vertical) coordinate)
     * @param z The z coordinate
     */
    public Point(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Construct the point (0, 0, 0)
     */
    public Point(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    /***
     * Get the Euclidean distance between two points
     * @param p The first point
     * @param q The second point
     * @return The distance between the points, ignoring elevation.
     */
    public static double distance(Point p, Point q){
        return Math.abs(Math.sqrt(Math.pow(p.getX() - q.getX(), 2) + Math.pow(p.getZ() - q.getZ(), 2)));
    }

    /**
     * Multiply the vector by a constant.
     * @param d The factor to multiply by.
     * @return The new point.
     */
    Point multiply(double d){
        int x = (int)(this.x * d);
        int z = (int)(this.z * d);

        return new Point(x, y, z);
    }

    /***
     * Get the Euclidean distance between two points
     * @param p The second point
     * @return The distance between the points, or -1 if the points are on different elevations.
     */
    double distance(Point p){
        return distance(this, p);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(!(obj instanceof Point))
            return false;
        Point p = (Point)obj;
        return this.x.equals(p.x)
                && this.y.equals(p.y)
                && this.z.equals(p.z);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", this.x, this.y, this.z);
    }
}
