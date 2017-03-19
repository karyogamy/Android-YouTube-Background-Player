package com.smedic.tubtub.youtube;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.smedic.tubtub.model.YouTubePlaylist;
import com.smedic.tubtub.utils.Config;
import com.smedic.tubtub.utils.SearchType;
import com.smedic.tubtub.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.smedic.tubtub.youtube.YouTubeSingleton.getYouTube;

/**
 * Created by smedic on 13.2.17..
 */

public class YouTubePlaylistsLoader extends AsyncTaskLoader<List<YouTubePlaylist>> {

    private static final String TAG = "SMEDIC";

    private YouTube youtube = getYouTube();
    private String keywords;
    private SearchType searchType;

    public YouTubePlaylistsLoader(Context context,
                                  @NonNull final String keyword,
                                  @NonNull final SearchType type) {
        super(context);
        this.keywords = keyword;
        this.searchType = type;
    }

    @Nullable
    private String ids() throws IOException {
        switch ( searchType ) {
            case BY_QUERY:
                YouTube.Search.List searchList = youtube.search().list("id,snippet");
                searchList.setKey(Config.YOUTUBE_API_KEY);
                searchList.setType("playlist");
                searchList.setMaxResults(Config.MAX_ALLOWED_RESULT_COUNT);
                searchList.setFields("items(id/playlistId)");
                searchList.setQ(keywords);

                final SearchListResponse searchListResponse = searchList.execute();
                final List<SearchResult> searchResults = searchListResponse.getItems();

                return concatenateIDs(searchResults);

            case BY_ID:
                return keywords;
            default:
                return keywords;
        }
    }

    @Override
    public List<YouTubePlaylist> loadInBackground() {
        try {
            final String id = ids();
            if ( id == null || id.isEmpty() ) return Collections.emptyList();

            YouTube.Playlists.List playlistSearches = youtube.playlists().list("id,snippet,contentDetails,status");
            playlistSearches.setKey(Config.YOUTUBE_API_KEY);
            playlistSearches.setFields("items(id,snippet/title,snippet/thumbnails/default/url,contentDetails/itemCount,status)");
            playlistSearches.setMaxResults(Config.MAX_ALLOWED_RESULT_COUNT);
            playlistSearches.setId( id );

            final PlaylistListResponse playListResponse = playlistSearches.execute();
            final List<Playlist> playlists = playListResponse.getItems();

            if (playlists != null) {

                Iterator<Playlist> iteratorPlaylistResults = playlists.iterator();
                if (!iteratorPlaylistResults.hasNext()) {
                    Log.d(TAG, " There aren't any results for your query.");
                }

                ArrayList<YouTubePlaylist> youTubePlaylistList = new ArrayList<>();

                while (iteratorPlaylistResults.hasNext()) {
                    Playlist playlist = iteratorPlaylistResults.next();

                    YouTubePlaylist playlistItem = new YouTubePlaylist(playlist.getSnippet().getTitle(),
                            playlist.getSnippet().getThumbnails().getDefault().getUrl(),
                            playlist.getId(),
                            playlist.getContentDetails().getItemCount(),
                            playlist.getStatus().getPrivacyStatus());
                    youTubePlaylistList.add(playlistItem);
                }

                return youTubePlaylistList;
            }
        } catch (IOException e) {
            Log.d(TAG, "loadInBackground: " + e.getMessage());
            cancelLoad();
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public void deliverResult(List<YouTubePlaylist> data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            return;
        }
        super.deliverResult(data);
    }

    @Override
    public void onCanceled(List<YouTubePlaylist> data) {
        super.onCanceled(data);
        Log.d(TAG, "onCanceled: ");
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
            final String id = result.getId().getPlaylistId();
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
