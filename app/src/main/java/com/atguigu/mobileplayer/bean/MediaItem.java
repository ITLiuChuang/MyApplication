package com.atguigu.mobileplayer.bean;

import java.io.Serializable;

/**
 * Created by 刘闯 on 2017/1/9.
 */

public class MediaItem implements Serializable{
    String name;
    long duration;
    long size;
    String data;
    String artist;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHeightUrl() {
        return heightUrl;
    }

    public void setHeightUrl(String heightUrl) {
        this.heightUrl = heightUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 图片路径
     */
    String imageUrl;

    /**
     *描述
     */
    String desc;
    String heightUrl;


    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }
}
