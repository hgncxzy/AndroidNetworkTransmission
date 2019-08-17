package com.example.parsejson.bean;

public class Object1 {
    /**
     *
     * [
     *   {
     *     "stuNo": 100,
     *     "name": "小明"
     *   },
     *   {
     *     "stuNo": 101,
     *     "name": "小张"
     *   }
     * ]
     * **/
    private int stuNo;
    private String name;

    public int getStuNo() {
        return stuNo;
    }

    public void setStuNo(int stuNo) {
        this.stuNo = stuNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Object1{" +
                "stuNo=" + stuNo +
                ", name='" + name + '\'' +
                '}';
    }
}
