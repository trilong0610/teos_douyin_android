package com.teont.douyin.data;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.teont.douyin.R;
import com.teont.douyin.activity.WelcomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotiNewLinkWork extends Worker {
    final String CHANNEL_ID = "610";

    private   SharedPreferences.Editor editor;

    DatabaseReference myData;

    public NotiNewLinkWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        myData = FirebaseDatabase.getInstance().getReference();
        initPreferences();
        createNotificationChannel();
    }

    @NonNull
    @Override
    public Result doWork() {

        myData.child("TotalProfile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
//                Lay tong so luong link tren server
                int totalProfile = snapshot.getValue(Integer.class);

//                Lay tong so luong link trong local(sharedPreferences)
                int localTotalProfile = WelcomeActivity.getTotalLocalLink();

//              Thong bao neu tong link server > tong link trong local
                if (totalProfile > localTotalProfile){
//                 Hien thi thong bao neu nguoi dung bat chuc nang thong bao
                    if (WelcomeActivity.isStatusSwitch()) {
                        //                   > Android 8.0
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            //                        Gan tong link local = tong link server
                            editor.putInt("localTotalProfile", totalProfile);
                            editor.commit();
                            Notification.Builder builder = new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                                    .setContentTitle("Đã có FB mới")
                                    .setSmallIcon(R.drawable.ic_fb)
                                    .setContentText("Follow ngay kẻo lỡ !")
                                    .setAutoCancel(true);
                            Notification notification = builder.build();
                            notificationManager.notify(1, notification);
                        }
                        //                   < Android 8.0
                        else {
                            //                        Gan tong link local = tong link server
                            editor.putInt("localTotalProfile", totalProfile);
                            editor.commit();
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                    .setContentTitle("Đã có FB mới")
                                    .setContentText("Follow ngay kẻo lỡ !")
                                    .setSmallIcon(R.drawable.ic_fb)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setAutoCancel(true);
                            Notification notification = builder.build();
                            notificationManager.notify(1, notification);
                        }

                        //                    Rung thong bao
                        if (Build.VERSION.SDK_INT >= 26) {
                            ((Vibrator) getApplicationContext().getSystemService(getApplicationContext().VIBRATOR_SERVICE)).vibrate(VibrationEffect.createWaveform(new long[]{0, 300, 150, 300}, -1));
                        } else {
                            ((Vibrator) getApplicationContext().getSystemService(getApplicationContext().VIBRATOR_SERVICE)).vibrate(new long[]{0, 300, 150, 300}, -1);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return Result.success();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getApplicationContext().getString(R.string.app_name);
            String description = getApplicationContext().getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initPreferences() {

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserSetting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
}
