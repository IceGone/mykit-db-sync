package io.mykit.db.oracle.build;

import io.mykit.db.common.build.BaseBuilder;
import io.mykit.db.common.constants.MykitDbSyncConstants;
import io.mykit.db.common.entity.BaseDBInfo;
import io.mykit.db.common.exception.MykitDbSyncException;
import io.mykit.db.common.utils.StringUtils;
import io.mykit.db.oracle.entity.DBInfo;
import io.mykit.db.oracle.entity.JobInfo;
import io.mykit.db.oracle.task.JobTask;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @description 数据同步构建类
 */
public class DBSyncBuilder extends BaseBuilder {
    private static Logger logger = LoggerFactory.getLogger(DBSyncBuilder.class);

    private DBInfo srcDb;
    private BaseDBInfo destDb;
    private List<JobInfo> jobList;
    private String code;

    private DBSyncBuilder(){
    }

    /**
     * 创建DBSyncBuilder对象
     * @return DBSyncBuilder对象
     */
    public static DBSyncBuilder builder(){
        return new DBSyncBuilder();
    }

    /**
     * 初始化数据库信息并解析jobs.xml填充数据
     * @return DBSyncBuilder对象
     */
    public DBSyncBuilder init(String configFile) {
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
                 jobList.add((JobInfo) elementInObject((Element) it.next(), new JobInfo()));
            }
            srcDb = (DBInfo)elementInObject(src, new DBInfo());
            destDb = (BaseDBInfo)elementInObject(dest, new BaseDBInfo());
            code = root.element(MykitDbSyncConstants.NODE_CODE).getTextTrim();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MykitDbSyncException(e.getMessage());
        }
        return this;
    }

    /**
     * 启动定时任务，同步数据库的数据
     */
    public void start() {
        for (int index = 0; index < jobList.size(); index++) {
            JobInfo jobInfo = jobList.get(index);
            String logTitle = "[" + code + "]" + jobInfo.getJobId() + " ";
            try {
                SchedulerFactory sf = new StdSchedulerFactory();
                Scheduler sched = sf.getScheduler();
                JobDetail job = newJob(JobTask.class).withIdentity(MykitDbSyncConstants.JOB_PREFIX.concat(jobInfo.getJobId()+""), code).build();
                job.getJobDataMap().put(MykitDbSyncConstants.SRC_DB, srcDb);
                job.getJobDataMap().put(MykitDbSyncConstants.DEST_DB, destDb);
                job.getJobDataMap().put(MykitDbSyncConstants.JOB_INFO, jobInfo);
                job.getJobDataMap().put(MykitDbSyncConstants.LOG_TITLE, logTitle);
                logger.info(jobInfo.getCron());
                CronTrigger trigger = newTrigger().withIdentity(MykitDbSyncConstants.TRIGGER_PREFIX.concat(jobInfo.getJobId()+""), code).withSchedule(cronSchedule(jobInfo.getCron())).build();
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
