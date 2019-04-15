package tk.pathfinder.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Iterator;

import tk.pathfinder.Map.*;
import tk.pathfinder.R;

/**
 * A Widget for viewing a map.
 */
public class MapView extends View {

    private static final int NODE_RADIUS = 15;
    Map map;
    int floor = 1;

    private TextPaint textPaint;
    private static Paint roomPaint;
    private Paint pathPaint;
    private float density;

    private static Bitmap locationIcon;
    private static Bitmap escalatorUp;
    private static Bitmap escalatorDown;
    private static Bitmap stairs;
    private static Bitmap elevator;

    // top right bottom left
    private int[] padding = new int[] {0, 0, 0, 0};
    protected int content_width;
    protected int content_height;
    // the map's center with respect to the canvas dimensions
    protected android.graphics.Point drawCenter;
    // how much the map is zoomed in
    protected double zoom = 1;

    public MapView(Context context) {
        super(context);
        init();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        density = getResources().getDisplayMetrics().density;
        // Set up paint objects
        textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(16 * density);
        textPaint.setShadowLayer(0.5f*density, 1*density, 1*density, Color.WHITE);

        if(roomPaint == null){
            roomPaint = new Paint();
            roomPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            roomPaint.setStyle(Paint.Style.FILL);
        }

        if(locationIcon == null){
            locationIcon = BitmapFactory.decodeResource(getResources(), R.drawable.map_loc);
            locationIcon = Bitmap.createScaledBitmap(locationIcon, (int)(2*NODE_RADIUS*density), (int)(2*NODE_RADIUS*density), false);
        }
        if(escalatorUp == null)
            escalatorUp = BitmapFactory.decodeResource(getResources(), R.drawable.escalator_up);
        if(escalatorDown == null)
            escalatorDown = BitmapFactory.decodeResource(getResources(), R.drawable.escalator_down);
        if(stairs == null)
            stairs = BitmapFactory.decodeResource(getResources(), R.drawable.stairs);
        if(elevator == null)
            elevator = BitmapFactory.decodeResource(getResources(), R.drawable.elevator);


        pathPaint = new Paint();
        pathPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(4*getResources().getDisplayMetrics().density);
    }

    /**
     * Get the 1000-based real-world point that the map is currently centered on
     * @return The current center of the map.
     */
    protected android.graphics.Point getMapCenter(){
        return new android.graphics.Point(500, 500);
    }

    /**
     * @param z A constant to zoom by, from 1 to 5.
     */
    public void setZoom(double z){
        if(z < 1)
            z = 1;
        if(z > 5)
            z = 5;

        double finalZ = z;

        // gradually zoom in or out
        ((Runnable) () -> {
            int timesPerSecond = 5;
            double seconds = 1;
            // increment five times a second.
            double increment = (finalZ - zoom) / (seconds * timesPerSecond);
            while (zoom != finalZ) {
                zoom += increment;
                invalidate();
                try {
                    wait(1000 / timesPerSecond);
                } catch (InterruptedException e) { }
            }
        }).run();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(map == null)
            return;

        // draw paths
        for(Iterator<Edge> edges = map.getEdges(); edges.hasNext(); ){
            Edge e = edges.next();

            android.graphics.Point p1 = translatePoint(e.getNode1().getPoint());
            android.graphics.Point p2 = translatePoint(e.getNode2().getPoint());

            // only draw the current floor
            if(e.getNode1().getPoint().getY() != floor && e.getNode2().getPoint().getY() != floor)
                continue;

            canvas.drawLine(
                    p1.x,
                    p1.y,
                    p2.x,
                    p2.y,
                    pathPaint
            );
        }

        int c = (int)(NODE_RADIUS*density);
        for(Iterator<Room> rooms = map.getRooms(); rooms.hasNext(); ){
            Room r = rooms.next();

            if(r.getPoint().getY() != floor)
                continue;

            android.graphics.Point p = translatePoint(r.getPoint());

            String text;
            if(r.getRoomNumber() == null)
                text = r.getName();
            else if(r.getName() == null)
                text = r.getRoomNumber();
            else text = r.getRoomNumber() + ": " + r.getName();
            canvas.drawBitmap(locationIcon, p.x - c, p.y - c, roomPaint);
            canvas.drawText(text, p.x, p.y + 2*NODE_RADIUS*density, textPaint);
        }
            android.graphics.Point testp = translatePoint(new Point(1000, 0, 1000));
            canvas.drawBitmap(locationIcon, testp.x, testp.y, roomPaint);

        for(Iterator<FloorConnector> connectors = map.getFloorConnectors(); connectors.hasNext(); ){
            FloorConnector con = connectors.next();

            if(!con.isFloorAccessible(floor))
                continue;

            android.graphics.Point p = translatePoint(con.getPoint());

            Bitmap icon;
            switch(con.getType()){
                case STAIRCASE:
                    icon = stairs;
                    break;
                case ELEVATOR:
                    icon = elevator;
                    break;
                case UP_ESCALATOR:
                    icon = escalatorUp;
                    break;
                case DOWN_ESCALATOR:
                    icon = escalatorDown;
                    break;
                default:
                    continue;
            }

            canvas.drawBitmap(icon, p.x - c, p.y - c, roomPaint);
            canvas.drawText(con.getName(), p.x, p.y + 2*NODE_RADIUS*density, textPaint);
        }
    }

    /**
     * Calculate the correct location of the point on the canvas.
     * @param p The 1000-based map ratio point returned from the API
     * @return A 2D point that can be applied to the canvas centered around the given center.
     */
    private android.graphics.Point translatePoint(Point p){
        // get the point on the map we are centered around.
        android.graphics.Point center = getMapCenter();
        // distance of the points from the map center.
        int x = p.getX() - center.x;
        int y = p.getZ() - center.y;
        // find the point on the board with the correct zoom
        // the content dimensions are given an offset to make sure a node at 1000x1000 has room to render
        x = (int)(zoom*((x*(content_width - 60*density))/1000d));
        y = (int)(zoom*((y* (content_height - 80*density))/1000d));
        x += drawCenter.x + padding[3];
        y += drawCenter.y + padding[0];
        return new android.graphics.Point(x, y);
    }

    @Override
    protected void onMeasure(int width, int height){
        int w = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int h = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(getDim(w, width), getDim(h, height));
    }

    // https://medium.com/@quiro91/custom-view-mastering-onmeasure-a0a0bb11784d
    private int getDim(int desired, int spec){
        int mode = MeasureSpec.getMode(spec);
        int size = MeasureSpec.getSize(spec);

        switch(mode){
            case MeasureSpec.EXACTLY:
                return size;
            case MeasureSpec.AT_MOST:
                return Math.min(desired, size);
            case MeasureSpec.UNSPECIFIED: default:
                return desired;
        }
    }

    @Override // called at least once, after the initial onMeasure
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        padding = new int[] {getPaddingTop(), getPaddingRight(), getPaddingBottom(), getPaddingLeft()};
        content_width = w - padding[3] - padding[1];
        content_height = h - padding[0] - padding[2];
        density = getResources().getDisplayMetrics().density;
        drawCenter = new android.graphics.Point(content_width / 2 + padding[3], content_height / 2 + padding[0]);
    }

    public void setMap(Map map){
        this.map = map;
        invalidate();
    }

    public void setFloor(int floor){
        this.floor = floor;
        invalidate();
    }
}
