package com.atguigu.mobileplayer.fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.atguigu.mobileplayer.base.BaseFragment;

/**
 * Created by 刘闯 on 2017/1/7.
 * 本地音频
 */

public class LocalAudioFragment extends BaseFragment {
    private TextView textView;
    @Override
    public View initView() {
        Log.e("TAG","本地音频ui初始化了");
        textView = new TextView(mContext);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(25);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        Log.e("TAG","本地音频数据初始化了");
        textView.setText("本地音频");
    }

    @Override
    public void onRefrshData() {
        super.onRefrshData();
        textView.setText("本地视频刷新了");
        Log.e("TAG","onHiddenChanged。。"+this.toString());
    }
}
