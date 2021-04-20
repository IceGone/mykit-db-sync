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

    /***
    * @Description: 从配置文件里获取 表映射关系
    * @Param: [configFile]
    * @return: io.mykit.db.transfer.conf.ConfType
    * @Author: bjchen
    * @Date: 2021/4/20
    */
    @Override
    public ConfType init(String configFile) {
        //获取除数据库连接之外的同步表配置
        getDBInfo(configFile);

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
            Element jobs = root.element(MykitDbSyncConstants.NODE_JOBS);
            // 遍历job即同步的表
            for (@SuppressWarnings("rawtypes")
                 Iterator it = jobs.elementIterator(MykitDbSyncConstants.NODE_JOB); it.hasNext();) {
                JobInfo jobInfo = (JobInfo) elementInObject((Element) it.next(), new JobInfo());
                //源数据表的字段配置信息不为空
                jobList.add(jobInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MykitDbSyncException(e.getMessage());
        }
        return this;
    }


}
