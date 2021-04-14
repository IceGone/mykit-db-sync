package io.mykit.db.transfer.factory;

import io.mykit.db.common.constants.MykitDbSyncConstants;
import io.mykit.db.common.utils.StringUtils;
import io.mykit.db.transfer.conf.ConfType;
import io.mykit.db.transfer.conf.DbConfType;
import io.mykit.db.transfer.conf.JobXmlConfType;

/**
 * @program: mykit-db-sync
 * @description: 创建配置文件来源的工厂类
 * @author: bjchen
 * @create: 2021-04-14
 **/
public class ConfTypeFactory {

    /**
     * 根据获取同步信息的类型创建不同的获取同步数据的对象
     * @param type:同步配置类型
     * @return 同步配置信息的对象
     */
    public static ConfType create(String type){
        if(StringUtils.isEmpty(type)) {return null;}
        switch (type) {
            case MykitDbSyncConstants.TYPE_CONF_JOBXML:
                return new JobXmlConfType();
            case MykitDbSyncConstants.TYPE_CONF_DATABASE:
                return new DbConfType();
            default:
                return null;
        }
    }
}
