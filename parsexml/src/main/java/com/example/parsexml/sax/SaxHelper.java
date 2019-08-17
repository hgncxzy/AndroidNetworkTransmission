package com.example.parsexml.sax;

import android.util.Log;

import com.example.parsexml.Info;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * sax 解析 xml 帮助类。根据 xml 文档的实际格式进行调整。
 * @author xzy
 */
@SuppressWarnings("unused")
public class SaxHelper extends DefaultHandler {
    private Info info;
    private Info.User user;
    private ArrayList<Info> infos;
    //当前解析的元素标签
    private String tagName = null;

    /**
     * 当读取到文档开始标志是触发，通常在这里完成一些初始化操作
     */
    @Override
    public void startDocument() {
        infos = new ArrayList<>();
        Log.i("SAX", "读取到文档头,开始解析xml");
    }


    /**
     * 读到一个开始标签时调用,第二个参数为标签名,最后一个参数为属性数组
     */
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) {
        if (localName.equals("request")) {
            info = new Info();
            user = info.new User();
        }
        this.tagName = localName;
    }


    /**
     * 读到到内容,第一个参数为字符串内容,后面依次为起始位置与长度
     */

    @Override
    public void characters(char[] ch, int start, int length) {
        //判断当前标签是否有效
        if (this.tagName != null) {
            String data = new String(ch, start, length);
            //读取标签中的内容
            switch (this.tagName) {
                case "realName":
                    info.setRealName(data);
                    break;
                case "identityNumber":
                    info.setIdentityNumber(Integer.parseInt(data));
                    break;
                case "phone":
                    info.setPhone(Integer.parseInt(data));
                    break;
                case "value":
                    user.setValue(Integer.parseInt(data));
                    break;
                case "name":
                    user.setName(data);
                    break;
                case "hehe":
                    user.setHehe(data);
                    break;
                case "age":
                    user.setAge(Integer.parseInt(data));
                    break;
            }

        }

    }

    /**
     * 处理元素结束时触发,这里将对象添加到结合中
     */
    @Override
    public void endElement(String uri, String localName, String qName) {
        if (localName.equals("request")) {
            info.setUser(user);
            infos.add(info);
            info = null;
            user = null;
        }
        this.tagName = null;
    }

    /**
     * 读取到文档结尾时触发，
     */
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        Log.i("SAX", "读取到文档尾,xml解析结束");
    }

    //获取infos集合
    public ArrayList<Info> getInfos() {
        return infos;
    }

}