package com.atguigu.mobileplayer.fragment;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.mobileplayer.R;
import com.atguigu.mobileplayer.base.BaseFragment;
import com.atguigu.mobileplayer.utils.Constant;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by 刘闯 on 2017/1/7.
 * 网络视频
 */

public class NetVideoFragment extends BaseFragment {
    @ViewInject(R.id.listview)
    private ListView listView;
    @ViewInject(R.id.tv_no_media)
    private TextView tv_no_media;

    @Override
    public View initView() {
        Log.e("TAG", "网络视频ui初始化了");
        View view = View.inflate(mContext, R.layout.fragment_net_video, null);
        //把view注入到xUtils3框中
        x.view().inject(NetVideoFragment.this, view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        Log.e("TAG", "网络视频数据初始化了");
        getDataFromNet();
    }

    /**
     * 使用xutils3联网请求数据
     */
    private void getDataFromNet() {
        //网络的路径
        RequestParams params = new RequestParams(Constant.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "xUtils3联网请求成功==" + result);
                Log.e("TAG", "线程名称==" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "xUtils3请求失败了==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onRefrshData() {
        super.onRefrshData();
        Log.e("TAG", "onHiddenChanged。。" + this.toString());
    }
}
