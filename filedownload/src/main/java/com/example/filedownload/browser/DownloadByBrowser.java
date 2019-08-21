package com.example.filedownload.browser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * 通过系统浏览器下载文件.
 *
 * @author xzy
 */
@SuppressWarnings("unused")
public class DownloadByBrowser {
    /**
     * 使用这种方法下载完全把工作交给了系统应用，自己的应用中不需要申请任何权限，方便简单快捷。但如此我们也不能知道
     * 下载文件的大小，不能监听下载进度和下载结果。
     *
     * @param context 上下文
     * @param url     下载 url
     */
    public static void downloadFileByBrowser(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }
}
