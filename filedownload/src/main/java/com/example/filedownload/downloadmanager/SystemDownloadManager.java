package com.example.filedownload.downloadmanager;


import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Objects;

/**
 * 调用系统下载器实现下载功能。
 *
 * @author xzy
 */
@SuppressWarnings("unused")
public class SystemDownloadManager {
    private static final String TAG = "SystemDownloadManager";
    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private static SystemDownloadManager systemDownloadManager = new SystemDownloadManager();

    public static SystemDownloadManager getInstance() {
        return systemDownloadManager;
    }

    public void downloadFileBySysDownloadManager(Context context, String url, String fileName, String mimeType) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 通知栏的下载通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(fileName);
        request.setMimeType(mimeType);
        // 保存到DIRECTORY_DOWNLOADS目录，文件名为 fileName
        File file = new File(Environment.DIRECTORY_DOWNLOADS, fileName);
        if (file.exists()) {
            boolean result = file.delete();
            Log.d(TAG, "file.delete():" + result);
        }
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName);
        long downloadId = Objects.requireNonNull(downloadManager).enqueue(request);
        Log.d(TAG, "downloadId:" + downloadId);
        //文件下载完成会发送完成广播，可注册广播进行监听
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        intentFilter.addAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        DownloadBroadcast mDownloadBroadcast = new DownloadBroadcast(file,mimeType);
        context.registerReceiver(mDownloadBroadcast, intentFilter);
        if (callback != null) {
            callback.callback(mDownloadBroadcast);
        }
    }

    public interface Callback {
        void callback(DownloadBroadcast downloadBroadcast);
    }
}
