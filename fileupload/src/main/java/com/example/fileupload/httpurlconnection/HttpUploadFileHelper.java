package com.example.fileupload.httpurlconnection;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * 使用 HttpUrlConnection 方式实现小文件上传
 * https://www.jianshu.com/p/b6ffab850d35
 *
 * @author xzy
 */
@SuppressWarnings("unused")
public class HttpUploadFileHelper {
    private final static String TAG = HttpUploadFileHelper.class.getSimpleName();

    /**
     * http 请求消息体中的上传文件边界标识
     */
    private static final String BOUNDARY = UUID.randomUUID().toString();
    /**
     * 文件类型
     */
    private static final String CONTENT_TYPE = "multipart/form-data";
    private static final String PREFIX = "--";

    /**
     * http 请求消息体中的回车换行
     */
    private static final String CRLF = "\r\n";
    private static final String CHARSET_UTF_8 = "UTF-8";
    /**
     * 表单名
     */
    private static final String FORM_NAME = "upload_file";

    private HttpUploadFileHelper() {

    }

    /**
     * 使用HttpUrlConnection来向服务器上传文件，在上传大文件时，会造成内存溢出
     *
     * @param url      上传 url
     * @param filePath 待上传文件路径
     * @param listener 上传监听回调
     */
    public static void sendByHttpUrlConnection(final String url, final String filePath, final UploadResultListener listener) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(filePath)) {//校验上传路径和文件
            return;
        }

        final File uploadFile = new File(filePath);
        if (uploadFile.exists() && uploadFile.isFile()) {
            new AsyncTask<Void, Void, String>() {

                @Override
                protected String doInBackground(Void... params) {

                    try {
                        StringBuilder headBuffer = new StringBuilder(); //构建文件头部信息
                        headBuffer.append(PREFIX);
                        headBuffer.append(BOUNDARY);
                        headBuffer.append(CRLF);
                        headBuffer.append("Content-Disposition: form-data; name=\"" + FORM_NAME + "\"; filename=\"").append(uploadFile.getName()).append("\"").append(CRLF);//模仿web上传文件提交一个form表单给服务器，表单名随意起
                        headBuffer.append("Content-Type: application/octet-stream" + CRLF);//若服务器端有文件类型的校验，必须明确指定Content-Type类型
                        headBuffer.append(CRLF);
                        Log.i(TAG, headBuffer.toString());
                        byte[] headBytes = headBuffer.toString().getBytes();

                        String endBuffer = CRLF +
                                PREFIX +
                                BOUNDARY +
                                PREFIX +
                                CRLF;
                        byte[] endBytes = endBuffer.getBytes();

                        URL remoteUrl = new URL(url);
                        HttpURLConnection httpURLConnection = (HttpURLConnection) remoteUrl.openConnection();
                        httpURLConnection.setDoOutput(true);//打开输出流
                        httpURLConnection.setDoInput(true);//打开输入流
                        httpURLConnection.setUseCaches(false);
                        httpURLConnection.setRequestMethod("POST");//上传文件必须要POST请求
                        httpURLConnection.setRequestProperty("Charset", CHARSET_UTF_8);//设置编码
                        httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);//设置http消息头部的Content-Type
                        String contentLength = String.valueOf(headBytes.length + endBytes.length + uploadFile.length());
                        httpURLConnection.setRequestProperty("Content-Length", contentLength);//设置内容长度
                        //httpURLConnection.setChunkedStreamingMode(0);
                        httpURLConnection.setFixedLengthStreamingMode(Long.valueOf(contentLength));
                        OutputStream outputStream = httpURLConnection.getOutputStream();
                        outputStream.write(headBytes);//输出文件头部

                        FileInputStream fileInputStream = new FileInputStream(uploadFile);
                        byte[] buffer = new byte[1024];
                        int length; // 每次上传的文件大小
                        float fileUploadSize = 0; // 当前已上传的文件大小
                        while ((length = fileInputStream.read(buffer)) != -1) {
                            if (listener != null) {
                                fileUploadSize += length;
                                float progress = fileUploadSize / uploadFile.length() * 100;
                                listener.progress((int) progress);
                            }
                            outputStream.write(buffer, 0, length);//输出文件内容
                        }
                        fileInputStream.close();

                        outputStream.write(endBytes);//输出结束行
                        outputStream.close();
                        return httpURLConnection.getResponseCode() + "--"+httpURLConnection.getContent().toString();

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        return "" + e.getMessage();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "" + e.getMessage();
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    if (listener != null) {
                        if ("200".equals(result)) {
                            listener.onSuccess();
                        } else {
                            listener.onFailure(result);
                        }
                    }
                }

            }.execute();

        }
    }



    /**
     * 监听上传结果
     */
    public interface UploadResultListener {
        /**
         * 上传进度
         **/
        int progress(int progress);

        /**
         * 上传成功
         */
        void onSuccess();

        /**
         * 上传失败
         */
        void onFailure(String result);
    }
}
