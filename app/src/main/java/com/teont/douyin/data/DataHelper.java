package com.teont.douyin.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.text.format.Formatter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.teont.douyin.BuildConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.WIFI_SERVICE;

public  class DataHelper {

    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    String dateTimeNow = df.format(c.getTime());
    String pathUpdate;
    File localFile = null;

    public static String  uidAdmin;

    public static ArrayList<ModalVideo> modalVideos = new ArrayList<>();

    public static int totalProfile = 0;

    public DataHelper() {


    }

    public DataHelper(PeriodicWorkRequest checkTotalProfileRequest, WorkManager workManager) {
        this.checkTotalProfileRequest = checkTotalProfileRequest;
        this.workManager = workManager;
    }

    PeriodicWorkRequest checkTotalProfileRequest;
    WorkManager workManager;

    //  Firebase
    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference storageRef = storage.getReference();
//    Firebase Database
    public static DatabaseReference myData  = FirebaseDatabase.getInstance().getReference();
    //    FirebaseAuth mAuth;
    public static ArrayList<ModalVideo> profileSearch = new ArrayList<>();

    public String getIpMobile(){
        try {
            for (java.util.Enumeration<java.net.NetworkInterface> en = java.net.NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                java.net.NetworkInterface networkinterface = en.nextElement();
                for (java.util.Enumeration<java.net.InetAddress> enumIpAddr = networkinterface.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    java.net.InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }

    public String getIpWifi(Context context) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
    }

    public void downloadApkUpdate(Context context, String uri){

        ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Đang tải bản update, vui lòng đợi\nChưa có chữ thất bại là vẫn đang tải đấy");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mProgressDialog.show();
        // Create a reference to a file from a Google Cloud Storage URI
        StorageReference islandRef = storage.getReferenceFromUrl(uri);
        // Create a reference to a file from a Google Cloud Storage URI

//        try {
//            localFile = File.createTempFile("TeosUpdate", ".apk");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ContextWrapper contextWrapper = new ContextWrapper(context.getApplicationContext());
//        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        localFile = new File(Environment.getExternalStorageDirectory() + "/Download/" + "TeosUpdate.apk");
//        localFile =  new File(directory,"TeosUpdate.apk");

//        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/apkUpdate";
        if (localFile.exists()){
            localFile.delete();
        }



        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                mProgressDialog.dismiss();
                Toast.makeText(context,"Tải thành công",Toast.LENGTH_SHORT).show();
                android.util.Log.i("pathDownload", localFile.getAbsolutePath());
                pathUpdate = localFile.getAbsolutePath();
                installApkUpdate(context,localFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                mProgressDialog.dismiss();
                Toast.makeText(context,"Tải thất bại",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void installApkUpdate(Context context, File file) {
        Toast.makeText(context, "Bắt đầu cài đặt", Toast.LENGTH_SHORT).show();
        if (pathUpdate != null) {
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(file.getAbsolutePath()));
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }
    boolean checkInstalled(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed ;
    }

//    public static void updateFbProfile(Context context, String id,String link, String imgUrl, String author, String key) {
//        // Create new post at /user-posts/$userid/$postid and at
//        // /posts/$postid simultaneously
////        String key = DataHelper.myData.child("ModalVideo").push().getKey();
//
//        ModalVideo user = new ModalVideo(id,link,imgUrl,author, key);
//
//        Map<String, Object> postValues = user.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
//
//        childUpdates.put("/ProfileFb/" +key, postValues);
//
//        DataHelper.myData.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                if(error == null)
//                    Toast.makeText(context,"Thay đổi thông tin thành công",Toast.LENGTH_SHORT).show();
//                else
//                    Toast.makeText(context,"Thay đổi thông tin thất bại !",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    public static void deleteFbProfile(Context context, String key){

        DataHelper.myData.child("ProfileFb").child(key).setValue(null, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error != null){
                    Toast.makeText(context,"Xóa thành công",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(context,"Xóa thất bại !",Toast.LENGTH_SHORT).show();
            }
        });

    }

//    public static void updateIdFbProfile(Context context, int position){
//        for (int index = position; index < DataHelper.modalVideos.size(); index++) {
//            ModalVideo fbProfile = modalVideos.get(position);
//            DataHelper.updateFbProfile(context, String.valueOf(index + 1),fbProfile.link,fbProfile.imageUrl,fbProfile.author,fbProfile.keyNote);
//        }
//    }

    public static int getSocialFromUrl(String url){
//       Kiem tra url la cua social nao
//       Sau do gan url + icon cua social do
//        -------Facebook-------
        if (url.contains("facebook.com/")){
            return 1;
        }
        else {
            if (url.contains("instagram.com/"))
                return 2;
            else
                return 3;
        }

    }

    public static String getUserNameFromUrl(int code, String url){
        switch (code){
//            -------FB---------
            case 1:
                if (url.contains("id="))
                    return url.split("id=", 2)[1];

                else
                    return url.split("facebook.com/", 2)[1];

//            -------IG---------
            case 2:
                return url.split("instagram.com/",2)[1];

            default:
                return url;
        }
    }

}
