package io.mykit.db.common.utils;

/**
 * @description 工具类
 */
public class Tool {

    /**
     * 产生随机字符串
     * @param length 字符串的长度
     * @return 随机的字符串
     */
    public static String generateString(int length) {
        if (length < 1)
            length = 6;
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String genStr = "";
        for (int index = 0; index < length; index++) {
            genStr = genStr + str.charAt((int) ((Math.random() * 100) % 26));
        }
        return genStr;
    }
}
