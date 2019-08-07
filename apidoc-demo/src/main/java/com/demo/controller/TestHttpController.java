package com.demo.controller;

import com.apidoc.annotation.Api;
import com.demo.bean.Result;
import org.springframework.web.bind.annotation.*;

/**
 * 用以测试HTTP请求动作
 */
@Api("测试HTTP请求动作")
@RestController
@RequestMapping("/testHttp")
public class TestHttpController {

    //未明确指明请求方式 默认全部可以 all
    @RequestMapping(value = "/all")
    public Result all() {
        return Result.success();
    }

    /**
     * get方式
     *
     * @return
     */
    @GetMapping(value = "/get")
    public Result get() {
        return Result.success();
    }

    /*
    post方式
     */
    @PostMapping(value = "/post")
    public Result post() {
        return Result.success();
    }

    /**
     * put方式
     *
     * @return
     */
    @PutMapping(value = "/put")
    public Result put() {
        return Result.success();
    }

    /**
     * delete方式
     *
     * @return
     */
    @DeleteMapping(value = "/delete")
    public Result delete() {
        return Result.success();
    }

    //多种方式并存
    @RequestMapping(value = "/both", method = {RequestMethod.GET, RequestMethod.POST})
    public Result both() {
        return Result.success();
    }


}
