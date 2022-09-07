package com.teont.douyin.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.teont.douyin.R;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;
import com.teont.douyin.fragment.UserFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static com.teont.douyin.R.drawable.*;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoItemViewHolder> {
    private Context context;
//    private static ItemClickListener mClickListener;

    public VideoAdapter(ArrayList<ModalVideo> modalVideos, Context c) {
        this.context = c;
    }

    @Override
    public int getItemCount() {
        return UserFragment.modalVideoArrayList.size();
    }

    @Override
    public VideoItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        parent.scrollTo(0,0);
        return new VideoItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VideoItemViewHolder holder, int position) {
        int currentPosition = position;
        ModalVideo modalVideo = UserFragment.modalVideoArrayList.get(position);
        Picasso.with(context)
                .load(modalVideo.dynamic_cover)
                .into(holder.iv_item_avatar);

        holder.txt_item_position.setText(String.valueOf("ID: " + (position + 1)));

        holder.txt_item_duration_douyin.setText("Time: " + (modalVideo.duration / 1000)  + "s");
        holder.chk_item_select.setChecked(modalVideo.isChecked);
//        -------------set username + icon social--------------
        holder.chk_item_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserFragment.modalVideoArrayList.get(currentPosition).isChecked = holder.chk_item_select.isChecked();
                Log.e("chk_item_select", String.valueOf(UserFragment.modalVideoArrayList.get(currentPosition).isChecked));
                UserFragment.adapter.notifyDataSetChanged();
            }
        });
    }


    public static class VideoItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView iv_item_avatar;
        public MaterialCheckBox chk_item_select;
        public MaterialTextView txt_item_duration_douyin;
        public MaterialTextView txt_item_position;

        //         Luu mau Items
        public VideoItemViewHolder(View itemView) {
            super(itemView);
//            tvUrl = (MaterialTextView) itemView.findViewById(R.id.tv_url);
            iv_item_avatar = itemView.findViewById(R.id.iv_item_avatar);
            chk_item_select = itemView.findViewById(R.id.chk_item_select);
            txt_item_duration_douyin = (MaterialTextView) itemView.findViewById(R.id.txt_item_duration_douyin);
            txt_item_position = itemView.findViewById(R.id.txt_item_position);
//            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public static Bitmap loadImageFromStorage(String path, long id) {

        try {
            File f=new File(path, id + ".jpg");
            //            ImageView img=(ImageView)findViewById(R.id.imgPicker);
//            img.setImageBitmap(b);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

//    // allows clicks events to be caught
//    void setClickListener(ItemClickListener itemClickListener) {
//        this.mClickListener = itemClickListener;
//    }
//    // parent activity will implement this method to respond to click events
//    public interface ItemClickListener {
//        void onItemClick(View view, int position);
//    }
}
