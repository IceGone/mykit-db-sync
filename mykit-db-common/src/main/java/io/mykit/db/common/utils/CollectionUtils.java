package io.mykit.db.common.utils;

import java.util.Map;

/**
 * @description 集合的工具类
 */
public class CollectionUtils {

    public static boolean isEmpty(Map<String, String> map){
        return map == null || map.isEmpty();
    }
}
