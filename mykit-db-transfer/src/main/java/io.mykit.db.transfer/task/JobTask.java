package io.mykit.db.transfer.task;

import io.mykit.db.common.constants.MykitDbSyncConstants;
import io.mykit.db.common.db.DbConnection;
import io.mykit.db.common.entity.SynServerStatus;
import io.mykit.db.common.exception.MykitDbSyncException;
import io.mykit.db.common.utils.DateUtils;
import io.mykit.db.transfer.entity.DBInfo;
import io.mykit.db.transfer.entity.JobInfo;
import io.mykit.db.transfer.factory.DBSyncFactory;
import io.mykit.db.transfer.sync.DBSync;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @description 同步数据库任务的具体实现
 */
public class JobTask extends DbConnection implements Job {
    private final Logger logger = LoggerFactory.getLogger(JobTask.class);

    /**
     * 执行同步数据库任务
     *
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        this.logger.info("开始任务调度: {}", DateUtils.parseDateToString(new Date(), DateUtils.DATE_TIME_FORMAT));
        Connection inConn = null;
        Connection outConn = null;
        SynServerStatus lsss = null;
        JobDataMap data = context.getJobDetail().getJobDataMap();
        DBInfo srcDb = (DBInfo) data.get(MykitDbSyncConstants.SRC_DB);
        DBInfo destDb = (DBInfo) data.get(MykitDbSyncConstants.DEST_DB);
        JobInfo jobInfo = (JobInfo) data.get(MykitDbSyncConstants.JOB_INFO);
        String logTitle = (String) data.get(MykitDbSyncConstants.LOG_TITLE);
        String env = (String) data.get(MykitDbSyncConstants.NODE_ENV);

        DBSync dbHelper = DBSyncFactory.create(destDb.getDbtype());

        try {
            inConn = getConnection(MykitDbSyncConstants.TYPE_SOURCE, srcDb);
            outConn = getConnection(MykitDbSyncConstants.TYPE_DEST, destDb);
            //从备调数据库的 syn_server_status 获取主调运行的最新状态
            lsss = dbHelper.insertOrUpdateSSS(inConn,outConn);

            long start = System.currentTimeMillis();
            //组装SQL前，先更新_syn表 如 lf_his_96lc_syn
            //需要处理 主调同步表_syn操作
            if(lsss.getIslive()==MykitDbSyncConstants.SERVER_ISLIVE_0){
                dbHelper.executeUpdateTableSyn(jobInfo,lsss,inConn,outConn);
            }
            if(lsss.getIslive()==MykitDbSyncConstants.SERVER_ISLIVE_1){
                dbHelper.executeUpdateTableSynReverse(jobInfo,lsss,inConn,outConn);
            }
            //备调优先同步到主调
            dbHelper.executeSQL(outConn,inConn,jobInfo,MykitDbSyncConstants.NODE_ENV_1);
            //主调同步到备调
            dbHelper.executeSQL(inConn,outConn,jobInfo,MykitDbSyncConstants.NODE_ENV_0);

        } catch (SQLException e) {
            this.logger.error(logTitle + e.getMessage());
            this.logger.error(logTitle + " SQL执行出错，请检查是否存在语法错误");
            throw new MykitDbSyncException(logTitle + e.getMessage());
        } finally {
            this.logger.info("关闭源数据库连接");
            destoryConnection(inConn);
            this.logger.info("关闭目标数据库连接");
            destoryConnection(outConn);
        }
    }
}
