package io.mykit.db.common.build;

import io.mykit.db.common.constants.MykitDbSyncConstants;
import org.dom4j.Element;

import java.lang.reflect.Field;

/**
 * @description 基础构建类
 */
public class BaseBuilder {

    /**
     * 解析e中的元素，将数据填充到o中
     * @param e 解析的XML Element对象
     * @param o 存放解析后的XML Element对象
     * @return 存放有解析后数据的Object
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    protected Object elementInObject(Element e, Object o) throws IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = o.getClass();
        while (clazz != null){
            Field[] fields = clazz.getDeclaredFields();
            for (int index = 0; index < fields.length; index++) {
                Field item = fields[index];
                //当前字段不是serialVersionUID，同时当前字段不包含serialVersionUID
                if (!MykitDbSyncConstants.FIELD_SERIALVERSIONUID.equals(item.getName()) && !item.getName().contains(MykitDbSyncConstants.FIELD_SERIALVERSIONUID)){
                    item.setAccessible(true);
                    item.set(o, e.element(item.getName()).getTextTrim());
                }
            }
            clazz = clazz.getSuperclass();
        }
        return o;
    }
}
