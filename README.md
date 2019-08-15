# AndroidNetworkTransmission （Android 网络传输）

### 简介

Android 基本的数据通信，使用原生形式(非三方框架)实现 get 请求，post 请求，图片下载，文件上传与下载，xml 解析等。

Android 基本数据通信包含 3 种通信接口。

1. 标准的 Java 接口：java.net。

2. Apache 接口：org.apache.http。

3. Android 网络接口

   - 一个接口是 android.net.http.AndroidHttpClient ，从包名可以看出是 Android 原生的接口。不过在深入源码，会发现依然是对 Apache HttpClient 的封装。

   - 一个接口是 org.apache.http.impl.client.DefaultHttpClient，从包名可以看出是对 Apache 接口的封装。

**综上，Android 网络接口本质是对 Apache 的封装。**我们先来回顾下基础概念。

### Android Http

#### 前言

　　说到HTTP协议，那必须要说说WWW了，WWW是环球信息网（World Wide Web ）的缩写，也可以简称为Web，中文名字为“万维网”。简单来说，WWW是以Internet作为传输媒介的一个应用系统，WWW网上基本的传输单位是Web网页。WWW的工作是基于B/S模型，由Web浏览器和Web服务器构成，两者之间采用超文本传输协议HTTP协议进行通信。

 　　HTTP协议是基于TCP/IP协议之上的协议，是Web浏览器和Web服务器之间的应用层的协议，是通用的、无状态的面向对象的协议。关于HTTP协议的详细讲解，请参见博客：[HTTP协议详解](http://blog.csdn.net/gueter/article/details/1524447)，里面讲解的很清楚，这里主要是说明HTTP在Java中的应用，为从其他技术下转向Android开发打好基础。

　　首先普及一下网络协议的知识，数据在Internet上传输，一般通过三种协议来实现发送信息和实现：

1. HTTP协议，也是在工作中最常用的，是建立在TCP/IP基础上实现的。
2. FTP协议。
3. TCP/IP协议，它也是最低层的协议，其它的方式必须要通过它，但是想要实现这种协议必须要实现socket编程，这种方法是用来上传一些比较大的文件，视频，进行断电续传的操作。

#### HTTP 协议

　　下面详细讲解一下HTTP协议，因为HTTP是无状态的协议，所以服务端并不记录客户端之前发送信息，一码归一码，所以HTTP协议使用报文头的形式记录状态，一般分为请求报文和响应报文。一般用户使用浏览器访问网页，是无需关心HTTP请求的报文头的，因为开发人员已经浏览器已经帮忙处理了，但是当进行开发工作的时候，这些是必须要了解的。

　　对于报文，一般关心请求方式，是GET或者是POST；请求数据类型，是文本还是音频；数据的编码格式，一般用utf-8；发送的数据长度；响应返回码，一般200为成功，其他响应码都是有问题。具体了解还是看看上面推荐的博客。

　　HTTP/1.1协议中一共定义了八种方法（有时也叫“动作”）来表明Request-URI指定的资源的不同操作方式，但是一般常用的就是GET和POST方式。

　　这里简单说一下GET方式和POST方式的差别：

1. GET是从服务器上获取数据，POST是向服务器传送数据。
2. 在客户端，GET方式在通过URL提交数据，数据在URL中可以看到；POST方式，数据放在HTML HEADER内提交。
3. 对于GET方式，服务器端用Request.QueryString获取变量的值，对于POST方式，服务器用Request.Form获取提交的数据。
4. GET方式提交的数据不能大于2KB（主要是URL长度限制），而POST则没有此限制。
5. 安全性问题。正如2中提到，使用GET的时候，参数会显示在地址栏上，而POST不会。所以，如果这些数据是中文数据而且是非敏感数据，那么使用GET；如果用户输入的数据不是中文字符而且包含敏感数据，那么还是使用POST为好。

#### Java 中使用 HTTP

　　下面通过两个例子来分别讲解一下GET和POST在Java中的使用，如果在Java中需要使用HTTP协议进行访问，一般通过HttpURLConnection类来实现。

```java
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

```

HttpURLConnection继承了URLConnection，所以在URLConnection的基础上进一步改进，增加了一些用于操作HTTP资源的便捷方法。Java中HttpURLConnection对象通过URL.openConnection()方法来获得，需要进行强制转换。先来介绍几个HttpURLConnection的常用方法：

- void setConnectTimeout(int timeout)：设置连接超时时长，如果超过timeout时长，则放弃连接，单位以毫秒计算。
- void setDoInput(boolean newValue) ：标志是否允许输入。
- void setDoOutput(boolean newValue)：标志是否允许输出。
- String getRequestMethod():获取发送请求的方法。
- int getResponseCode():获取服务器的响应码。
- void setRequestMethod(String method)：设置发送请求的方法。
- void setRequestProperty(String field,String newValue)：设置请求报文头，并且只对当前HttpURLConnection有效。

更多源码请参考项目 [javahttp](https://github.com/hgncxzy/AndroidNetworkTransmission/tree/master/javahttp)

### Android Apache HttpClient 

HttpClient 是 Apache 开源组织提供的一个开源的项目，从名字上就可以看出，它是一个简单的 HTTP 客户端（并不是浏览器），可以发送 HTTP 请求，接受 HTTP 响应。但是不会缓存服务器的响应，不能执行 HTTP 页面中签入嵌入的 JS 代码，自然也不会对页面内容进行任何解析、处理，这些都是需要开发人员来完成的。

现在Android已经成功集成了 HttpClient，所以开发人员在 Android 项目中可以直接使用 HttpClient 来想 Web 站点提交请求以及接受响应，如果使用其他的Java项目，需要引入进相应的Jar包。

**HttpClient**

HttpClient其实是一个 interface 类型，HttpClient 封装了对象需要执行的 Http 请求、身份验证、连接管理和其它特性。从文档上看，HttpClient 有三个已知的实现类分别是：AbstractHttpClient, AndroidHttpClient, DefaultHttpClient，会发现有一个专门为 Android 应用准备的实现类 AndroidHttpClient，当然使用常规的 DefaultHttpClient 也可以实现功能，但是既然开发的是 Android 应用程序，还是使用 Android 专有的实现类，一定有其优势。

从两个类包所有在位置就可以看出区别，AndroidHttpClient 定义在 android.net.http.AndroidHttpClient 包下，属于 Android原生的http访问，但底层依然是对 Apache 的封装(可以通过源码发现).

DefaultHttpClient 定义在 org.apache.http.impl.client.DefaultHttpClient 包下，属于对 apche 项目的支持。而AndroidHttpClient 没有公开的构造函数，只能通过静态方法 newInstance() 方法来获得 AndroidHttpClient 对象。

##### AndroidHttpClient 对于 DefaultHttpClient 做了一些改进，使其更使用用于 Android 项目：

1. 关掉过期检查，自连接可以打破所有的时间限制。
2. 可以设置 ConnectionTimeOut（连接超时）和 SoTimeout（读取数据超时）。
3. 关掉重定向。
4. 使用一个 Session 缓冲用于 SSL Sockets。
5. 如果服务器支持，使用 gzip 压缩方式用于在服务端和客户端传递的数据。
6. 默认情况下不保留 Cookie。　　　　

##### 简单来说，用 HttpClient 发送请求、接收响应都很简单，只需要几个步骤即可：

1. 创建 HttpClient 对象。
2. 创建对应的发送请求的对象，如果需要发送GET请求，则创建HttpGet对象，如果需要发送 POST 请求，则创建HttpPost 对象。
3. 对于发送请求的参数，GET 和 POST 使用的方式不同，GET方式可以使用拼接字符串的方式，把参数拼接在 URL 结尾；POST 方式需要使用 setEntity(HttpEntity  entity) 方法来设置请求参数。
4. 调用 HttpClient 对象的 execute（HttpUriRequest  request）发送请求，执行该方法返回一个 HttpResponse 对象。
5. 调用 HttpResponse 的对应方法获取服务器的响应头、响应内容等。

##### 配置注意事项

使用 apache 接口需要在 AS 中做如下配置：

必须在 build.gradle 文件中先声明下面的编译时依赖：

```groovy
android {
    // 设置 apache 依赖库
    useLibrary 'org.apache.http.legacy'
    compileSdkVersion 29
    buildToolsVersion "29.0.0"
    ...
 }
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



#### DefaultHttpClient

先看看使用 DefaultHttpClient 方式发送 Web 站点请求，上面已经简要说明了步骤，在这里简要说明一个参数的传递问题，对于 GET 方式，只需要拼接字符串就在URL结尾即可，但是对于 POST 方式，需要传递HttpEntity对象，HttpEntity为一个接口，有多个实现类，可以使用其间接子继承，UrlEncodedFormEntity 类来保存请求参数，并传递给 HttpPost。

因为 Android4.0 之后对使用网络有特殊要求，已经无法再在主线程中访问网络了，必须使用多线程访问的模式，其他的一些信息在代码注释中已经说明。

```java
package com.xzy.defaulthttpclient;

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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 使用 android.net.http 方式实现请求。
 *
 * @author xzy
 */
@SuppressWarnings("all")
public class DefaultHttpClientUtils {

    public static void get(String url, HttpCallBackListener httpCallBackListener) {
        try {
            HttpClient client = new DefaultHttpClient();
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
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
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

```

更多源码请参考 [defaulthttpclient](https://github.com/hgncxzy/AndroidNetworkTransmission/tree/master/defaulthttpclient)

#### AndroidHttpClient

使用 AndroidHttpClient 的方式和 DefaultHttpClient 差不多，不多的几点区别上面已经说明，但是在此例子中没有体现。有一点需要注意的是，AndroidHttpClient 是一个 final 类，也没有公开的构造函数，所以无法使用 new 的形式对其进行实例化，必须使用 AndroidHttpClient.newInstance() 方法获得 AndroidHttpClient 对象。

```java
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
 * 使用 android.net.http 方式实现请求。
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

```

更多源码请参考 [androidhttpclient](https://github.com/hgncxzy/AndroidNetworkTransmission/tree/master/androidhttpclient)

### 总结

根据官方文档上说的显示，Android 包括两个 Http 客户端：HttpURLConnection 和 Apache  HttpClient。并且都支持HTTPS，流媒体上传下载，并且可配置超时以及支持 IPv6 和连接池技术。

但是因为移动设备的局限性，HttpURLConnection 会是比 Apache  Http 更好的选择，因为其 API 简单，运行消耗内存小，并且具有公开化的压缩算法，以及响应缓存，能更好的减少网络使用，提供运行速度和节省电池。

但是也不能否认 Apache  HttpClient，它有大量的灵活的 API，实现比较稳定，少有 Bug，可造成的问题就是很难在不影响其兼容性的情况下对其进行改进了。

现在 Android 开发者已经慢慢放弃 Apache  HttpClient 的使用，转而使用 HttpURLConnection。但是对于 Android2.2 之前的版本，HttpURLConnection 具有一个致命的BUG，在响应输入流 InputStream 中调用 Close() 方法将会阻碍连接池，因为这个 BUG，只能放弃连接池的使用，但是 Apache  HttpClient 不存在这个问题，当然 Android2.3 之后的版本中，HttpURLConnection 已经解决了这个BUG，可以放心使用。

### 参考文章

1. https://www.cnblogs.com/plokmju/p/java-HTTP.html
2. https://www.cnblogs.com/plokmju/p/Android_apacheHttpClient.html

### 联系作者

1. ID : hgncxzy
2. 邮箱：[hgncxzy@qq.com](mailto:hgncxzy@qq.com)
3. 项目地址：https://github.com/hgncxzy/AndroidNetworkTransmission

