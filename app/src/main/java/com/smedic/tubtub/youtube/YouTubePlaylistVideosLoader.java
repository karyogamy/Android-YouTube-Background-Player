package com.smedic.tubtub.youtube;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.smedic.tubtub.model.YouTubeVideo;
import com.smedic.tubtub.utils.Config;
import com.smedic.tubtub.utils.Utils;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.smedic.tubtub.youtube.YouTubeSingleton.getInstance;

/**
 * Created by smedic on 5.3.17..
 */

public class YouTubePlaylistVideosLoader extends AsyncTaskLoader<List<YouTubeVideo>> {

    private final static String TAG = "SMEDIC";
    private YouTube youtube = getInstance().getYouTube();
    private String playlistId;

    public YouTubePlaylistVideosLoader(Context context, String playlistId) {
        super(context);
        this.playlistId = playlistId;
    }

    @Override
    public List<YouTubeVideo> loadInBackground() {

        List<PlaylistItem> playlistItemList = new ArrayList<>();
        List<YouTubeVideo> playlistItems = new ArrayList<>();

        // Retrieve the playlist of the channel's uploaded videos.
        YouTube.PlaylistItems.List playlistItemRequest;
        try {
            playlistItemRequest = youtube.playlistItems().list("id,contentDetails,snippet,status");

            playlistItemRequest.setPlaylistId(playlistId);
            playlistItemRequest.setMaxResults(Config.MAX_ALLOWED_RESULT_COUNT);
            playlistItemRequest.setFields("items(contentDetails/videoId,snippet/title," +
                    "snippet/thumbnails/default/url,status/privacyStatus),nextPageToken");
            playlistItemRequest.setKey(Config.YOUTUBE_API_KEY);

            final PlaylistItemListResponse playlistItemResult = playlistItemRequest.execute();
            playlistItemList.addAll(playlistItemResult.getItems());

            Log.d(TAG, "all items size: " + playlistItemList.size());
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() == 404) {
                //youTubeVideosReceiver.onPlaylistNotFound(playlistId, e.getStatusCode());
                Log.d(TAG, "loadInBackground: 404 error");
                return Collections.emptyList();
            } else {
                e.printStackTrace();
            }
        } catch (UnknownHostException e) {
            //Toast.makeText(activity.getApplicationContext(), "Check internet connection", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return Collections.emptyList();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

        //videos to get duration
        YouTube.Videos.List videosList = null;
        try {
            videosList = youtube.videos().list("id,contentDetails");
            videosList.setKey(Config.YOUTUBE_API_KEY);
            videosList.setFields("items(contentDetails/duration)");

            //save all ids from searchList list in order to find video list
            StringBuilder contentDetails = new StringBuilder();

            int ii = 0;
            for (PlaylistItem result : playlistItemList) {
                contentDetails.append(result.getContentDetails().getVideoId());
                if (ii < playlistItemList.size() - 1)
                    contentDetails.append(",");
                ii++;
            }
            //find video list
            videosList.setId(contentDetails.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        VideoListResponse resp = null;
        try {
            resp = videosList.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Video> videoResults = resp.getItems();
        Iterator<PlaylistItem> pit = playlistItemList.iterator();
        Iterator<Video> vit = videoResults.iterator();
        while (pit.hasNext()) {
            final PlaylistItem playlistItem = pit.next();
            if (!playlistItem.getStatus().getPrivacyStatus().equals("public")) {
                continue;
            }

            YouTubeVideo youTubeVideo = new YouTubeVideo();
            youTubeVideo.setId(playlistItem.getContentDetails().getVideoId());
            youTubeVideo.setTitle(playlistItem.getSnippet().getTitle());
            youTubeVideo.setThumbnailURL(playlistItem.getSnippet().getThumbnails().getDefault().getUrl());

            //video info
            final Video videoItem = vit.next();
            if (videoItem != null) {
                String isoTime = videoItem.getContentDetails().getDuration();
                String time = Utils.convertDuration(isoTime);
                youTubeVideo.setDuration(time);
            } else {
                youTubeVideo.setDuration("NA");
            }
            playlistItems.add(youTubeVideo);
        }
        return playlistItems;
    }

    @Override
    public void deliverResult(List<YouTubeVideo> data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            return;
        }
        super.deliverResult(data);
    }
}
