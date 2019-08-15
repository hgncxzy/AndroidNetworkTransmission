package com.xzy.androidhttp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends Activity implements AndroidHttpClientUtils.HttpCallBackListener {
    private Button get,post;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Disposable disposable;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = findViewById(R.id.result);
        get = findViewById(R.id.get);
        post = findViewById(R.id.post);

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getReq();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postReq();
            }
        });
    }


    public void getReq() {
        disposable = Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        // get 请求
                        AndroidHttpClientUtils.get("http://www.baidu.com", MainActivity.this);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e("xzy", Objects.requireNonNull(throwable.getMessage()));
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void postReq() {
        disposable = Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        // post 请求
                        Map<String,String> map = new HashMap<>();
                        map.put("name","xzy");
                        AndroidHttpClientUtils.post("http://www.baidu.com", map, MainActivity.this);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e("xzy", Objects.requireNonNull(throwable.getMessage()));
                    }
                });
        compositeDisposable.add(disposable);
    }


    @Override
    public void onFinish(InputStream response) {
        disposable = Flowable.just(response)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream s) throws IOException {
                        result.setText(AndroidHttpClientUtils.inputStream2String(s));
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
