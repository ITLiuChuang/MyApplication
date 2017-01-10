package com.atguigu.mobileplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mobileplayer.R;
import com.atguigu.mobileplayer.bean.MediaItem;
import com.atguigu.mobileplayer.utils.Utils;
import com.atguigu.mobileplayer.view.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 刘闯 on 2017/1/9.
 */

public class SystemVideoPlayerActivity extends Activity implements View.OnClickListener {

    private static final String TAG = SystemVideoPlayerActivity.class.getSimpleName();//"SystemVideoPlayerActivity;
    /**
     * 视频默认屏幕大小播放
     */
    private static final int VIDEO_TYPE_DEFAULT = 1;
    /**
     * 视频全屏播放
     */
    private static final int VIDEO_TYPE_FULL = 2;
    private VideoView videoview;
    /**
     * 进度更新
     */
    private static final int PROGRESS = 0;
    /**
     * 隐藏控制面板
     */
    private static final int HIDE_MEDIA_CONTROLLER = 1;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystetime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwichePlayer;
    private LinearLayout llBottom;
    private TextView tvCurrenttime;
    private SeekBar seekBarVideo;
    private TextView tvDuration;
    private Button btuExit;
    private Button btuPre;
    private Button btuStartPause;
    private Button btuNext;
    private Button btuSwichScreen;
    private Utils utils;
    private MyBroadcastReceiver receiver;
    private float startY;
    /**
     * 滑动的区域
     */
    private int touchRang = 0;
    /**
     * 当按下时候的音量
     */
    private int mVol;
    /**
     * 列表数据
     */
    private ArrayList<MediaItem> mediaItems;
    private int position;
    private GestureDetector detector;
    /**
     * 是否隐藏控制面板
     */
    private boolean isShowMediaController = false;
    /**
     * 视频是否全屏显示
     */
    private boolean isFullScreen = false;
    private int screenWidth = 0;
    private int screeHeight = 0;
    private int videoWidth = 0;
    private int videoHeight = 0;
    /**
     * 音频管理者
     */
    private AudioManager am;
    /**
     * 当前音量
     */
    private int currentVolume;
    /**
     * 最大音量
     */
    private int maxVolme;
    /**
     * 是否静音
     */
    private boolean isMute = false;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-01-09 18:48:55 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_video_player);
        videoview = (VideoView) findViewById(R.id.videoview);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystetime = (TextView) findViewById(R.id.tv_systetime);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnSwichePlayer = (Button) findViewById(R.id.btn_swiche_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrenttime = (TextView) findViewById(R.id.tv_currenttime);
        seekBarVideo = (SeekBar) findViewById(R.id.seekBar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btuExit = (Button) findViewById(R.id.btu_exit);
        btuPre = (Button) findViewById(R.id.btu_pre);
        btuStartPause = (Button) findViewById(R.id.btu_start_pause);
        btuNext = (Button) findViewById(R.id.btu_next);
        btuSwichScreen = (Button) findViewById(R.id.btu_swich_screen);

        btnVoice.setOnClickListener(this);
        btnSwichePlayer.setOnClickListener(this);
        btuExit.setOnClickListener(this);
        btuPre.setOnClickListener(this);
        btuStartPause.setOnClickListener(this);
        btuNext.setOnClickListener(this);
        btuSwichScreen.setOnClickListener(this);

        //隐藏控制面板
        hideMediaController();

        //获取音频的最大值15,当前值
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        maxVolme = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //和SeekBar关联
        seekbarVoice.setMax(maxVolme);
        seekbarVoice.setProgress(currentVolume);
    }


    /**
     * 视频播放地址
     */
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        findViews();
        getData();
        initData();
        //设置视频加载的监听
        setLinstener();
        setData();
    }

    private void initData() {
        utils = new Utils();

        //注册监听电量广播
        receiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        //监听电量的变化
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, filter);

        //初始化手势识别器
        detector = new GestureDetector(this, new MySimpleOnGestureListener());
        //得到屏幕的宽和高
        DisplayMetrics outMetrics = new DisplayMetrics();
        //得到屏幕的参数类
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        //屏幕的宽和高
        screenWidth = outMetrics.widthPixels;
        screeHeight = outMetrics.heightPixels;
    }

    class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            startAndPause();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (isFullScreen) {
                //设置默认
                setVideoType(VIDEO_TYPE_DEFAULT);
            } else {
                //全屏显示
                setVideoType(VIDEO_TYPE_FULL);

            }
            return super.onDoubleTap(e);

        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (isShowMediaController) {
                //隐藏
                hideMediaController();
                //消息移除
                handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            } else {
                //显示
                showMediaController();
                //重新发消息4秒隐藏
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    private void setVideoType(int videoTypeDefault) {
        switch (videoTypeDefault) {
            case VIDEO_TYPE_FULL:
                isFullScreen = true;
                videoview.setViewSize(screenWidth, screeHeight);
                //把按钮设置-默认
                btuSwichScreen.setBackgroundResource(R.drawable.btn_screen_default_selector);
                break;
            case VIDEO_TYPE_DEFAULT://视频画面的默认
                isFullScreen = false;
                //视频原始的画面大小
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;
                /**
                 * 计算后的值
                 */
                int width = screenWidth;
                int height = screeHeight;
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }

                //把计算好的视频大小传递过去
                videoview.setViewSize(width, height);
                //把按钮设置-全屏
                btuSwichScreen.setBackgroundResource(R.drawable.btn_screen_full_selector);
                break;
        }
    }

    //显示控制面板
    private void showMediaController() {
        isShowMediaController = true;
        llTop.setVisibility(View.VISIBLE);
        llBottom.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏控制面板
     */
    private void hideMediaController() {
        isShowMediaController = false;
        llTop.setVisibility(View.GONE);
        llBottom.setVisibility(View.GONE);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HIDE_MEDIA_CONTROLLER:
                    //隐藏控制面板
                    hideMediaController();
                    break;
                case PROGRESS://视频播放进度更新
                    int currentPosition = videoview.getCurrentPosition();
                    //设置视频的更新
                    tvCurrenttime.setText(utils.stringForTime(currentPosition));

                    //得到系统的时间并且更新
                    tvSystetime.setText(getSystemTime());
                    //不断发送消息
                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }
        }
    };


    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //得到电量
            int level = intent.getIntExtra("level", 0);
            //主线程
            setBattery(level);
        }
    }

    /**
     * 设置电量
     *
     * @param level
     */
    private void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);

        }
    }

    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-01-09 18:48:55 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */

    public void onClick(View v) {
        if (v == btnVoice) {
            // Handle clicks for btnVoice
            if (v == btnVoice) {
                isMute = !isMute;
                updataVoiceProgress(currentVolume);
            }
        } else if (v == btnSwichePlayer) {
            // Handle clicks for btnSwichePlayer
        } else if (v == btuExit) {
            // Handle clicks for btuExit
        } else if (v == btuPre) {//上一个点击事件
            setPreVideo();

        } else if (v == btuStartPause) {
            startAndPause();
        } else if (v == btuNext) {//下一个点击事件
            // Handle clicks for btuNext
            setNextVideo();
        } else if (v == btuSwichScreen) {
            // Handle clicks for btuSwichScreen
            if (isFullScreen) {
                //设置默认
                setVideoType(VIDEO_TYPE_DEFAULT);
            } else {
                //全屏显示
                setVideoType(VIDEO_TYPE_FULL);
            }
        }
        //移除消息
        handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        //重新发消息
        handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
    }

    private void updataVoiceProgress(int progress) {
        if (isMute) {
            /**
             * 第一个参数:声音的类型
             * 第二个参数:声音的值1~15
             * 第三个参数: 1 显示系统调的声音  0  不显示
             */
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekbarVoice.setProgress(0);
            if (progress <= 0) {
                //设置静音
                isMute = true;
            }else{
                isMute = false;
            }
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            seekbarVoice.setProgress(progress);
        }
        currentVolume = progress;
    }

    private void startAndPause() {
        //是否在播放
        if (videoview.isPlaying()) {
            //点击设置为暂停
            videoview.pause();
            //按钮状态改变为播放
            btuStartPause.setBackgroundResource(R.drawable.btn_start_selector);
        } else {
            ////点击设置为暂停
            videoview.start();
            //按钮状态改变为暂停
            btuStartPause.setBackgroundResource(R.drawable.btn_pause_selector);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        //是否释放资源,子类的
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        //消息移除
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //根据位置获取播放视频的对象
            MediaItem mediaItem = mediaItems.get(position);
            videoview.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());
        } else if (uri != null) {
            //设置播放地址
            videoview.setVideoURI(uri);
            tvName.setText(uri.toString());
        }

    }

    private void setLinstener() {
        //设置视频播放监听：准备好的监听，播放出错监听，播放完成监听
        videoview.setOnPreparedListener(new MyOnPreparedListener());

        videoview.setOnErrorListener(new MyOnErrorListener());

        videoview.setOnCompletionListener(new MyOnCompletionListener());

        //设置控制面板
        // videoview.setMediaVoiceController(new MediaController(this));

        //设置视频的拖动监听(快进)
        seekBarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());

        //设置声音的拖动监听(加减音量)
        seekbarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());
    }

    private class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 状态变化时回调
         *
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                updataVoiceProgress(progress);
            }
        }

        /**
         * 手指按下时回调
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //移除消息
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        }

        /**
         * 手指离开时回调
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //发送消息
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);

        }
    }

    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 状态变化时回调
         *
         * @param seekBar
         * @param progress 当前改变的进度-要拖动到的位置
         * @param fromUser 用户导致的改变true,否则false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                //相应用户拖动
                videoview.seekTo(progress);
            }
        }

        /**
         * 手指一按下的时候回调
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //移除消息
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        }

        /**
         * 手指离开的时候回调
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //发送消息
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
        }
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            //1.单个视频-退出播放器
            //2.视频列表-播放下一个
            Toast.makeText(SystemVideoPlayerActivity.this, "视频播放完成", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 播放上一个
     */
    private void setPreVideo() {
        //判断下一个列表
        if (mediaItems != null && mediaItems.size() > 0) {
            position--;
            if (position >= 0) {
                MediaItem mediaItem = mediaItems.get(position);
                //设置标题
                tvName.setText(mediaItem.getName());
                //设置播放地址
                videoview.setVideoPath(mediaItem.getData());
                //校验2按钮状态
                checkButtonStatus();
            } else {
                //越界
                position = 0;
            }
        }
    }

    /**
     * 设置播放下一个
     */
    private void setNextVideo() {
        //1.判断一下列表
        if (mediaItems != null && mediaItems.size() > 0) {
            position++;
            if (position < mediaItems.size()) {
                MediaItem mediaItem = mediaItems.get(position);
                //设置标题
                tvName.setText(mediaItem.getName());
                //设置播放地址
                videoview.setVideoPath(mediaItem.getData());
                //主题的校验
                checkButtonStatus();
                if (position == mediaItems.size() - 1) {
                    Toast.makeText(SystemVideoPlayerActivity.this, "已经播放到最后一个了", Toast.LENGTH_SHORT).show();
                }
            } else {
                //越界
                position = mediaItems.size() - 1;
                finish();
            }
        }
        //单个uri
        else if (uri != null) {
            finish();
        }
    }

    private void checkButtonStatus() {
        //1.判断下一个列表
        if (mediaItems != null && mediaItems.size() > 0) {
            //1.其他设置默认
            setButtonEnabe(true);
            //2.播放第0个,上一个按钮设置成灰色
            if (position == 0) {
                btuPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btuPre.setEnabled(false);
            }
            //3.播放最后一个下一个按钮设置成灰色
            if (position == mediaItems.size() - 1) {
                btuNext.setBackgroundResource(R.drawable.btn_next_gray);
                btuNext.setEnabled(false);
            }
        }
        //2.单个的uri
        else if (uri != null) {
            //上一个和下一个都设置成灰色
            setButtonEnabe(false);

        }
    }

    /**
     * 设置按钮的课点击状态
     *
     * @param isEnable
     */
    private void setButtonEnabe(boolean isEnable) {
        if (isEnable) {
            btuPre.setBackgroundResource(R.drawable.btn_pre_selector);
            btuNext.setBackgroundResource(R.drawable.btn_next_selector);
        } else {
            btuPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btuNext.setBackgroundResource(R.drawable.btn_next_gray);

        }
        btuPre.setEnabled(isEnable);
        btuNext.setEnabled(isEnable);
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(SystemVideoPlayerActivity.this, "播放出错了，亲", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        /**
         * 当底层加载视频准备完成的时候回调
         *
         * @param mp
         */
        @Override
        public void onPrepared(MediaPlayer mp) {
            //得到视频原始的大小
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();

            //设置默认大小
            setVideoType(VIDEO_TYPE_DEFAULT);
            //开始播放
            videoview.start();
            //准备好的时候,将视频的总播放时长和SeekBar关联起来
            int duration = videoview.getDuration();
            seekBarVideo.setMax(duration);

            //设置时长
            tvDuration.setText(utils.stringForTime(duration));

            //发消息
            handler.sendEmptyMessage(PROGRESS);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把事件传给手势识别器
        detector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //1.按下记录起始坐标
            startY = event.getY();
            // 2.记录最大的滑动区域（屏幕的高），当前的音量
            touchRang = Math.min(screeHeight, screenWidth);
            mVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            //移除消息
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            //记录最终坐标
            float endY = event.getY();
            //计算滑动的距离
            float distanceY = startY - endY;
            //滑动屏幕的距离 ： 总距离  = 改变的声音 ： 总声音
            //改变的声音 = （滑动屏幕的距离 / 总距离)*总声音
            float delta = (distanceY / touchRang) * maxVolme;
            // 设置的声音  = 原来记录的 + 改变的声音
            int volue = (int) Math.min(Math.max(delta + mVol, 0), maxVolme);
            if (delta != 0) {
                updataVoiceProgress(volue);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 得到播放地址
     */
    private void getData() {
        //一个地址:从文件发起的单个播放请求
        uri = getIntent().getData();
        //得到视频列表
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //改变音量
            currentVolume--;
            updataVoiceProgress(currentVolume);
            //移除消息
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            //发消息
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,4000);
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            //改变音量
            currentVolume++;
            updataVoiceProgress(currentVolume);
            //移除消息
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            //发消息
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,4000);

        }
        return super.onKeyDown(keyCode, event);
    }
}

