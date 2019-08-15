package com.example.xzy.comm.get;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 从网络下载一张图片的 demo。使用的是标准的 java 接口：java.net。
 */
public class ImageDownloadActivity extends AppCompatActivity {

    private String imgUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_" +
            "10000&sec=1563017808711&di=8288d54cf1c44bb0e7ebabc027afc3cf&imgtype=0&src=" +
            "http%3A%2F%2Fpic3.16pic.com%2F00%2F54%2F91%2F16pic_5491457_b.jpg";

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ImageView mDownloadImageIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_img_download);

        EditText mImgUrlEt = findViewById(R.id.imgUrl);
        mImgUrlEt.setText(imgUrl);
        mDownloadImageIv = findViewById(R.id.image);
    }

    public  void handle(View view){
        Disposable disposable = Flowable.just(imgUrl)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String imgUrl) throws Exception {
                        return ImageService.getImage(imgUrl);
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
                        Toast.makeText(ImageDownloadActivity.this
                                , "下载异常:" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        compositeDisposable.add(disposable);
    }
}
