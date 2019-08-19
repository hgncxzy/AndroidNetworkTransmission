package com.example.fileupload.socket;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.example.fileupload.httpurlconnection.HttpUploadFileHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 *使用 Socket 方式实现大文件上传
 * https://www.jianshu.com/p/b6ffab850d35
 * @author xzy
 */
@SuppressWarnings("unused")
public class SocketUploadFileHelper {
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

    private SocketUploadFileHelper() {

    }

    /**
     * 使用Socket向服务器上传文件，上传大文件时建议使用Socket，才不会造成内存溢出
     *
     * @param url 服务器地址
     * @param filePath 待上传文件路径
     * @param listener 回调监听
     */
    public static void uploadFileBySocket(final String url, String filePath, final HttpUploadFileHelper.UploadResultListener listener) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(filePath)) {
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
                        int length; // 每次上传的文件大小
                        float fileUploadSize = 0; // 当前已上传的文件大小
                        while ((length = fileInputStream.read(buffer)) != -1) {//输出文件内容
                            if (listener != null) {
                                fileUploadSize += length;
                                float progress = fileUploadSize / uploadFile.length() * 100;
                                listener.progress((int) progress);
                            }
                            outputStream.write(buffer, 0, length);
                        }
                        fileInputStream.close();

                        outputStream.write(endBytes);//输出结束行
                        outputStream.close();

                        return "true";

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        return e.getMessage();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        return e.getMessage();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return e.getMessage();
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    if (listener != null) {
                        if ("true".equals(result)) {
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
