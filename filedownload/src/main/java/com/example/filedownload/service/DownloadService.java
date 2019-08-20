package com.example.filedownload.service;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.example.filedownload.MainActivity;
import com.example.filedownload.broadcast.DownloadBroadcast;

import java.io.File;

/**
 * 下载工具类
 */
public class DownloadService {
    private static final String TAG = "DownloadService";
    public static String IMG_URL = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000" +
            "&sec=1566328465929&di=be802b12aca56865c80cb104205ca682&imgtype=0&src=http%3A%2F%2Fimg007." +
            "hc360.cn%2Fhb%2Fp8L6411a5b2b1744935bb5F79b79D9b7037.jpg";

    private DownloadService(){

    }

    /**
     *
     * 使用这种方法下载完全把工作交给了系统应用，自己的应用中不需要申请任何权限，方便简单快捷。但如此我们也不能知道
     * 下载文件的大小，不能监听下载进度和下载结果。
     * @param context 上下文
     * @param url 下载 url
     */
    public static void downloadFileByBrowser(Context context, String url){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    public static void downloadFileBySysDownloadManager(Context context,String url,String fileName){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 通知栏的下载通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(fileName);
//        request.setMimeType("application/vnd.android.package-archive");
        request.setMimeType("*/*");
        // 保存到DIRECTORY_DOWNLOADS目录，文件名为 fileName
        File file = new File(Environment.DIRECTORY_DOWNLOADS, fileName);
        if (file.exists()) {
            file.delete();
        }
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName);
        long downloadId = downloadManager.enqueue(request);
        Log.d(TAG, "downloadId:" + downloadId);
        //文件下载完成会发送完成广播，可注册广播进行监听
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        intentFilter.addAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        DownloadBroadcast mDownloadBroadcast = new DownloadBroadcast(file);
        context.registerReceiver(mDownloadBroadcast, intentFilter);
    }
}
