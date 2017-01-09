package com.atguigu.mobileplayer.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 刘闯 on 2017/1/6.
 */

public abstract class BaseFragment extends Fragment {
    /**
     * 上下文
     */
    public Context mContext;

    /**
     * 当系统创建当前BaseFragment类的时候回调
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    /**
     * 当系统要创建Fragment的视图的时候回调这个方法
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView();
    }

    /**
     * 抽象方法让孩子实现
     *
     * @return
     */
    public abstract View initView();

    /**
     * 当Activity创建成功的时候回调该方法
     * 初始化数据
     * 联网请求数据
     * 绑定数据
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public void initData() {

    }

    /**
     * @param hidden
     * false：当前类显示
     * true:当前类隐藏
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("TAG", "onHiddenChanged。。" + this.toString() + ",hidden==" + hidden);
        if (!hidden) {
            onRefrshData();
        }
    }

    /**
     * 当子类要刷新数据的时候重写该方法
     */
    public void onRefrshData() {

    }

}
