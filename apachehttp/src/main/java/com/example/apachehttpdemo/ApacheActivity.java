package com.example.apachehttpdemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import io.reactivex.Flowable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.example.apachehttpdemo.consant.Constant.getUrl;
import static com.example.apachehttpdemo.consant.Constant.imgUrl;
import static com.example.apachehttpdemo.consant.Constant.postUrl;

/**
 * 使用 Apache 接口实现的 get 请求、post 请求、图片下载 demo。
 */

public class ApacheActivity extends Activity {

    private TextView resultView;
    private ImageView imageView;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Disposable disposable;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultView = findViewById(R.id.resultView);
        imageView = findViewById(R.id.imgeView01);
    }

    public void get(View view){
        disposable = Flowable.just(getUrl)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, String>() {
                   @Override
                   public String apply(String url) {
                       return ApacheHttpUtils.get(url);
                   }
                 })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        resultView.setText(s);
                    }
                })
                .subscribe();
        compositeDisposable.add(disposable);

    }

    public void post(View view){
        disposable = Flowable.just(postUrl)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String url) {
                        return ApacheHttpUtils.post(url);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        resultView.setText(s);
                    }
                })
                .subscribe();
        compositeDisposable.add(disposable);
    }

    public void image(View view){
        disposable = Flowable.just(imgUrl)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String url) {
                        return ApacheHttpUtils.image(url);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                })
                .subscribe();
        compositeDisposable.add(disposable);
    }
}