package tk.pathfinder.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Iterator;

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

    private float[] grav = null;
    private float[] geo = null;
    private double rotation;


    public NavigationView(Context context){
        super(context);
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
    }

    public void setDestination(Room room){
        destination = room;
        Map map = status.getCurrentMap();
        Node current = map.closestNode(status.getCurrentLocation());
        try{
            currentPath = Navigation.NavigatePath(status.getCurrentMap(), current, destination);
        }
        catch(NoValidPathException e){
            listener.onNoPath(e);
        }
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        // update current location
        Point current = status.getCurrentLocation();
        Node currentNode = status.getCurrentMap().closestNode(current);
        if(trackingLocation){
            mapCenter.x = current.getX();
            mapCenter.y = current.getZ();
        }

        // recalculate path
        if(!currentPath.contains(currentNode)){
            try{
                currentPath = Navigation.NavigatePath(status.getCurrentMap(), currentNode, destination);
                getDirection(currentNode);
            }
            catch(NoValidPathException e) { listener.onNoPath(e); }
        }

        if(direction.x == 0 && direction.y == 0){
            listener.onArrival();
            return;
        }

        drawEdges(canvas);

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

        // draw the directional marker
        drawUser(canvas);

    }

    private void drawUser(Canvas canvas){
        int x = content_width / 2;
        int y = content_height / 2;

        canvas.drawCircle(x, y, 6 * density, userPaint);

        x = (int)(8 * density * Math.cos(rotation)) + x;
        y = (int)(8 * density * Math.sin(rotation)) + y;

        canvas.drawCircle(x, y, 2 * density, userPaint);
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        switch(event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                grav = event.values;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                geo = event.values;
                break;
            default:
                return;
        }

        if(grav != null && geo != null){
            float[] identity = new float[9];
            float[] rotation = new float[9];

            if(SensorManager.getRotationMatrix(rotation, identity, grav, geo)){
                float[] orientation = new float[3];
                SensorManager.getOrientation(rotation, orientation);
                this.rotation = Math.toDegrees(orientation[0]);
                invalidate();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public void getDirection(Node current){
        int x = (int)Math.signum(destination.getPoint().getX() - current.getPoint().getX());
        int y = (int)Math.signum(destination.getPoint().getZ() - current.getPoint().getZ());
        direction = new android.graphics.Point(x, y);
    }

    @Override
    public void resetPosition(){
        Point p = status.getCurrentLocation();
        mapCenter.x = p.getX();
        mapCenter.y = p.getZ();
    }

    public void setCallbackListener(NavigationListener l){
        listener = l;
    }

    public interface NavigationListener{
        void onNoPath(NoValidPathException e);
        void onArrival();
    }
}
