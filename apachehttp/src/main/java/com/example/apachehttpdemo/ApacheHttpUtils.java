package com.example.apachehttpdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 使用 apache 接口 HttpClient 实现的 get 请求、post 请求、图片下载 封装工具类。
 */
@SuppressWarnings("unused")
class ApacheHttpUtils {

    /**
     * apache 方式实现的 get 请求。
     *
     * @param url                  请求 url
     * @param httpCallBackListener 回调
     */
    static void get(String url, HttpCallBackListener httpCallBackListener) {
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                if (httpCallBackListener != null) {
                    httpCallBackListener.onFinish(inputStream2String(httpResponse.getEntity().getContent()));
                }
            } else {
                if (httpCallBackListener != null) {
                    httpCallBackListener.onError(new Exception(httpResponse.getStatusLine().getStatusCode() + ""));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (httpCallBackListener != null) {
                httpCallBackListener.onError(e);
            }
        }
    }

    /**
     * apache 方式实现的 post 请求。
     *
     * @param url                  请求 url
     * @param map                  请求 参数
     * @param httpCallBackListener 回调
     */
    static void post(String url, Map<String, String> map, HttpCallBackListener httpCallBackListener) {
        List<NameValuePair> list = new ArrayList<>();
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        try {
            HttpPost httpPost = new HttpPost(url);
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, HTTP.UTF_8);
            httpPost.setEntity(entity);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                if (httpCallBackListener != null) {
                    httpCallBackListener.onFinish(inputStream2String(httpResponse.getEntity().getContent()));
                }
            } else {
                if (httpCallBackListener != null) {
                    httpCallBackListener.onError(new Exception(httpResponse.getStatusLine().getStatusCode() + ""));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (httpCallBackListener != null) {
                httpCallBackListener.onError(e);
            }
        }
    }

    /**
     * apache 接口实现的图片下载。
     *
     * @param url 请求图片的 url。
     * @return bitmap
     */
    static Bitmap image(String url) {
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();
            System.out.println(inputStream.available());
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
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


    /**
     * 回调接口。
     */
    interface HttpCallBackListener {

        /**
         * 回调成功。
         *
         * @param response 成功响应
         */
        void onFinish(String response);

        /**
         * 回调失败。
         *
         * @param e 异常回调
         */
        void onError(Exception e);
    }
}
