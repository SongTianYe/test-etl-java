package com.sty.transfor;

import static com.sty.constant.EtcConstant.DELIMITER;

/**
 * @Author: SongTianYe
 * @Description: 转换器类 须在yaml中配置类名及方法名（public static）
 * @Date: 2022/11/15 14:33
 */

public class Converter {

    public static String test(String in) {
        return "hello my Converter";
    }

    /**
     * 简单例子根据实际开发定制
     * @param param 入参（）
     * @return
     */
    public static String testCreateRowKey(String... param) {
        return String.join(DELIMITER, param);
    }

}
