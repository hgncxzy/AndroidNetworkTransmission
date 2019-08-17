package com.example.parsejson.bean;

import java.util.List;

public class Object2 {
    /**
     *
     * {
     *   "total": 2,
     *   "success": true,
     *   "appList": [
     *     {
     *       "id": 1,
     *       "name": "小猪"
     *     },
     *     {
     *       "id": 2,
     *       "name": "小猫"
     *     }
     *   ]
     * }
     * **/
    private int total;
    private boolean success;
    private List<App> appList;


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<App> getAppList() {
        return appList;
    }

    public void setAppList(List<App> appList) {
        this.appList = appList;
    }

    @Override
    public String toString() {
        return "Object2{" +
                "total=" + total +
                ", success=" + success +
                ", appList.size=" + appList.size() +
                '}';
    }

    public class App{
        private int id ;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "App{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
