package io.mykit.db.oracle;

import io.mykit.db.common.exception.MykitDbSyncException;
import io.mykit.db.common.utils.DateUtils;
import io.mykit.db.oracle.build.DBSyncBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @description 程序的启动入口类
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args){
        if (args.length <= 0){
            throw new MykitDbSyncException("必须指定配置文件的目录，例如：/home/db/sync/jobs.xml");
        }
        logger.info("同步数据开始===>>>" + DateUtils.parseDateToString(new Date(), DateUtils.DATE_TIME_FORMAT));
        DBSyncBuilder.builder().init(args[0]).start();
    }
}
