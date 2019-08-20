package com.example.filedownload.broadcast;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.example.filedownload.BuildConfig;

import java.io.File;

public  class DownloadBroadcast extends BroadcastReceiver {

    private final File mFile;

    public DownloadBroadcast(File file) {
        mFile = file;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("action:" , action);
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            Intent intent1 = new Intent(Intent.ACTION_VIEW);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri uri1 = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", mFile);
//                    intent1.setDataAndType(uri1, "application/vnd.android.package-archive");
                intent1.setDataAndType(uri1, "*/*");
            } else {
//                intent1.setDataAndType(Uri.fromFile(mFile), "application/vnd.android.package-archive");
                intent1.setDataAndType(Uri.fromFile(mFile), "*/*");
            }
            Log.d("mFile:" , mFile.getAbsolutePath());
            try {
                context.startActivity(intent1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}