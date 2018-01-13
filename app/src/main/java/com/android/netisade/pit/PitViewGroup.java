package com.android.netisade.pit;

import android.content.Context;
import android.content.res.TypedArray;
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
import java.util.Comparator;

/**
 * Created by nsade on 08-Jan-18.
 */

public class PitViewGroup extends ViewGroup {
    private ArrayList<PitPointView> points = new ArrayList<>();
    private ArrayList<PitLineView> lines = new ArrayList<>();
    private final int NUM_OF_POINT_TO_INIT = 5;
    boolean initialized = false;
    private int screenWidth;
    private int screenHeight;
    private int defaultXposNewPoint;
    private int defaultYposNewPoint;

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
        init(context);
    }

    private void init(final Context context)
    {
        if(!initialized) {
            addButton(context);
            getScreenSize(context);
            addAxis(context);
            PitPointView p1 = new PitPointView(context, 200, 500);
            PitPointView p2 = new PitPointView(context, 400, 600);
            PitPointView p3 = new PitPointView(context, 300, 300);
            PitPointView p4 = new PitPointView(context, 800, 1000);
            PitPointView p5 = new PitPointView(context, 1000, 800);
            points.add(p1);
            points.add(p2);
            points.add(p3);
            points.add(p4);
            points.add(p5);
            sortPointsArray();
            for (PitPointView point : points){
                addPointListener(point,context);
            }
            initialized = true;
        }
        for (PitPointView point : points)
            this.addView(point);
        drawLines(context);
    }

    private void addAxis(Context context)
    {
        View yLine = new View(context);
        View xLine = new View(context);
        yLine.setLayoutParams(new PitViewGroup.LayoutParams(1,screenHeight));
        xLine.setLayoutParams(new PitViewGroup.LayoutParams(screenWidth,1));
        yLine.setBackgroundColor(Color.BLACK);
        xLine.setBackgroundColor(Color.BLACK);
        yLine.setX(screenWidth/2);
        xLine.setY(screenHeight/2);
        this.addView(yLine);
        this.addView(xLine);
    }

    private void getScreenSize(Context context)
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        defaultXposNewPoint = screenWidth/2-PitPointView.POINT_SIZE_PIXELS/2-PitPointView.TOUCH_FACTOR/2;
        defaultYposNewPoint = screenHeight/2-PitPointView.POINT_SIZE_PIXELS/2-PitPointView.TOUCH_FACTOR/2;
    }

    private void addPointListener ( PitPointView point,final Context context)
    {
        point.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sortPointsArray();
                drawLines(context);
                return false;
            }
        });

    }

    private void addButton(final Context context)
    {
        Button addPointButton = new Button(context);
        addPointButton.setText("Add point!");
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
        PitPointView newPoint = new PitPointView(context, defaultXposNewPoint, defaultYposNewPoint);
        insertToArray(newPoint);
        addPointListener(newPoint,context);
        this.addView(newPoint);
        drawLines(context);
    }


    private void insertToArray(PitPointView newPoint)
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
        if (!added)
            points.add(newPoint);//Add to the end of the list
    }

    private void drawLines(Context context)
    {
        //remove lines
        if(lines.size()>0)
            for(PitLineView line: lines)
                removeView(line);

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

    private void sortPointsArray ()
    {
        Collections.sort(points, new Comparator<PitPointView>() {
            @Override
            public int compare(PitPointView first, PitPointView secend) {
                return first.getX() < secend.getX() ? -1 : (secend.getX() < first.getX()) ? 1 : 0;
            }
        });
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


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new PitViewGroup.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
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

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            // Pull the layout param values from the layout XML during
            // inflation.  This is not needed if you don't care about
            // changing the layout behavior in XML.
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.CustomLayoutLP);
            gravity = a.getInt(R.styleable.CustomLayoutLP_android_layout_gravity, gravity);
            position = a.getInt(R.styleable.CustomLayoutLP_layout_position, position);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }


}
