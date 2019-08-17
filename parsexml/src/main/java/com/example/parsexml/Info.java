package com.example.parsexml;

/**
 * 根据 xml 提炼的 bean
 * @author xzy
 */
@SuppressWarnings("unused")
public class Info {
    private String realName;
    private int identityNumber;
    private int phone;
    private User user;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(int identityNumber) {
        this.identityNumber = identityNumber;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public String toString() {
        return "Info{" +
                "realName='" + realName + '\'' +
                ", identityNumber=" + identityNumber +
                ", phone=" + phone +
                ", user=" + user.toString() +
                '}';
    }

    public class User{
        private int value;
        private String name;
        private String hehe;
        private int age;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHehe() {
            return hehe;
        }

        public void setHehe(String hehe) {
            this.hehe = hehe;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "value=" + value +
                    ", name='" + name + '\'' +
                    ", hehe='" + hehe + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
