package com.example.parsexml.dom;

import android.content.Context;

import com.example.parsexml.Info;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * dom 解析 xml。
 * @author xzy
 */
@SuppressWarnings("unused")
public class ParseXMLWithDom {
    public static ArrayList<Info> parseXMLFromAssertsWithDom(Context context,String fileName) {
        ArrayList<Info> infoArrayList = new ArrayList<>();
        try {
            //①获得DOM解析器的工厂示例:
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            //②从Dom工厂中获得dom解析器
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            //③把要解析的xml文件读入Dom解析器
            Document doc = dbBuilder.parse(context.getResources().getAssets().open(fileName));
            //④得到文档中名称为person的元素的结点列表
            NodeList nList = doc.getElementsByTagName("request");
            //⑤遍历该集合,显示集合中的元素以及子元素的名字
            for(int i = 0;i < nList.getLength();i++) {
                //先从Person元素开始解析
                Element infoElement = (Element) nList.item(i);
                Info info = new Info();
                Info.User user = info.new User();
                NodeList childNoList = infoElement.getChildNodes();
                for(int j = 0;j < childNoList.getLength();j++) {
                    Node childNode = childNoList.item(j);
                    //判断子note类型是否为元素Note
                    if(childNode.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element childElement = (Element) childNode;
                        String nodeName = childElement.getNodeName();
                        String nodeValue = childElement.getFirstChild().getNodeValue();
                        if("realName".equals(nodeName)){
                            info.setRealName(nodeValue);
                        }else if("identityNumber".equals(nodeName)){
                            info.setIdentityNumber(Integer.valueOf(nodeValue));
                        }else if ("phone".equals(nodeName)){
                            info.setPhone(Integer.valueOf(nodeValue));
                        }else if("user".equals(nodeName)){
                            NodeList nodeList1 = childNode.getChildNodes();
                            for(int k = 0;k < nodeList1.getLength();k++) {
                                Node childNode1 = nodeList1.item(k);
                                //判断子node类型是否为元素Node
                                if (childNode1.getNodeType() == Node.ELEMENT_NODE) {
                                    Element childElement1 = (Element) childNode1;
                                    String nodeName1 = childElement1.getNodeName();
                                    String nodeValue1 = childElement1.getFirstChild().getNodeValue();
                                    if("value".equals(nodeName1)){
                                        user.setValue(Integer.valueOf(nodeValue1));
                                    }else if("name".equals(nodeName1)){
                                        user.setName(nodeValue1);
                                    }else if("aa".equals(nodeName1)){
                                        NodeList nodeList2 = childNode1.getChildNodes();
                                        for(int r = 0;r < nodeList2.getLength();r++) {
                                            Node childNode2 = nodeList2.item(r);
                                            if (childNode2.getNodeType() == Node.ELEMENT_NODE) {
                                                Element childElement2 = (Element) childNode2;
                                                String nodeName2 = childElement2.getNodeName();
                                                String nodeValue2 = childElement2.getFirstChild().getNodeValue();
                                                if("hehe".equals(nodeName2)){
                                                    user.setHehe(nodeValue2);
                                                }
                                            }
                                        }
                                    }else if("age".equals(nodeName1)){
                                        user.setAge(Integer.valueOf(nodeValue1));
                                    }
                                }
                            }

                        }
                    }
                }
                info.setUser(user);
                infoArrayList.add(info);
            }
        } catch (Exception e) {e.printStackTrace();}
        return infoArrayList;
    }
}
