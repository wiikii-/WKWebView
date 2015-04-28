package com.wiikii.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wiikii.library.R;

/**
 * Created by wiikii on 15/4/9.
 */
public class SRProgressBar extends RelativeLayout {

    private float mCurrentProgress = 0.0F;
    private float mMaxProgress = 100.0F;
    private int mTotalWidth;
    private ImageView mProgressImageView;

    public SRProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initProgressBar();
    }

    public SRProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initProgressBar();
    }

    public SRProgressBar(Context context) {
        super(context);
        initProgressBar();
    }

    private float calculatorProgress(float progress) {
        return Math.min(Math.max(progress, 0.0F), this.mMaxProgress);
    }

    private void initProgressBar() {
        this.mProgressImageView = new ImageView(getContext());
        this.mProgressImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(this.mProgressImageView, layoutParams);
        this.mProgressImageView.setImageResource(R.drawable.browser_progress);
    }

    private void refreshProgress() {
        LayoutParams layoutParams = (LayoutParams)this.mProgressImageView.getLayoutParams();
        layoutParams.width = ((int)(this.mCurrentProgress / this.mMaxProgress * this.mTotalWidth));
        this.mProgressImageView.setLayoutParams(layoutParams);
    }

    public float getProgress() {
        return this.mCurrentProgress;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.mTotalWidth = getWidth();
        refreshProgress();
    }

    public void setProgress(float progress) {
        this.mCurrentProgress = calculatorProgress(progress);
        if (this.mTotalWidth > 0) {
            refreshProgress();
        }
    }
}
