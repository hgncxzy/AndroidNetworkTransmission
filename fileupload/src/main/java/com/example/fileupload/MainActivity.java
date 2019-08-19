package com.example.fileupload;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fileupload.httpurlconnection.HttpUploadFileHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements HttpUploadFileHelper.UploadResultListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Per.isGrantExternalRW(this);
        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://yapi.demo.qunar.com/mock/87945/http/file/upload";
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.txt";
                        HttpUploadFileHelper.sendByHttpUrlConnection(url, path, MainActivity.this);

                    }
                }).start();
            }
        });
    }

    /**
     * https://blog.csdn.net/dulinanaaa/article/details/89181410
     * 将inputStream转化为file
     *
     * @param is
     * @param file 要输出的文件目录
     */
    public static void inputStream2File(InputStream is, File file) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int len = 0;
            byte[] buffer = new byte[8192];

            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } finally {
            if (os != null) {
                os.close();
            }
            if (is != null) {
                is.close();
            }

        }
    }

    @Override
    public int progress(final int progress) {
        Log.d("xzy", progress + "");
        runOnUiThread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                TextView textView = findViewById(R.id.progress);
                textView.setText(progress + "%");
            }
        });
        return 0;
    }

    @Override
    public void onSuccess() {
        Log.d("xzy", "success");
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(String result) {
        Log.d("xzy", "failure");
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }
}
