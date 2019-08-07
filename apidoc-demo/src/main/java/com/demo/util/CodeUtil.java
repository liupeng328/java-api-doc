package com.demo.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Description: 获取关键字
 * @Author:
 * @CreateDate: 2018/3/19 14:34
 */
public class CodeUtil {

    /** 定义图片的width */
    private static final int WIDTH = 90;
    /** 定义图片的height */
    private static final int HEIGHT = 20;
    /** 定义图片上显示验证码的个数 */
    private static final int CODE_COUNT = 4;
    /** 定义图片上验证码的距左上点的X距离 */
    private static final int CODE_X = 15;
    /** 定义图片上字符距左上点的Y距离 */
    private static final int CODE_Y = 16;
    /** 定义图片上字符字体高度 */
    private static final int FONT_HEIGHT = 18;
    /** 定义图片上干扰线数量 */
    private static final int INTERFERE_NUM = 40;
    /** 定义图片上字符范围 */
    private static final char[] CODE_SEQUENCE = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    /**
     * 生成一个map集合
     * code为生成的验证码
     * codePic为生成的验证码BufferedImage对象
     * @return
     */
    public static Map<String,Object> generateCodeAndImage() {
        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics gd = buffImg.getGraphics();
        // 创建一个随机数生成器类
        Random random = new Random();
        // 将图像填充为白色
        gd.setColor(Color.WHITE);
        gd.fillRect(0, 0, WIDTH, HEIGHT);
        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("fixedsys", Font.BOLD, FONT_HEIGHT);
        // 设置字体。
        gd.setFont(font);
        // 画边框。
        gd.setColor(Color.BLACK);
        gd.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);
        // 随机产生40条干扰线，使图象中的认证码不易被其它程序探测到。
        gd.setColor(Color.BLACK);
        for (int i = 0; i < INTERFERE_NUM; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            gd.drawLine(x, y, x + xl, y + yl);
        }
        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuffer randomCode = new StringBuffer();
        int red = 0, green = 0, blue = 0;
        // 随机产生codeCount数字的验证码。
        for (int i = 0; i < CODE_COUNT; i++) {
            // 得到随机产生的验证码数字。
            String code = String.valueOf(CODE_SEQUENCE[random.nextInt(36)]);
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            // 用随机产生的颜色将验证码绘制到图像中。
            gd.setColor(new Color(red, green, blue));
            gd.drawString(code, (i + 1) * CODE_X, CODE_Y);
            // 将产生的四个随机数组合在一起。
            randomCode.append(code);
        }
        Map<String,Object> map  =new HashMap<String,Object>(2);
        //存放验证码
        map.put("code", randomCode);
        //存放生成的验证码BufferedImage对象
        map.put("image", buffImg);
        return map;
    }








}
