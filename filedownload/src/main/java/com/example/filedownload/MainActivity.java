package com.example.filedownload;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.example.filedownload.browser.DownloadByBrowser;
import com.example.filedownload.downloadmanager.DownloadBroadcast;
import com.example.filedownload.downloadmanager.SystemDownloadManager;
import static com.example.filedownload.Per.isGrantExternalRW;
import static com.example.filedownload.constant.Constants.IMG_URL;

public class MainActivity extends AppCompatActivity implements SystemDownloadManager.Callback {

    private DownloadBroadcast downloadBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isGrantExternalRW(MainActivity.this);
        handle();
    }

    private void handle() {
        findViewById(R.id.download1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadByBrowser.downloadFileByBrowser(MainActivity.this, IMG_URL);
            }
        });

        findViewById(R.id.download2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemDownloadManager
                        .getInstance()
                        .downloadFileBySysDownloadManager(MainActivity.this, IMG_URL, "test.jpg","image/*");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (downloadBroadcast != null) {
            unregisterReceiver(downloadBroadcast);
        }
    }

    @Override
    public void callback(DownloadBroadcast downloadBroadcast) {
        this.downloadBroadcast = downloadBroadcast;
    }
}
