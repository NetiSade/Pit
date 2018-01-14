package com.android.netisade.pit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by nsade on 10-Jan-18.
 */

public class PitLineView extends View {

    private PitPointView startPoint ;
    private PitPointView endPoint ;
    private Paint paint;
    private final int PAINT_STROKE_WIDTH = 8;


    public PitLineView(Context context, PitPointView startPoint, PitPointView endPoint) {
        super(context);
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(PAINT_STROKE_WIDTH);
        int layoutWidth = (int)(endPoint.getX() - startPoint.getX());
        int layoutHeight = (int) Math.abs(startPoint.getY() - endPoint.getY());
        layoutWidth = Math.max(layoutWidth,PAINT_STROKE_WIDTH);
        layoutHeight = Math.max(layoutHeight,PAINT_STROKE_WIDTH);
        setLayoutParams(new PitViewGroup.LayoutParams(layoutWidth,layoutHeight));
        if(startPoint.getY()<endPoint.getY()) {
            setX(startPoint.getX() + PitPointView.TOUCH_FACTOR/2 + (PitPointView.POINT_SIZE_PIXELS / 2));
            setY(startPoint.getY() + PitPointView.TOUCH_FACTOR/2 +(PitPointView.POINT_SIZE_PIXELS / 2));
        }
        else
        {
            setX(startPoint.getX() + PitPointView.TOUCH_FACTOR/2 + (PitPointView.POINT_SIZE_PIXELS / 2));
            setY(endPoint.getY() + PitPointView.TOUCH_FACTOR/2 + (PitPointView.POINT_SIZE_PIXELS / 2));
        }
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int canvasH = canvas.getHeight();
        int canvasW = canvas.getWidth();
        if(startPoint.getY()<endPoint.getY())
            canvas.drawLine(0,0,canvasW,canvasH, paint);
        else
            canvas.drawLine(0,canvasH,canvasW,0, paint);
    }
}
