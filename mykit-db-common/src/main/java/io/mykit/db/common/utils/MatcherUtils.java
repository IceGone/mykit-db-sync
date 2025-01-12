package io.mykit.db.common.utils;

import java.util.regex.Pattern;

/**
 * @description 正则匹配规则
 */
public class MatcherUtils {

    /**
     * 验证时间字符串格式输入是否正确
     */
    public static boolean isDateTime(String datetime){
        Pattern p = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1][0-9])|([2][0-4]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        return p.matcher(datetime).matches();
    }


    public static void main(String[] args){
        System.out.println(isDateTime("2016-5-2 08:02:02"));
        System.out.println(isDateTime("2016-02-29 08:02:02.0"));
        System.out.println(isDateTime("2015-02-28 08:02:02"));
        System.out.println(isDateTime("2016-02-02 082:02"));
    }
}
