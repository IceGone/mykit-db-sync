package io.mykit.db.transfer.conf;

import io.mykit.db.common.constants.MykitDbSyncConstants;
import io.mykit.db.common.db.DbConnection;
import io.mykit.db.common.exception.MykitDbSyncException;
import io.mykit.db.common.utils.StringUtils;
import io.mykit.db.transfer.entity.DBInfo;
import io.mykit.db.transfer.entity.JobInfo;
import io.mykit.db.transfer.task.JobTask;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.util.ElementScanner6;
import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import static io.mykit.db.common.constants.CharacterConstants.*;
import static io.mykit.db.common.constants.MykitDbSyncConstants.SQL_SELECT_END;
import static io.mykit.db.common.constants.MykitDbSyncConstants.SQL_SELECT_START;
import static io.mykit.db.common.utils.DbUtil.qr;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @program: mykit-db-sync
 * @description: 抽象类：基础数据填充
 * @author: bjchen
 * @create: 2021-04-14
 **/
public abstract class AbstractConfType extends DbConnection implements ConfType  {
    private static Logger logger = LoggerFactory.getLogger(AbstractConfType.class);


    protected DBInfo srcDb;
    protected DBInfo destDb;
    protected List<JobInfo> jobList;
    protected String code;

    protected AbstractConfType(){}


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

    /***
    * @Description: 从job.xml 获取 数据库 连接信息
    */
    public ConfType getDBInfo(String configFile){
        srcDb = new DBInfo();
        destDb = new DBInfo();
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
            // 解析e中的元素，将数据填充到o中
            elementInObject(src, srcDb);
            elementInObject(dest, destDb);
            code = root.element(MykitDbSyncConstants.NODE_CODE).getTextTrim();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MykitDbSyncException(e.getMessage());
        }
        return this;
    }

    /***
     * @Description: 由于lf_syn_job_conf字段较多，为简化配置 ，此处将各字段进行预处理
     * @Param: [con, jobInfo]
     * @Param: [数据库连接, 任务计划信息]
     * @return: io.mykit.db.transfer.entity.JobInfo
     * @Author: bjchen
     * @Date: 2021/4/14
     */
    @Override
    public List<JobInfo> reformJobInfo(Connection con, List<JobInfo> jobInfos) {
        //所有字段
        List<String> allColumn =null;
        for(JobInfo jobInfo:jobInfos){
            try{
                allColumn = qr.query(con,MykitDbSyncConstants.getColumnNameSql(MykitDbSyncConstants.TYPE_DB_MYSQL,jobInfo.getDestTable()),new ColumnListHandler<>(1));
            }catch (Exception e){
                logger.error("获取lf_syn_job_conf表字段异常"+e.getMessage());
            }
            //单独更新
            updateJobInfo(jobInfo,allColumn);
        }
        //jobInfo 各个字段并返回
        return  jobInfos;
    }

    /***
     * @Description: 更新各sql
     * @Param: [jobInfo, allColumn]
     * @return: io.mykit.db.transfer.entity.JobInfo
     * @Author: bjchen
     * @Date: 2021/4/20
     */
    private void updateJobInfo(JobInfo jobInfo, List<String> allColumn){
            //获取主键
            List<String> destTableKeyList = Arrays.asList(jobInfo.getDestTableKey().trim().split("\\,"));
            //更新srcSql (select id, avatar, email, name, password, username from user)
            jobInfo.setSrcSql(reforeJobInfoParam(jobInfo.getSrcSql(),jobInfo.getDestTable(),allColumn,destTableKeyList,SQL_SELECT_START,SQL_SELECT_END,false));
            //更新srcTableFields (id, avatar, email, name, password, username)
            jobInfo.setSrcTableFields(reforeJobInfoParam(jobInfo.getSrcTableFields(),jobInfo.getDestTable(),allColumn,destTableKeyList,CHARACTER_EMPTY_STR,CHARACTER_EMPTY_STR,false));
            //更新destTableFields (id, avatar, email, name, password, username)
            jobInfo.setDestTableFields(reforeJobInfoParam(jobInfo.getDestTableFields(),jobInfo.getDestTable(),allColumn,destTableKeyList,CHARACTER_EMPTY_STR,CHARACTER_EMPTY_STR,false));
            //更新destTableUpdate (avatar, email, name, password, username)
            jobInfo.setDestTableUpdate(reforeJobInfoParam(jobInfo.getDestTableUpdate(),jobInfo.getDestTable(),allColumn,destTableKeyList,CHARACTER_EMPTY_STR,CHARACTER_EMPTY_STR,true));
    }

    /***
     * @Description: 判断是否需要重置 参数为 * 重置，否则不需要
     * @Param: [param]
     * @return: boolean
     * @Author: bjchen
     * @Date: 2021/4/14
     */
    private boolean isNeedReform(String param) {
        return "*".equals(param.trim());
    }

    /***
     * @Description: 判断是否需要重置sql:只有*号且 字段大于0
     * @Param: [jobInfo, allColumn, destTableKeyList, sqlStart, sqlEnd, isKeyRemoved]
     * @Param: [jobInfo, allColumn, destTableKeyList, 如果是sql需要填select, 如果是sql，需要填 from , 如果是字段，需要将字段去掉]
     * @return: void
     * @Author: bjchen
     * @Date: 2021/4/14
     */
    private String reforeJobInfoParam(String sql ,String destTable,List<String> allColumn, List<String> destTableKeyList, String sqlStart,String sqlEnd,boolean isKeyRemoved) {
        StringBuilder param =new StringBuilder(sqlStart);
        if(isNeedReform(sql)&&allColumn.size()>0){
            for(int i =0;i<allColumn.size();++i ){
                String column= allColumn.get(i);
                //如需要移除key，则构建参数时将主键key移除
                if(isKeyRemoved&&destTableKeyList.contains(column)){
                    continue;
                }
                param.append(column).append(i==allColumn.size()-1?CHARACTER_EMPTY_STR:CHARACTER_COMMA);
            }
            return param.append(sqlEnd.equals(CHARACTER_EMPTY_STR)?CHARACTER_EMPTY_STR:sqlEnd+destTable).toString();
        }else {
            return sql;
        }
    }

    /**
     * 启动定时任务，同步数据库的数据
     */
    @Override
    public void start() {
        for (int index = 0; index < jobList.size(); index++) {
            JobInfo jobInfo = jobList.get(index);
            String logTitle = "[" + code + "]" + jobInfo.getName() + " ";
            try {
                SchedulerFactory sf = new StdSchedulerFactory();
                Scheduler sched = sf.getScheduler();
                JobDetail job = newJob(JobTask.class).withIdentity(MykitDbSyncConstants.JOB_PREFIX.concat(jobInfo.getName()), code).build();
                job.getJobDataMap().put(MykitDbSyncConstants.SRC_DB, srcDb);
                job.getJobDataMap().put(MykitDbSyncConstants.DEST_DB, destDb);
                job.getJobDataMap().put(MykitDbSyncConstants.JOB_INFO, jobInfo);
                job.getJobDataMap().put(MykitDbSyncConstants.LOG_TITLE, logTitle);
                logger.info(jobInfo.getCron());
                CronTrigger trigger = newTrigger().withIdentity(MykitDbSyncConstants.TRIGGER_PREFIX.concat(jobInfo.getName()), code).withSchedule(cronSchedule(jobInfo.getCron())).build();
                sched.scheduleJob(job, trigger);
                sched.start();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(logTitle + e.getMessage());
                logger.error(logTitle + " run failed");
                continue;
            }
        }
    }
}
