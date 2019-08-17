package com.example.parsexml.pull;

import android.content.Context;
import android.util.Xml;

import com.example.parsexml.Info;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

/**
 * pull 解析 xml
 * @author xzy
 */
@SuppressWarnings("unused")
public class ParseXMLWithPull {

    /**
     * 解析 assert 目录下面的 xml 文件。
     * 用法举例:
     * 比如要读取 assets/test.xml)，你需要这样做：
     * 1. 如果没有 assets 文件夹，请先新建，其中 assets 与 java 和 res 在同一个层次。
     * 2. 调用该方法，传入 test.xml 文件名，记得包含后缀
     * 3. 针对 xml 的具体的字段，修改该方法中对应的字段，以获取到对应的 key 保存的 object 对象
     * 4. 将 object 对象取出（通过 map#get）,并转换为具体的类型(String,Int...)
     *
     * @param context  上下文
     * @param fileName xml 的文件名 包含后缀
     * @return Map<String, Object>
     */
    public static ArrayList<Info> parseXMLFromAssertsWithPull(Context context, String fileName) {
        ArrayList<Info> infoArrayList = new ArrayList<>();
        Info info = null;
        Info.User user  = null;
        try {
            try {
                InputStream is = context.getResources().getAssets().open(fileName);
                XmlPullParser xmlParser = Xml.newPullParser();
                try {
                    xmlParser.setInput(is, "UTF-8");
                    int event = xmlParser.getEventType();
                    while (event != XmlPullParser.END_DOCUMENT) {

                        switch (event) {
                            case XmlPullParser.START_DOCUMENT:
                                info = new Info();
                                user = info.new User();
                                break;
                            case XmlPullParser.START_TAG:
                               String parserName = xmlParser.getName();
                                if ("realName".equals(parserName)) {
                                    String realName = xmlParser.nextText();
                                    Objects.requireNonNull(info).setRealName(realName);
                                } else if ("identityNumber".equals(parserName)) {
                                    String identityNumber = xmlParser.nextText();
                                    Objects.requireNonNull(info).setIdentityNumber(Integer.parseInt(identityNumber));
                                } else if ("phone".equals(parserName)) {
                                    String phone = xmlParser.nextText();
                                   Objects.requireNonNull(info).setPhone(Integer.valueOf(phone));
                                } else if ("value".equals(parserName)) {
                                    String value = xmlParser.nextText();
                                   Objects.requireNonNull(user).setValue(Integer.valueOf(value));
                                } else if ("name".equals(parserName)) {
                                    String name = xmlParser.nextText();
                                    Objects.requireNonNull(user).setName(name);
                                } else if ("hehe".equals(parserName)) {
                                    String hehe = xmlParser.nextText();
                                   Objects.requireNonNull(user).setHehe(hehe);
                                } else if ("age".equals(parserName)) {
                                    String age = xmlParser.nextText();
                                   Objects.requireNonNull(user).setAge(Integer.valueOf(age));
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                Objects.requireNonNull(info).setUser(user);
                                infoArrayList.add(info);
                                break;
                        }

                        event = xmlParser.next();
                    }
                    is.close();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infoArrayList;
    }
}
