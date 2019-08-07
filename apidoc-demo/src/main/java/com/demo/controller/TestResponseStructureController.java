package com.demo.controller;

import com.apidoc.annotation.Api;
import com.demo.bean.Result;
import com.demo.util.CodeUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Map;

/**
 * 测试不同类型/结构的响应
 */
@Api("测试不同类型/结构的响应")
@RestController
@RequestMapping("/testResponseStructure")
public class TestResponseStructureController {

    //json响应数据
    @RequestMapping(value = "/json")
    public Result josn() {
        return Result.success();
    }

    //二进制响应数据
    @GetMapping(value = "/blob")
    public void getPictureCode(HttpServletRequest request, HttpServletResponse resp) {
        // 调用工具类生成的验证码和验证码图片
        Map<String, Object> codeMap = CodeUtil.generateCodeAndImage();
        String code = codeMap.get("code").toString();

        //缓存验证码
        HttpSession session = request.getSession();
        session.setAttribute("pictureCode", code);

        // 禁止图像缓存
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", -1);
        resp.setContentType("image/jpeg");
        // 将图像输出到Servlet输出流中。
        ServletOutputStream sos;
        try {
            sos = resp.getOutputStream();
            ImageIO.write((RenderedImage) codeMap.get("image"), "jpeg", sos);
            sos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
