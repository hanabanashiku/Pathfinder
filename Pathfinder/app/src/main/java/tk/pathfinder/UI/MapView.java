package tk.pathfinder.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.Iterator;

import tk.pathfinder.Map.*;
import tk.pathfinder.R;

/**
 * A Widget for viewing a map.
 */
public class MapView extends View {

    private static final int NODE_RADIUS = 15;
    protected Map map;
    protected int floor = 1;
    protected int[] floorRange;

    private TextPaint textPaint;
    private static Paint roomPaint;
    protected Paint pathPaint;
    {}    protected float density;

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
    // the map's center with respect to map coordinates
    protected android.graphics.Point mapCenter = new android.graphics.Point(500, 500);
    // how much the map is zoomed in
    protected double zoom = 1;

    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;

    protected boolean pagingNotAllowed = false;
    protected boolean trackingLocation = true;

    public MapView(Context context) {
        super(context);
        init(context);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
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

        // set up event listeners
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    /**
     * @param z A constant to zoom by, from 1 to 5.
     */
    protected void setZoom(double z){
        if(z < 1)
            z = 1;
        if(z > 5)
            z = 5;

        zoom = z;
        invalidate();
    }

    /**
     * Reset the map's positioning and zoom to the default.
     */
    public void resetPosition(){
        mapCenter.x = 500;
        mapCenter.y = 500;
        setZoom(1);
        trackingLocation = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(map == null)
            return;

        drawEdges(canvas);
        drawNodes(canvas);
        drawFloorConnectors(canvas);
    }

    protected void drawEdges(Canvas canvas){
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
    }

    protected void drawNodes(Canvas canvas){
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
    }

    protected void drawFloorConnectors(Canvas canvas){
        int c = (int)(NODE_RADIUS*density);

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
    protected android.graphics.Point translatePoint(Point p){
        // get the point on the map we are centered around.
        // distance of the points from the map center.
        int x = p.getX() - mapCenter.x;
        int y = p.getZ() - mapCenter.y;
        // find the point on the board with the correct zoom
        // the content dimensions are given an offset to make sure a node at 1000x1000 has room to render
        x = (int)(zoom*((x*(content_width - 60*density))/1000d));
        y = (int)(zoom*((y* (content_height - 80*density))/1000d));
        x += drawCenter.x + padding[3];
        y += drawCenter.y + padding[0];
        return new android.graphics.Point(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        boolean ret = scaleDetector.onTouchEvent(ev);
        ret = gestureDetector.onTouchEvent(ev) || ret;
        ret = super.onTouchEvent(ev) || ret;
        return ret;
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
        this.floorRange = map.getFloorRange();
        invalidate();
    }

    public void setFloor(int floor){
        this.floor = floor;
        invalidate();
    }

    /**
     * A listener to detect scaling by panning fingers on the view.
     * Note that onScale will only run if it has detected a valid scale motion.
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector){
            setZoom(Math.max(1, zoom*Math.min(detector.getScaleFactor(), 5.0)));
            return true;
        }
    }

    /**
     * A gesture listener to be used in combination with ScaleListener.
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override // move the center
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distX, float distY){
            // offset in terms of the original map
            double offsetX = (1000*distX) / content_width / Math.max(1, (9/10d)*zoom);
            double offsetY = (1000*distY) / content_height / zoom;

            mapCenter.x = (int)Math.min(Math.max(0, mapCenter.x + offsetX), 1000);
            mapCenter.y = (int)Math.min(Math.max(0, mapCenter.y + offsetY), 1000);
            trackingLocation = false;
            invalidate();
            return true;
        }

        @Override // change floors
        public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY){

            // check if the velocity is not strong enough to be a fling
            // or if the fling is directed upwards or downwards with a tolerance of 250. (i.e. fling up)
            // or if paging is not allowed (i.e. for NavigationView)
            if(pagingNotAllowed || Math.abs(vX) < 750 || Math.abs(vY) - Math.abs(vX) >= 250)
                return true;

            // get the velocity magnitudes to determine if the page is turning left or right
            boolean left = (e2.getX() - e1.getX()) < 0;
            // turn page left
            if(left){
                Log.d("MapView", "Changing floor left");
                if(floor == floorRange[0])
                    setFloor(floorRange[1]);
                else setFloor(floor - 1);
            }

            // turn right
            else{
                Log.d("MapView", "Changing floor right");
                if(floor == floorRange[1])
                    setFloor(floorRange[0]);
                else
                    setFloor(floor + 1);
            }
            return true;
        }
    }
}
