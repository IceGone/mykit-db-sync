/**
 * Copyright 2018-2118 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * @version 1.0.0
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
        //主服务器状态，默认正常运行
        int serverStatus =0;
        JobDataMap data = context.getJobDetail().getJobDataMap();
        DBInfo srcDb = (DBInfo) data.get(MykitDbSyncConstants.SRC_DB);
        DBInfo destDb = (DBInfo) data.get(MykitDbSyncConstants.DEST_DB);
        JobInfo jobInfo = (JobInfo) data.get(MykitDbSyncConstants.JOB_INFO);
        String logTitle = (String) data.get(MykitDbSyncConstants.LOG_TITLE);
        String env = (String) data.get(MykitDbSyncConstants.NODE_ENV);

        DBSync dbHelper = DBSyncFactory.create(destDb.getDbtype());

        try {
            if(MykitDbSyncConstants.NODE_ENV_0.equals(env)){
                inConn = getConnection(MykitDbSyncConstants.TYPE_SOURCE, srcDb);
                outConn = getConnection(MykitDbSyncConstants.TYPE_DEST, destDb);
                //获取主调运行的最新状态
                SynServerStatus lastSynServerStatus = dbHelper.getLastSynServerStatus(outConn);
                // 根据主调运行状态 保存 备调的 syn_server_status 表
                serverStatus = dbHelper.insertOrUpdateSSS(inConn,outConn);

                //主调正常

                //主备切换
            }else if(MykitDbSyncConstants.NODE_ENV_1.equals(env)){
                inConn = getConnection(MykitDbSyncConstants.TYPE_DEST, destDb);
                outConn = getConnection(MykitDbSyncConstants.TYPE_SOURCE, srcDb);
            }

            if (inConn == null) {
                this.logger.error("请检查源数据连接!");
                throw new MykitDbSyncException("请检查源数据连接!");
            } else if (outConn == null) {
                this.logger.error("请检查目标数据连接!");
                throw new MykitDbSyncException("请检查目标数据连接!");
            }
            long start = System.currentTimeMillis();
            //组装SQL前，先更新_syn表 如 lf_his_96lc_syn
            dbHelper.executeUpdateTableSyn(jobInfo,inConn,outConn);

            List<String> sql = dbHelper.assembleSaveSQL(jobInfo.getSrcSql(), inConn, jobInfo);
            List<String> sqlDel = dbHelper.assembleDelSQL(jobInfo.getSrcSqlDel(), inConn, jobInfo);
            this.logger.info("组装SQL耗时: " + (System.currentTimeMillis() - start) + "ms");
            if (sql != null&&sql.size()>2) {
                this.logger.debug(sql.toString());
                long eStart = System.currentTimeMillis();
                dbHelper.executeSQL(sql, inConn,outConn);
                this.logger.info("执行SQL语句耗时: " + (System.currentTimeMillis() - eStart) + "ms");
            }
            if (sql != null&&sqlDel.size()>2) {
                this.logger.debug(sqlDel.toString());
                long eStart = System.currentTimeMillis();
                dbHelper.executeSQL(sqlDel, inConn,outConn);
                this.logger.info("执行SQL语句耗时: " + (System.currentTimeMillis() - eStart) + "ms");
            }
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
