package io.mykit.db.common.entity;

import java.io.Serializable;

/**
 * @description 基础数据库信息
 */
public class BaseDBInfo implements Serializable {
    private static final long serialVersionUID = -5546462343272223569L;

    //数据库连接
    private String url;
    //数据库用户名
    private String username;
    //数据库密码
    private String password;
    //数据库驱动
    private String driver;
    //数据库类型(对应mysql还是sqlserver,还是oracle)
    private String dbtype;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDbtype() {
        return dbtype;
    }

    public void setDbtype(String dbtype) {
        this.dbtype = dbtype;
    }
}
