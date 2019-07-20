package com.example.imagedownloaddemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 图片下载工具类封装。使用的是标准的 java 接口：java.net。
 */
class ImageService {

    /**
     * 获取图片
     *
     * @param path 图片路径
     * @return bitmap
     */
    static Bitmap getImage(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream inStream = conn.getInputStream();
            return BitmapFactory.decodeStream(inStream);
        }
        return null;
    }
}
