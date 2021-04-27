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
import io.mykit.db.common.exception.MykitDbSyncException;
import io.mykit.db.common.utils.Tool;
import io.mykit.db.transfer.entity.JobInfo;
import io.mykit.db.transfer.sync.DBSync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static io.mykit.db.common.constants.MykitDbSyncConstants.TABLE_SYN_END;
import static io.mykit.db.common.utils.StringUtils.getListByStringSplit;

/**
 * @description 执行数据库同步的抽象类
 * @version 1.0.0
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
    protected Map<String,Date>getTableKeyAndlasttimeMap(JobInfo jobInfo, Connection conn) throws SQLException {
        Map<String,Date>  tableKeyAndlasttimeMap =new HashMap<>(2^12);
        List<String> destTableKeyList = getListByStringSplit(jobInfo.getDestTableKey(), "\\,");
        int dtkSize = destTableKeyList.size();
        String sql = MykitDbSyncConstants.getSqlTablekeyAndLasttime(jobInfo.getDestTable(),jobInfo.getDestTableKey(),2);
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
                tableKeyAndlasttimeMap.put(tableKey.toString(),rs.getDate("LASTTIME"));
            }

        }catch (Exception e){
            logger.error("获取同步表的联合主键和时间失败！"+e.getMessage());
        }finally {
            psm.close();
        }
        return tableKeyAndlasttimeMap;
    }

    /***
    * @Description: 拼接 保存同步表sql
    * @Param: [sMap, jobInfo]
    * @return: java.lang.String
    * @Author: bjchen
    * @Date: 2021/4/27
    */
    protected List<String> getSaveSql(int opearate,Map<String, Date> sMap, JobInfo jobInfo) {
        List<String> saveSqls =new ArrayList<>(2^3);
        String uniqueName = Tool.generateString(6) + "_" + jobInfo.getJobId();
        String key="";
        int count =0;
        //更新
        if(sMap.size()>0){
            saveSqls.add(new StringBuffer("alter table ").append(jobInfo.getDestTable()).append(TABLE_SYN_END).append(" add constraint ").append(uniqueName)
                    .append(" unique (").append(jobInfo.getDestTableKey()).append(")").toString());
            StringBuilder sqlHead = new StringBuilder("insert into ").append(jobInfo.getDestTable()).append(TABLE_SYN_END).append(" (").append("JOBID,")
                    .append(jobInfo.getDestTableKey()).append(",CREATETIME,SYNTIME,TYPE,OPEARATE,SYNCOUNT,SYNSTATUS" ).append(") values ");
            StringBuilder sqlTail =new StringBuilder(" on duplicate key update ").append("SYNCOUNT=SYNCOUNT+1,SYNSTATUS=if(SYNCOUNT+1>0,0,SYNSTATUS)");
            StringBuilder sql = new StringBuilder(sqlHead);

            // 参数配置化
            int type =0;
            int syncount =1;
            int synstatus =0;
            //INSERT INTO `blogs`.`lf_his_96lc_syn`(`JOBID`,  `BUSID`,`CALIBERID`,`YMD`, `CREATETIME`, `SYNTIME`,`TYPE`,`OPEARATE`, `SYNSTATUS`) VALUES (
            // 2, '5','00','20210101', '2020-10-31 09:03:00', NULL, 0, 0,0);
            for(Iterator<String> it=sMap.keySet().iterator();it.hasNext();){
                key =it.next();
                sql.append("('").append(jobInfo.getJobId()).append("',").append(key).append(",").append("SYSDATE()").append(",").append("NULL").append(",'")
                        .append(type).append("','").append(opearate).append("','").append(syncount).append("','").append(synstatus).append("'),");

                count++;

                if(count%4000==0){
                    sql=sql.deleteCharAt(sql.length()-1);
                    sql.append(sqlTail);
                    saveSqls.add(sql.toString());

                    sql = new StringBuilder(sqlHead);
                }

            }
            if(count%4000!=0){
                sql=sql.deleteCharAt(sql.length()-1);
                sql.append(sqlTail);
                saveSqls.add(sql.toString());

            }

            saveSqls.add(new StringBuilder("alter table ").append(jobInfo.getDestTable()).append(TABLE_SYN_END).append(" drop index ").append(uniqueName).toString());
        }


        return saveSqls;
    }


    /***
    * @Description: 获取删除sql
    * @Param: [dMap, jobInfo]
    * @return: java.lang.String
    * @Author: bjchen
    * @Date: 2021/4/27
    */
    protected String getDelSql(Map<String, Date> dMap, JobInfo jobInfo) {
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
}
