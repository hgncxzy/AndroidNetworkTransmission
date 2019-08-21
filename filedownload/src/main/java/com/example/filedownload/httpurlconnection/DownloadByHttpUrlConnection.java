package com.example.filedownload.httpurlconnection;

import android.os.Environment;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 使用 HttpUrlConnection 下载图片
 *
 * @author xzy
 */
@SuppressWarnings("unused")
public class DownloadByHttpUrlConnection {
    private static final String TAG = "DownloadByHttpUrlConnec";

//    /**
//     * 获取图片
//     *
//     * @param path 图片路径
//     * @return bitmap
//     */
//    static Bitmap image(String path) throws Exception {
//        URL url = new URL(path);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setConnectTimeout(5000);
//        conn.setRequestMethod("GET");
//        if (conn.getResponseCode() == 200) {
//            InputStream inStream = conn.getInputStream();
//            return BitmapFactory.decodeStream(inStream);
//        }
//        return null;
//    }

    /**
     * 供外部调用的 get 请求。
     *
     * @param path   请求 url
     * @param params 请求参数 map 类型
     */
    public static void get(String path, String fielName,Map<String, String> params, HttpCallBackListener httpCallBackListener) {
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
                    // 保存文件到磁盘
                    WriteFile(in, Environment.getExternalStorageDirectory().getAbsolutePath(), getFileName(conn,fielName));
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


    private static String getFileName(HttpURLConnection httpURLConnection,String defaultFileName) {
        // 打印HTTP header
        Map headers = httpURLConnection.getHeaderFields();
        Set keys = headers.keySet();
        for (Object key : keys) {
            Log.d(TAG, key + " ----------------------------- " + httpURLConnection.getHeaderField((String) key));
        }
        // 转换编码
        String contentDisposition = null;
        try {
            /**
             *
             * https://www.cnblogs.com/SkyGood/p/3959118.html
             * 如果响应中设置了 response.setHeader("Content-disposition", "attachment;filename=" +filename);就可以通过如下方式
             * 拿到文件名，否则为空
             * **/
            String temp  = httpURLConnection.getHeaderField("content-Disposition");
            if(temp != null){
                contentDisposition = URLDecoder.decode(temp, "UTF-8");

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(contentDisposition != null){
            Log.d(TAG, "contentDisposition----"+Objects.requireNonNull(contentDisposition));
            // 匹配文件名
            Pattern pattern = Pattern.compile(".*fileName=(.*)");
            Matcher matcher = pattern.matcher(contentDisposition);
            return matcher.group(1);
        }
        return defaultFileName;

    }

    /**
     * 写入文件
     *
     * @param inputStream 下载文件的字节流对象
     * @param sdPath      文件的存放目录
     * @param fileName    文件名
     */
    private static void WriteFile(InputStream inputStream, String sdPath, String fileName) {
        // 写盘
        RandomAccessFile file;
        try {
            file = new RandomAccessFile(sdPath + "/" + fileName, "rw");

            byte[] buffer = new byte[1024];
            while (true) {
                int len = inputStream.read(buffer);
                if (len == -1) {
                    break;
                }
                file.write(buffer, 0, len);
            }
            file.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 回调接口。
     */
    public interface HttpCallBackListener {

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
