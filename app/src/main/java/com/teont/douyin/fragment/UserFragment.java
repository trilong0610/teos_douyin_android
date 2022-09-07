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
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.teont.douyin.R;
import com.teont.douyin.data.AsyncResponse;
import com.teont.douyin.data.ModalVideo;
import com.google.android.material.textview.MaterialTextView;
import com.teont.douyin.data.VideoAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserFragment extends Fragment implements View.OnClickListener {

    public TextView btn_user_douyin_search;

    public static Context contextHomeFragment;

    public LinearProgressIndicator process_user_download_item;

    public LinearProgressIndicator process_user_get_info;

    public static ArrayList<ModalVideo> modalVideoArrayList;

    public TextInputLayout txt_layout_user_douyin_url;

    public RecyclerView rv_user_videos;

    public View layout_user_main;

    public MaterialCheckBox chk_user_select_all;

    public MaterialTextView txt_user_video_downloaded;
    public int countVideoDownloaded = 0;
    public static VideoAdapter adapter;

    ArrayList<ModalVideo> videoSelected;

    private MaterialButton btn_user_download;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        modalVideoArrayList = new ArrayList<>();
        videoSelected = new ArrayList<>();
        // Khởi tạo RecyclerView.
        layout_user_main = view.findViewById(R.id.layout_user_main);
        rv_user_videos = (RecyclerView) view.findViewById(R.id.rv_user_videos);

        chk_user_select_all = view.findViewById(R.id.chk_user_select_all);

        process_user_download_item = view.findViewById(R.id.process_user_download_item);
        process_user_get_info = view.findViewById(R.id.process_user_get_info);

        txt_user_video_downloaded = view.findViewById(R.id.txt_user_video_downloaded);

        btn_user_download  = view.findViewById(R.id.btn_user_download);
//       Gan view
        btn_user_douyin_search = view.findViewById(R.id.btn_user_douyin_search);
        //tvNameUserHome.setText("Hi, " + mAuth.getCurrentUser().getDisplayName());
        txt_layout_user_douyin_url = view.findViewById(R.id.txt_layout_user_douyin_url);

//        tvUrl = (MaterialTextView) findViewById(R.id.tv_url);

//        Set layout de hien thi thong tin trong recycle view
        RecyclerView.LayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
//        Đảo ngược chiều thêm item từ dưới lên
       // linearLayoutManager.setReverseLayout(true);

//        linearLayoutManager.setStackFromEnd(true);
        //linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
//        rvUsers.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rv_user_videos.setLayoutManager(linearLayoutManager);

        adapter = new VideoAdapter(modalVideoArrayList, getContext());

        rv_user_videos.setAdapter(adapter);

        rv_user_videos.scrollToPosition(modalVideoArrayList.size() - 1);

        btn_user_douyin_search.setOnClickListener(this::onClick);
        btn_user_download.setOnClickListener(this::onClick);
        chk_user_select_all.setOnClickListener(this::onClick);
        return view;
    }


    @Override
    public void onClick(View view) {

        if (view == btn_user_douyin_search){
            // An ban phim
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(layout_user_main.getWindowToken(), 0);
            // Kiem tra url dung dinh dang khong
            Pattern pattern = Pattern.compile("http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+");
            List<String> url_videos = new ArrayList<String>();
            Matcher mUrlVideo = pattern.matcher(txt_layout_user_douyin_url.getEditText().getText());
            while (mUrlVideo.find()) {
                url_videos.add(mUrlVideo.group());
            }

            if (url_videos.size() <= 0){ // text khong co url
                txt_layout_user_douyin_url.setError("Không tìm thấy đường dẫn nào!");
            }
            else{

                chk_user_select_all.setChecked(false);
                txt_layout_user_douyin_url.setError(null);
                // text co link douyin, lay thong tin
                modalVideoArrayList = new ArrayList<>();
                Snackbar.make(layout_user_main,"Đang lấy dữ liệu", BaseTransientBottomBar.LENGTH_SHORT).show();
                new GetAllVideoUserTask(view.getContext()).execute(txt_layout_user_douyin_url.getEditText().getText().toString());
                adapter.notifyDataSetChanged();

                // Neu co video thi moi hien nut tai
                btn_user_download.setVisibility(View.VISIBLE);

            }

        }

        if (view == btn_user_download){
            // An button tim kiem va tai xuong
            btn_user_download.setVisibility(View.INVISIBLE);
            btn_user_douyin_search.setVisibility(View.INVISIBLE);

            // Lay nhung video duoc chon
            videoSelected = new ArrayList<>();
            for (int i = 0; i < modalVideoArrayList.size(); i++) {
                if (modalVideoArrayList.get(i).isChecked){
                    videoSelected.add(modalVideoArrayList.get(i));
                }
            }
            if (videoSelected.size() > 0) {
                Snackbar.make(layout_user_main,"Đang tải video, vui lòng đợi!", BaseTransientBottomBar.LENGTH_SHORT).show();
                // Download video
                    //Log.e("Download",modalVideoArrayList.get(i).url_download);
                txt_user_video_downloaded.setText(String.format("%s/%s","0",videoSelected.size()));
                for (int i = 0; i < videoSelected.size(); i++) {
                    new DownloadTask(this.getContext()).execute(videoSelected.get(i).url_download);
                }

            }
            else {
                btn_user_download.setVisibility(View.VISIBLE);
                btn_user_douyin_search.setVisibility(View.VISIBLE);
                Snackbar.make(layout_user_main,"Vui lòng chọn video", BaseTransientBottomBar.LENGTH_SHORT).show();

            }

        }

        if (view == chk_user_select_all){
            for (int i = 0; i < modalVideoArrayList.size(); i++) {
                modalVideoArrayList.get(i).isChecked = chk_user_select_all.isChecked();
            }
            adapter.notifyDataSetChanged();
        }
    }

    // Cap nhat video da tai

    AsyncResponse asyncResponse = new AsyncResponse() {
        @Override
        public void processDownloadFinish(String url) {
            Log.e("Downloaded", url);
            countVideoDownloaded++;
            txt_user_video_downloaded.setText(String.format("%s/%s",countVideoDownloaded,videoSelected.size()));
            //process_user_download_total.setProgress(countVideoDownloaded * (100 / videoSelected.size()));
            if (countVideoDownloaded == videoSelected.size())
            {
                countVideoDownloaded = 0;
                //process_user_download_total.setProgress(0);
                txt_user_video_downloaded.setText("");
                Snackbar.make(layout_user_main,"Tải xuống hoàn tất!", BaseTransientBottomBar.LENGTH_SHORT).show();
                btn_user_download.setVisibility(View.VISIBLE);
                btn_user_douyin_search.setVisibility(View.VISIBLE);
            }

        }
    };

    private class GetAllVideoUserTask extends AsyncTask<String, Integer, ArrayList<ModalVideo>> {

        private ArrayList<ModalVideo> modalVideos = new ArrayList<>();
        public GetAllVideoUserTask(Context context) {
        }

        @Override
        protected ArrayList<ModalVideo> doInBackground(String... strings) {
            String url;
            String uid_user = null;
            Request request;
            String max_cursor = "0";
            OkHttpClient client;
            publishProgress(1);
            // regex lay url tu link share
            Pattern pattern = Pattern.compile("http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+");
            List<String> url_videos = new ArrayList<String>();
            Matcher mUrlVideo = pattern.matcher(strings[0]);
            while (mUrlVideo.find()) {
                url_videos.add(mUrlVideo.group());
            }

            url = url_videos.get(0);
            Log.e("url", url);
            Log.e("url_pc", url.toString());
            publishProgress(10);
            // truy cap url de lay uid user
            client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @NotNull
                        @Override
                        public Response intercept(@NotNull Chain chain) throws IOException {
                            Request originalRequest = chain.request();

                            Request requestWithUserAgent = originalRequest.newBuilder()
                                    .header("User-Agent",
                                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                                    .build();
                            return chain.proceed(requestWithUserAgent);
                        }
                    })
                    .build();

            request = new Request.Builder()
                    .url(url)
                    .build();

            // lay uid user
            try {
                Response response = client.newCall(request).execute();
                publishProgress(20);
                // Kiem tra url nhap vao la user hay video
                if (response.request().url().toString().contains("video"))
                    return null;

                String[] res_url = response.request().url().toString().split("/");
                uid_user = res_url[res_url.length - 1];
                uid_user = uid_user.split("\\?")[0];
                Log.e("url", uid_user);
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            publishProgress(50);
            int process = 0;
            while(true) {
                // Lay danh sach video cua user
                String api = String.format("https://www.iesdouyin.com/web/api/v2/aweme/post/?sec_uid=%s&count=40&max_cursor=%s",
                        uid_user,String.valueOf(max_cursor));
                Log.e("data1", String.valueOf(api));
                request = new Request.Builder()
                        .url(api)
                        .build();

                try {

                    Response response = client.newCall(request).execute();

                    //publishProgress(80);

                    // Convert response thanh json
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    //Log.e("data1", String.valueOf(jsonObject));
                    // Lay danh sach url


                    JSONArray aweme_list = jsonObject
                            .getJSONArray("aweme_list");
                    Log.e("TAG1", String.valueOf(aweme_list.length()));
                    // lay thong tin video tu response json
                    for (int i = 0; i < aweme_list.length(); i++) {
                       JSONObject json = (JSONObject) aweme_list.get(i);
                       String aweme_id = json.getString("aweme_id");

                       String vidID = json.getJSONObject("video").getString("vid");
                       if (vidID.isEmpty())
                           continue;

                       String url_download = String.format("https://aweme.snssdk.com/aweme/v1/play/?video_id=%s&ratio=1080p&line=0",
                               vidID);
                       String dynamic_cover = json.getJSONObject("video").
                               getJSONObject("cover").
                               getJSONArray("url_list").get(0).toString();
                       double duration = json.getJSONObject("video").getDouble("duration");
                       //int file_lenght = getSizeFromUrl(url_download);
                        int file_lenght = 0;
                       boolean isChecked = false;

                       modalVideos.add(new ModalVideo(aweme_id,url_download,dynamic_cover,duration,file_lenght,isChecked));
                        publishProgress(process);
                        process++;
                    }
                    // Kiem tra neu het video thi thoat vong lap
                    Log.e("has_more", String.valueOf(jsonObject.getBoolean("has_more")));
                    Log.e("max_cursor", String.valueOf(jsonObject.getString("max_cursor")));
                    if (jsonObject.getBoolean("has_more"))
                    {

                        max_cursor = jsonObject.getString("max_cursor");

                    }
                    else
                        break;

                }
                catch (IOException | JSONException e) {
                    e.printStackTrace();
                    break;
                }
            }
            publishProgress(80);
            publishProgress(100);
            return modalVideos;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            process_user_get_info.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<ModalVideo> modalVideos) {
            if (modalVideos == null){
                Snackbar.make(layout_user_main,"Vui lòng nhập link user, đây là link video !!!", BaseTransientBottomBar.LENGTH_SHORT).show();
                btn_user_download.setVisibility(View.VISIBLE);
                btn_user_douyin_search.setVisibility(View.VISIBLE);
                process_user_get_info.setProgress(0);
                return;
            }
            modalVideoArrayList.clear();
            modalVideoArrayList.addAll(modalVideos);
            adapter.notifyDataSetChanged();
            process_user_get_info.setProgress(0);
            Snackbar.make(layout_user_main,"Tìm thấy " + modalVideoArrayList.size() + " video", BaseTransientBottomBar.LENGTH_SHORT).show();
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
            URL url = null;
            try {
                url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                String idVideo = url.toString().split("=")[1];

                String videoFileName = System.currentTimeMillis() + ".mp4";
                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
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
                Uri uriCurrent = context.getContentResolver().insert(MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), valuesvideos);                //OutputStream outputStream
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
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                Snackbar.make(layout_user_main,"Đã xảy ra lỗi khi tải xuống!",BaseTransientBottomBar.LENGTH_SHORT).show();
                btn_user_download.setVisibility(View.VISIBLE);
                btn_user_douyin_search.setVisibility(View.VISIBLE);
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
            return url.toString();
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
            process_user_download_item.setVisibility(View.VISIBLE);
            process_user_download_item.setProgress(0);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            process_user_download_item.setProgress(progress[0]);

        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            // An thanh process tai video xuong
            process_user_download_item.setProgress(0);
            asyncResponse.processDownloadFinish(result);

        }
    }
}
