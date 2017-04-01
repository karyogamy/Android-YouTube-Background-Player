package com.smedic.tubtub.model;

import java.io.Serializable;

public abstract class YoutubePlayable implements Serializable {

    private String id;
    private String title;
    private String thumbnailURL;

    public YoutubePlayable() {
        this.id = "";
        this.title = "";
        this.thumbnailURL = "";
    }

    public YoutubePlayable( final String id, final String title, final String thumbnailURL ) {
        this.id = id;
        this.title = title;
        this.thumbnailURL = thumbnailURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnail) {
        this.thumbnailURL = thumbnail;
    }

    @Override
    public String toString() {
        return "YouTubePlayable {" +
                "id='" + id + '\'' +
                ", title='" + title + "\'}";
    }
}
