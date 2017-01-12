package com.atguigu.mobileplayer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.mobileplayer.R;
import com.atguigu.mobileplayer.activity.SystemVideoPlayerActivity;
import com.atguigu.mobileplayer.adapter.NetVideoAdapter;
import com.atguigu.mobileplayer.base.BaseFragment;
import com.atguigu.mobileplayer.bean.MediaItem;
import com.atguigu.mobileplayer.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import static com.atguigu.mobileplayer.R.id.listview;

/**
 * Created by 刘闯 on 2017/1/7.
 * 网络视频
 */

public class NetVideoFragment extends BaseFragment {

    /**
     * 集合
     */
    private ArrayList<MediaItem> mediaItems;
    private NetVideoAdapter adapter;
    @ViewInject(listview)
    private ListView listView;
    @ViewInject(R.id.tv_no_media)
    private TextView tv_no_media;

    @Override
    public View initView() {
        Log.e("TAG", "网络视频ui初始化了");
        View view = View.inflate(mContext, R.layout.fragment_net_video, null);
        //把view注入到xUtils3框中
        x.view().inject(NetVideoFragment.this, view);
        //才初始化好的
        listView.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //传递列表数据
            Intent intent = new Intent(mContext, SystemVideoPlayerActivity.class);

            Bundle bundle = new Bundle();
            //列表数据
            bundle.putSerializable("videolist", mediaItems);
            intent.putExtras(bundle);
            //传递点击的位置
            intent.putExtra("position", position);
            startActivity(intent);

        }
    }

    @Override
    public void initData() {
        super.initData();
        Log.e("TAG", "网络视频数据初始化了");
       // String json = CacheUtils.getString(mContext,Constant.NET_URL);
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
                Log.e("TAG", "xUtils3联网请求成功==");
                Log.e("TAG", "线程名称==" + Thread.currentThread().getName());
              //  CacheUtils.putString(mContext,Constant.NET_URL,result);
                processData(result);
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

    /**
     * 解析json数据：gson解析，fastjson解析和手动解析（原生的api）
     * 显示数据-设置适配器
     *
     * @param json
     */
    private void processData(String json) {
        mediaItems = parsedJson(json);

        Log.e("TAG", "mediaItems.get(0).getName()==" + mediaItems.get(0).getName());
        if (mediaItems != null && mediaItems.size() > 0) {
            //有数据
            tv_no_media.setVisibility(View.GONE);
            adapter = new NetVideoAdapter(mContext,mediaItems);
            listView.setAdapter(adapter);
        }else {
            tv_no_media.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<MediaItem> parsedJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("trailers");


            for (int i = 0; i < jsonArray.length(); i++) {

                MediaItem mediaItem = new MediaItem();

                mediaItems.add(mediaItem);//添加到集合中

                JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
                String name = jsonObjectItem.optString("movieName");
                mediaItem.setName(name);
                String desc = jsonObjectItem.optString("videoTitle");
                mediaItem.setDesc(desc);
                String url = jsonObjectItem.optString("url");
                mediaItem.setData(url);
                String hightUrl = jsonObjectItem.optString("hightUrl");
                mediaItem.setHeightUrl(hightUrl);
                String coverImg = jsonObjectItem.optString("coverImg");
                mediaItem.setImageUrl(coverImg);
                int videoLength = jsonObjectItem.optInt("videoLength");
                mediaItem.setDuration(videoLength);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return mediaItems;

    }

    @Override
    public void onRefrshData() {
        super.onRefrshData();
        Log.e("TAG", "onHiddenChanged。。" + this.toString());
    }
}
