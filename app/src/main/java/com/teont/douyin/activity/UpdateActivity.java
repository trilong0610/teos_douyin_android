package com.teont.douyin.activity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.teont.douyin.R;
import com.teont.douyin.data.DataHelper;
import com.google.android.material.button.MaterialButton;

public class UpdateActivity extends AppCompatActivity {

    MaterialButton btnExitUpdate;
    MaterialButton btnUpdateUpdate;
    Button btnUpdateCoppyUrl;
    ImageView iv_logo_update;
    int count = 0;
    // declare the dialog as a member field of your activity
    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        btnExitUpdate = findViewById(R.id.btn_exit_update);
        btnUpdateCoppyUrl = findViewById(R.id.btn_update_coppy_url);
        btnUpdateUpdate = findViewById(R.id.btn_update_update);
        iv_logo_update = findViewById(R.id.iv_logo_update);

        mProgressDialog = new ProgressDialog(UpdateActivity.this);

        if (!checkPermission()){
            requestPermission();
        }


        btnExitUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnUpdateUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                String urlUpdate = intent.getStringExtra("urlUpdate");
//
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(urlUpdate));
//                startActivity(intent);
                DataHelper dataHelper = new DataHelper();

                if(!urlUpdate.isEmpty() && urlUpdate != null)
                    dataHelper.downloadApkUpdate(UpdateActivity.this,urlUpdate.replace("\"",""));



            }
        });

        btnUpdateCoppyUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Gets a handle to the clipboard service.
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                // Creates a new text clip to put on the clipboard
                Intent intent = getIntent();
                String urlUpdate = intent.getStringExtra("urlUpdate");
                Log.d("TAG_URL_APK_1", urlUpdate);
                if(!urlUpdate.isEmpty() && urlUpdate != null){
                    ClipData clip = ClipData.newPlainText("urlUpdate",urlUpdate.replace("\"",""));
                    // Set the clipboard's primary clip.
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(UpdateActivity.this,"Đã sao chép liên kết",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(UpdateActivity.this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(UpdateActivity.this, WRITE_EXTERNAL_STORAGE);
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
            ActivityCompat.requestPermissions(UpdateActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, 2296);
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

