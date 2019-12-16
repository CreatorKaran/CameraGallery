package com.example.cameragallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PhotoActivity extends AppCompatActivity {

    private Context mContext;
    private TextView mTextViewInfo;
    private TextView mTextViewPercentage;
    private ProgressBar mProgressBar;
    private int mProgressStatus = 0;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);

            mTextViewInfo.setText("Battery Scale : " + scale);

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
            aleartLevel(level,isCharging);
            mTextViewInfo.setText(mTextViewInfo.getText() + "\nBattery Level : " + level);
            float percentage = level/ (float) scale;
            mProgressStatus = (int) ((percentage)*100);
            mTextViewPercentage.setText("" + mProgressStatus + "%");
            mTextViewInfo.setText(mTextViewInfo.getText() +
                    "\nPercentage : "+ mProgressStatus + "%");
            mProgressBar.setProgress(mProgressStatus);
        }
    };

    MediaPlayer mediaPlayer;
    private void aleartLevel(int level, boolean isCharging) {


        if(level >= 88 && isCharging==true){
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), notification);

        }
    }

    private RelativeLayout rlMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        rlMain = findViewById(R.id.rlMain);
        mContext = getApplicationContext();
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(mBroadcastReceiver,iFilter);
        mTextViewInfo = findViewById(R.id.tv_info);
        mTextViewPercentage = findViewById(R.id.tv_percentage);
        mProgressBar =findViewById(R.id.pb);

        rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
            }
        });
    }


}
