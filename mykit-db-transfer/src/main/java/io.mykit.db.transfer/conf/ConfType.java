package io.mykit.db.transfer.conf;


import io.mykit.db.transfer.entity.JobInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @program: mykit-db-sync
 * @description: 接口：获取表映射关系
 * @author: bjchen
 * @create: 2021-04-14
 **/
public interface ConfType {

    ConfType init(String configFile);

    List<JobInfo> reformJobInfo(Connection con, List<JobInfo> jobInfos) throws SQLException;

    void start();
}
