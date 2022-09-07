package com.teont.douyin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.teont.douyin.R;
import com.teont.douyin.data.NotiNewLinkWork;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class WelcomeActivity extends AppCompatActivity {

    Animation topAnim, bottomAnim;
    ImageView ivLogo;
    MaterialTextView tvLogo,tvAuthor;


    PackageManager manager;
    PackageInfo info;

    String urlUpdate;

    public static int totalProfileDB = 0;

    public static PeriodicWorkRequest checkTotalProfileRequest;

    public static WorkManager workManager;

    public static SharedPreferences sharedPreferences;

    public static SharedPreferences.Editor editor;

    public static boolean statusSwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

//        Dinh nghia cac view
        AnhXa();

        initPreferences();

//        Delay + hien thi hieu ung truoc khi mo HomeActiv
        int SPLASH_SCREEN = 1800;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

//                Kiem tra neu chua co internet thi chuyen den man hinh yeu cau bat internet
//                Neu da co internet thi chuyen den home
                if (checkInternet()){
                    Intent gotoHome = new Intent(WelcomeActivity.this, HomeActivity.class);
                    startActivity(gotoHome);
                    finish();
                }
                else {
                    Intent intent = new Intent(WelcomeActivity.this, NoInternetActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, SPLASH_SCREEN);
    }

    private void AnhXa(){
        topAnim = AnimationUtils.loadAnimation(this,R.anim.welcome_top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.welcome_bottom_animation);

        ivLogo = findViewById(R.id.iv_logo);
        tvLogo = findViewById(R.id.tv_logo);
        tvAuthor = findViewById(R.id.tv_author);

        ivLogo.setAnimation(topAnim);
        tvLogo.setAnimation(bottomAnim);
        tvAuthor.setAnimation(bottomAnim);

        DatabaseReference myData = FirebaseDatabase.getInstance().getReference();

        manager = this.getPackageManager();
        try {
            info = manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionCode = info.versionCode;

        //        Lap lai work trong 1 tiếng
        checkTotalProfileRequest = new PeriodicWorkRequest.Builder(NotiNewLinkWork.class, 1, TimeUnit.HOURS).build();

        workManager = WorkManager.getInstance(getApplicationContext());
    }

    private boolean checkInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())){
            return true;
        }
        else
            return false;
    }

    private void initPreferences() {

        sharedPreferences = getSharedPreferences("UserSetting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        statusSwitch = WelcomeActivity.sharedPreferences.getBoolean("addNewLink", false);

    }

    private void runWorkManger(){

        workManager.enqueue(checkTotalProfileRequest);

        if (!statusSwitch){
            workManager.cancelAllWork();
        }

    }
    public static int getTotalLocalLink(){
        return sharedPreferences.getInt("localTotalProfile",0);
    };

    public static boolean isStatusSwitch() {
        return statusSwitch;
    }

}