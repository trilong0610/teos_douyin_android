package com.teont.douyin.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import com.teont.douyin.R;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment implements View.OnClickListener {

    TextInputEditText txt_main_douyin_url;
    TextInputLayout txt_layout_main_douyin_url;
    MaterialButton btn_main_download;
    TextView btn_main_douyin_search;

    View layout_main_container;

    LinearProgressIndicator process_main_get_info, process_main_download;
    MaterialCardView card_main_download;
    MaterialCardView card_main_info;
    MaterialTextView txt_main_id_douyin,
            txt_main_size_douyin,
            txt_main_duration_douyin;

    ShapeableImageView img_main_douyin;
    Uri uriCurrent;
    String videoFileName;
    String url_download;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        layout_main_container = view.findViewById(R.id.layout_main_container);
        txt_main_douyin_url = view.findViewById(R.id.txt_main_douyin_url);
        txt_layout_main_douyin_url = view.findViewById(R.id.txt_layout_main_douyin_url);

        btn_main_download = view.findViewById(R.id.btn_main_download);
        btn_main_douyin_search = view.findViewById(R.id.btn_main_douyin_search);
        process_main_get_info = view.findViewById(R.id.process_main_get_info);
        process_main_get_info.setVisibility(View.INVISIBLE);

        process_main_download = view.findViewById(R.id.process_main_download);

        card_main_info = view.findViewById(R.id.card_main_info);
        card_main_info.setVisibility(View.INVISIBLE);

        card_main_download = view.findViewById(R.id.card_main_download);
        card_main_download.setVisibility(View.INVISIBLE);

        txt_main_id_douyin = view.findViewById(R.id.txt_main_id_douyin);
        txt_main_duration_douyin = view.findViewById(R.id.txt_main_duration_douyin);
        txt_main_size_douyin = view.findViewById(R.id.txt_main_size_douyin);

        img_main_douyin = view.findViewById(R.id.img_main_douyin);

        btn_main_download.setOnClickListener(this::onClick);
        btn_main_douyin_search.setOnClickListener(this::onClick);

        // layout tu co lai khi ban phim hien len
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        return view;
    }

        @Override
        public void onClick (View view){

            if (view == btn_main_douyin_search) {
                process_main_get_info.setVisibility(View.VISIBLE);

                // An ban phim khi bam tim kiem
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout_main_container.getWindowToken(), 0);

                // Kiem tra url dung dinh dang khong
                Pattern pattern = Pattern.compile("http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+");
                List<String> url_videos = new ArrayList<String>();
                Matcher mUrlVideo = pattern.matcher(txt_main_douyin_url.getText());
                while (mUrlVideo.find()) {
                    url_videos.add(mUrlVideo.group());
                }
                // text nhap vap khong co url
                if (url_videos.size() <= 0){
                    txt_layout_main_douyin_url.setError("Không tìm thấy đường dẫn nào!");
                }
                // Tim thay url dung dinh dang
                else{
                    Snackbar.make(layout_main_container,"Đang lấy dữ liệu", BaseTransientBottomBar.LENGTH_SHORT).show();
                    // url la link douyin, lay thong tin
                    txt_layout_main_douyin_url.setError(null);
                    new GetInfoVideoTask(view.getContext()).execute(txt_main_douyin_url.getText().toString());

                }
            }
            if (view == btn_main_download) {
                // Tao task download video da tim thay
                new DownloadTask(this.getContext()).execute(url_download);
            }
        }

        private class GetInfoVideoTask extends AsyncTask<String, Integer, HashMap> {

            private Context context;

            public GetInfoVideoTask(Context context) {
                this.context = context;
            }

            @Override
            protected HashMap doInBackground(String... strings) {
                String id_video = null;
                String url;
                Request request;
                OkHttpClient client;
                HashMap<String, String> data = new HashMap<String, String>();

                publishProgress(10);
                // regex lay url tu link nhap vao
                Pattern pattern = Pattern.compile("http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+");
                List<String> url_videos = new ArrayList<String>();
                Matcher mUrlVideo = pattern.matcher(strings[0]);
                while (mUrlVideo.find()) {
                    url_videos.add(mUrlVideo.group());
                }

                url = url_videos.get(0);

                publishProgress(20);

                // Lay id video
                // Tao client
                client = new OkHttpClient.Builder()
                        .addInterceptor(new Interceptor() {
                            @NotNull
                            @Override
                            public Response intercept(@NotNull Chain chain) throws IOException {
                                Request originalRequest = chain.request();
                                Request requestWithUserAgent = originalRequest.newBuilder()
                                        .header("User-Agent",
                                                "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1")
                                        .build();
                                return chain.proceed(requestWithUserAgent);
                            }
                        })
                        .build();
                // Request den douyin
                request = new Request.Builder()
                        .url(url)
                        .build();

                try {

                    Response response = client.newCall(request).execute();

                    // Kiem tra co phai url video hay khong hay la user
                    if (response.request().url().toString().contains("user"))
                        return null;
                    // Lay id douyin dang so tu url response
                    pattern = Pattern.compile("\\d+");
                    List<String> id_videos = new ArrayList<String>();
                    Matcher mIdVideo = pattern.matcher(response.request().url().toString());
                    while (mIdVideo.find()) {
                        id_videos.add(mIdVideo.group());
                    }
                    id_video = id_videos.get(0);
                    Log.e("id_video", id_video.toString());
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                publishProgress(60);

                // Lay thong tin video tu api
                request = new Request.Builder()
                        .url("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + id_video)
                        .build();

                try {

                    Response response = client.newCall(request).execute();

                    publishProgress(80);

                    // Convert response thanh json
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    Log.e("jsonObject", jsonObject.toString());


                    JSONObject item_list = (JSONObject) jsonObject
                            .getJSONArray("item_list").get(0);
                    Log.e("item_list", item_list.toString());

                    // lay thong tin video tu response json
                    String url_download = item_list.getJSONObject("video").getJSONObject("play_addr")
                            .getJSONArray("url_list").get(0).toString();

                    String duration = item_list.getJSONObject("video").getString("duration");
                    String aweme_id = item_list.getString("aweme_id");
                    String dynamic_cover = item_list.getJSONObject("video")
                            .getJSONObject("dynamic_cover")
                            .getJSONArray("url_list").get(0).toString();

                    url_download = url_download.replace("playwm", "play");
                    url_download = url_download.replace("720p", "1080p");

                    // Lay size file
                    InputStream input = null;
                    OutputStream output = null;
                    HttpURLConnection connection = null;
                    URL _url = new URL(url_download);
                    connection = (HttpURLConnection) _url.openConnection();
                    connection.connect();

                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file


                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    int fileLength = connection.getContentLength();

                    connection.disconnect();

                    data.put("url_download", url_download);
                    data.put("duration", duration);
                    data.put("aweme_id", aweme_id);
                    data.put("dynamic_cover", dynamic_cover);
                    data.put("fileLength", String.valueOf(fileLength));
                    response.close();

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                publishProgress(100);
                Log.e("data", data.toString());
                return data;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {

                process_main_get_info.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(HashMap hashMap) {

                if (hashMap == null){
                    Snackbar.make(layout_main_container, "Vui lòng nhập link video, đây là link user !!!", BaseTransientBottomBar.LENGTH_SHORT).show();
                    process_main_get_info.setProgress(0);
                    return;
                }


                String duration = (String) hashMap.get("duration");
                url_download = (String) hashMap.get("url_download");
                String aweme_id = (String) hashMap.get("aweme_id");

                double duration_s = ((Integer.parseInt(duration) / 1000));
                duration = String.valueOf(duration_s) + "s";

                String dynamic_cover = (String) hashMap.get("dynamic_cover");

                String fileLength = (String) hashMap.get("fileLength");
                double roundOff = (double) Math.round((Integer.parseInt(fileLength) / 1000000.0) * 100) / 100;
                fileLength = String.valueOf(roundOff) + "M";

                //Picasso.get().load(dynamic_cover).into(img_main_douyin);
                Picasso.with(context).load(dynamic_cover).into(img_main_douyin);
                txt_main_id_douyin.setText(aweme_id);
                txt_main_duration_douyin.setText(duration);
                txt_main_size_douyin.setText(fileLength);

                process_main_get_info.setProgress(0);
                process_main_get_info.setVisibility(View.INVISIBLE);
                card_main_info.setVisibility(View.VISIBLE);
                card_main_download.setVisibility(View.VISIBLE);
            }


        }

        private class DownloadTask extends AsyncTask<String, Integer, String> {

            private Context context;
            private PowerManager.WakeLock mWakeLock;

            public DownloadTask(Context context) {
                this.context = context;
            }

            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            protected String doInBackground(String... sUrl) {
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(sUrl[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    videoFileName = System.currentTimeMillis() + ".mp4";

                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return "Server returned HTTP " + connection.getResponseCode()
                                + " " + connection.getResponseMessage();
                    }

                    int fileLength = connection.getContentLength();

                    // download the file
                    input = connection.getInputStream();
//                "/sdcard/Movies/"+ videoFileName
                    ContentValues valuesvideos;
                    valuesvideos = new ContentValues();
                    valuesvideos.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/");
                    valuesvideos.put(MediaStore.Video.Media.TITLE, videoFileName);
                    valuesvideos.put(MediaStore.Video.Media.DISPLAY_NAME, videoFileName);
                    valuesvideos.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                    valuesvideos.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
                    valuesvideos.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
                    // valuesvideos.put(MediaStore.Video.Media.IS_PENDING, 1);
                    uriCurrent = context.getContentResolver().insert(MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), valuesvideos);                //OutputStream outputStream
                    output = context.getContentResolver().openOutputStream(uriCurrent);

                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
                            return null;
                        }
                        total += count;
                        // Cap nhat process....
                        if (fileLength > 0) // only if total length is known
                            publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }
                } catch (Exception e) {
                    return e.toString();
                } finally {
                    try {
                        if (output != null)
                            output.flush();
                        output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                    }

                    if (connection != null)
                        connection.disconnect();
                }


                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // take CPU lock to prevent CPU from going off if the user
                // presses the power button during download
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        getClass().getName());
                mWakeLock.acquire();
                card_main_download.setVisibility(View.VISIBLE);
                process_main_download.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onProgressUpdate(Integer... progress) {
                super.onProgressUpdate(progress);
                // if we get here, length is known, now set indeterminate to false

                process_main_download.setIndeterminate(false);
                process_main_download.setMax(100);
                process_main_download.setProgress(progress[0]);

            }

            @Override
            protected void onPostExecute(String result) {
                mWakeLock.release();

                // An thanh process tai video xuong
                process_main_download.setVisibility(View.INVISIBLE);
                process_main_download.setProgress(0);

                // thong bao ket qua
                if (result != null)
                    Snackbar.make(layout_main_container,"Tải video thất bại: " + result, Snackbar.LENGTH_SHORT).show();
                else
                    Snackbar.make(layout_main_container,"Đã tải video", Snackbar.LENGTH_SHORT).show();
            }
        }


 }
