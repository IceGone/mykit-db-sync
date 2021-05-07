package io.mykit.db.transfer.sync.impl;

import io.mykit.db.common.entity.SynServerStatus;
import io.mykit.db.transfer.entity.JobInfo;
import io.mykit.db.transfer.sync.DBSync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

/**
 * @description SQL Server数据库同步实现
 */
public class SQLServerSync extends AbstractDBSync implements DBSync {
    private Logger logger = LoggerFactory.getLogger(SQLServerSync.class);

    @Override
    public void executeSQL(Connection inConn, Connection outConn, JobInfo jobInfo, String env) throws SQLException{

    }

    @Override
    public String assembleSQL(String srcSql, Connection conn, JobInfo jobInfo) throws SQLException {
        String fieldStr = jobInfo.getDestTableFields();
        String[] fields = jobInfo.getDestTableFields().split(",");
        fields = this.trimArrayItem(fields);
        String[] updateFields = jobInfo.getDestTableUpdate().split(",");
        updateFields = this.trimArrayItem(updateFields);
        String destTableKey = jobInfo.getDestTableKey();
        String destTable = jobInfo.getDestTable();
        Statement stat = conn.createStatement();
        ResultSet rs = stat.executeQuery(srcSql);
        StringBuffer sql = new StringBuffer();
        long count = 0;
        while (rs.next()) {
            sql.append("if not exists (select ").append(destTableKey).append(" from ").append(destTable).append(" where ").append(destTableKey).append("='").append(rs.getString(destTableKey))
                    .append("')").append("insert into ").append(destTable).append("(").append(fieldStr).append(") values(");
            for (int index = 0; index < fields.length; index++) {
                sql.append("'").append(rs.getString(fields[index])).append(index == (fields.length - 1) ? "'" : "',");
            }
            sql.append(") else update ").append(destTable).append(" set ");
            for (int index = 0; index < updateFields.length; index++) {
                sql.append(updateFields[index]).append("='").append(rs.getString(updateFields[index])).append(index == (updateFields.length - 1) ? "'" : "',");
            }
            sql.append(" where ").append(destTableKey).append("='").append(rs.getString(destTableKey)).append("';");
            count++;
            // this.logger.info("第" + count + "耗时: " + (new Date().getTime() - oneStart) + "ms");
        }
        this.logger.info("总共查询到 " + count + " 条记录");
        if (rs != null) {
            rs.close();
        }
        if (stat != null) {
            stat.close();
        }
        return count > 0 ? sql.toString() : null;
    }

    @Override
    public List<String> assembleSaveSQL(String paramString, Connection paramConnection, JobInfo paramJobInfo,String env) throws SQLException {
        return null;
    }

    @Override
    public List<String> assembleDelSQL(String paramString, Connection paramConnection, JobInfo paramJobInfo,String env) throws SQLException {
        return null;
    }

    @Override
    public void executeSQL(String sql, Connection conn) throws SQLException {
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.executeUpdate();
        conn.commit();
        pst.close();
    }

    @Override
    public void executeSQL(String sql, Connection inConn, Connection outConn) throws SQLException {

    }

    @Override
    public void executeSQL(List<String> sql, Connection inConn, Connection outConn) throws SQLException {

    }

    @Override
    public void executeSQL(List<String> sql, Connection conn) throws SQLException {

    }

    @Override
    public void executeUpdateTableSyn(JobInfo jobInfo, SynServerStatus lsss, Connection inConn, Connection outConn) throws SQLException {

    }

    @Override
    public void executeUpdateTableSynReverse(JobInfo jobInfo, SynServerStatus lsss, Connection inConn, Connection outConn) throws SQLException {

    }

    @Override
    public SynServerStatus insertOrUpdateSSS(Connection inConn, Connection outConn) {
        return null;
    }

    @Override
    public SynServerStatus getLastSynServerStatus(Connection outConn) {
        return null;
    }
}
