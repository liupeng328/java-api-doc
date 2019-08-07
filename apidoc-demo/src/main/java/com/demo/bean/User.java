package com.demo.bean;


/**
 * 用户实体类
 */
public class User {

    //多种注释测试 注释的读取是否正确
    /**
     * 编号
     */
    private String id;
    /*
    姓名
     */
    private String name;
    private Integer age;//年龄
    /**
     * 测试对象的自嵌套
     */
    private  User user;
    /**
     * 测试对象的互相嵌套
     */
    private People people;

    public User() {
    }

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }


    public User(String id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public People getPeople() {
        return people;
    }

    public void setPeople(People people) {
        this.people = people;
    }
}
