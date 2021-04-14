package io.mykit.db.transfer.conf;

import io.mykit.db.common.constants.MykitDbSyncConstants;
import io.mykit.db.transfer.entity.DBInfo;
import io.mykit.db.transfer.entity.JobInfo;
import io.mykit.db.transfer.task.JobTask;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @program: mykit-db-sync
 * @description: 抽象类：基础数据填充
 * @author: bjchen
 * @create: 2021-04-14
 **/
public abstract class AbstractConfType implements ConfType {
    private static Logger logger = LoggerFactory.getLogger(AbstractConfType.class);


    protected DBInfo srcDb;
    protected DBInfo destDb;
    protected List<JobInfo> jobList;
    protected String code;

    protected AbstractConfType(){}

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
