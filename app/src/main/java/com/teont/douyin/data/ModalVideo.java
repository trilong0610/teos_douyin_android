package com.teont.douyin.data;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ModalVideo {
    public String aweme_id;
    public String url_download;
    public String dynamic_cover;
    public double duration;
    public int file_lenght;
    public boolean isChecked;

    public ModalVideo() {
    }

    public ModalVideo(String aweme_id, String url_download, String dynamic_cover, double duration, int file_lenght, boolean isChecked) {
        this.aweme_id = aweme_id;
        this.url_download = url_download;
        this.dynamic_cover = dynamic_cover;
        this.duration = duration;
        this.file_lenght = file_lenght;
        this.isChecked = isChecked;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("aweme_id", aweme_id);
        result.put("url_download", url_download);
        result.put("dynamic_cover", dynamic_cover);
        result.put("duration", duration);
        result.put("file_lenght", file_lenght);
        result.put("isChecked", isChecked);
        return result;
    }
}
