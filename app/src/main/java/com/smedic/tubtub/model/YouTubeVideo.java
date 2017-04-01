/*
 * Copyright (C) 2016 SMedic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smedic.tubtub.model;

/**
 * YouTube video class
 * Created by smedic on 3.2.16..
 */
public class YouTubeVideo extends YoutubePlayable {
    private String duration;
    private String viewCount;

    public YouTubeVideo() {
        super();
        this.duration = "";
        this.viewCount = "";
    }

    public YouTubeVideo(YouTubeVideo newVideo) {
        super( newVideo.getId(), newVideo.getTitle(), newVideo.getThumbnailURL() );
        this.duration = newVideo.duration;
        this.viewCount = newVideo.viewCount;
    }

    public YouTubeVideo(final String id,
                        final String title,
                        final String thumbnailURL,
                        final String duration,
                        final String viewCount) {
        super( id, title, thumbnailURL );
        this.duration = duration;
        this.viewCount = viewCount;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    @Override
    public String toString() {
        return "YouTubeVideo {" +
                "id='" + this.getId() + '\'' +
                ", title='" + this.getTitle() + '\'' +
                '}';
    }
}
