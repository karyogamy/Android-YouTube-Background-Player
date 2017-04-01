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
 * YouTube playlist class
 * Created by Stevan Medic on 8.3.16..
 */
public class YouTubePlaylist extends YoutubePlayable {

    private long numberOfVideos;
    private String status;

    public YouTubePlaylist() {
        super();
        this.numberOfVideos = 0;
        this.status = "";
    }

    public YouTubePlaylist(final String title,
                           final String thumbnailURL,
                           final String id,
                           final long numberOfVideos,
                           final String status) {

        super( id, title, thumbnailURL );
        this.numberOfVideos = numberOfVideos;
        this.status = status;
    }

    public long getNumberOfVideos() {
        return numberOfVideos;
    }

    public void setNumberOfVideos(long numberOfVideos) {
        this.numberOfVideos = numberOfVideos;
    }

    public String getStatus() {
        return status;
    }

    public void setPrivacy(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "YouTubePlaylist {" +
                "id='" + super.getId() + '\'' +
                ", title='" + super.getTitle() + '\'' +
                ", number of videos=" + numberOfVideos +
                ", " + status +
                '}';
    }
}
