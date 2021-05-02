package io.mykit.db.transfer.sync;


import io.mykit.db.common.entity.SynServerStatus;
import io.mykit.db.transfer.entity.JobInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @description 数据库同步接口
 */
public interface DBSync {

    /**
     *
     * @param paramString:同步参数
     * @param paramConnection：数据库连接
     * @param paramJobInfo：同步任务
     * @return
     * @throws SQLException
     */
    String assembleSQL(String paramString, Connection paramConnection, JobInfo paramJobInfo) throws SQLException;

    /**
     * @param paramString:同步参数
     * @param paramConnection：数据库连接
     * @param paramJobInfo：同步任务
     * @return
     * @throws SQLException
     */
    List<String> assembleSaveSQL(String paramString, Connection paramConnection, JobInfo paramJobInfo) throws SQLException;

    /**
     * @param paramString:同步参数
     * @param paramConnection：数据库连接
     * @param paramJobInfo：同步任务
     * @return
     * @throws SQLException
     */
    List<String> assembleDelSQL(String paramString, Connection paramConnection, JobInfo paramJobInfo) throws SQLException;
    /**
     * @param sql：要执行的SQL语句
     * @param conn：数据库连接
     * @throws SQLException
     */
    void executeSQL(String sql, Connection conn) throws SQLException;

    /**
     *
     * @param sql：要执行的SQL语句
     * @param inConn：数据库连接:主
     * @param outConn：数据库连接:备
     * @throws SQLException
     */
    void executeSQL(String sql, Connection inConn,Connection outConn) throws SQLException;

    /**
     *
     * @param sql：要执行的SQL语句
     * @param inConn：数据库连接:主
     * @param outConn：数据库连接:备
     * @throws SQLException
     */
    void executeSQL(List<String> sql, Connection inConn, Connection outConn) throws SQLException;

    /**
     * @param jobInfo：更新要更新的表的主键:由于无法修改配置文件，无法跨库查询，默认更新当前日期前后两旬之内的数据
     * @param inConn：数据库连接:主
     * @param outConn：数据库连接:备
     * @throws SQLException
     */
    void executeUpdateTableSyn(JobInfo jobInfo, Connection inConn,Connection outConn) throws SQLException;

    /***
    * @Description: 根据主调是否正常运行更新 备调数据库的 syn_server_status 表
    * @Param: [inConn, outConn]
    * @return: java.lang.Integer
    * @Author: bjchen
    * @Date: 2021/4/29
    */
    Integer insertOrUpdateSSS(Connection inConn, Connection outConn, SynServerStatus lastSynServerStatus) throws SQLException;

    /***
    * @Description: 获取备调 syn_server_status 表的最新状态
    * @Param: [outConn]
    * @return: io.mykit.db.common.entity.SynServerStatus
    * @Author: bjchen
    * @Date: 2021/4/29
    */
    SynServerStatus getLastSynServerStatus(Connection outConn) throws SQLException;


}
