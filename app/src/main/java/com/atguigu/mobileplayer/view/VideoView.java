package com.atguigu.mobileplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by 刘闯 on 2017/1/10.
 */

public class VideoView extends android.widget.VideoView {
    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    public void setViewSize(int screenWidth, int screeHeight) {
        //视频画面的宽和高
        ViewGroup.LayoutParams l = getLayoutParams();
        l.width = screenWidth;
        l.height = screeHeight;
        setLayoutParams(l);
    }
}
