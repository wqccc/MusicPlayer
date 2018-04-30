package com.example.bupt.musicplayer.Utils;

import com.example.bupt.musicplayer.Model.Lyric;

        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileNotFoundException;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.UnsupportedEncodingException;
        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by Administrator on 2016/8/10.
 */
public class GetLyric {

    /**
     * 静态方法获取歌词文件的内容
     * @param path
     * @return
     */
    public static List<Lyric> getCurrentMusicLyric(String path){
        String uri=path.replace(".mp3",".lrc");
        File lrcFile=new File(uri);
        FileInputStream fis = null;
        List<Lyric> lyrics=new ArrayList<Lyric>();
        try {
            fis = new FileInputStream(lrcFile);
            InputStreamReader isr = new InputStreamReader(fis, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String s="";
            while((s=br.readLine())!=null){
                if((s.indexOf("[ar:") != -1) || (s.indexOf("[ti:") != -1)
                        || (s.indexOf("[by:") != -1)
                        || (s.indexOf("[al:") != -1)
                        || (s.indexOf("[ly:") != -1)
                        || (s.indexOf("[mu:") != -1)
                        || (s.indexOf("[pu:") != -1)
                        || (s.indexOf("[offset:") != -1)
                        || (s.indexOf("[ma:") != -1)
                        || (s.indexOf("[total:") != -1))
                    continue;
                else{
                    s.replace(" ","");
                    if(s.equals(""))
                        continue;
                    s=s.replace("[","");
                    s=s.replace("]","@");
                    s=s.replaceAll("<\\d{1,10}>","");
                    String[] lyric=s.split("@");
                    if(lyric.length>0){
                        Lyric lrc=new Lyric();
                        int lrcTime=changeToTime(lyric[0]);
                        if(lrcTime==-1)
                            continue;
                        lrc.setPlayTime(lrcTime);
                        if(lyric.length<2)
                            lrc.setContextLyric(" ");
                        else {
                            lrc.setContextLyric(lyric[1]);
                        }
                        lyrics.add(lrc);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lyrics;
    }

    /**
     * 将字符转换为毫秒数
     * @param time
     * @return
     */
    public static int changeToTime(String time){
        System.out.println(time);
        time=time.replace(":","@");
        time=time.replace(".","@");
        String[] times=time.split("@");
        System.out.println(time+"--------------");
        if(times.length!=3){
            return -1;
        }
        if(times.length>0){
            System.out.println(times[0]+"-----------"+times[1]+"___________"+times[2]);
            try {
                int minute = Integer.parseInt(times[0]);
                int second = Integer.parseInt(times[1]);
                int millisecond = Integer.parseInt(times[2]);
                int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
                return currentTime;
            }
            catch(NumberFormatException e){
                return -1;
            }
        }
        else{
            return 0;
        }
    }
}

