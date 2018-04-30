package com.example.bupt.musicplayer.Adapter;

import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.TextView;

        import com.example.bupt.musicplayer.Model.MusicInfo;
        import com.example.bupt.musicplayer.R;

        import java.util.List;

/**
 * Created by Administrator on 2016/8/5.
 */
public class MusicAdapter extends BaseAdapter {

    private Context context;
    private List<MusicInfo> musicInfoList;

    public MusicAdapter(Context context,List<MusicInfo> musicInfoList){
        this.context=context;
        this.musicInfoList=musicInfoList;
    }

    @Override
    public int getCount() {
        return musicInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.musicline,viewGroup,false);
            viewHolder=new ViewHolder();
            viewHolder.name=(TextView)view.findViewById(R.id.name);
            viewHolder.artist=(TextView)view.findViewById(R.id.artist);
            viewHolder.duration=(TextView)view.findViewById(R.id.duration);
            view.setTag(viewHolder);
        }
        else {
            viewHolder =(ViewHolder) view.getTag();
        }
        viewHolder.name.setText(musicInfoList.get(i).getTitle());
        viewHolder.artist.setText(musicInfoList.get(i).getArtist());
        viewHolder.duration.setText(becomeTime(musicInfoList.get(i).getDuration()));
        return view;
    }
    static class ViewHolder{
        TextView name;
        TextView artist;
        TextView duration;
    }

    //将毫秒转换为--：--形式
    protected String becomeTime(long time){
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
}

