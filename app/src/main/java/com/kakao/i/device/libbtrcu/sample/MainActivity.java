package com.kakao.i.device.libbtrcu.sample;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.kakao.i.device.libbtrcu.CMDRecv;
import com.kakao.i.device.libbtrcu.NativeHIDRaw;
import com.kakao.i.device.libbtrcu.PcmDataRecv;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "BTRCU_JNI";
    private Thread thr_PcmDataRecv;
    private Thread thr_CMDRecv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, 12345678);

        thr_PcmDataRecv = new Thread(new PcmDataRecv());
        thr_PcmDataRecv.start();

        thr_CMDRecv = new Thread(new CMDRecv());
        thr_CMDRecv.start();

        Button btn_recStart = (Button) findViewById(R.id.btn_start);
        btn_recStart.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.e(TAG, "btn_recStart ");
                NativeHIDRaw.getInstance().recStart();
            }
        });

        Button btn_recStop = (Button) findViewById(R.id.btn_stop);
        btn_recStop.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.e(TAG, "btn_recStop ");
                NativeHIDRaw.getInstance().recStop();
            }
        });

        Button btn_isConnected = (Button) findViewById(R.id.btn_check);
        btn_isConnected.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.e(TAG, "btn_isConnected ");
                boolean ret = false;
                ret = NativeHIDRaw.getInstance().isConnected();
                Log.e(TAG, "" + (ret ? "Remote Controller is connected." : "Remote Controller is NOT connected."));
            }
        });


    }
}
