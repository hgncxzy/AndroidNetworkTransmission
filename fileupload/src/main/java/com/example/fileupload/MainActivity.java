package com.example.fileupload;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fileupload.httpurlconnection.HttpUploadFileHelper;

public class MainActivity extends AppCompatActivity implements HttpUploadFileHelper.UploadResultListener {
    private ProgressBar uploadBar;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Per.isGrantExternalRW(this);

        uploadBar = findViewById(R.id.uploadProgressbar);

        findViewById(R.id.uploadWithHttp).setOnClickListener(new View.OnClickListener() {
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


        findViewById(R.id.uploadWithSocket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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
