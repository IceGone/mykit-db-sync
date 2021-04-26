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
package io.mykit.db.transfer.sync.impl;

import io.mykit.db.common.constants.MykitDbSyncConstants;
import io.mykit.db.common.utils.StringUtils;
import io.mykit.db.common.utils.Tool;
import io.mykit.db.transfer.entity.JobInfo;
import io.mykit.db.transfer.sync.DBSync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * @description MySQL数据库同步实现
 * @version 1.0.0
 */
public class MySQLSync extends AbstractDBSync implements DBSync {
    private Logger logger = LoggerFactory.getLogger(MySQLSync.class);

    @Override
    public String assembleSQL(String srcSql, Connection conn, JobInfo jobInfo) throws SQLException {
        String uniqueName = Tool.generateString(6) + "_" + jobInfo.getJobId();
        String[] destFields = jobInfo.getDestTableFields().split(MykitDbSyncConstants.FIELD_SPLIT);
        destFields = this.trimArrayItem(destFields);
        //默认的srcFields数组与destFields相同
        String[] srcFields = destFields;
        String srcField = jobInfo.getSrcTableFields();
        if(!StringUtils.isEmpty(srcField)){
            srcFields = this.trimArrayItem(srcField.split(MykitDbSyncConstants.FIELD_SPLIT));
        }
        Map<String, String> fieldMapper = this.getFieldsMapper(srcFields, destFields);
        String[] updateFields = jobInfo.getDestTableUpdate().split(MykitDbSyncConstants.FIELD_SPLIT);
        updateFields = this.trimArrayItem(updateFields);
        String destTable = jobInfo.getDestTable();
        String destTableKey = jobInfo.getDestTableKey();
        List<String> destTableKeyList = Arrays.asList(jobInfo.getDestTableKey().trim().split("\\,"));
        PreparedStatement pst = conn.prepareStatement(srcSql);
        ResultSet rs = pst.executeQuery();
        StringBuilder sql = new StringBuilder();
        StringBuilder sqlUpdateSyn = new StringBuilder();
        //UPDATE lf_his_96lc_syn SET SYNCOUNT=SYNCOUNT-1,SYNSTATUS=if(SYNCOUNT-1=0,2,SYNSTATUS),SYNTIME=SYSDATE() WHERE BUSID='5' and CALIBERID='00' and YMD='20210101';
        sql.append("insert into ").append(destTable).append(" (").append(jobInfo.getDestTableFields()).append(") values ");
        sqlUpdateSyn.append("UPDATE ").append(destTable).append(MykitDbSyncConstants.TABLE_SYN_END)
                .append(" SET SYNCOUNT=SYNCOUNT-1,SYNSTATUS=if(SYNCOUNT-1=0,2,SYNSTATUS),SYNTIME=SYSDATE() WHERE (")
                .append(jobInfo.getDestTableKey()).append(") in (");
        long count = 0;
        while (rs.next()) {
            sql.append("(");
            sqlUpdateSyn.append("(");
            for (int index = 0,indexkey=0; index < destFields.length; index++) {
                Object fieldValue = rs.getObject(fieldMapper.get(destFields[index].trim()));
                if (fieldValue == null){
                    sql.append(fieldValue).append(index == (destFields.length - 1) ? "" : ",");
                }else{
                    sql.append("'").append(fieldValue).append(index == (destFields.length - 1) ? "'" : "',");
                    //以主键作为 更新条件
                    if(destTableKeyList.contains(destFields[index])){
                        sqlUpdateSyn.append("'").append(fieldValue).append(indexkey++ == (destTableKeyList.size() - 1) ? "'" : "',");
                    }
                }
            }
            sql.append("),");
            sqlUpdateSyn.append("),");
            count++;
        }
        if (rs != null) {
            rs.close();
        }
        if (pst != null) {
            pst.close();
        }
        if (count > 0) {
            sql = sql.deleteCharAt(sql.length() - 1);
            sqlUpdateSyn = sqlUpdateSyn.deleteCharAt(sqlUpdateSyn.length() - 1).append(")");
            if ((!StringUtils.isEmpty(jobInfo.getDestTableUpdate())) && (!StringUtils.isEmpty(jobInfo.getDestTableKey()))) {
                sql.append(" on duplicate key update ");
                for (int index = 0; index < updateFields.length; index++) {
                    sql.append(updateFields[index]).append("= values(").append(updateFields[index]).append(index == (updateFields.length - 1) ? ")" : "),");
                }
                return new StringBuffer("alter table ").append(destTable).append(" add constraint ").append(uniqueName).append(" unique (").append(destTableKey).append(");")
                        .append(sql.toString())
                        .append(";alter table ").append(destTable).append(" drop index ").append(uniqueName)
                        .append(";").append(sqlUpdateSyn.toString()).toString();
            }
            logger.debug(sql.toString());
            return sql.toString();
        }
        return null;
    }

    @Override
    public void executeSQL(String sql, Connection conn) throws SQLException {
        PreparedStatement pst = conn.prepareStatement("");
        String[] sqlList = sql.split(";");
        for (int index = 0; index < sqlList.length; index++) {
            pst.addBatch(sqlList[index]);
        }
        pst.executeBatch();
        conn.commit();
        pst.close();
    }

    /***
    * @Description: 【sql,主库,备库】 先执行备调更新 lf_his_96lc,再执行主调同步表 lf_his_96lc_syn
    * @Param: [sql, inConn, outConn]
    * @return: void
    * @Author: bjchen
    * @Date: 2021/1/26
    */
    @Override
    public void executeSQL(String sql, Connection inConn,Connection outConn) throws SQLException {
        PreparedStatement inPsm = inConn.prepareStatement("");
        PreparedStatement outPsm = outConn.prepareStatement("");
        String[] sqlList = sql.split(";");
        for (int index = 0; index < sqlList.length-1; index++) {
            outPsm.addBatch(sqlList[index]);
        }
        inPsm.addBatch(sqlList[sqlList.length-1]);

        outPsm.executeBatch();
        inPsm.executeBatch();
        outConn.commit();
        inConn.commit();
        outPsm.close();
        inPsm.close();
    }

    @Override
    public void executeUpdateTableSyn(JobInfo jobInfo, Connection inConn, Connection outConn) throws SQLException {
        /
    }


}
