package com.example.bupt.musicplayer;

import android.Manifest;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.pm.PackageManager;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.bupt.musicplayer.Adapter.MusicAdapter;
        import com.example.bupt.musicplayer.Model.MusicInfo;
        import com.example.bupt.musicplayer.Service.MusicService;
        import com.example.bupt.musicplayer.Utils.Media;

        import java.util.ArrayList;
        import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView listView;
    private ImageView musicImg;
    private TextView musicTitle;
    private TextView musicArtist;
    private ImageView previous;
    private ImageView play;
    private ImageView next;
    private List<MusicInfo> musicList;
    private MusicAdapter adapter;
    private LinearLayout musicUiInfo;

    private int currentPlay=0;//记录当前播放列表位置
    private Boolean playStatus=false;
    private Intent intent;
    broadcastByMusicUi receiver1;
    MainActivityReceiver  receiver2;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x123){
                listView.setAdapter(adapter);
            }
            else if(msg.what==0x124){
                if(msg.obj.equals("Previous")){
                    previousClicked();
                }
                else if(msg.obj.equals("Play")){
                    playClisked();
                }
                else if(msg.obj.equals("Next")){
                    nextClicked();
                }
            }
            else if(msg.what==0x125){
                currentPlay++;
                musicImg.setImageResource(R.drawable.music5);
                musicTitle.setText(musicList.get(currentPlay).getTitle());
                musicArtist.setText(musicList.get(currentPlay).getArtist());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        blind();
        getMusicList();
        init();
        listView.setOnItemClickListener(new ItemClicked());
        previous.setOnClickListener(this);
        play.setOnClickListener(this);
        next.setOnClickListener(this);
        musicUiInfo.setOnClickListener(this);
        broadcastReceiver();
        Intent service=new Intent(MainActivity.this,MusicService.class);
        service.putExtra("Status",3);
        startService(service);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver1);
        unregisterReceiver(receiver2);
        if(intent!=null)
            stopService(intent);
    }

    /**
     * 绑定按钮
     */
    protected void blind(){
        listView=(ListView)findViewById(R.id.listView);
        musicImg=(ImageView)findViewById(R.id.musicImg);
        musicTitle=(TextView)findViewById(R.id.musicTitle);
        musicArtist=(TextView)findViewById(R.id.musicArtist);
        previous=(ImageView)findViewById(R.id.previous);
        play=(ImageView)findViewById(R.id.play);
        next=(ImageView)findViewById(R.id.next);
        musicUiInfo=(LinearLayout)findViewById(R.id.musicUiInfo);
    }

    /**
     * 初始化
     */
    protected void init(){
        musicImg.setImageResource(R.drawable.music5);
        musicTitle.setText(musicList.get(currentPlay).getTitle());
        musicArtist.setText(musicList.get(currentPlay).getArtist());
    }

    /**
     * 启动线程获取音乐信息
     */
    protected void getMusicList(){
        int checkCallPhonePermission = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE);
        if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},123);
        musicList=new ArrayList<MusicInfo>();
        musicList=new Media().getMusicList(MainActivity.this);
        adapter=new MusicAdapter(MainActivity.this,musicList);
        handler.sendEmptyMessage(0x123);
    }


    /**
     * 按钮点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.previous: previousClicked();break;
            case R.id.play:     playClisked();break;
            case R.id.next:     nextClicked();break;
            case R.id.musicUiInfo:
                Intent toUi=new Intent(MainActivity.this,MusicUiActivity.class);
                toUi.putExtra("Title",musicList.get(currentPlay).getTitle());
                toUi.putExtra("Status",playStatus);
                toUi.putExtra("CurrentPlay",currentPlay);
                startActivity(toUi);
                break;
        }
    }

    /**
     * ListView的Item点击事件
     */
    class ItemClicked implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            currentPlay=i;
            musicImg.setImageResource(R.drawable.music5);
            musicTitle.setText(musicList.get(i).getTitle());
            musicArtist.setText(musicList.get(i).getArtist());
            play();
        }
    }

    /**
     * 点击上一首按钮previous
     */
    protected void previousClicked(){
        currentPlay--;
        if(currentPlay<0){
            Toast.makeText(MainActivity.this,"没有上一首了",Toast.LENGTH_SHORT).show();
            currentPlay++;
        }
        else{
            musicImg.setImageResource(R.drawable.music5);
            musicTitle.setText(musicList.get(currentPlay).getTitle());
            musicArtist.setText(musicList.get(currentPlay).getArtist());
            play();
        }
    }

    /**
     * 点击播放按钮play
     * Status为0：暂停  1：播放
     */
    protected void playClisked() {
        if (!playStatus) {
            play();
        } else {
            pasue();
        }
    }

    /**
     * 播放音乐
     */
    protected void play(){
        intent = new Intent(MainActivity.this, MusicService.class);
        intent.putExtra("Status",1);
        intent.putExtra("Uri", musicList.get(currentPlay).getUrl());
        intent.putExtra("CurrentPlay",currentPlay);
        startService(intent);
        play.setImageResource(R.drawable.pasue_icon);
        playStatus=true;
    }

    /**
     * 暂停
     */
    protected void pasue(){
        intent = new Intent(MainActivity.this, MusicService.class);
        intent.putExtra("Status",0);
        play.setImageResource(R.drawable.play_iocn);
        playStatus=false;
        startService(intent);
    }

    /**
     * 点击下一首按钮next
     */
    protected void nextClicked(){
        currentPlay++;
        if(currentPlay>musicList.size()-1){
            Toast.makeText(MainActivity.this,"没有下一首了",Toast.LENGTH_SHORT).show();
            currentPlay--;
        }
        else{
            musicImg.setImageResource(R.drawable.music5);
            musicTitle.setText(musicList.get(currentPlay).getTitle());
            musicArtist.setText(musicList.get(currentPlay).getArtist());
            play();
        }
    }

    /**
     * 注册广播
     */
    protected void broadcastReceiver(){
        receiver1=new broadcastByMusicUi();
        IntentFilter filter1=new IntentFilter();
        filter1.addAction("MUSICUI_CONTROL");
        registerReceiver(receiver1,filter1);
        receiver2=new MainActivityReceiver();
        IntentFilter filter2=new IntentFilter();
        filter2.addAction("UI_CHANGE");
        registerReceiver(receiver2,filter2);
    }

    /**
     * 接受来自MusicUI的广播   使MusicUI可以控制Service
     * Previous：上一首 Play：播放、暂停  Next：下一首
     */
    class broadcastByMusicUi extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg=new Message();
            msg.what=0x124;
            msg.obj=intent.getStringExtra("Control");
            handler.sendMessage(msg);
        }
    }

    /**
     * 接受来自Service的广播
     */
    class MainActivityReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("Type").equals("UI"))
                handler.sendEmptyMessage(0x125);
        }
    }
}

