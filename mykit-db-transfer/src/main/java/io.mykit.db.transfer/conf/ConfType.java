package io.mykit.db.transfer.conf;

/**
 * @program: mykit-db-sync
 * @description: 接口：获取表映射关系
 * @author: bjchen
 * @create: 2021-04-14
 **/
public interface ConfType {
    ConfType init(String configFile);

    void start();
}
