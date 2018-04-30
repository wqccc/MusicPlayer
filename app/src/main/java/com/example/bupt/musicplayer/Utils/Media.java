package com.example.bupt.musicplayer.Utils;

        import android.content.Context;
        import android.database.Cursor;
        import android.provider.MediaStore;

        import com.example.bupt.musicplayer.Model.MusicInfo;

        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by Administrator on 2016/8/7.
 */
public class Media {
    /**
     * 从ContextProvider中查找音乐信息保存在list中
     * @param context
     * @return
     */
    public static List<MusicInfo> getMusicList(Context context){
        List<MusicInfo> musicInfoList=new ArrayList<MusicInfo>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        for (int i = 0; i < cursor.getCount(); i++) {
            MusicInfo MusicInfo = new MusicInfo();
            cursor.moveToNext();
            long id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));   //音乐id
            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));//音乐标题
            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家
            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));//时长
            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));  //文件大小
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));  //文件路径
            String album=cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM));//专辑
            long albumId = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));//专辑id
            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐
            if (isMusic != 0) {     //只把音乐添加到集合当中
                MusicInfo.setId(id);
                MusicInfo.setTitle(title);
                MusicInfo.setArtist(artist);
                MusicInfo.setDuration(duration);
                MusicInfo.setSize(size);
                MusicInfo.setUrl(url);
                MusicInfo.setAlbum(albumId);
                musicInfoList.add(MusicInfo);
            }
        }
        cursor.close();
        return musicInfoList;
    }
}

