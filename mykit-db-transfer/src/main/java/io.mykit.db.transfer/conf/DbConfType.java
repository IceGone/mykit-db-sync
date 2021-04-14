package io.mykit.db.transfer.conf;

/**
 * @program: mykit-db-sync
 * @description: 通过数据库表：lf_syn_job_conf 获取同步的配置信息
 * @author: bjchen
 * @create: 2021-04-14
 **/
public class DbConfType extends AbstractConfType{
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

    @Override
    public ConfType init(String configFile) {
        return null;
    }
}
