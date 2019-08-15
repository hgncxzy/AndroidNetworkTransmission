package com.xzy.androidhttp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xzy.androidhttp.consant.Constant.getUrl;
import static com.xzy.androidhttp.consant.Constant.imgUrl;
import static com.xzy.androidhttp.consant.Constant.postUrl;

public class MainActivity extends Activity implements AndroidHttpClientUtils.HttpCallBackListener {
    private Button get, post, getImg;
    private ImageView imageView;
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
        getImg = findViewById(R.id.getImg);
        imageView = findViewById(R.id.image);
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
        getImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImg();
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
                        AndroidHttpClientUtils.get(getUrl, MainActivity.this);
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
                        Map<String, String> map = new HashMap<>();
                        map.put("name", "xzy");
                        AndroidHttpClientUtils.post(postUrl, map, MainActivity.this);
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

    public void getImg() {
        disposable = Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, Bitmap>() {
                    @Override
                    public Bitmap apply(Integer integer) {
                        return AndroidHttpClientUtils.image(imgUrl);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                })
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) {

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
