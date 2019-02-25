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

    public Point(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    /***
     * @param y The point to add
     * @return A new point this + y
     */
    public Point add(Point y){
        return new Point(this.x + y.x, this.y + y.y, this.z + y.z);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    /***
     * Get the Euclidian distance between two points
     * @param p The first point
     * @param q The second point
     * @return The distance between the points, or -1 if the points are on different elevations.
     */
    public static double distance(Point p, Point q){
        if(p.getY() != q.getY())
            return -1;
        return Math.abs(Math.sqrt(Math.pow(p.getX() - q.getX(), 2) + Math.pow(p.getZ() - q.getZ(), 2)));
    }

    /***
     * Get the Euclidian distance between two points
     * @param p The second point
     * @return The distance between the points, or -1 if the points are on different elevations.
     */
    public double distance(Point p){
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
