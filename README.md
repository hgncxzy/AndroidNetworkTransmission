# AndroidBasicDataCommunication
Android 基本的数据通信，使用原生形式(非三方框架)实现 get 请求，post 请求，图片下载，文件上传与下载，xml 解析等。

### 1. 使用 java.net 接口

#### 1. get 请求

详细例子请查看 getrequestdemo。

```java
package com.example.xzy.comm.get;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;

/**
 * get 请求工具类封装。使用的是标准的 java 接口：java.net
 */
class HttpGetUtil {

    /**
     * 供外部调用的 get 请求。
     * @param path 请求 url
     * @param params 请求参数 map 类型
     */
    static void getRequest(String path, Map<String, String> params, HttpCallBackListener httpCallBackListener){
        StringBuilder sb = new StringBuilder(path);
        if(params!=null && !params.isEmpty()){
            sb.append("?");
            for(Map.Entry<String, String> entry : params.entrySet()){
                sb.append(entry.getKey()).append("=");
                try {
                    sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    if(httpCallBackListener != null){
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
            if(httpCallBackListener != null){
                httpCallBackListener.onError(e);
            }
        }
        Objects.requireNonNull(conn).setConnectTimeout(5000);
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            if(httpCallBackListener!= null){
                httpCallBackListener.onError(e);
            }
        }
        try {
            if(conn.getResponseCode() == 200){
                InputStream in = conn.getInputStream();
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
                String line;
                StringBuilder response = new StringBuilder();
                while((line = bufferReader.readLine())!= null){
                    response.append(line);
                }
                if(httpCallBackListener != null){
                    httpCallBackListener.onFinish(response.toString());
                }
            }
        } catch (IOException e) {
            if(httpCallBackListener != null){
                httpCallBackListener.onError(e);
            }
        }finally {
            conn.disconnect();
        }
    }



    /**
     * 回调接口。
     */
    interface HttpCallBackListener {

        /**
         * 回调成功。
         * @param response 成功响应
         */
        void onFinish(String response);

        /**
         * 回调失败。
         * @param e 异常回调
         */
        void onError(Exception e);
    }
}



```

#### 2. post 请求

详细例子请查看 postrequestdemo。

```java
package com.example.postrequestdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;

/**
 * post 请求工具类封装。使用的是标准的 java 接口：java.net
 */
class HttpPostUtil {

     static void postRequest(String path, Map<String, String> params,HttpCallBackListener httpCallBackListener){
        StringBuilder sb = new StringBuilder();
        if(params!=null && !params.isEmpty()){
            for(Map.Entry<String, String> entry : params.entrySet()){
                sb.append(entry.getKey()).append("=");
                try {
                    sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    if(httpCallBackListener != null){
                        httpCallBackListener.onError(e);
                    }
                }
                sb.append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        byte[] data = sb.toString().getBytes();
        HttpURLConnection conn = null;
        try {
             conn = (HttpURLConnection) new URL(path).openConnection();
        }catch (IOException e) {
             if(httpCallBackListener != null){
                 httpCallBackListener.onError(e);
             }
         }
         Objects.requireNonNull(conn).setConnectTimeout(5000);
         try {
             conn.setRequestMethod("POST");
         } catch (ProtocolException e) {
             if(httpCallBackListener != null){
                 httpCallBackListener.onError(e);
             }
         }
         conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", data.length+"");
         OutputStream outStream = null;
         try {
             outStream = conn.getOutputStream();
         } catch (IOException e) {
             if(httpCallBackListener != null){
                 httpCallBackListener.onError(e);
             }
         }
         try {
             Objects.requireNonNull(outStream).write(data);
             outStream.flush();
             if(conn.getResponseCode() == 200){
                 InputStream in = conn.getInputStream();
                 BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
                 String line;
                 StringBuilder response = new StringBuilder();
                 while((line = bufferReader.readLine())!= null){
                     response.append(line);
                 }
                 if(httpCallBackListener != null){
                     httpCallBackListener.onFinish(response.toString());
                 }
             }
         } catch (IOException e) {
             if(httpCallBackListener != null){
                 httpCallBackListener.onError(e);
             }
         }finally {
             conn.disconnect();
         }
    }
    /**
     * 回调接口。
     */
    interface HttpCallBackListener {

        /**
         * 回调成功。
         * @param response 成功响应
         */
        void onFinish(String response);

        /**
         * 回调失败。
         * @param e 异常回调
         */
        void onError(Exception e);
    }
}

```

#### 3. 下载图片

详细例子请查看 imagedownloaddemo。

```java
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
     * @param path 图片路径
     * @return bitmap
     */
    static Bitmap getImage(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == 200){
            InputStream inStream = conn.getInputStream();
            return BitmapFactory.decodeStream(inStream);
        }
        return null;
    }
}

```

### 2. 使用 Apache 接口

使用 apache 接口需要在 AS 中做如下配置：

必须在 build.gradle 文件中先声明下面的编译时依赖：

```groovy
android { useLibrary 'org.apache.http.legacy' }
```

如果添加后在 bulid.gradle 中出现如下错误：

```groovy
Gradle DSL method not found: 'useLibrary()'
```

请确保你的gradle版本设置高于1.3.0-rc2.

另外需要引入 apache 包的依赖：

```groovy
 // 引入 apache 依赖库
 implementation 'org.apache:apache:21'
```

然后在项目中就可以使用 apache 接口了。详细例子请查看 apachehttpdemo.

#### 1. get 请求

```java
 /**
     * apache 方式实现的 get 请求。
     * @param url 请求 url
     */
     static String get(String url) {
         HttpResponse httpResponse;
         StringBuilder result = new StringBuilder();
         try {
             HttpGet httpGet = new HttpGet(url);
             HttpClient httpClient = new DefaultHttpClient();
             httpResponse = httpClient.execute(httpGet);
             HttpEntity httpEntity = httpResponse.getEntity();
             result.append(EntityUtils.toString(httpEntity, "utf-8"));
             StatusLine statusLine = httpResponse.getStatusLine();
             result.append(statusLine.getProtocolVersion() + "\r\n");
             int statusCode = statusLine.getStatusCode();
             result.append(statusCode + "\r\n");

         } catch (IOException e) {
             e.printStackTrace();
         }
         return result.toString();
    }
```

#### 2. post 请求

```java
 /**
     * apache 方式实现的 post 请求。
     * @param url 请求 url
     */
    static String post(String url){
        HttpResponse httpResponse;
        StringBuilder result = new StringBuilder();
        try {
            HttpPost httpPost = new HttpPost(url);
            HttpClient httpClient = new DefaultHttpClient();
            httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            result.append(EntityUtils.toString(httpEntity, "utf-8"));
            StatusLine statusLine = httpResponse.getStatusLine();
            statusLine.getProtocolVersion();
            int statusCode = statusLine.getStatusCode();
            result.append(statusCode+"\r\n");

        } catch (IOException e) {
           e.printStackTrace();
        }
        return result.toString();
    }
```

#### 3. 下载图片

