package com.android.netisade.pit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


/**
 * Created by nsade on 08-Jan-18.
 */

public class PitViewGroup extends ViewGroup {
    private ArrayList<PitPointView> points = new ArrayList<>();
    private ArrayList<PitLineView> lines = new ArrayList<>();
    private final int NUM_OF_POINT_TO_INIT = 5;
    boolean initialized;
    public static int screenWidth;
    public static int screenHeight;
    public static final int LOWER_MARGIN = 500;
    public static final int UPPER_MARGIN = 120;
    private int defaultXPos;
    private int defaultYPos;
    private Random rand = new Random();
    private View yLine;
    private View xLine;


    /** The amount of space used by children in the left gutter. */
    private int mLeftWidth;

    /** The amount of space used by children in the right gutter. */
    private int mRightWidth;

    /** These are used for computing child frames based on their gravity. */
    private final Rect mTmpContainerRect = new Rect();
    private final Rect mTmpChildRect = new Rect();

    public PitViewGroup(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void init(final Context context)
    {
        if(!initialized) {
            addButton(context);
            getScreenSize(context);
            addAxis(context);
            for (int i =0;i<NUM_OF_POINT_TO_INIT;i++) {
                int x = rand.nextInt(screenWidth - PitPointView.POINT_SIZE_PIXELS);
                int y = UPPER_MARGIN + rand.nextInt(screenHeight-LOWER_MARGIN-UPPER_MARGIN);
                PitPointView point = new PitPointView(context,x,y);
                points.add(point);
                addPointListener(point,context);
            }
            sortPointsArray();
            initialized = true;
        }
        for (PitPointView point : points)
            this.addView(point);
        drawAllLines(context);
    }

    public void onRotate(Context context)
    {
        for (PitPointView point : points)
            convertPoint(point);
        changScreenSize(context);
        changeAxis(context);
        drawAllLines(context);
        //addButton(context);
    }

    private void changScreenSize(Context context)
    {
        int temp = screenWidth;
        screenWidth = screenHeight;
        screenHeight = temp;
        defaultXPos = screenWidth/2-PitPointView.POINT_SIZE_PIXELS/2-PitPointView.TOUCH_FACTOR/2;
        defaultYPos = screenHeight/2-PitPointView.POINT_SIZE_PIXELS/2-PitPointView.TOUCH_FACTOR/2;
    }

    private void convertPoint( PitPointView point)
    {
        float oldX = point.getX();
        float oldY = point.getY();
        int newScreenWidth = screenHeight;
        int newScreenHeight = screenWidth;
        double xPercentageOfTheScreen = oldX/screenWidth;
        double yPercentageOfTheScreen = oldY/screenHeight;
        float newX = (float) xPercentageOfTheScreen*newScreenWidth;
        float newY = (float) yPercentageOfTheScreen*newScreenHeight;
        newX = Math.min(newScreenWidth-PitPointView.POINT_SIZE_PIXELS,newX);
        newY = Math.min(newY,newScreenHeight-PitViewGroup.LOWER_MARGIN);
        newY = Math.max(newY,UPPER_MARGIN);
        point.setX(newX);
        point.setY(newY);
    }

    private void drawAllLines(Context context)
    {
        //remove lines
        if(lines.size()>0)
            for(PitLineView line: lines)
                removeView(line);
        lines.clear();

        //create lines
        for(int i = 0;i<points.size()-1;i++)
        {
            PitPointView pointA = points.get(i);
            PitPointView pointB = points.get(i+1);
            PitLineView line = new PitLineView(context,pointA,pointB);
            lines.add(line);
            this.addView(line);
        }

    }

    private void addAxis(Context context)
    {
        yLine = new View(context);
        xLine = new View(context);
        yLine.setLayoutParams(new PitViewGroup.LayoutParams(1,screenHeight));
        xLine.setLayoutParams(new PitViewGroup.LayoutParams(screenWidth,1));
        yLine.setBackgroundColor(Color.BLACK);
        xLine.setBackgroundColor(Color.BLACK);
        yLine.setX(screenWidth/2);
        xLine.setY(screenHeight/2);
        this.addView(yLine);
        this.addView(xLine);
    }

    private void changeAxis(Context context)
    {
        yLine.setLayoutParams(new PitViewGroup.LayoutParams(1,screenHeight));
        xLine.setLayoutParams(new PitViewGroup.LayoutParams(screenWidth,1));
        yLine.setX(screenWidth/2);
        xLine.setY(screenHeight/2);
    }

    private void getScreenSize(Context context)
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        defaultXPos = screenWidth/2-PitPointView.POINT_SIZE_PIXELS/2-PitPointView.TOUCH_FACTOR/2;
        defaultYPos = screenHeight/2-PitPointView.POINT_SIZE_PIXELS/2-PitPointView.TOUCH_FACTOR/2;
    }

    private void addPointListener (final PitPointView point, final Context context)
    {
        point.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int pointIndex = points.indexOf(point);
                if (pointIndex>0 && points.get(pointIndex-1).getX()>point.getX())
                {
                    int indexToSwap = points.indexOf(points.get(pointIndex-1));
                    Collections.swap(points,indexToSwap,pointIndex);
                }
                else if (pointIndex<points.size()-1 && points.get(pointIndex+1).getX()<point.getX())
                {
                    int indexToSwap = points.indexOf(points.get(pointIndex+1));
                    Collections.swap(points,indexToSwap,pointIndex);
                }
                drawLines(context,pointIndex);
                return false;
            }
        });
    }

    private void addButton(final Context context)
    {
        Button addPointButton = new Button(context);
        addPointButton.setText(R.string.add_point_button_text);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addPointButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPoint(context);
            }
        });
        this.addView(addPointButton,lp);
    }

    public void addNewPoint(Context context)
    {
        PitPointView newPoint = new PitPointView(context, defaultXPos, defaultYPos);
        int ind = insertToArray(newPoint);
        addPointListener(newPoint,context);
        this.addView(newPoint);
        drawLinesOfNewPoint(context,ind);
    }

    private void drawLinesOfNewPoint(Context context, int ind)
    {
        if (ind == 0)
        {
            PitLineView line = new PitLineView(context,points.get(0), points.get(1));
            this.addView(line);
            lines.add(0,line);
            return;
        }
        else {
            if (ind == points.size() - 1) {
                PitLineView line = new PitLineView(context, points.get(ind - 1), points.get(ind));
                this.addView(line);
                lines.add(line);
            } else {
                PitLineView lineA = new PitLineView(context, points.get(ind - 1), points.get(ind));
                PitLineView lineB = new PitLineView(context, points.get(ind), points.get(ind + 1));
                removeView(lines.get(ind - 1));
                this.addView(lineA);
                this.addView(lineB);
                lines.set(ind - 1, lineA);
                lines.add(ind, lineB);
            }
        }
    }

    private int insertToArray(PitPointView newPoint)
    {
        boolean added = false;
        int i = 0;
        while(!added && i<points.size())
        {
            PitPointView point = points.get(i);
            if (point.getX()>newPoint.getX())
            {
                points.add(i,newPoint);
                added = true;
            }
            i++;
        }
        if (!added) {
            points.add(newPoint);//Add to the end of the list
            return points.size()-1;
        }
        return i-1;
    }

    private void drawLines(Context context, int pointOnMoveInd)
    {
        /*
        //remove lines
        if(lines.size()>0)
            for(PitLineView line: lines)
                removeView(line);
        lines.clear();
        */
        if (pointOnMoveInd == 0)
        {
            removeView(lines.get(0));
            PitLineView newLine = new PitLineView(context,points.get(0),points.get(1));
            lines.set(0,newLine);
            this.addView(newLine);
        }
        else if (pointOnMoveInd == points.size()-1)
        {
            removeView(lines.get(lines.size()-1));
            PitLineView newLine = new PitLineView(context,points.get(points.size()-2),points.get(points.size()-1));
            lines.set(lines.size()-1,newLine);
            this.addView(newLine);
        }

        else
        {
            removeView(lines.get(pointOnMoveInd-1));
            removeView(lines.get(pointOnMoveInd));
            PitLineView newLineA = new PitLineView(context,points.get(pointOnMoveInd-1),points.get(pointOnMoveInd));
            PitLineView newLineB = new PitLineView(context,points.get(pointOnMoveInd),points.get(pointOnMoveInd+1));
            lines.set(pointOnMoveInd-1,newLineA);
            lines.set(pointOnMoveInd,newLineB);
            this.addView(newLineA);
            this.addView(newLineB);
        }

    }

    private void sortPointsArray ()
    {
        Collections.sort(points);
    }

    /**
     * Ask all children to measure themselves and compute the measurement of this
     * layout based on the children.
     */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        // These keep track of the space we are using on the left and right for
        // views positioned there; we need member variables so we can also use
        // these for layout later.
        mLeftWidth = 0;
        mRightWidth = 0;

        // Measurement will ultimately be computing these values.
        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        // Iterate through all children, measuring them and computing our dimensions
        // from their size.
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                // Measure the child.
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);

                // Update our size information based on the layout params.  Children
                // that asked to be positioned on the left or right go in those gutters.
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.position == LayoutParams.POSITION_LEFT) {
                    mLeftWidth += Math.max(maxWidth,
                            child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                } else if (lp.position == LayoutParams.POSITION_RIGHT) {
                    mRightWidth += Math.max(maxWidth,
                            child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                } else {
                    maxWidth = Math.max(maxWidth,
                            child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                }
                maxHeight = Math.max(maxHeight,
                        child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }

        // Total width is the maximum width of all inner children plus the gutters.
        maxWidth += mLeftWidth + mRightWidth;

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        // Report our final dimensions.
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();

        // These are the far left and right edges in which we are performing layout.
        int leftPos = getPaddingLeft();
        int rightPos = right - left - getPaddingRight();

        // This is the middle region inside of the gutter.
        final int middleLeft = leftPos + mLeftWidth;
        final int middleRight = rightPos - mRightWidth;

        // These are the top and bottom edges in which we are performing layout.
        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();

                // Compute the frame in which we are placing this child.
                if (lp.position == LayoutParams.POSITION_LEFT) {
                    mTmpContainerRect.left = leftPos + lp.leftMargin;
                    mTmpContainerRect.right = leftPos + width + lp.rightMargin;
                    leftPos = mTmpContainerRect.right;
                } else if (lp.position == LayoutParams.POSITION_RIGHT) {
                    mTmpContainerRect.right = rightPos - lp.rightMargin;
                    mTmpContainerRect.left = rightPos - width - lp.leftMargin;
                    rightPos = mTmpContainerRect.left;
                } else {
                    mTmpContainerRect.left = middleLeft + lp.leftMargin;
                    mTmpContainerRect.right = middleRight - lp.rightMargin;
                }
                mTmpContainerRect.top = parentTop + lp.topMargin;
                mTmpContainerRect.bottom = parentBottom - lp.bottomMargin;

                // Use the child's gravity and size to determine its final
                // frame within its container.
                Gravity.apply(lp.gravity, width, height, mTmpContainerRect, mTmpChildRect);

                // Place the child.
                child.layout(mTmpChildRect.left, mTmpChildRect.top,
                        mTmpChildRect.right, mTmpChildRect.bottom);

            }
        }
    }

    /**
     * Custom per-child layout information.
     */

    public static class LayoutParams extends MarginLayoutParams {
        /**
         * The gravity to apply with the View to which these layout parameters
         * are associated.
         */
        public int gravity = Gravity.TOP | Gravity.START;

        public static int POSITION_MIDDLE = 0;
        public static int POSITION_LEFT = 1;
        public static int POSITION_RIGHT = 2;

        public int position = POSITION_MIDDLE;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

    }

}
