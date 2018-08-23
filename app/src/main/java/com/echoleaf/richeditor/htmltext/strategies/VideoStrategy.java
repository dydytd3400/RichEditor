package com.echoleaf.richeditor.htmltext.strategies;

import android.net.Uri;

public interface VideoStrategy {

    /**
     * Upload the video to server and return the url of the video at server
     *
     * @param uri
     * @return
     */
    String uploadVideo(Uri uri);

    /**
     * Upload the video to server and return the url of the video at server
     *
     * @param videoPath
     * @return
     */
    String uploadVideo(String videoPath);
}
