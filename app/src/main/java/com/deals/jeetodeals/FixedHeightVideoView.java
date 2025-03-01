package com.deals.jeetodeals;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class FixedHeightVideoView extends VideoView {
    public FixedHeightVideoView(Context context) {
        super(context);
    }

    public FixedHeightVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedHeightVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int fixedHeight = 300 * getResources().getDisplayMetrics().densityDpi / 160; // Convert dp to px
        int heightSpec = MeasureSpec.makeMeasureSpec(fixedHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}

