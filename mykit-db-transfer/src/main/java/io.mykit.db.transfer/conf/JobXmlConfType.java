package io.mykit.db.transfer.conf;

import io.mykit.db.common.constants.MykitDbSyncConstants;
import io.mykit.db.common.exception.MykitDbSyncException;
import io.mykit.db.common.utils.StringUtils;
import io.mykit.db.transfer.entity.DBInfo;
import io.mykit.db.transfer.entity.JobInfo;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @program: mykit-db-sync
 * @description: 通过job.xml获取同步的配置信息
 * @author: bjchen
 * @create: 2021-04-14
 **/
public class JobXmlConfType extends AbstractConfType{
    public JobXmlConfType(){
        super();
    }
    /**
     * 创建DBSyncBuilder对象
     * @return DBSyncBuilder对象
     */
    public static JobXmlConfType builder(){
        return new JobXmlConfType();
    }

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

    @Override
    public ConfType init(String configFile) {
        srcDb = new DBInfo();
        destDb = new DBInfo();
        jobList = new ArrayList<JobInfo>();
        SAXReader reader = new SAXReader();
        try {
            Element root = null;
            // 读取xml的配置文件名，并获取其里面的节点
            if(StringUtils.isEmpty(configFile)){
                root = reader.read(MykitDbSyncConstants.JOB_CONFIG_FILE).getRootElement();
            }else{
                root = reader.read(new File(configFile)).getRootElement();
            }
            Element src = root.element(MykitDbSyncConstants.NODE_SOURCE);
            Element dest = root.element(MykitDbSyncConstants.NODE_DEST);
            Element jobs = root.element(MykitDbSyncConstants.NODE_JOBS);
            // 遍历job即同步的表
            for (@SuppressWarnings("rawtypes")
                 Iterator it = jobs.elementIterator(MykitDbSyncConstants.NODE_JOB); it.hasNext();) {
                JobInfo jobInfo = (JobInfo) elementInObject((Element) it.next(), new JobInfo());
                //源数据表的字段配置信息不为空
                jobList.add(jobInfo);
            }
            //
            elementInObject(src, srcDb);
            elementInObject(dest, destDb);
            code = root.element(MykitDbSyncConstants.NODE_CODE).getTextTrim();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MykitDbSyncException(e.getMessage());
        }
        return this;
    }


}
