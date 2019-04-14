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
    int content_width;
    int content_height;

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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(map == null)
            return;

        // draw paths
        for(Iterator<Edge> edges = map.getEdges(); edges.hasNext(); ){
            Edge e = edges.next();

            Point p1 = translatePoint(e.getNode1().getPoint());
            Point p2 = translatePoint(e.getNode2().getPoint());

            // only draw the current floor
            if(p1.getY() != floor && p2.getY() != floor)
                continue;

            canvas.drawLine(
                    p1.getX(),
                    p1.getZ(),
                    p2.getX(),
                    p2.getZ(),
                    pathPaint
            );
        }

        int c = (int)(NODE_RADIUS*density);
        for(Iterator<Room> rooms = map.getRooms(); rooms.hasNext(); ){
            Room r = rooms.next();

            if(r.getPoint().getY() != floor)
                continue;

            Point p = translatePoint(r.getPoint());

            String text;
            if(r.getRoomNumber() == null)
                text = r.getName();
            else if(r.getName() == null)
                text = r.getRoomNumber();
            else text = r.getRoomNumber() + ": " + r.getName();
            canvas.drawBitmap(locationIcon, p.getX() - c, p.getZ() - c, roomPaint);
            canvas.drawText(text, p.getX(), p.getZ() + 2*NODE_RADIUS*density, textPaint);
        }

        for(Iterator<FloorConnector> connectors = map.getFloorConnectors(); connectors.hasNext(); ){
            FloorConnector con = connectors.next();

            if(!con.isFloorAccessible(floor))
                continue;

            Point p = translatePoint(con.getPoint());

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

            canvas.drawBitmap(icon, p.getX() - c, p.getZ() - c, roomPaint);
            canvas.drawText(con.getName(), p.getX(), p.getZ() + 2*NODE_RADIUS*density, textPaint);
        }
    }

    private Point translatePoint(Point p){
        int x = (int)(1.2*(p.getX()/1000d) * content_width + padding[3]);
        int y = (int)(1.2*(p.getZ()/1000d) * content_height + padding[0]);

        return new Point(x, p.getY(), y);
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        padding = new int[] {getPaddingTop(), getPaddingRight(), getPaddingBottom(), getPaddingLeft()};
        content_width = w - padding[3] - padding[1];
        content_height = h - padding[0] - padding[2];
        density = getResources().getDisplayMetrics().density;
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
