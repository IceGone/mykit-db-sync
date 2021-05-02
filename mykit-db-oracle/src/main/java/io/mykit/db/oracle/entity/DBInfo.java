package io.mykit.db.oracle.entity;

import io.mykit.db.common.entity.BaseDBInfo;

/**
 * @description 数据库信息
 */
public class DBInfo extends BaseDBInfo {
    private static final long serialVersionUID = -1307131236484165237L;
    //上次同步最后SCN号
    private String lastScn = "0";
    //源数据库客户端用户名
    private String clientUserName;
    //日志文件的路径
    private String logPath;
    //数据字典的路径
    private String dataDictionary;

    public String getLastScn() {
        return lastScn;
    }

    public void setLastScn(String lastScn) {
        this.lastScn = lastScn;
    }

    public String getClientUserName() {
        return clientUserName;
    }

    public void setClientUserName(String clientUserName) {
        this.clientUserName = clientUserName;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public String getDataDictionary() {
        return dataDictionary;
    }

    public void setDataDictionary(String dataDictionary) {
        this.dataDictionary = dataDictionary;
    }

}
