package com.teont.douyin.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.teont.douyin.BuildConfig;
import com.teont.douyin.R;
import com.teont.douyin.data.DataHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AboutUsFragment extends Fragment implements View.OnClickListener {

    View layout_about_main;
    ImageView contactFb;
    ImageView contactGmail;
    ImageView contactZalo;
    ImageView avt_tac_gia;

    ImageView donate_momo;
    ImageView donate_vpbank;
    ImageView donate_zalopay;

    TextView tvContentVersion;
    TextView tv_about_us_version;

    int count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        contactFb = view.findViewById(R.id.contact_fb);
        contactGmail = view.findViewById(R.id.contact_gmail);
        contactZalo = view.findViewById(R.id.contact_zalo);

        donate_momo = view.findViewById(R.id.donate_momo);
        donate_vpbank = view.findViewById(R.id.donate_vpbank);
        donate_zalopay = view.findViewById(R.id.donate_zalopay);

        layout_about_main = view.findViewById(R.id.layout_about_main);

        avt_tac_gia = view.findViewById(R.id.avt_tac_gia);
        tvContentVersion = view.findViewById(R.id.tv_about_us_content_version);
        tv_about_us_version = view.findViewById(R.id.tv_about_us_version);
        tv_about_us_version.setText("Tèo's Douyin " + BuildConfig.VERSION_NAME);

        contactFb.setOnClickListener(this::onClick);
        contactGmail.setOnClickListener(this::onClick);
        contactZalo.setOnClickListener(this::onClick);
        avt_tac_gia.setOnClickListener(this::onClick);
        donate_momo.setOnClickListener(this::onClick);
        donate_vpbank.setOnClickListener(this::onClick);
        donate_zalopay.setOnClickListener(this::onClick);
        setContentVersion();


        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == contactFb){
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100004596572732"));
            startActivity(i);
        }
        if (view == contactGmail){
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "trilong0610@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));

        }
        if (view == contactZalo){
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://zalo.me/0374234303"));
            startActivity(i);
        }
        if (view == donate_momo){
            // Gets a handle to the clipboard service.
            ClipboardManager clipboard = (ClipboardManager)
                   getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            // Creates a new text clip to put on the clipboard
            ClipData clip = ClipData.newPlainText("momo","0374234303");
                // Set the clipboard's primary clip.
            clipboard.setPrimaryClip(clip);
            Snackbar.make(layout_about_main,"Đã sao chép số Momo", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
        if (view == donate_vpbank){
            // Gets a handle to the clipboard service.
            ClipboardManager clipboard = (ClipboardManager)
                    getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            // Creates a new text clip to put on the clipboard
            ClipData clip = ClipData.newPlainText("momo","669061099");
            // Set the clipboard's primary clip.
            clipboard.setPrimaryClip(clip);
            Snackbar.make(layout_about_main,"Đã sao chép số tài khoản VPBank", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
        if (view == donate_zalopay){
            // Gets a handle to the clipboard service.
            ClipboardManager clipboard = (ClipboardManager)
                    getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            // Creates a new text clip to put on the clipboard
            ClipData clip = ClipData.newPlainText("momo","0374234303");
            // Set the clipboard's primary clip.
            clipboard.setPrimaryClip(clip);
            Snackbar.make(layout_about_main,"Đã sao chép số ZaloPay", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }


    public void setContentVersion(){
        DataHelper.myData.child("Update").child("content").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvContentVersion.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}