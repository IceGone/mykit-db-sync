package io.mykit.db.transfer;

import io.mykit.db.common.constants.MykitDbSyncConstants;
import io.mykit.db.common.utils.DateUtils;
import io.mykit.db.transfer.factory.ConfTypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


/**
 * @program: mykit-db-sync
 * @description: 主备调同步 220kv
 * @author: bjchen
 * @create: 2021-04-14
 **/
public class SynBuslfNw220kvMain {
    private static Logger logger = LoggerFactory.getLogger(SynBuslfNw220kvMain.class);

    public static void main(String[] args) {
        String jobXmlPath ="D:\\01_software\\01_java\\03_IntelliJ IDEA 2019.1.3\\01_workSpaces\\mykit-db-sync\\mykit-db-transfer\\mysql_mysql_jobs.xml";
        /*if (args.length <= 0){
            throw new MykitDbSyncException("必须指定配置文件的目录，例如：/home/db/sync/jobs.xml");
        }*/

        logger.info("同步数据开始===>>>" + DateUtils.parseDateToString(new Date(), DateUtils.DATE_TIME_FORMAT));
        //从数据库表
        ConfTypeFactory.create(MykitDbSyncConstants.TYPE_CONF_DATABASE).init(jobXmlPath).start();
        //ConfTypeFactory.create(MykitDbSyncConstants.TYPE_CONF_JOBXML).init(jobXmlPath).start();
        //DBSyncBuilder.builder().init(jobXmlPath).start();
    }
}
