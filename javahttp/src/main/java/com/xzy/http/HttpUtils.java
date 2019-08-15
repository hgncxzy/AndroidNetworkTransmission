package com.xzy.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;

/**
 * get 请求工具类封装。使用的是标准的 java 接口：java.net
 *
 * @author xzy
 */
@SuppressWarnings("all")
class HttpUtils {

    /**
     * 供外部调用的 get 请求。
     *
     * @param path       请求 url
     * @param params     请求参数 map 类型
     */
    static void get(String path,Map<String, String> params, HttpCallBackListener httpCallBackListener) {
        StringBuilder sb = new StringBuilder(path);
        if (params != null && !params.isEmpty()) {
            sb.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey()).append("=");
                try {
                    sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    if (httpCallBackListener != null) {
                        httpCallBackListener.onError(e);
                    }
                }
                sb.append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(sb.toString()).openConnection();
        } catch (IOException e) {
            if (httpCallBackListener != null) {
                httpCallBackListener.onError(e);
            }
        }
        Objects.requireNonNull(conn).setConnectTimeout(5000);
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            if (httpCallBackListener != null) {
                httpCallBackListener.onError(e);
            }
        }
        try {
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                if (httpCallBackListener != null) {
                    httpCallBackListener.onFinish(in);
                }
            } else {
                if (httpCallBackListener != null) {
                    httpCallBackListener.onError(new Exception(conn.getResponseMessage()));
                }
            }
        } catch (IOException e) {
            if (httpCallBackListener != null) {
                httpCallBackListener.onError(e);
            }
        } finally {
            conn.disconnect();
        }
    }


    public static void post(String path, Map<String, String> params, HttpCallBackListener httpCallBackListener) {
        HttpURLConnection conn = null;
        try {
            StringBuilder sb = new StringBuilder();
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    sb.append(entry.getKey()).append("=");
                    try {
                        sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        if (httpCallBackListener != null) {
                            httpCallBackListener.onError(e);
                        }
                    }
                    sb.append("&");
                }
                sb.deleteCharAt(sb.length() - 1);
            }
            byte[] data = sb.toString().getBytes();
            conn = (HttpURLConnection) new URL(path).openConnection();
            Objects.requireNonNull(conn).setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", data.length + "");
            OutputStream outStream = null;
            outStream = conn.getOutputStream();
            Objects.requireNonNull(outStream).write(data);
            outStream.flush();
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                if (httpCallBackListener != null) {
                    httpCallBackListener.onFinish(in);
                }
            } else {
                if (httpCallBackListener != null) {
                    httpCallBackListener.onError(new Exception(conn.getResponseMessage()));
                }
            }
        } catch (IOException e) {
            if (httpCallBackListener != null) {
                httpCallBackListener.onError(e);
            }
        } finally {
            conn.disconnect();
        }
    }


    /**
     * 获取图片
     *
     * @param path 图片路径
     * @return bitmap
     */
    static Bitmap image(String path) throws Exception {
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


    public static String inputStream2String(InputStream is) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = is.read(b)) != -1; n++) {
            stringBuffer.append(new String(b, 0, n));
        }
        return stringBuffer.toString();
    }

    public static Bitmap inputStream2Bitmap(InputStream inputStream) throws Exception {
        return BitmapFactory.decodeStream(inputStream);
    }




    /**
     * 回调接口。
     */
    interface HttpCallBackListener {

        /**
         * 回调成功。
         *
         * @param inputStream 成功响应
         */
        void onFinish(InputStream inputStream);

        /**
         * 回调失败。
         *
         * @param e 异常回调
         */
        void onError(Exception e);
    }
}


