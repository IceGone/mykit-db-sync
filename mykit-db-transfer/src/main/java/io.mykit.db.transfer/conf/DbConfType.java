package io.mykit.db.transfer.conf;

import io.mykit.db.common.constants.MykitDbSyncConstants;
import io.mykit.db.transfer.entity.JobInfo;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

import static io.mykit.db.common.utils.DbUtil.qr;

/**
 * @program: mykit-db-sync
 * @description: 通过数据库表：lf_syn_job_conf 获取同步的配置信息
 * @author: bjchen
 * @create: 2021-04-14
 **/
public class DbConfType extends AbstractConfType {
    private final Logger logger = LoggerFactory.getLogger(DbConfType.class);

    public DbConfType(){
        super();
    }
    /**
     * 创建DBSyncBuilder对象
     * @return DBSyncBuilder对象
     */
    public static DbConfType builder(){
        return new DbConfType();
    }

    /***
    * @Description: 从数据库表 lf_syn_job_conf 里获取表映射关系
    * @Param: [configFile]
    * @return: io.mykit.db.transfer.conf.ConfType
    * @Author: bjchen
    * @Date: 2021/4/20
    */
    @Override
    public ConfType init(String configFile) {
        //获取除数据库连接信息
        this.getDBInfo(configFile);
        String sql = "select * from syn_job_conf";
        Connection conn =null;
        try {
            // 从备调
            conn = getConnection(MykitDbSyncConstants.TYPE_DEST, destDb);
            jobList= qr.query(conn,sql,new BeanListHandler<JobInfo>(JobInfo.class));
        }catch (Exception e){
            logger.error("获取数据库连接失败");
        }
        //预拼接sql
        this.reformJobInfo(conn,jobList);

        return this;
    }


}
