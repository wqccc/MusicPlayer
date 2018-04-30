package com.example.bupt.musicplayer;

import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.support.design.widget.NavigationView;
        import android.support.v4.view.GravityCompat;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.SeekBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.bupt.musicplayer.Model.MusicInfo;
        import com.example.bupt.musicplayer.Utils.Media;
        import com.example.bupt.musicplayer.View.LrcView;

        import java.util.ArrayList;
        import java.util.List;

public class MusicUiActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    private TextView musicTitle;
    private ImageView previous;
    private ImageView play;
    private ImageView next;
    public static LrcView lyric;
    private SeekBar seekbar;
    MusicUiActivityReceiver receiver;
    private TextView timeStart;
    private TextView timeEnd;

    private List<MusicInfo> musicInfoList;//音乐信息
    private Boolean playStatus;//音乐播放状态--方便控制UI
    private int currentPlay;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x123){
                currentPlay++;
                playStatus = true;
                play.setImageResource(R.drawable.pasue_icon);
                musicTitle.setText(musicInfoList.get(currentPlay).getTitle());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_ui_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        blind();
        init();
        previous.setOnClickListener(this);
        play.setOnClickListener(this);
        next.setOnClickListener(this);
        seekbar.setOnSeekBarChangeListener(new seekbarLinister());
        broadcastReceiver();
        Intent broadcast = new Intent("MUSICUI_MUSIC_UI");
        broadcast.putExtra("Type", "Lyric");
        broadcast.putExtra("Display",1);
        sendBroadcast(broadcast);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.music_ui_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            finish();
            return true;
        }
        else if(id==R.id.action_settings2){
            Intent broadcast = new Intent("MUSICUI_MUSIC_UI");
            broadcast.putExtra("Type", "OneLoop");
            sendBroadcast(broadcast);
            Toast.makeText(MusicUiActivity.this,"单曲循环中",Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(id==R.id.action_settings3){
            Intent broadcast = new Intent("MUSICUI_MUSIC_UI");
            broadcast.putExtra("Type", "ListLoop");
            sendBroadcast(broadcast);
            Toast.makeText(MusicUiActivity.this,"列表循环中",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent broadcast = new Intent("MUSICUI_MUSIC_UI");
        broadcast.putExtra("Type", "Lyric");
        broadcast.putExtra("Display",0);
        unregisterReceiver(receiver);
    }

    /**
     * 绑定按钮
     */
    protected void blind(){
        musicTitle=(TextView)findViewById(R.id.musicTitle);
        previous=(ImageView)findViewById(R.id.previous);
        play=(ImageView)findViewById(R.id.play);
        next=(ImageView)findViewById(R.id.next);
        lyric=(LrcView)findViewById(R.id.lyric);
        seekbar=(SeekBar)findViewById(R.id.seekbar);
        timeStart=(TextView)findViewById(R.id.timeStart);
        timeEnd=(TextView)findViewById(R.id.timeEnd);
    }

    /**
     * 初始化UI
     */
    protected void init(){
        musicInfoList=new ArrayList<MusicInfo>();
        musicInfoList=new Media().getMusicList(MusicUiActivity.this);
        Intent intent=getIntent();
        musicTitle.setText(intent.getStringExtra("Title"));
        playStatus=intent.getBooleanExtra("Status",false);
        currentPlay=intent.getIntExtra("CurrentPlay",-1);
        int max= (int) musicInfoList.get(currentPlay).getDuration();
        seekbar.setMax(max);
        timeEnd.setText(becomeTime(max));
        if(playStatus){
            play.setImageResource(R.drawable.pasue_icon);
        }
        else{
            play.setImageResource(R.drawable.play_iocn);
        }
//        new mAsyncTask().execute(musicInfoList.get(currentPlay).getUrl());
    }

    /**
     * 将时间转化为--:--型
     * @param time
     * @return
     */
    protected String becomeTime(int time){
        StringBuffer becometime=new StringBuffer();
        int million= (int) (time/1000);
        int m=million/60;
        int s=million%60;
        if(m>=10)
            becometime.append(m);
        else
            becometime.append("0"+m);
        if(s>=10)
            becometime.append(":"+s);
        else
            becometime.append(":0"+s);
        return becometime.toString();
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch(id){
            case R.id.previous:
                previousClicked();
                break;
            case R.id.play:
                playClicked();
                break;
            case R.id.next:
                nextClicked();
                break;
        }
    }

    /**
     * 播放上一首
     */
    protected void previousClicked(){
        currentPlay--;
        if(currentPlay<0){
            currentPlay++;
        }
        else {
            int max=(int) musicInfoList.get(currentPlay).getDuration();
            seekbar.setMax(max);
            timeEnd.setText(becomeTime(max));
            Intent broadcast = new Intent("MUSICUI_CONTROL");
            broadcast.putExtra("Control", "Previous");
            sendBroadcast(broadcast);
            playStatus = true;
            play.setImageResource(R.drawable.pasue_icon);
            musicTitle.setText(musicInfoList.get(currentPlay).getTitle());
        }
    }

    /**
     * 播放
     */
    protected void playClicked(){
        Intent broadcast=new Intent("MUSICUI_CONTROL");
        broadcast.putExtra("Control","Play");
        sendBroadcast(broadcast);
        if(!playStatus) {
            play.setImageResource(R.drawable.pasue_icon);
            playStatus=true;
        }
        else{
            play.setImageResource(R.drawable.play_iocn);
            playStatus=false;
        }
    }

    /**
     * 播放下一首
     */
    protected void nextClicked(){
        currentPlay++;
        if(currentPlay>musicInfoList.size()-1){
            currentPlay--;
        }
        else {
            int max=(int) musicInfoList.get(currentPlay).getDuration();
            seekbar.setMax(max);
            timeEnd.setText(becomeTime(max));
            Intent broadcast = new Intent("MUSICUI_CONTROL");
            broadcast.putExtra("Control", "Next");
            sendBroadcast(broadcast);
            playStatus = true;
            play.setImageResource(R.drawable.pasue_icon);
            musicTitle.setText(musicInfoList.get(currentPlay).getTitle());
        }
    }

    /**
     * 用户改变进度
     */
    class seekbarLinister implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if(b){
                Intent intent=new Intent("MUSICUI_MUSIC_UI");
                intent.putExtra("Type","ChangeSeekbar");
                intent.putExtra("SeekTime",i);
                sendBroadcast(intent);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    /**
     * 注册广播
     */
    protected void broadcastReceiver(){
        receiver=new MusicUiActivityReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("UI_CHANGE");
        registerReceiver(receiver,filter);
    }

    /**
     * 广播
     */
    class MusicUiActivityReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("Type").equals("UI")) {
                handler.sendEmptyMessage(0x123);
            }
            else if(intent.getStringExtra("Type").equals("Seekbar")){
                int current=intent.getIntExtra("Time",-1);
                seekbar.setProgress(current);
                timeStart.setText(becomeTime(current));
            }
        }
    }

//    /**
//     * 异步任务加载歌词
//     */
//    class mAsyncTask extends AsyncTask<String,Void,String>{
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String s=new GetLyric().getCurrentMusicLyric(strings[0]);
//            return s;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            lyric.setText(s);
//        }
//    }
}

