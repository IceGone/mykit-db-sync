package io.mykit.db.transfer.sync.impl;

import io.mykit.db.common.constants.MykitDbSyncConstants;
import io.mykit.db.common.entity.SynServerStatus;
import io.mykit.db.common.utils.StringUtils;
import io.mykit.db.common.utils.Tool;
import io.mykit.db.transfer.entity.JobInfo;
import io.mykit.db.transfer.sync.DBSync;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static io.mykit.db.common.constants.CharacterConstants.*;
import static io.mykit.db.common.constants.MykitDbSyncConstants.SQL_VALUES_COUNT;
import static io.mykit.db.common.constants.MykitDbSyncConstants.TABLE_SYN_END;
import static io.mykit.db.common.utils.DbUtil.qr;
import static io.mykit.db.common.utils.StringUtils.getListByStringSplit;


/**
 * @description MySQL数据库同步实现
 */
public class MySQLSync extends AbstractDBSync implements DBSync {
    private Logger logger = LoggerFactory.getLogger(MySQLSync.class);

    @Override
    public List<String> assembleSaveSQL(String srcSql, Connection conn, JobInfo jobInfo) throws SQLException {
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
        List<String> destTableKeyList = getListByStringSplit(destTableKey,"\\,");
        PreparedStatement pst = conn.prepareStatement(srcSql);
        ResultSet rs = pst.executeQuery();
        StringBuilder sqlSave = new StringBuilder();
        StringBuilder sqlSaveHead = new StringBuilder("insert into ").append(destTable).append(" (").append(jobInfo.getDestTableFields()).append(") values ");
        StringBuilder sqlSaveTail= new StringBuilder();
        if ((!StringUtils.isEmpty(jobInfo.getDestTableUpdate())) && (!StringUtils.isEmpty(destTableKey))) {
            sqlSaveTail.append(" on duplicate key update ");
            for (int index = 0; index < updateFields.length; index++) {
                sqlSaveTail.append(updateFields[index]).append("= values(").append(updateFields[index]).append(index == (updateFields.length - 1) ? ")" : "),");
            }
        }
        sqlSave.append(sqlSaveHead);

        StringBuilder sqlUpdateSyn = new StringBuilder();
        StringBuilder sqlUpdateSynHead =new StringBuilder("UPDATE ").append(destTable).append(TABLE_SYN_END)
                .append(" SET SYNCOUNT=SYNCOUNT-1,SYNSTATUS=if(SYNCOUNT<1,2,SYNSTATUS),SYNTIME=SYSDATE() WHERE (")
                .append(destTableKey).append(") in (");
        //UPDATE lf_his_96lc_syn SET SYNCOUNT=SYNCOUNT-1,SYNSTATUS=if(SYNCOUNT-1=0,2,SYNSTATUS),SYNTIME=SYSDATE() WHERE BUSID='5' and CALIBERID='00' and YMD='20210101';
        sqlUpdateSyn.append(sqlUpdateSynHead);
        long count = 0;
        //由于数据量可能较大，对可执行sql进行拆分
        List<String> executeSqls =new ArrayList<>(2^4);

        executeSqls.add(getAlterkey(jobInfo.getDestTable(),uniqueName,destTableKey));

        while (rs.next()) {
            count++;
            sqlSave.append("(");
            sqlUpdateSyn.append("(");
            for (int index = 0,indexkey=0; index < destFields.length; index++) {
                Object fieldValue = rs.getObject(fieldMapper.get(destFields[index].trim()));
                if (fieldValue == null){
                    sqlSave.append(fieldValue).append(index == (destFields.length - 1) ? "" : ",");
                }else{
                    sqlSave.append("'").append(fieldValue).append(index == (destFields.length - 1) ? "'" : "',");
                    //以主键作为 更新条件
                    if(destTableKeyList.contains(destFields[index])){
                        sqlUpdateSyn.append("'").append(fieldValue).append(indexkey++ == (destTableKeyList.size() - 1) ? "'" : "',");
                    }
                }
            }
            sqlSave.append("),");
            sqlUpdateSyn.append("),");

            //每SQL_VALUES_COUNT条作为一次insert或update
            if(count%SQL_VALUES_COUNT==0){
                // 同步表 备调
                addExecuteSqls(executeSqls,sqlSave,sqlSaveTail);
                // 同步信息表 主调
                addExecuteSqls(executeSqls,sqlUpdateSyn,new StringBuilder(CHARACTER_1));
                sqlSave = new StringBuilder(sqlSaveHead);
                sqlUpdateSyn = new StringBuilder(sqlUpdateSynHead);
            }
        }

        //最后的不到SQL_VALUES_COUNT条记录作为一次insert或update
        if(count%SQL_VALUES_COUNT!=0){
            addExecuteSqls(executeSqls,sqlSave,sqlSaveTail);
            addExecuteSqls(executeSqls,sqlUpdateSyn,new StringBuilder(CHARACTER_1));
        }

        executeSqls.add(getDropkey(jobInfo.getDestTable(),uniqueName));

        if (rs != null) {
            rs.close();
        }
        if (pst != null) {
            pst.close();
        }

        logger.debug(executeSqls.toString());
        return executeSqls;
    }

    @Override
    public List<String> assembleDelSQL(String srcSql, Connection conn, JobInfo jobInfo) throws SQLException {
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
        String destTable = jobInfo.getDestTable();
        String destTableKey = jobInfo.getDestTableKey();
        List<String> destTableKeyList = getListByStringSplit(destTableKey,"\\,");
        PreparedStatement pst = conn.prepareStatement(srcSql);
        ResultSet rs = pst.executeQuery();
        StringBuilder sqlDel = new StringBuilder();
        StringBuilder sqlDelHead = new StringBuilder("delete from ").append(destTable).append(" where (").append(destTableKey).append(") in (");

        sqlDel.append(sqlDelHead);

        StringBuilder sqlUpdateSyn = new StringBuilder();
        StringBuilder sqlUpdateSynHead =new StringBuilder("UPDATE ").append(destTable).append(TABLE_SYN_END)
                .append(" SET SYNCOUNT=SYNCOUNT-1,SYNSTATUS=if(SYNCOUNT<1,2,SYNSTATUS),SYNTIME=SYSDATE() WHERE (")
                .append(destTableKey).append(") in (");
        //UPDATE lf_his_96lc_syn SET SYNCOUNT=SYNCOUNT-1,SYNSTATUS=if(SYNCOUNT-1=0,2,SYNSTATUS),SYNTIME=SYSDATE() WHERE BUSID='5' and CALIBERID='00' and YMD='20210101';
        sqlUpdateSyn.append(sqlUpdateSynHead);
        long count = 0;
        //由于数据量可能较大，对可执行sql进行拆分
        List<String> executeSqls =new ArrayList<>(2^4);

        executeSqls.add(getAlterkey(jobInfo.getDestTable(),uniqueName,destTableKey));

        while (rs.next()) {
            count++;
            sqlDel.append("(");
            sqlUpdateSyn.append("(");
            for (int indexkey=0; indexkey < destTableKeyList.size(); indexkey++) {
                Object fieldValue = rs.getObject( fieldMapper.get(destTableKeyList.get(indexkey).trim()));
                //主键不为空，无需判断
                sqlDel.append("'").append(fieldValue).append(indexkey == (destTableKeyList.size() - 1) ? "'" : "',");
                sqlUpdateSyn.append("'").append(fieldValue).append(indexkey== (destTableKeyList.size() - 1) ? "'" : "',");
            }
            sqlDel.append("),");
            sqlUpdateSyn.append("),");

            //每SQL_VALUES_COUNT条作为一次insert或update
            if(count%SQL_VALUES_COUNT==0){
                // 同步表 备调
                addExecuteSqls(executeSqls,sqlDel,new StringBuilder(CHARACTER_1));
                // 同步信息表 主调
                addExecuteSqls(executeSqls,sqlUpdateSyn,new StringBuilder(CHARACTER_1));
                sqlDel = new StringBuilder(sqlDelHead);
                sqlUpdateSyn = new StringBuilder(sqlUpdateSynHead);
            }
        }

        //最后的不到SQL_VALUES_COUNT条记录作为一次insert或update
        if(count%SQL_VALUES_COUNT!=0){
            addExecuteSqls(executeSqls,sqlDel,new StringBuilder(CHARACTER_1));
            addExecuteSqls(executeSqls,sqlUpdateSyn,new StringBuilder(CHARACTER_1));
        }

        executeSqls.add(getDropkey(jobInfo.getDestTable(),uniqueName));

        if (rs != null) {
            rs.close();
        }
        if (pst != null) {
            pst.close();
        }

        logger.debug(executeSqls.toString());
        return executeSqls;
    }


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
        List<String> destTableKeyList = getListByStringSplit(destTableKey,"\\,");
        PreparedStatement pst = conn.prepareStatement(srcSql);
        ResultSet rs = pst.executeQuery();
        StringBuilder sql = new StringBuilder();
        StringBuilder sqlHead = new StringBuilder("insert into ").append(destTable).append(" (").append(jobInfo.getDestTableFields()).append(") values ");
        StringBuilder sqlTail= new StringBuilder();
        if ((!StringUtils.isEmpty(jobInfo.getDestTableUpdate())) && (!StringUtils.isEmpty(destTableKey))) {
            sqlTail.append(" on duplicate key update ");
            for (int index = 0; index < updateFields.length; index++) {
                sqlTail.append(updateFields[index]).append("= values(").append(updateFields[index]).append(index == (updateFields.length - 1) ? ")" : "),");
            }
        }
        sql.append(sqlHead);

        StringBuilder sqlUpdateSyn = new StringBuilder();
        StringBuilder sqlUpdateSynHead =new StringBuilder("UPDATE ").append(destTable).append(TABLE_SYN_END)
                .append(" SET SYNCOUNT=SYNCOUNT-1,SYNSTATUS=if(SYNCOUNT-1=0,2,SYNSTATUS),SYNTIME=SYSDATE() WHERE (")
                .append(destTableKey).append(") in (");
        //UPDATE lf_his_96lc_syn SET SYNCOUNT=SYNCOUNT-1,SYNSTATUS=if(SYNCOUNT-1=0,2,SYNSTATUS),SYNTIME=SYSDATE() WHERE BUSID='5' and CALIBERID='00' and YMD='20210101';
        sqlUpdateSyn.append(sqlUpdateSynHead);
        long count = 0;
        //由于数据量可能较大，对可执行sql进行拆分
        List<String> executeSqls =new ArrayList<>(2^4);

        executeSqls.add(getAlterkey(jobInfo.getDestTable(),uniqueName,destTableKey));

        while (rs.next()) {
            count++;
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

            //每SQL_VALUES_COUNT条作为一次insert或update
            if(count%SQL_VALUES_COUNT==0){
                addExecuteSqls(executeSqls,sql,sqlTail);
                addExecuteSqls(executeSqls,sqlUpdateSyn,new StringBuilder(CHARACTER_EMPTY_STR));
                sql = new StringBuilder(sqlHead);
            }
        }

        //最后的不到SQL_VALUES_COUNT条记录作为一次insert或update
        if(count%SQL_VALUES_COUNT!=0){
            addExecuteSqls(executeSqls,sql,sqlTail);
            addExecuteSqls(executeSqls,sqlUpdateSyn,new StringBuilder(CHARACTER_EMPTY_STR));
        }

        executeSqls.add(getDropkey(jobInfo.getDestTable(),uniqueName));

        if (rs != null) {
            rs.close();
        }
        if (pst != null) {
            pst.close();
        }

        if (count > 0) {
            sql = sql.deleteCharAt(sql.length() - 1);
            sqlUpdateSyn = sqlUpdateSyn.deleteCharAt(sqlUpdateSyn.length() - 1).append(")");
            if ((!StringUtils.isEmpty(jobInfo.getDestTableUpdate())) && (!StringUtils.isEmpty(destTableKey))) {
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

    public void executeSQL(List<String> sqls, Connection conn) throws SQLException {
        PreparedStatement pst = conn.prepareStatement("");
        for(String sql:sqls){
            String[] sqlList = sql.split(";");
            for (int index = 0; index < sqlList.length; index++) {
                pst.addBatch(sqlList[index]);
                pst.executeBatch();
            }
        }
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

    /***
    * @Description: 【sql,主库,备库】 先执行备调更新 lf_his_96lc,再执行主调同步表 lf_his_96lc_syn
    * @Param: [sql, inConn, outConn]
    * @return: void
    * @Author: bjchen
    * @Date: 2021/1/26
    */
    @Override
    public void executeSQL(List<String> sql, Connection inConn,Connection outConn) throws SQLException {
        PreparedStatement inPsm = inConn.prepareStatement("");
        PreparedStatement outPsm = outConn.prepareStatement("");
        int size = sql.size();
        //添加主键约束
        outPsm.addBatch(sql.get(0));
        for (int i = 1; i <size-1; i++) {
            //备调
            if(i%2==1){
                outPsm.addBatch(sql.get(i));
                outPsm.executeBatch();
            }
            //主调
            if (i%2==0){
                inPsm.addBatch(sql.get(i));
                inPsm.executeBatch();
            }
        }
        //移除主键约束
        outPsm.addBatch(sql.get(size-1));

        //提交
        outConn.commit();
        inConn.commit();
        outPsm.close();
        inPsm.close();
    }

    @Override
    public void executeUpdateTableSyn(JobInfo jobInfo, Connection inConn, Connection outConn) throws SQLException {
        //获取 主库日期内数据 (处理后作为insert or update)
        Map<String, Date> sMap =getTableKeyAndlasttimeMap(jobInfo,inConn);
        //获取 备库日期内数据 (处理后作为delete)
        Map<String, Date> dMap =getTableKeyAndlasttimeMap(jobInfo,outConn);

        // insert or update 用 sMap
        String key="";
        for(Iterator<String> it=sMap.keySet().iterator();it.hasNext();){
            key =it.next();
            //主调有，备调也有
            if(dMap.containsKey(key)){
                if(sMap.get(key).compareTo(dMap.get(key))>0){
                    //更新。不对sMap做操作，对dMap操作
                }else {
                    //不更新，移除sMap对应的key
                    it.remove();
                }
                //移除dMap对应的key
                dMap.remove(key);
            }
        }
        int opearateSave =0;
        int opearateDel =1;
        //保存数据入 同步表对应的 _syn 表
        //OPEARATE =0 （后续执行insert or update）
        List<String> saveSql = getExecuteSqls(opearateSave,sMap, jobInfo);
        //OPEARATE =1 （后续执行 目标表的 delete）
        List<String> delSql = getExecuteSqls(opearateDel,dMap, jobInfo);
        //执行sql
        if( saveSql!=null && saveSql.size()>0 ){
            this.logger.debug(saveSql.toString());
            executeSQL(saveSql,inConn);
        }
        if( delSql!=null && delSql.size()>0 ){
            this.logger.debug(delSql.toString());
            executeSQL(delSql,inConn);
        }
    }

    @Override
    public Integer insertOrUpdateSSS(Connection inConn, Connection outConn, SynServerStatus lsss) throws SQLException{
        List<String> netids =null;
        String sql ="";

        if(inConn!=null){
            try {
               sql = MykitDbSyncConstants.getSqlTestConnection(MykitDbSyncConstants.TABLE_LF_CTRL_NET,MykitDbSyncConstants.FIEL_NEIID);
                netids = qr.query(inConn,sql,new ColumnListHandler<>(1));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //正常运行(包括恢复正常)
            if(netids!=null&&netids.size()>0){
                sql = MykitDbSyncConstants.getSqlSavelive(lsss.getIslive(),lsss.getId());
                executeSQL(sql,outConn);
                return MykitDbSyncConstants.SERVER_ISLIVE_0 ;

            }else {
                //主机宕机
                sql = MykitDbSyncConstants.getSqlSaveDown(MykitDbSyncConstants.SERVER_ISLIVE_1,lsss.getId());
                executeSQL(sql,outConn);
                return MykitDbSyncConstants.SERVER_ISLIVE_1 ;
            }
        }
        //主调 宕机
        if (inConn ==null){
            sql = MykitDbSyncConstants.getSqlSaveDown(MykitDbSyncConstants.SERVER_ISLIVE_1,lsss.getId());
            executeSQL(sql,outConn);
            return MykitDbSyncConstants.SERVER_ISLIVE_1 ;
        }
        return null;
    }

    @Override
    public SynServerStatus getLastSynServerStatus(Connection outConn) throws SQLException{
        try {
            String sql = MykitDbSyncConstants.getMaxEntityFromTable(MykitDbSyncConstants.TABLE_SYN_SERVER_STATUS,MykitDbSyncConstants.FIEL_ID,MykitDbSyncConstants.SQL_SELECT_LIMIT_1);
            return qr.query(outConn,sql,new BeanHandler<SynServerStatus>(SynServerStatus.class));

        }catch (Exception e){
                this.logger.error("查询备调最新服务器状态失败");
                this.logger.error("异常为"+e.getMessage());
        }
        return null;
    }


}
