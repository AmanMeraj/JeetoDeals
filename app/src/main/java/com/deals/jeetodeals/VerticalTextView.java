package com.deals.jeetodeals;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class VerticalTextView extends androidx.appcompat.widget.AppCompatTextView {

    public final static int ORIENTATION_UP_TO_DOWN = 0;
    public final static int ORIENTATION_DOWN_TO_UP = 1;
    public final static int ORIENTATION_LEFT_TO_RIGHT = 2;
    public final static int ORIENTATION_RIGHT_TO_LEFT = 3;

    Rect text_bounds = new Rect();
    private int direction;

    public VerticalTextView(Context context) {
        super(context);
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerticalTextView);
        direction = a.getInt(R.styleable.VerticalTextView_direction, 0); // Fix here
        a.recycle();

        requestLayout();
        invalidate();
    }


    public void setDirection(int direction) {
        this.direction = direction;

        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        getPaint().getTextBounds(getText().toString(), 0, getText().length(),
                text_bounds);
        if (direction == ORIENTATION_LEFT_TO_RIGHT
                || direction == ORIENTATION_RIGHT_TO_LEFT) {
            setMeasuredDimension(measureHeight(widthMeasureSpec),
                    measureWidth(heightMeasureSpec));
        } else if (direction == ORIENTATION_UP_TO_DOWN
                || direction == ORIENTATION_DOWN_TO_UP) {
            setMeasuredDimension(measureWidth(widthMeasureSpec),
                    measureHeight(heightMeasureSpec));
        }

    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = text_bounds.height() + getPaddingTop()
                    + getPaddingBottom();
            // result = text_bounds.height();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = text_bounds.width() + getPaddingLeft() + getPaddingRight();
            // result = text_bounds.width();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        int startX = 0;
        int startY = 0;
        int stopX = 0;
        int stopY = 0;
        Path path = new Path();

        // Up to Down (top to bottom)
        if (direction == ORIENTATION_UP_TO_DOWN) {
            startX = (getWidth() - text_bounds.height()) / 2;  // Center horizontally
            startY = (getHeight() - text_bounds.width()) / 2;  // Center vertically
            stopX = (getWidth() - text_bounds.height()) / 2;   // Keep the same X position
            stopY = (getHeight() + text_bounds.width()) / 2;   // Move to the bottom
            path.moveTo(startX, startY);
            path.lineTo(stopX, stopY);
        }
        // Down to Up (bottom to top)
        else if (direction == ORIENTATION_DOWN_TO_UP) {
            startX = (getWidth() - text_bounds.width()) / 2;  // Center horizontally
            startY = getHeight() - text_bounds.height();      // Start at the bottom
            stopX = startX;                                  // Keep the same X position
            stopY = 0;                                       // End at the top
            path.moveTo(startX, startY);
            path.lineTo(stopX, stopY);
        }
        // Left to Right (normal)
        else if (direction == ORIENTATION_LEFT_TO_RIGHT) {
            startX = (getWidth() - text_bounds.width()) / 2;  // Center horizontally
            startY = (getHeight() + text_bounds.height()) / 2; // Center vertically
            stopX = startX + text_bounds.width();             // Move right horizontally
            stopY = startY;                                  // Keep Y position same
            path.moveTo(startX, startY);
            path.lineTo(stopX, stopY);
        }
        // Right to Left
        else if (direction == ORIENTATION_RIGHT_TO_LEFT) {
            startX = (getWidth() + text_bounds.width()) / 2;   // Center horizontally in reverse
            startY = (getHeight() - text_bounds.height()) / 2; // Center vertically
            stopX = startX - text_bounds.width();              // Move left horizontally
            stopY = startY;                                   // Keep Y position same
            path.moveTo(startX, startY);
            path.lineTo(stopX, stopY);
        }

        this.getPaint().setColor(this.getCurrentTextColor());
        canvas.drawTextOnPath(getText().toString(), path, 0, 0, this.getPaint());

        canvas.restore();
    }

}