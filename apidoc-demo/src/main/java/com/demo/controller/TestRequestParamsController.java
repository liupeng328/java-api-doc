package com.demo.controller;

import com.apidoc.annotation.Api;
import com.demo.bean.Result;
import com.demo.bean.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用以测试请求参数是否正确
 */
@Api("测试请求参数")
@RestController
@RequestMapping("/testRequestParams")
public class TestRequestParamsController {

    //没有参数
    @GetMapping(value = "/noParams")
    public Result noParams() {
        return Result.success();
    }

    //一个参数 基本类型
    @PostMapping(value = "/oneInt")
    public Result oneInt(@RequestBody int i) {
        return Result.success(i);
    }

    //一个参数 自定义对象
    @PostMapping(value = "/oneUser")
    public Result oneUser(@RequestBody User user) {
        return Result.success(user);
    }



    //基本参数 除对象外的参数 暂未支持from表单需要再加
    @PostMapping(value = "/baseParams")
    public Result baseParams( byte byte1,
                             short short1,
                             int int1,
                             long long1,
                             boolean boolean1,
                             char char1,

                             Byte byte2,
                             Short short2,
                             Integer integer,
                             Long long2,
                             Boolean boolean2,
                             Character character,

                             String string,
                             MultipartFile file,
                             Date date) {
        return Result.success();
    }

    //object参数
    @PostMapping(value = "/object")
    public Result object(@RequestBody Object obj) {
        return Result.success();
    }

    //自定义参数
    @PostMapping(value = "/user")
    public Result user(@RequestBody User user) {
        return Result.success();
    }

    /**
     *  对象中嵌套对象
     */
    @PostMapping(value = "/userSelf")
    public Result userSelf(@RequestBody User user) {
        return Result.success();
    }

    //泛型list
    @PostMapping(value = "/list")
    public Result list(@RequestBody List<User> list) {
        return Result.success();
    }

    //泛型嵌套泛型list  //todo 暂不实现
//    @PostMapping(value = "/listlist")
//    public Result listlist(@RequestBody List<List<User>> listList) {
//        return Result.success();
//    }

    //泛型Set
    @PostMapping(value = "/setint")
    public Result setint(@RequestBody Set<Integer> set) {
        return Result.success();
    }

    //泛型Set
    @PostMapping(value = "/set")
    public Result set(@RequestBody Set<User> set) {
        return Result.success();
    }

    //泛型map
    @PostMapping(value = "/map") //todo 暂不实现
    public Result map(@RequestBody Map<String, User> map) {
        return Result.success();
    }

    //数组 基本类型
    @PostMapping(value = "/array")
    public Result array(@RequestBody Integer[] ints) {
        return Result.success();
    }

    //int类型数组
    @PostMapping(value = "/intArray")
    public Result intArray(@RequestBody int[] ints) {
        return Result.success(ints);
    }


    //多维数组 基本类型
    @PostMapping(value = "/arrays")
    public Result arrays(@RequestBody String[][] strings) {
        return Result.success(strings);
    }

    //数组 对象类型
    @PostMapping(value = "/arrayObject")
    public Result arrayObject(@RequestBody User[] users) {
        return Result.success();
    }

    //多维数组 对象类型
    @PostMapping(value = "/arrayObjects")
    public Result arrayObjects(@RequestBody User[][] users) {
        return Result.success();
    }

    //文件
    @PostMapping(value = "/file")
    public Result file(MultipartFile file) {
        return Result.success();
    }

    //多文件
    @PostMapping(value = "/files")
    public Result files(MultipartFile[] files) {
        return Result.success();
    }



}
