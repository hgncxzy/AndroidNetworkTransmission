package com.example.parsexml;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parsexml.dom.ParseXMLWithDom;
import com.example.parsexml.pull.ParseXMLWithPull;
import com.example.parsexml.sax.ParseXMLWithSax;

import java.util.ArrayList;

/**
 * xml 解析的三种方式实现的 demo。
 * @author xzy
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.pull).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Info> infoArrayList = ParseXMLWithPull.parseXMLFromAssertsWithPull(MainActivity.this,"test.xml");
                if(infoArrayList != null && infoArrayList.size() >0){
                    Toast.makeText(MainActivity.this, infoArrayList.get(0).toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.sax).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Info> infoArrayList = ParseXMLWithSax.parseXMLFromAssertsWithSax(MainActivity.this,"test.xml");
                if(infoArrayList != null && infoArrayList.size() >0){
                    Toast.makeText(MainActivity.this, infoArrayList.get(0).toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.dom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Info> infoArrayList = ParseXMLWithDom.parseXMLFromAssertsWithDom(MainActivity.this,"test.xml");
                if(infoArrayList != null && infoArrayList.size() >0){
                    Toast.makeText(MainActivity.this, infoArrayList.get(0).toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
