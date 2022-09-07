package com.teont.douyin.activity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.teont.douyin.R;
import com.teont.douyin.data.DataHelper;
import com.teont.douyin.fragment.AboutUsFragment;
import com.teont.douyin.fragment.UserFragment;
import com.teont.douyin.fragment.HomeFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomeActivity extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;


    public static int methodLogin;// Giữ phương thức đăng nhập: Email = 0, Google = 1
    private DatabaseReference myData;

    PackageManager manager;
    PackageInfo info;
    private int versionCode;

    String urlUpdate = null;
    MaterialTextView txt_home_title;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        AnhXa();

        setEventFirebase();

        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main,new HomeFragment()).commit();

        bottomMenu();

        //Snackbar.make(mainContainer,"Đang nhận dữ liệu...", Snackbar.LENGTH_SHORT).show();
        if(!checkPermission()){
            requestPermission();
        };

    }

    private void AnhXa(){
        myData = FirebaseDatabase.getInstance().getReference();
        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        chipNavigationBar.setItemSelected(R.id.mnu_item_douyin,true);
        txt_home_title =  findViewById(R.id.txt_home_title);
        View mainContainer = findViewById(R.id.main_container);
        manager = this.getPackageManager();
        try {
            info = manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionCode = info.versionCode;
    }

    private void setEventFirebase(){
        //    ----------Kiểm tra bảo trì của app----------------
        myData.child("MaintenanceApp").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().getValue(Boolean.class)) {
                    Intent intent = new Intent(HomeActivity.this, MaintenanceActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


        //    ----------Kiểm tra update của app----------------
        myData.child("Update").child("Version").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                int versionFirebase = task.getResult().getValue(Integer.class);
                if (versionFirebase > versionCode){
                    // lay url apk update
                    myData.child("Update").child("UrlUpdate").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.d("TAG_URL_APK_0", "That bai");
                                Toast.makeText(HomeActivity.this, "Lấy đường dẫn cập nhật thất bại!", Toast.LENGTH_SHORT).show();;
                            }
                            else {
                                Log.d("TAG_URL_APK_0", String.valueOf(task.getResult().getValue()));
                                urlUpdate = String.valueOf(task.getResult().getValue(String.class));
                                Log.d("TAG_URL_APK_0", urlUpdate);
                                // chuyen den man hinh cap nhat
                                Intent intent = new Intent(HomeActivity.this, UpdateActivity.class);
                                intent.putExtra("urlUpdate",urlUpdate);

                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                }
            }
        });



    }

    private void bottomMenu() {


        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.mnu_item_douyin:
                        fragment = new HomeFragment();
                        break;
                    case R.id.mnu_item_user:
                        fragment = new UserFragment();
                        break;
                    case R.id.mnu_item_about_us:
                        fragment = new AboutUsFragment();
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.activity_main,fragment).commit();
            }
        });


    }

    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(HomeActivity.this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(HomeActivity.this, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, 2296);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                    Toast.makeText(this, "Đã cấp quyền sử dụng bộ nhớ!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Vui lòng cấp quyền sử dụng bộ nhớ!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}

