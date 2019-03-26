package tk.pathfinder.UI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Iterator;

import tk.pathfinder.Map.*;
import tk.pathfinder.Map.Point;
import tk.pathfinder.R;

/**
 * TODO: document your custom view class.
 */
public class MapView extends View {

    private TextPaint textPaint;
    private Paint roomPaint;
    private Paint connectorPaint;
    private Paint pathPaint;

    private Map map;
    private int currentFloor = 1;

    private int contentWidth;
    private int contentHeight;

    private static final int NODE_RADIUS = 4;

    public MapView(Context context) {
        super(context);
        init(null, 0);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);

        roomPaint = new Paint();
        roomPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        connectorPaint = new Paint();
        connectorPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        pathPaint = new Paint();
        pathPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // check for nulls
        if(map == null)
            return;

        // draw paths (first so nodes are above the line if there is overlap)
        for(Iterator<Edge> edges = map.getEdges(); edges.hasNext(); ){
            Edge e = edges.next();

            Point p1 = translatePoint(e.getNode1().getPoint());
            Point p2 = translatePoint(e.getNode2().getPoint());

            // only draw for current floor
            if(p1.getY() != currentFloor && p2.getY() != currentFloor)
                continue;

            int x1 = p1.getX();
            int x2 = p2.getX();
            int y1 = p2.getZ();
            int y2 = p2.getZ();

            canvas.drawLine(x1, y1, x2, y2, pathPaint);
        }

        // draw room nodes
        for(Iterator<Room> rooms = map.getRooms(); rooms.hasNext(); ){
            Room room = rooms.next();

            // only draw for current floor
            if(room.getPoint().getY() != currentFloor)
                continue;

            Point p = translatePoint(room.getPoint());

            canvas.drawCircle(p.getX(), p.getZ(), NODE_RADIUS, roomPaint);
            canvas.drawText(room.getRoomNumber(), p.getX(), p.getZ() - NODE_RADIUS - 3, textPaint);
        }

        // draw elevators/stairs
        for(Iterator<FloorConnector> connectors = map.getFloorConnectors(); connectors.hasNext(); ){
            FloorConnector connector = connectors.next();

            // only draw for current flow
            if(connector.getPoint().getY() != currentFloor)
                continue;

            Point p = translatePoint(connector.getPoint());

            canvas.drawCircle(p.getX(), p.getZ(), NODE_RADIUS, connectorPaint);
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

    // TODO make sure the floor exists
    public void setCurrentFloor(int floor){
        currentFloor = floor;
        invalidate(); // redraw the map
    }

    public int getCurrentFloor(){
        return currentFloor;
    }

    private Point translatePoint(Point p){
        // points are returned from the db as a percentage of the total height/width multiplied by 1000.
        int x = (p.getX() / 1000) * contentWidth;
        int y = (p.getZ() / 1000) * contentHeight;
        return new Point(x, p.getY(), y);
    }
}
