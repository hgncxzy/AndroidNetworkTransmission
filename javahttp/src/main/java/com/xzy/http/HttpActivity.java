package com.xzy.http;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xzy.http.HttpUtils.image;
import static com.xzy.http.HttpUtils.post;
import static com.xzy.http.consant.Constant.getUrl;
import static com.xzy.http.consant.Constant.imgUrl;
import static com.xzy.http.consant.Constant.postUrl;

/**
 * 使用的是标准的 java 接口：java.net ,通过 get 请求的 demo。
 */
public class HttpActivity extends Activity implements HttpUtils.HttpCallBackListener {
    private EditText titleText;
    private EditText lengthText;
    private TextView result;
    private ImageView mDownloadImageIv;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Disposable disposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_get_layout);
        titleText = findViewById(R.id.title);
        lengthText = findViewById(R.id.length);
        result = findViewById(R.id.result);
        mDownloadImageIv = findViewById(R.id.image);
    }

    public void getReq(View v) {
        disposable = Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        // get 请求
                        HttpUtils.get(getUrl, getParams(),
                                HttpActivity.this);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        compositeDisposable.add(disposable);
    }

    public void postReq(View v) {
        disposable = Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        // post 请求
                        post(postUrl, getParams(),
                                HttpActivity.this);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        compositeDisposable.add(disposable);
    }


    public void getImageReq(View view) {
        Disposable disposable = Flowable.just(imgUrl)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String imgUrl) throws Exception {
                        return image(imgUrl);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) {
                        mDownloadImageIv.setImageBitmap(bitmap);
                    }
                })
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Toast.makeText(HttpActivity.this
                                , "下载异常:" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        compositeDisposable.add(disposable);
    }


    private Map<String, String> getParams() {
        String name = titleText.getText().toString();
        String age = lengthText.getText().toString();
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("age", age);
        return map;
    }

    @Override
    public void onFinish(String response) {
        disposable = Flowable.just(response)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        result.setText(s);
                    }
                })
                .subscribe();
        compositeDisposable.add(disposable);
    }

    @Override
    public void onError(Exception e) {
        disposable = Flowable.just(e)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Exception>() {
                    @Override
                    public void accept(Exception e) {
                        result.setText(e.getMessage());
                    }
                })
                .subscribe();
        compositeDisposable.add(disposable);
    }
}