package com.example.xzy.comm.get;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * get 请求 demo
 */
public class UserInformationActivity extends Activity implements HttpGetUtil.HttpCallBackListener{
    private EditText titleText;
    private EditText lengthText;
    String path = "http://mock.fcbox.com/mock/515/getRequest";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Disposable disposable;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleText = findViewById(R.id.title);
        lengthText = findViewById(R.id.length);
    }

    public void save(View v){
        disposable = Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        // get 请求
                        HttpGetUtil.getRequest(path,getParams(),
                                UserInformationActivity.this);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        compositeDisposable.add(disposable);
    }


    private Map<String,String> getParams(){
        String name = titleText.getText().toString();
        String age = lengthText.getText().toString();
        Map<String,String> map = new HashMap<>();
        map.put("name",name);
        map.put("age",age);
        return map;
    }

    @Override
    public void onFinish(String response) {
        disposable = Flowable.just(response)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        Toast.makeText(UserInformationActivity.this,
                                getString(R.string.success)+"--返回值:"+s,
                                Toast.LENGTH_LONG).show();
                    }
                })
                .subscribe();
        compositeDisposable.add(disposable);
    }

    @Override
    public void onError(Exception e) {
        disposable = Flowable.just(e)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
               .doOnNext(new Consumer<Exception>() {
                   @Override
                   public void accept(Exception e) {
                       Toast.makeText(UserInformationActivity.this,
                               getString(R.string.fail)+"--异常信息："+e.getMessage(),
                               Toast.LENGTH_LONG).show();
                   }
               })
                .subscribe();
        compositeDisposable.add(disposable);
    }
}