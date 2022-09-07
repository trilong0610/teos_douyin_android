package com.teont.douyin.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.teont.douyin.R;
import com.google.android.material.button.MaterialButton;

public class NoInternetActivity extends AppCompatActivity {

    MaterialButton exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        exit = findViewById(R.id.btn_exit_no_internet);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}