package io.mykit.db.transfer.sync.impl;


import io.mykit.db.common.constants.MykitDbSyncConstants;
import io.mykit.db.common.entity.SynServerStatus;
import io.mykit.db.common.exception.MykitDbSyncException;
import io.mykit.db.common.utils.Tool;
import io.mykit.db.transfer.entity.JobInfo;
import io.mykit.db.transfer.sync.DBSync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

import static io.mykit.db.common.constants.MykitDbSyncConstants.SQL_VALUES_COUNT;
import static io.mykit.db.common.constants.MykitDbSyncConstants.TABLE_SYN_END;
import static io.mykit.db.common.utils.StringUtils.getListByStringSplit;

/**
 * @description 执行数据库同步的抽象类
 */
public abstract class AbstractDBSync implements DBSync {
    private static Logger logger = LoggerFactory.getLogger(AbstractDBSync.class);


    /**
     * 去除String数组每个元素中的空格
     * @param src 需要去除空格的数组
     * @return 去除空格后的数组
     */
    protected String[] trimArrayItem(String[] src){
        if(src == null || src.length == 0) return src;
        String[] dest = new String[src.length];
        for(int i = 0; i < src.length; i++){
            dest[i] = src[i].trim();
        }
        return dest;
    }

    /**
     * 构建字段的映射关系
     */
    protected Map<String, String> getFieldsMapper(String[] srcFields, String[] destFields){
        if (srcFields.length != destFields.length){
            throw new MykitDbSyncException("源数据库与目标数据库的字段必须一一对应");
        }
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < srcFields.length; i++){
            map.put(destFields[i].trim(), srcFields[i].trim());
        }
        return map;
    }

    /***
    * @Description: 获取同步表的联合主键和时间:只获取当前时间的前后10天的数据
    */
    protected Map<String, Timestamp>getTableKeyAndlasttimeMap(JobInfo jobInfo, SynServerStatus lsss, Connection conn) throws SQLException {
        Map<String,Timestamp>  tableKeyAndlasttimeMap =new HashMap<>(2^12);
        List<String> destTableKeyList = getListByStringSplit(jobInfo.getDestTableKey(), "\\,");
        int dtkSize = destTableKeyList.size();
        String sql = MykitDbSyncConstants.getSqlTablekey (jobInfo.getDestTable(),jobInfo.getDestTableKey(),jobInfo.getSyncount(),lsss.getDowntime(),lsss.getIslive());
        PreparedStatement psm =null;
        ResultSet rs =null;
        StringBuilder tableKey =null;
        try{
            psm = conn.prepareStatement(sql);
            rs= psm.executeQuery();
            //获取主键
            while (rs.next()){
                tableKey =new StringBuilder();
                for (int i=0; i<dtkSize;++i){
                    tableKey.append("'").append(i==dtkSize-1?rs.getObject(destTableKeyList.get(i))+"'":rs.getObject(destTableKeyList.get(i))+"',");
                }
                tableKeyAndlasttimeMap.put(tableKey.toString(),rs.getTimestamp("LASTTIME"));
            }

        }catch (Exception e){
            logger.error("获取同步表的联合主键和时间失败！"+e.getMessage());
        }finally {
            psm.close();
            return tableKeyAndlasttimeMap;
        }
    }

    /***
    * @Description: 拼接 保存同步表sql
    * @Param: [sMap, jobInfo]
    * @return: java.lang.String
    * @Author: bjchen
    * @Date: 2021/4/27
    */
    protected List<String> getExecuteSqls(SynServerStatus lsss, int opearate, Map<String, Timestamp> sMap, JobInfo jobInfo) {
        List<String> executeSqls =new ArrayList<>(2^4);
        String uniqueName = Tool.generateString(6) + "_" + jobInfo.getJobId();
        String key="";
        long count =0;
        //更新
        if(sMap.size()>0){
            executeSqls.add(getAlterkey(jobInfo.getDestTable()+TABLE_SYN_END,uniqueName,jobInfo.getDestTableKey()));

            StringBuilder sqlHead = new StringBuilder("insert into ").append(jobInfo.getDestTable()).append(TABLE_SYN_END).append(" (").append("JOBID,")
                    .append(jobInfo.getDestTableKey()).append(",CREATETIME,SYNTIME,ENV,OPEARATE,SYNCOUNT,SYNSTATUS" ).append(") values ");
            //最多同步数据量为2即可
            StringBuilder sqlTail =new StringBuilder(" on duplicate key update ").append("SYNCOUNT=if(SYNCOUNT<2,SYNCOUNT+1,SYNCOUNT),SYNSTATUS=if(SYNCOUNT+1>0,0,SYNSTATUS)");
            StringBuilder sql = new StringBuilder(sqlHead);

            // 后期 参数配置化
            int type =0;
            int syncount =1;
            int synstatus =0;

            for(Iterator<String> it=sMap.keySet().iterator();it.hasNext();){
                key =it.next();
                sql.append("('").append(jobInfo.getJobId()).append("',").append(key).append(",").append("SYSDATE()").append(",").append("NULL").append(",'")
                        .append(lsss.getIslive()).append("','").append(opearate).append("','").append(syncount).append("','").append(synstatus).append("'),");

                count++;

                if(count%SQL_VALUES_COUNT==0){
                    addExecuteSqls(executeSqls,sql,sqlTail);
                    sql = new StringBuilder(sqlHead);
                }
            }

            if(count%SQL_VALUES_COUNT!=0){
                addExecuteSqls(executeSqls,sql,sqlTail);
            }

            executeSqls.add(getDropkey(jobInfo.getDestTable()+TABLE_SYN_END,uniqueName));
        }

        return executeSqls;
    }


    /***
    * @Description: 获取删除sql
    * @Param: [dMap, jobInfo]
    * @return: java.lang.String
    * @Author: bjchen
    * @Date: 2021/4/27
    */
    protected String getDelSql(Map<String, Timestamp> dMap, JobInfo jobInfo) {
        if (dMap != null && dMap.size() > 0) {
            StringBuilder sql = new StringBuilder();
            String key="";
            sql.append("delete from ").append(jobInfo.getDestTable()).append(TABLE_SYN_END).append(" where (").append(jobInfo.getDestTableKey()).append(") in (");
            for (Iterator<String> it = dMap.keySet().iterator(); it.hasNext(); ) {
                key = it.next();
                sql.append("(").append(key).append("),");
            }
            sql.deleteCharAt(sql.length()-1);
            sql.append(")");
            return sql.toString();
        }
        return null;
    }

    /***
    * @Description: 拼接sql后添加到sqls
    * @Param: [executeSqls, sql, sqlTail]
    * @return: void
    * @Author: bjchen
    * @Date: 2021/4/28
    */
    protected void addExecuteSqls(List<String> executeSqls, StringBuilder sql, StringBuilder sqlTail) {

        sql=sql.deleteCharAt(sql.length()-1);
        sql.append(sqlTail);
        executeSqls.add(sql.toString());
    }

    /**
    * @Description: 添加联合主键
    * @Param: [tableName, uniqueName]
    * @return: java.lang.String
    * @Author: bjchen
    * @Date: 2021/4/28
    */
    protected String getAlterkey(String tableName, String uniqueName,String uniqueKey ) {
        return new StringBuffer("alter table ").append(tableName).append(" add constraint ").append(uniqueName)
                .append(" unique (").append(uniqueKey).append(")").toString();
    }

    /***
    * @Description: 移除联合主键
    * @Param: [tableName, uniqueName]
    * @return: java.lang.String
    * @Author: bjchen
    * @Date: 2021/4/28
    */
    protected String getDropkey(String tableName, String uniqueName) {
        return new StringBuilder("alter table ").append(tableName).append(" drop index ").append(uniqueName).toString();
    }
}
