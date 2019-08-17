package com.example.parsexml.sax;

import android.content.Context;

import com.example.parsexml.Info;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * sax 解析 xml 工具类。
 * @author xzy
 */
@SuppressWarnings("unused")
public class ParseXMLWithSax {

    public static ArrayList<Info> parseXMLFromAssertsWithSax(Context context,String fileName){
        //获取文件资源建立输入流对象
        InputStream is = null;
        try {
            is = context.getResources().getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //①创建XML解析处理器
        SaxHelper saxHelper = new SaxHelper();
        //②得到SAX解析工厂
        SAXParserFactory factory = SAXParserFactory.newInstance();
        //③创建SAX解析器
        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        /*④将xml解析处理器分配给解析器,对文档进行解析,将事件发送给处理器*/
        try {
            parser.parse(is, saxHelper);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return saxHelper.getInfos();
    }
}
