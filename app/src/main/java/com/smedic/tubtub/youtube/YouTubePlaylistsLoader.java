package com.smedic.tubtub.youtube;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.smedic.tubtub.model.YouTubePlaylist;
import com.smedic.tubtub.utils.Config;

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

    public YouTubePlaylistsLoader(Context context) {
        super(context);
    }

    @Override
    public List<YouTubePlaylist> loadInBackground() {
        try {
            YouTube.Playlists.List searchList = youtube.playlists().list("id,snippet,contentDetails,status");

            searchList.setId("PL08F79EB0B416E4BE"); // testing only

            searchList.setKey(Config.YOUTUBE_API_KEY);
            searchList.setFields("items(id,snippet/title,snippet/thumbnails/default/url,contentDetails/itemCount,status)");
            searchList.setMaxResults(Config.MAX_ALLOWED_RESULT_COUNT);

            PlaylistListResponse playListResponse = searchList.execute();
            List<Playlist> playlists = playListResponse.getItems();

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
        } catch (UserRecoverableAuthIOException e) {
            Log.d(TAG, "loadInBackground: exception REQUEST_AUTHORIZATION");
            cancelLoad();
            e.printStackTrace();
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
}
