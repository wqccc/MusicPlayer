package com.example.bupt.musicplayer.Service;

import android.app.Service;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.media.MediaPlayer;
        import android.os.Handler;
        import android.os.IBinder;
        import android.os.Message;
        import android.support.annotation.Nullable;
        import android.widget.Toast;

        import com.example.bupt.musicplayer.Model.Lyric;
        import com.example.bupt.musicplayer.Model.MusicInfo;
        import com.example.bupt.musicplayer.MusicUiActivity;
        import com.example.bupt.musicplayer.Utils.GetLyric;
        import com.example.bupt.musicplayer.Utils.Media;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Timer;
        import java.util.TimerTask;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;
    private List<MusicInfo> musicInfoList;
    private String currentPath="";
    private int currentPlay=0;
    private int currentTime;
    private int duration;
    private String playWay="";
    private List<Lyric> lyrics=new ArrayList<Lyric>();
    broadcastByActivity receiver;
    private int index2=0;
    private int displayLyric=0;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if(msg.what==0x129){
                if(currentPath.equals("")){
                    currentPath=musicInfoList.get(0).getUrl();
                    initLrc();
                    currentPath="";
                }
                else {
                    initLrc();
                }
            }
        }
    };
    private Handler handler2=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==99){
                Intent bradcast=new Intent("UI_CHANGE");
                bradcast.putExtra("Type","Seekbar");
                bradcast.putExtra("Time",mediaPlayer.getCurrentPosition());
                sendBroadcast(bradcast);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer=new MediaPlayer();
        musicInfoList=new ArrayList<MusicInfo>();
        musicInfoList=new Media().getMusicList(MusicService.this);
        broadcastReceiver();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {//根据不同的播放方式进行操作
                if(playWay.equals("OneLoop")){
                    index2=0;
                    mediaPlayer.start();
                }
                else if(playWay.equals("ListLoop")){
                    if(currentPlay+1<=musicInfoList.size()){
                        index2=0;
                        currentPlay++;
                        currentPath=musicInfoList.get(currentPlay).getUrl();
                        play(0);
                        Intent bradcast=new Intent("UI_CHANGE");
                        bradcast.putExtra("Type","UI");
                        sendBroadcast(bradcast);
                    }
                    else{
                        index2=0;
                        Toast.makeText(MusicService.this,"播放完毕",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        index2=0;
        int status=intent.getIntExtra("Status",-1);
        if(status==0){
            pasue();
        }
        if(status==1) {
            String playPath = intent.getStringExtra("Uri");
            currentPlay=intent.getIntExtra("CurrentPlay",-1);
            if(currentPath.equals("")){
                currentPath=playPath;
                play(0);
                System.out.println("当前currentPlay为空------------------------------");
            }
            else {
                if(currentPath.equals(playPath)) {
                    play(1);
                    System.out.println("恢复当前currentPlay------------------------------");
                }
                else {
                    currentPath = playPath;
                    play(0);
                    System.out.println("当前currentPlay"+currentPath+"------------------------------");
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /**
     * 播放音乐
     * @param time  参数为0：从头播放   为1：从当前时间播放
     */
    protected void play(int time){
        if(displayLyric==1) {
            initLrc();
//            handler2.sendEmptyMessage(111);
        }
        if(time==0) {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(currentPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            mediaPlayer.start();
        }
    }

    /**
     *暂停
     */
    protected void pasue(){
        mediaPlayer.pause();
    }

    /**
     *初始化歌词
     */
    protected void initLrc(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler2.sendEmptyMessage(99);
            }
        },0,1000);
        lyrics=new GetLyric().getCurrentMusicLyric(currentPath);
        if(lyrics.size()==0)
            MusicUiActivity.lyric.setText("没有歌词");
        else {
            MusicUiActivity.lyric.setLyrics(lyrics);
            handler.postDelayed(runnable,100);
//            MusicUiActivity.lyric.setText(lyrics.get(0).getContextLyric() + lyrics.get(0).getPlayTime());
        }
    }

    /**
     * 使用handler隔50毫秒发送一次更新歌词
     */
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            MusicUiActivity.lyric.setIndex(index());
            MusicUiActivity.lyric.invalidate();
            handler.postDelayed(runnable,50);
        }
    };

    /**
     * 根据播放位置定位歌词位置
     * @return
     */
    public int index2(){
        if(mediaPlayer.isPlaying()){
            currentTime=mediaPlayer.getCurrentPosition();
            duration=mediaPlayer.getDuration();
        }
        if(currentTime<duration){
            if(index2==lyrics.size()-1){
                return index2;
            }
            for (int i = index2; i < lyrics.size()-1; i++) {
                if(currentTime>lyrics.get(i).getPlayTime()&&currentTime<lyrics.get(i+1).getPlayTime()){
                    index2=i;
                    return i;
                }
            }
            return index2;
        }
        return index2;
    }

    public int index(){
        int index=0;
        if(mediaPlayer.isPlaying()){
            currentTime=mediaPlayer.getCurrentPosition();
            duration=mediaPlayer.getDuration();
        }
        if(currentTime<duration){
            for (int i = 0; i < lyrics.size(); i++) {
                if (i < lyrics.size() - 1) {
                    if (currentTime < lyrics.get(i).getPlayTime() && i == 0) {
                        index = i;
                    }
                    if (currentTime > lyrics.get(i).getPlayTime()
                            && currentTime < lyrics.get(i + 1).getPlayTime()) {
                        index = i;
                    }
                }
                if (i == lyrics.size() - 1
                        && currentTime > lyrics.get(i).getPlayTime()) {
                    index = i;
                }
            }
        }
        return index;
    }

    /**
     * 注册广播
     */
    protected void broadcastReceiver(){
        receiver=new broadcastByActivity();
        IntentFilter filter=new IntentFilter();
        filter.addAction("MUSICUI_MUSIC_UI");
        registerReceiver(receiver,filter);
    }

    class broadcastByActivity extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("Type").equals("ChangeSeekbar")){
                mediaPlayer.seekTo(intent.getIntExtra("SeekTime",-1));
            }
            if(intent.getStringExtra("Type").equals("Lyric")){
                displayLyric=intent.getIntExtra("Display",-1);
                handler.sendEmptyMessage(0x129);
            }
            else {
                playWay = intent.getStringExtra("Type");
                System.out.println("-------------------" + playWay + "------------------");
            }
        }
    }
}

