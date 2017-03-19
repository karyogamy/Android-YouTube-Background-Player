package com.smedic.tubtub.youtube;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.smedic.tubtub.model.YouTubeVideo;
import com.smedic.tubtub.utils.Config;
import com.smedic.tubtub.utils.Utils;

import java.io.IOException;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static com.smedic.tubtub.youtube.YouTubeSingleton.getYouTube;

/**
 * Created by smedic on 13.2.17..
 */

public class YouTubeVideosLoader extends AsyncTaskLoader<List<YouTubeVideo>> {

    private YouTube youtube = getYouTube();
    private String keywords;

    public YouTubeVideosLoader(Context context, String keywords) {
        super(context);
        this.keywords = keywords;
    }

    @Override
    public List<YouTubeVideo> loadInBackground() {

        ArrayList<YouTubeVideo> items = new ArrayList<>();
        try {
            YouTube.Search.List searchList = youtube.search().list("id,snippet");
            YouTube.Videos.List videosList = youtube.videos().list("id,contentDetails,statistics");

            searchList.setKey(Config.YOUTUBE_API_KEY);
            searchList.setType("video");
            searchList.setMaxResults(Config.MAX_ALLOWED_RESULT_COUNT);
            searchList.setFields("items(id/videoId,snippet/title,snippet/thumbnails/default/url)");
            searchList.setQ(keywords);

            videosList.setKey(Config.YOUTUBE_API_KEY);
            videosList.setFields("items(contentDetails/duration,statistics/viewCount)");

            //search
            SearchListResponse searchListResponse = searchList.execute();
            List<SearchResult> searchResults = searchListResponse.getItems();

            //find video list
            videosList.setId(concatenateIDs(searchResults));  //save all ids from searchList list in order to find video list
            VideoListResponse resp = videosList.execute();
            List<Video> videoResults = resp.getItems();
            //make items for displaying in listView
            for (int i = 0; i < searchResults.size(); i++) {
                YouTubeVideo item = new YouTubeVideo();
                //searchList list info
                item.setTitle(searchResults.get(i).getSnippet().getTitle());
                item.setThumbnailURL(searchResults.get(i).getSnippet().getThumbnails().getDefault().getUrl());
                item.setId(searchResults.get(i).getId().getVideoId());
                //video info
                if (videoResults.get(i) != null) {
                    BigInteger viewsNumber = videoResults.get(i).getStatistics().getViewCount();
                    String viewsFormatted = NumberFormat.getIntegerInstance().format(viewsNumber) + " views";
                    item.setViewCount(viewsFormatted);
                    String isoTime = videoResults.get(i).getContentDetails().getDuration();
                    String time = Utils.convertISO8601DurationToNormalTime(isoTime);
                    item.setDuration(time);
                } else {
                    item.setDuration("NA");
                }

                //add to the list
                items.add(item);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("SMEDIC", "loadInBackground: return " + items.size());
        return items;
    }

    @Override
    public void deliverResult(List<YouTubeVideo> data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            return;
        }
        super.deliverResult(data);
    }

    /**
     * Concatenates provided ids in order to search for all of them at once and not in many iterations (slower)
     *
     * @param searchResults results acquired from search query
     * @return concatenated ids
     */
    private static String concatenateIDs(List<SearchResult> searchResults) {

        StringBuilder contentDetails = new StringBuilder();
        for (SearchResult result : searchResults) {
            final String id = result.getId().getVideoId();
            if (id != null) {
                contentDetails.append(id);
                contentDetails.append(",");
            }
        }

        if (contentDetails.length() == 0) {
            return null;
        }

        if (contentDetails.toString().endsWith(",")) {
            contentDetails.setLength(contentDetails.length() - 1); //remove last ,
        }
        return contentDetails.toString();
    }
}
