package com.demo.controller;

import com.apidoc.annotation.Api;
import com.demo.bean.Result;
import com.demo.bean.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 测试不同类型/结构的请求
 */
@Api("测试不同类型/结构的请求")
@RestController
@RequestMapping("/testRequestParamStructure")
public class TestRequestParamStructureController {

    //url 无参数
    @GetMapping(value = "/url")
    public Result url() {
        return Result.success();
    }

    //url 一个参数
    @GetMapping(value = "/urlParam")
    public Result urlParam(int a) {
        return Result.success();
    }


    //url 多个参数
    @GetMapping(value = "/urlParams")
    public Result urlParams(int a, String b) {
        return Result.success();
    }


    ////uri 一个参数
    @GetMapping(value = "/uri/{a}")
    public Result uri(@PathVariable("a") int a) {
        return Result.success();
    }

    //uri 多参数
    @GetMapping(value = "/uri/{a}/{b}")
    public Result uriParams(@PathVariable("a") int a, @PathVariable int b) {
        return Result.success();
    }

    //json参数
    @PostMapping(value = "/json")
    public Result json(@RequestBody User user) {
        return Result.success();
    }

    //文件上传
    @PostMapping(value = "/file")
    public Result file(MultipartFile file) {
        return Result.success(file.getOriginalFilename());
    }

    //多文件上传
    @PostMapping(value = "/files")
    public Result files(MultipartFile[] files) {
        return Result.success(files.length);
    }

}
