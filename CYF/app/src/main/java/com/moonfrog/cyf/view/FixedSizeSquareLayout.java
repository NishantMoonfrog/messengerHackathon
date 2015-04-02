package com.moonfrog.cyf.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by srinath on 02/04/15.
 */
public class FixedSizeSquareLayout extends SquareLayout {
    public FixedSizeSquareLayout(Context context) {
        super(context);
    }

    public FixedSizeSquareLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(700, 700);
    }
}
