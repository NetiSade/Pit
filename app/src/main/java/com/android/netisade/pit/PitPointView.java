package com.android.netisade.pit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;

import java.util.Comparator;

/**
 * Created by nsade on 09-Jan-18.
 */

public class PitPointView extends View implements Comparable<PitPointView>
{
    private Paint paint;
    private RectF rectPosition;
    public final static int POINT_SIZE_PIXELS = 90;
    public final static int TOUCH_FACTOR = 50;

    public PitPointView(Context context,int xPos, int yPos)
    {
        super(context);
        setX(xPos);
        setY(yPos);
        setLayoutParams(new PitViewGroup.LayoutParams(POINT_SIZE_PIXELS+TOUCH_FACTOR,POINT_SIZE_PIXELS+TOUCH_FACTOR));
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        int canvasH = canvas.getHeight();
        int canvasW = canvas.getWidth();
        rectPosition =
                new RectF(TOUCH_FACTOR,TOUCH_FACTOR,canvasW-TOUCH_FACTOR,canvasH-TOUCH_FACTOR);
        canvas.drawOval(rectPosition, paint);
    }


    public boolean onTouchEvent (MotionEvent event)
    {
        changePos(event.getX()+getX(),event.getY()+getY());
        return true;
    }

    public void changePos(float newX,float newY)
    {
        if(newX<PitViewGroup.screenWidth-POINT_SIZE_PIXELS)
            setX(newX);
        if(newY<PitViewGroup.screenHeight-PitViewGroup.LOWER_MARGIN && newY>PitViewGroup.UPPER_MARGIN)
            setY(newY);
    }

    @Override
    public int compareTo(@NonNull PitPointView pointToCompare) {
        return this.getX() < pointToCompare.getX() ? -1 : (this.getX() < pointToCompare.getX()) ? 1 : 0;
    }
}
