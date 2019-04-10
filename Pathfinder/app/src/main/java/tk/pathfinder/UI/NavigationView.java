package tk.pathfinder.UI;

import android.content.Context;
import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Iterator;

import tk.pathfinder.Map.*;
import tk.pathfinder.Map.Path;
import tk.pathfinder.Map.Point;
import tk.pathfinder.exceptions.NoValidPathException;

/**
 * A widget for viewing a map.
 */
public class NavigationView extends View implements SensorEventListener {

    private TextPaint textPaint;
    private Paint blackPaint;
    private Paint bluePaint;
    private Paint connectorPaint;
    private Paint pathPaint;

    private Context ctx;
    Path path;
    private Map map;
    private Node currentNode;
    private Room destination;
    private int contentWidth;
    private int contentHeight;

    SensorManager sensorManager;
    Sensor accelerometer;
    Sensor magneticField;
    private float[] grav = null;
    private float[] geo = null;
    private double rotation;

    private static final int NODE_RADIUS = 4;

    public NavigationView(Context context) {
        super(context);
        init(null, 0);
    }

    public NavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public NavigationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
        ctx = context;
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    private void init(AttributeSet attrs, int defStyle) {


        textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);

        blackPaint = new Paint();
        blackPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        blackPaint.setStyle(Paint.Style.FILL);
        blackPaint.setColor(Color.rgb(0, 0, 0));

        bluePaint = new Paint();
        bluePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        blackPaint.setStyle(Paint.Style.FILL);
        bluePaint.setColor(Color.rgb(27, 140, 247));
        bluePaint.setShadowLayer(-2, 0, 0, Color.rgb(255, 255, 255));

        connectorPaint = new Paint();
        connectorPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        pathPaint = new Paint();
        pathPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setFlags(Color.rgb(69, 204, 69));

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void setDestination(Room value){
        destination = value;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // check for nulls
        if(map == null)
            return;

        AppStatus app = (AppStatus)ctx.getApplicationContext();
        Node closest = map.closestNode(app.getCurrentLocation());
        if(closest != currentNode){
            currentNode = closest;
            try{
                path = Navigation.NavigatePath(map, map.closestNode(app.getCurrentLocation()), destination);
            }
            catch(NoValidPathException e){
                return; // todo deal with this
            }
        }

        // draw the location marker in the middle
        drawUser(canvas);

        // draw edges and highlight the correct path to take.
        // TODO don't highlight already visited nodes
        for(Iterator<Edge> i = map.getEdges(); i.hasNext(); ){
            Edge e = i.next();
            Point p1 = getPointOffset(e.getNode1().getPoint());
            Point p2 = getPointOffset(e.getNode2().getPoint());

            if(path.contains(e.getNode1(), e.getNode2()))
                canvas.drawLine(p1.getX(), p1.getZ(), p2.getX(), p2.getZ(), pathPaint);
            else
                canvas.drawLine(p1.getX(), p1.getZ(), p2.getX(), p2.getZ(), blackPaint);
        }

        for(Iterator<Room> i = map.getRooms(); i.hasNext(); ) {
            Room r = i.next();
            if(r.getFloor() != currentNode.getFloor())
                return;
            drawRoom(r, canvas);
        }

        for(Iterator<FloorConnector> i = map.getFloorConnectors(); i.hasNext(); ){
            FloorConnector c = i.next();
            if(c.getFloor() != currentNode.getFloor())
                return;
            drawFloorConnector(c, canvas);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        contentWidth = w - paddingLeft - paddingRight;
        contentHeight = h - paddingTop - paddingBottom;
    }



    public void setMap(Map map){
        this.map = map;
    }

    public Map getMap(){
        return this.map;
    }

    // get offset of the point with respect to centering.
    private Point getPointOffset(Point p){
        p = translatePoint(p);
        Point current = ((AppStatus)ctx.getApplicationContext()).getCurrentLocation();
        current = translatePoint(current);
        double distance = Point.distance(p, current);

        int x =  (int)((contentWidth / 2) + Math.signum(p.getX() - current.getX()) * (2 * distance));
        int y = p.getY();
        int z =  (int)((contentHeight / 2) + Math.signum(p.getZ() - current.getZ()) * (2 * distance));

        return new Point(x, y, z);
    }

    // draw the point in the middle that represents the user's position
    private void drawUser(Canvas c){
        int x = contentWidth / 2;
        int y = contentHeight / 2;
        double rotation = this.rotation;

        // draw the main circle
        c.drawCircle(x, y, 6, bluePaint);
        x = (int)(8*Math.cos(rotation)) + x;
        y = (int)(8 * Math.sin(rotation)) + y;

        // draw the directional marker
        c.drawCircle(x, y, 2, bluePaint);
    }

    private void drawRoom(Room r, Canvas canvas){
        Point p = getPointOffset(r.getPoint());
        canvas.drawCircle(p.getX(), p.getZ(), NODE_RADIUS, blackPaint);
        String text = "";
        if(r.getRoomNumber() != null)
            text = r.getRoomNumber();
        text += " " + r.getRoomNumber();
        canvas.drawText(text, p.getX(), p.getZ() - NODE_RADIUS - 2, textPaint);
    }

    private void drawFloorConnector(FloorConnector c, Canvas canvas){
        Point p = getPointOffset(c.getPoint());
        canvas.drawCircle(p.getX(), p.getZ(), NODE_RADIUS, connectorPaint);
        canvas.drawText(c.getName(), p.getX(), p.getZ() - NODE_RADIUS - 2, textPaint);
    }

    private Point translatePoint(Point p){
        // points are returned from the db as a percentage of the total height/width multiplied by 1000.
        int x = (p.getX() / 1000) * contentWidth;
        int y = (p.getZ() / 1000) * contentHeight;
        return new Point(x, p.getY(), y);
    }

    // update compass
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                this.grav = event.values;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                geo = event.values;
            default:
                return;
        }

        if(this.grav != null && geo != null){
            float[] identity = new float[9];
            float[] rotation = new float[9];

            if(SensorManager.getRotationMatrix(rotation, identity, this.grav, geo)){
                float[] orientation = new float[3];
                 SensorManager.getOrientation(rotation, orientation);
                 this.rotation = Math.toDegrees(orientation[0]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
