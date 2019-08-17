package com.example.parsejson;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parsejson.bean.Object1;
import com.example.parsejson.bean.Object2;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.parseJsonArray).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                       final List<Object1> list = JsonUtils.getJSONArray("http://yapi.demo.qunar.com/mock/87945/json1");
                       if(list != null && list.size() > 0){
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   String result = "";
                                   for(int i =0;i<list.size();i++){
                                       result += list.get(i).toString();
                                   }
                                   Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
                               }
                           });
                       }
                    }
                });
                thread.start();
            }
        });

        findViewById(R.id.parseJsonObject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final List<Object2> list = JsonUtils.getJSONObject("http://yapi.demo.qunar.com/mock/87945/json2");
                        if(list != null && list.size() > 0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Object2 object2 = list.get(0);
                                    Toast.makeText(MainActivity.this,JsonUtils.toJson(object2),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                thread.start();
            }
        });

        findViewById(R.id.parseComplexObject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final List<Map<String,String>> list = JsonUtils.getComplexJSON("http://yapi.demo.qunar.com/mock/87945/json3");
                        if(list != null && list.size() > 0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,list.size()+"",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                thread.start();
            }
        });
    }
}
