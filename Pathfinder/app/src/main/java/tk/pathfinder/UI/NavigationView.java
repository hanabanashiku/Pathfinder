package tk.pathfinder.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;

import tk.pathfinder.Map.Edge;
import tk.pathfinder.Map.Map;
import tk.pathfinder.Map.Navigation;
import tk.pathfinder.Map.Node;
import tk.pathfinder.Map.Path;
import tk.pathfinder.Map.Point;
import tk.pathfinder.Map.Room;
import tk.pathfinder.exceptions.NoValidPathException;

public class NavigationView extends MapView implements SensorEventListener {

    private Room destination;
    private AppStatus status;
    private Path currentPath;
    private android.graphics.Point direction;
    private NavigationListener listener;

    private static Paint destinationPathPaint;
    private static Paint previousPathPaint;
    private static Paint userPaint;

    private final float[] grav = new float[3];
    private final float[] geo = new float[3];
    private final float[] rotation = new float[9];
    private final float[] orientation = new float[3];
    private double angle;



    public NavigationView(Context context) {
        super(context);
        init(context);
    }

    public NavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NavigationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    protected void init(Context context){
        super.init(context);

        status = (AppStatus)context.getApplicationContext();
        pagingNotAllowed = true;

        if(destinationPathPaint == null){
            destinationPathPaint = new Paint(pathPaint);
            destinationPathPaint.setColor(Color.BLUE);
        }

        if(previousPathPaint == null){
            previousPathPaint = new Paint(pathPaint);
            previousPathPaint.setColor(Color.GRAY);
        }

        if(userPaint == null){
            userPaint = new Paint();
            userPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            userPaint.setColor(Color.BLUE);
            userPaint.setStyle(Paint.Style.FILL);
        }

        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL);

        setMap(status.getCurrentMap());
        resetPosition();
    }

    public void setDestination(Room room){
        destination = room;
        new NavigationRunnable().start();
    }

    @Override
    public void onDraw(Canvas canvas){
        if(map == null)
            return;
        floor = status.getCurrentLocation().getY();


        // update current location
        Point current = status.getCurrentLocation();
        if(trackingLocation){
            mapCenter.x = current.getX();
            mapCenter.y = current.getZ();
        }

        drawEdges(canvas);

        if(currentPath != null && currentPath.length() > 0) {
            // draw the path to follow
            for (Edge e : currentPath) {
                Paint paint;
                if (direction.x == 1 && (e.getNode2().getPoint().getX() >= current.getX() || e.getNode2().getPoint().getX() >= current.getY())
                        || direction.y == 1 && (e.getNode2().getPoint().getZ() < current.getZ() || e.getNode2().getPoint().getZ() < current.getZ())) {
                    paint = destinationPathPaint;
                } else
                    paint = previousPathPaint;

                android.graphics.Point e1 = translatePoint(e.getNode1().getPoint());
                android.graphics.Point e2 = translatePoint(e.getNode2().getPoint());

                canvas.drawLine(e1.x, e1.y, e2.x, e2.y, paint);
            }
        }

        drawNodes(canvas);
        drawFloorConnectors(canvas);

        // draw the directional marker
        drawUser(canvas);
    }

    private void drawUser(Canvas canvas){
        android.graphics.Point p = translatePoint(status.getCurrentLocation());

        userPaint.setColor(Color.BLACK);
        canvas.drawCircle(p.x, p.y, 10 * density, userPaint);
        userPaint.setColor(Color.WHITE);
        canvas.drawCircle(p.x, p.y, 9 * density, userPaint);

        userPaint.setColor(Color.BLUE);
        canvas.drawCircle(p.x, p.y, 6 * density, userPaint);

        int x = (int)(14 * density * Math.cos(angle)) + p.x;
        int y = (int)(14 * density * Math.sin(angle)) + p.y;
        canvas.drawCircle(x, y, 4 * density, userPaint);
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        switch(event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                filter(grav, event.values);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                filter(geo, event.values);
                break;
            default:
                return;
        }

        if(SensorManager.getRotationMatrix(rotation, null, grav, geo)) {
            SensorManager.getOrientation(rotation, orientation);
            this.angle = orientation[0];
            invalidate();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    // sets the direction we need to move to get to the destination, on each axis. (1 is forward)
    public void getDirection(Node current){
        if(current == null)
            return;
        int x = (int)Math.signum(destination.getPoint().getX() - current.getPoint().getX());
        int y = (int)Math.signum(destination.getPoint().getZ() - current.getPoint().getZ());
        direction = new android.graphics.Point(x, y);
    }

    public boolean recalculatePath(Node current) {
        try {
            currentPath = Navigation.NavigatePath(map, current, destination);
            getDirection(current);
        } catch (NoValidPathException e) {
            listener.onNoPath(e);
        }

        if (direction.x == 0 && direction.y == 0) {
            listener.onArrival();
            return true;
        }
        return false;
    }

    @Override
    public void resetPosition(){
        Point p = status.getCurrentLocation();
        mapCenter.x = p.getX();
        mapCenter.y = p.getZ();
        trackingLocation = true;
        zoom = 3.5;
    }

    public void setCallbackListener(NavigationListener l){
        listener = l;
    }

    public interface NavigationListener{
        void onNoPath(NoValidPathException e);
        void onArrival();
    }

    // use a low-pass filter on sensor values to smooth them.
    private void filter(float[] matrix, float[] values){
        final float alpha = 0.8f;

        matrix[0] = alpha * matrix[0] + (1 - alpha) * values[0];
        matrix[1] = alpha * matrix[1] + (1 - alpha) * values[1];
        matrix[2] = alpha * matrix[2] + (1 - alpha) * values[2];
    }

    private class NavigationRunnable extends Thread {

        @Override
        public void run() {
            recalculatePath(map.closestNode(status.getCurrentLocation()));

            while(destination != null){
                Node current = map.closestNode(status.getCurrentLocation());

                // we have arrived
                if (currentPath.length() == 0){
                    listener.onArrival();
                    currentPath = null;
                    break;
                }

                // we are lost, recalculate
                else if(!currentPath.contains(current))
                        recalculatePath(current);
            }
        }
    }
}
