package com.extraordinary.weaves.tendency.bean;

import java.io.Serializable;
import java.util.List;

public class GeDanData implements Serializable {
    public List<Music> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }

    private List<Music> musicList;

    public String getGeDanName() {
        return geDanName;
    }

    public GeDanData(List<Music> musicList, String geDanName,long geDanId) {
        this.musicList = musicList;
        this.geDanName = geDanName;
        this.geDanId = geDanId;
    }

    public GeDanData() {
        super();
    }

    public void setGeDanName(String geDanName) {
        this.geDanName = geDanName;
    }

    private String geDanName;

    public long getGeDanId() {
        return geDanId;
    }

    public void setGeDanId(long geDanId) {
        this.geDanId = geDanId;
    }

    private long geDanId;

}
