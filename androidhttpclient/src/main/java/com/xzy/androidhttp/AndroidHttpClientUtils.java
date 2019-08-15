package com.xzy.androidhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 使用 apache  AndroidHttpClient 方式实现 get、post、image 请求。
 *
 * @author xzy
 */
@SuppressWarnings("all")
public class AndroidHttpClientUtils {

    public static void get(String url, HttpCallBackListener httpCallBackListener) {
        try {
            HttpClient client = AndroidHttpClient.newInstance("");
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                if (httpCallBackListener != null) {
                    httpCallBackListener.onError(new Exception(response.toString()));
                }
            } else {
                if (httpCallBackListener != null) {
                    httpCallBackListener.onFinish(inputStream2String(response.getEntity().getContent()));
                }
            }
            ((AndroidHttpClient) client).close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            if (httpCallBackListener != null) {
                httpCallBackListener.onError(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (httpCallBackListener != null) {
                httpCallBackListener.onError(e);
            }
        }
    }

    public static void post(String url, Map<String, String> map, HttpCallBackListener httpCallBackListener) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, HTTP.UTF_8);
            HttpClient client = AndroidHttpClient.newInstance("");
            HttpPost post = new HttpPost(url);
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (httpCallBackListener != null) {
                    httpCallBackListener.onFinish(inputStream2String(response.getEntity().getContent()));
                }
            } else {
                if (httpCallBackListener != null) {
                    httpCallBackListener.onError(new Exception(response.getStatusLine().getStatusCode() + ""));
                }
            }
            ((AndroidHttpClient) client).close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            if (httpCallBackListener != null) {
                httpCallBackListener.onError(e);
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
            HttpClient httpClient = AndroidHttpClient.newInstance("");
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

    public static String inputStream2String(InputStream inputStream) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = inputStream.read(b)) != -1; n++) {
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
