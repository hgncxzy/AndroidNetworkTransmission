package com.example.fileupload.httpurlconnection;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.UUID;
/**
 * https://www.jianshu.com/p/b6ffab850d35
 */
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
     * @param url
     * @param filePath
     * @param listener
     */
    public static void sendByHttpUrlConnection(final String url, final String filePath, final UploadResultListener listener) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(filePath)) {//校验上传路径和文件
            return;
        }

        final File uploadFile = new File(filePath);
        if (uploadFile.exists() && uploadFile.isFile()) {
            new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(Void... params) {

                    try {
                        StringBuffer headBuffer = new StringBuffer(); //构建文件头部信息
                        headBuffer.append(PREFIX);
                        headBuffer.append(BOUNDARY);
                        headBuffer.append(CRLF);
                        headBuffer.append("Content-Disposition: form-data; name=\"" + FORM_NAME + "\"; filename=\"" + uploadFile.getName() + "\"" + CRLF);//模仿web上传文件提交一个form表单给服务器，表单名随意起
                        headBuffer.append("Content-Type: application/octet-stream" + CRLF);//若服务器端有文件类型的校验，必须明确指定Content-Type类型
                        headBuffer.append(CRLF);
                        Log.i(TAG, headBuffer.toString());
                        byte[] headBytes = headBuffer.toString().getBytes();

                        StringBuffer endBuffer = new StringBuffer();//构建文件结束行
                        endBuffer.append(CRLF);
                        endBuffer.append(PREFIX);
                        endBuffer.append(BOUNDARY);
                        endBuffer.append(PREFIX);
                        endBuffer.append(CRLF);
                        byte[] endBytes = endBuffer.toString().getBytes();

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

                        OutputStream outputStream = httpURLConnection.getOutputStream();
                        outputStream.write(headBytes);//输出文件头部

                        FileInputStream fileInputStream = new FileInputStream(uploadFile);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fileInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, length);//输出文件内容
                        }
                        fileInputStream.close();

                        outputStream.write(endBytes);//输出结束行
                        outputStream.close();

                        if (httpURLConnection.getResponseCode() == 200) {//发送成功
                            return true;
                        } else {
                            return false;
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        return false;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    super.onPostExecute(result);
                    if (listener != null) {
                        if (result) {
                            listener.onSuccess();
                        } else {
                            listener.onFailure();
                        }
                    }
                }

            }.execute();

        }
    }

    /**
     * 使用Socket向服务器上传文件，上传大文件时建议使用Socket，才不会造成内存溢出
     *
     * @param url
     * @param filePath
     * @param listener
     */
    public static void sendBySocket(final String url, String filePath, final UploadResultListener listener) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(filePath)) {
            return;
        }

        final File uploadFile = new File(filePath);
        if (uploadFile.exists() && uploadFile.isFile()) {
            new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        StringBuffer headBuffer = new StringBuffer(); //构建文件头部信息
                        headBuffer.append(PREFIX);
                        headBuffer.append(BOUNDARY);
                        headBuffer.append(CRLF);
                        headBuffer.append("Content-Disposition: form-data; name=\"" + FORM_NAME + "\"; filename=\"" + uploadFile.getName() + "\"" + CRLF);//模仿web上传文件提交一个form表单给服务器，表单名随意起
                        headBuffer.append("Content-Type: application/octet-stream" + CRLF);//若服务器端有文件类型的校验，必须明确指定Content-Type类型
                        headBuffer.append(CRLF);
                        Log.i(TAG, headBuffer.toString());
                        byte[] headBytes = headBuffer.toString().getBytes();

                        StringBuffer endBuffer = new StringBuffer();//构建文件结束行
                        endBuffer.append(CRLF);
                        endBuffer.append(PREFIX);
                        endBuffer.append(BOUNDARY);
                        endBuffer.append(PREFIX);
                        endBuffer.append(CRLF);
                        byte[] endBytes = endBuffer.toString().getBytes();

                        URL remoteUrl = new URL(url);
                        Socket socket = new Socket(remoteUrl.getHost(), remoteUrl.getPort());
                        OutputStream outputStream = socket.getOutputStream();
                        PrintStream printStream = new PrintStream(outputStream, true, CHARSET_UTF_8);

                        //输出请求头，用println输出可以省了后面的换行
                        printStream.println("POST " + url + " HTTP/1.1");
                        printStream.println("Content-Type: multipart/form-data; boundary=" + BOUNDARY);
                        String contentLength = String.valueOf(headBytes.length + endBytes.length + uploadFile.length());
                        printStream.println("Content-Length: " + contentLength);
                        printStream.println();//根据 HTTP 协议，空行将结束头信息

                        outputStream.write(headBytes);//输出文件头部

                        FileInputStream fileInputStream = new FileInputStream(uploadFile);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fileInputStream.read(buffer)) != -1) {//输出文件内容
                            outputStream.write(buffer, 0, length);
                        }
                        fileInputStream.close();

                        outputStream.write(endBytes);//输出结束行
                        outputStream.close();

                        return true;

                    } catch (MalformedURLException e) {
                        e.printStackTrace();

                    } catch (UnknownHostException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    super.onPostExecute(result);
                    if (listener != null) {
                        if (result) {
                            listener.onSuccess();

                        } else {
                            listener.onFailure();

                        }
                    }
                }

            }.execute();
        }

    }

    /**
     * 监听上传结果
     */
    public static interface UploadResultListener {

        /**
         * 上传成功
         */
        public void onSuccess();

        /**
         * 上传失败
         */
        public void onFailure();
    }
}
