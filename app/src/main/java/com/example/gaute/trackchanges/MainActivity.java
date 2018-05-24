package com.example.gaute.trackchanges;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.security.Permission;

public class MainActivity extends AppCompatActivity {

    private Button buttonCallLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonCallLog = findViewById(R.id.button_calllog);
    }

    @Override
    protected void onStart() {
        super.onStart();

        buttonCallLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CallLogTracker callLogTracker = new CallLogTracker();
                        System.out.println("click happened!");
                        callLogTracker.start();
                        while (true) {
                            try {
                                callLogTracker.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                //delete call logs 1000 times
                CallLogDeleter callLogDeleter = new CallLogDeleter();
                int count = 0;
                callLogDeleter.start();

                while (count < 1000) {
                    try {
                        callLogDeleter.sleep(1000);
                        count++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class CallLogDeleter extends Thread {
        @Override
        public void run() {
            super.run();
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{"android.permission.WRITE_CALL_LOG"}, 1);
            }
            getContentResolver().delete(CallLog.Calls.CONTENT_URI, null, null);;
        }
    }

    private class CallLogTracker extends Thread {
        @Override
        public void run() {
            super.run();
            MyContentObserver myContentObserver = new MyContentObserver(null);
            System.out.println("content observer registered!");
            getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, myContentObserver);
        }
    }

    class MyContentObserver extends ContentObserver {
        MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            System.out.println("Call logs changed!");
        }
    }
}
