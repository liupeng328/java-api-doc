package com.demo.controller;

import com.apidoc.annotation.Api;
import com.apidoc.utis.JsonUtil;
import com.demo.bean.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 用以做覆盖测试，测试响应参数类型
 */
@Api("测试响应参数")
@RestController
@RequestMapping("/testResponseParams")
public class TestResponseParamsController {

    //没有响应值
    @GetMapping(value = "/noResponse")
    public void noParams() {
    }

    //响应一个整数
    @GetMapping(value = "/oneInt")
    public int oneInt() {
        return 1;
    }

    //响应一个小数
    @GetMapping(value = "/oneFloat")
    public Float oneFloat() {
        return 1.1F;
    }

    //响应一个boolean
    @GetMapping(value = "/oneBoolean")
    public boolean oneBoolean() {
        return true;
    }

    //响应一个字符串
    @GetMapping(value = "/oneString")
    public String oneString() {
        return "hello apidoc";
    }

    //响应一个Object
    @GetMapping(value = "/oneObject")
    public Object manyBase() {
        return new Object();
    }

    //响应一个数组
    @GetMapping(value = "/oneArray")
    public int[] oneArray() {
        return new int[]{1, 3};
    }

    //响应一个多维数组
    @GetMapping(value = "/oneManyArray")
    public int[][] oneManyArray() {
        return new int[][]{{1, 2}, {5}};
    }

    //响应一个自定义对象组成的多维数组
    @GetMapping(value = "/oneManyObjectArray")
    public User[][] oneManyObjectArray() {
        return null;
    }
    //响应一个自定义对象
    @GetMapping(value = "/oneUser")
    public Result oneUser() {
        return Result.success();
    }

    /**
     * 响应一个对象中嵌套对象的自定义对象
     */
    @GetMapping(value = "/userSelf")
    public User userSelf() {
        User user = new User();
        People people = new People();
        people.setName("人们");
        user.setName("用户");
        user.setPeople(people);
        //此对象有两层user->people
        return user;
    }

    //泛型 Collection
    @GetMapping(value = "/collection")
    public Collection<String> collection() {
        List<String> list = new ArrayList<>();
        list.add("test");
        list.add("测试");
        return list;
    }

    //泛型 Map
    @GetMapping(value = "/map")
    public Map<Integer, String> map() {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "test");
        map.put(2, "测试");
        return map;
    }

    //自定义泛型Result<User>
    @GetMapping(value = "/gen")
    public Result<User> gen() {
        return null;
    }
    //自定义泛型 Result<String>
    @GetMapping(value = "/genstr")
    public Result<String> genstr() {
        return null;
    }

    //泛型嵌套泛型
    @GetMapping(value = "/collectionAndMap")
    public Collection<Map<Integer, String>> collectionAndMap() {
        List<Map<Integer, String>> list = new ArrayList<>();
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "test");
        map.put(2, "测试");
        list.add(map);
        return list;
    }

    //泛型嵌套泛型的分页
    public Result<PageResult<User>> page(Page<People> people) {
        return null;
    }

    public static void main(String[] args) {

        Type[] typeName = ((ParameterizedType)Result.class.getGenericSuperclass()).getActualTypeArguments();
        System.out.println(typeName);
    }

}
