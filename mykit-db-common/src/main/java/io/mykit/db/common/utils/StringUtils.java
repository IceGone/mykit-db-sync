package io.mykit.db.common.utils;

import java.util.Arrays;
import java.util.List;

/**
 * @description 字符串工具类
 */
public class StringUtils {

    public static boolean isEmpty(String str){
        return str == null || "".equals(str.trim());
    }
    /***
    * @Description: 根据 字符串以及分割符获取对应的列表list
    */
    public static List<String> getListByStringSplit(String str,String split){
       return Arrays.asList(str.trim().split(split));
    }
    /***
    * @Description: 根据 字符串以及分割符获取对应的数组array
    */
    public static String[] getArrayByStringSplit(String str,String split){
       return str.trim().split(split);
    }
}
