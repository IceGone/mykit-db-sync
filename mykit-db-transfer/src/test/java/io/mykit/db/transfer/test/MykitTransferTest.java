package io.mykit.db.transfer.test;

import io.mykit.db.transfer.build.DBSyncBuilder;

/**
 * @description 测试功能
 */
public class MykitTransferTest {

    public static void main(String[] args){
        String path = System.getProperty("user.dir");
        path = path.replace("\\", "/").concat("/").concat("mykit-db-transfer").concat("/").concat("mysql_oracle_jobs.xml");
        System.out.println(path);
        DBSyncBuilder.builder().init(path).start();
    }
}
