package com.example.filedownload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.filedownload.broadcast.DownloadBroadcast;
import com.example.filedownload.service.DownloadService;

import java.io.File;
import java.util.logging.Logger;

import static com.example.filedownload.service.DownloadService.IMG_URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isGrantExternalRW(MainActivity.this);
        handle();
    }

    private void handle(){
        findViewById(R.id.download1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadService.downloadFileByBrowser(MainActivity.this,IMG_URL);
            }
        });

        findViewById(R.id.download2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    DownloadService.downloadFileBySysDownloadManager(MainActivity.this, IMG_URL,"test.jpg");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mDownloadBroadcast != null) {
//            unregisterReceiver(mDownloadBroadcast);
//        }
    }

    static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            return false;
        }
        return true;
    }

}
