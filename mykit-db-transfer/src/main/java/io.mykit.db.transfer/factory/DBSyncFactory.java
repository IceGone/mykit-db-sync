package io.mykit.db.transfer.factory;

import io.mykit.db.transfer.sync.DBSync;
import io.mykit.db.transfer.sync.impl.MySQLSync;
import io.mykit.db.transfer.sync.impl.OracleSync;
import io.mykit.db.transfer.sync.impl.SQLServerSync;
import io.mykit.db.common.utils.StringUtils;
import io.mykit.db.common.constants.MykitDbSyncConstants;

/**
 * @description 创建同步对象的工厂类
 */
public class DBSyncFactory {

    /**
     * 根据数据库的类型创建不同的同步数据库数据的对象
     * @param type:数据库类型
     * @return 同步数据库数据的对象
     */
    public static DBSync create(String type){
        if(StringUtils.isEmpty(type)) return null;
        switch (type) {
            case MykitDbSyncConstants.TYPE_DB_MYSQL:
                return new MySQLSync();
            case MykitDbSyncConstants.TYPE_DB_SQLSERVER:
                return new SQLServerSync();
            case MykitDbSyncConstants.TYPE_DB_ORACLE:
                return new OracleSync();
            default:
                return null;
        }
    }
}
