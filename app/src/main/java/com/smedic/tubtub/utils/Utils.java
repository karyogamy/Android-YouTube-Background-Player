package com.smedic.tubtub.utils;

import android.support.annotation.Nullable;
import android.util.Log;

import com.smedic.tubtub.model.YouTubeVideo;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Helper methods
 * Created by smedic on 4.2.16..
 */
public class Utils {

    private static final String TAG = "SMEDIC JSON";

    /**
     * Converting ISO8601 formatted duration to normal readable time
     */
    public static String convertDuration(String isoTime) {
        final String COLON = ":";
        final Period duration = Period.parse( isoTime );
        final PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays().appendSeparator(COLON)
                .appendHours().minimumPrintedDigits(2).appendSeparator(COLON)
                .appendMinutes().minimumPrintedDigits(2).appendSeparator(COLON)
                .appendSeconds().minimumPrintedDigits(2).toFormatter();

        return formatter.print( duration.normalizedStandard() );
    }

    /**
     * Prints videos nicely formatted
     * @param videos
     */
    public static void prettyPrintVideos(List<YouTubeVideo> videos) {
        Log.d(TAG, "=============================================================");
        Log.d(TAG, "\t\tTotal Videos: " + videos.size());
        Log.d(TAG, "=============================================================\n");

        Iterator<YouTubeVideo> playlistEntries = videos.iterator();

        while (playlistEntries.hasNext()) {
            YouTubeVideo playlistItem = playlistEntries.next();
            Log.d(TAG, " video name  = " + playlistItem.getTitle());
            Log.d(TAG, " video id    = " + playlistItem.getId());
            Log.d(TAG, " duration    = " + playlistItem.getDuration());
            Log.d(TAG, " thumbnail   = " + playlistItem.getThumbnailURL());
            Log.d(TAG, "\n-------------------------------------------------------------\n");
        }
    }

    /**
     * Prints video nicely formatted
     * @param playlistEntry
     */
    public static void prettyPrintVideoItem(YouTubeVideo playlistEntry) {
        Log.d(TAG, "*************************************************************");
        Log.d(TAG, "\t\tItem:");
        Log.d(TAG, "*************************************************************");

        Log.d(TAG, " video name  = " + playlistEntry.getTitle());
        Log.d(TAG, " video id    = " + playlistEntry.getId());
        Log.d(TAG, " duration    = " + playlistEntry.getDuration());
        Log.d(TAG, " thumbnail   = " + playlistEntry.getThumbnailURL());
        Log.d(TAG, "\n*************************************************************\n");
    }

    /**
     * Extracts id from youtube share intent url
     *
     * @param ytUrl youtube url containing the id, see regex101.com/r/5BtZT1/1
     * @return the id for that url
     * */
    @Nullable
    public static String extractId(String ytUrl) {
        if (ytUrl == null) return null;
        String vId = null;

        final Pattern pattern = Pattern.compile(
                "^.*(youtu.be\\/|list=)([^#\\&\\?]*).*",
                Pattern.CASE_INSENSITIVE
        );
        final Matcher matcher = pattern.matcher(ytUrl);

        if ( matcher.matches() ) vId = matcher.group(2);

        return vId;
    }
}
