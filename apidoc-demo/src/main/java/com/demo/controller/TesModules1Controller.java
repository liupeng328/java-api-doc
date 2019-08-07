package com.demo.controller;

import com.apidoc.annotation.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试一个模块由多个类组成
 */
@Api("测试一个模块由多个类组成")
@RestController
@RequestMapping("/testModules1")
public class TesModules1Controller {

    //第一个类的接口
    @GetMapping(value = "/action")
    public void action() {
    }


}
