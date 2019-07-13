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

/**
 * 使用 Apache 接口实现的 get 请求、post 请求、图片下载 demo。
 */

public class HTTPDemoActivity extends Activity {

    private TextView resutlView;
    private ImageView imageView;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Disposable disposable;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resutlView = findViewById(R.id.resultView);
        imageView = findViewById(R.id.imgeView01);
    }

    public void get(View view){
        String path = "http://mock.fcbox.com/mock/515/getRequest";
        disposable = Flowable.just(path)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, String>() {
                   @Override
                   public String apply(String url) {
                       return HttpUtils.get(url);
                   }
                 })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        resutlView.setText(s);
                    }
                })
                .subscribe();
        compositeDisposable.add(disposable);

    }

    public void post(View view){
        String path = "http://mock.fcbox.com/mock/515/postRequest";
        disposable = Flowable.just(path)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String url) {
                        return HttpUtils.post(url);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        resutlView.setText(s);
                    }
                })
                .subscribe();
        compositeDisposable.add(disposable);
    }

    public void image(View view){
        String path = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_" +
                "10000&sec=1563617434&di=d537abb896f6f79c61ffd945e12be12a&imgtype=" +
                "jpg&er=1&src=http%3A%2F%2Fpic3.16pic.com%2F00%2F54%2F91%2F16pic_5491457_b.jpg";
        disposable = Flowable.just(path)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String url) {
                        return HttpUtils.getImage(url);
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