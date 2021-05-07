package io.mykit.db.common.constants;


import java.sql.Timestamp;

/**
 * @description 常量类
 */
public class MykitDbSyncConstants {

    /***
     * @Description: 同步时每几条数据执行一次
     */
    public static final Integer SQL_VALUES_COUNT =100;

    /***
     * @Description: 保存进 _syn 表：构建保存sql
     */
    public static final Integer OPEARATE_SAVE_0 =0;

    /***
     * @Description: 保存进 _syn 表：构建删除sql
     */
    public static final Integer OPEARATE_DEL_1 =1;

    /***
     * @Description: 表查询条数
     */
    public static final String SQL_SELECT_LIMIT_1 ="1";
    /***
     * @Description: 表查询条数
     */
    public static final String SQL_SELECT_LIMIT_2 ="2";
    /***
     * @Description: 服务器状态 0,正常,1:宕机
     */
    public static final int SERVER_ISLIVE_0 =0;
    /***
     * @Description: 服务器状态 0,正常,1:宕机
     */
    public static final int SERVER_ISLIVE_1 =1;

    /***
    * @Description: 同步表对应的同步信息表后缀
    */
    public static final String SQL_TYPE_SAVE ="save";
    /***
    * @Description: 同步表对应的同步信息表后缀
    */
    public static final String SQL_TYPE_DELETE ="delete";
    /***
    * @Description: 同步表对应的同步信息表后缀
    */
    public static final String TABLE_SYN_END ="_syn";
    /***
    * @Description: 任务计划 配置 拼接sql 头
    */
    public static final String SQL_SELECT_START ="select " ;
    /***
     * @Description: 任务计划 配置 拼接sql 尾
     */
    public static final String SQL_SELECT_END =" from " ;

    /***
    * 任务计划 配置 的来源：job.xml
    */
    public static final String TYPE_CONF_JOBXML ="job.xml";
    /***
    * 任务计划 配置 的来源：数据库 表 lf_syn_job_conf
    */
    public static final String TYPE_CONF_DATABASE ="database";
    /***
     * @Description: 任务计划配置 job.xml 或者 数据库lf_syn_job_conf 的各字段名
     */
    public static final String JOB_CONF_COLUMN_SRCSQL ="srcSql";
    /***
     * @Description: 任务计划配置 job.xml 或者 数据库lf_syn_job_conf 的各字段名
     */
    public static final String JOB_CONF_COLUMN_SRCTABLEFIELDS ="srcTableFields";
    /***
     * @Description: 任务计划配置 job.xml 或者 数据库lf_syn_job_conf 的各字段名
     */
    public static final String JOB_CONF_COLUMN_DESTTABLEFIELDS ="destTableFields";
    /***
     * @Description: 任务计划配置 job.xml 或者 数据库lf_syn_job_conf 的各字段名
     */
    public static final String JOB_CONF_COLUMN_DESTTABLEUPDATE ="destTableUpdate";
    /**
     * Oracle数据库时间格式
     */
    public static final String ORACLE_DATE_FORMAT = "yyyy-mm-dd hh24:mi:ss";

    /**
     * 日期分割符
     */
    public static final String DATE_SPLIT = "-";

    /**
     * 时间分割符
     */
    public static final String TIME_SPLIT = ":";

    /**
     * 配置的字段分隔符
     */
    public static final String FIELD_SPLIT = ",";

    /**
     * 源数据库
     */
    public static final String TYPE_SOURCE = "source";

    /**
     * 目标数据库
     */
    public static final String TYPE_DEST = "dest";

    /**
     * sqlserver数据库
     */
    public static final String TYPE_DB_SQLSERVER = "sqlserver";

    /**
     * MySQL数据库
     */
    public static final String TYPE_DB_MYSQL = "mysql";
    /**
     * Oracle数据库
     */
    public static final String TYPE_DB_ORACLE = "oracle";

    /**
     * 序列化标识的字段
     */
    public static final String FIELD_SERIALVERSIONUID = "serialVersionUID";

    /**
     * 配置文件的目录
     */
    public static final String JOB_CONFIG_FILE = "jobs.xml";

    /**
     * 对应xml文件的source节点
     */
    public static final String NODE_SOURCE = "source";
    /**
     * 对应xml文件的dest节点
     */
    public static final String NODE_DEST = "dest";
    /**
     * 对应xml文件的jobs节点
     */
    public static final String NODE_JOBS = "jobs";
    /**
     * 对应xml文件的job节点
     */
    public static final String NODE_JOB = "job";
    /**
     * 对应xml文件的code节点
     */
    public static final String NODE_CODE = "code";
    /**
     * 对应xml文件的env节点: 程序所在环境(必须配置)：主调:0 ，备调 :1
     */
    public static final String NODE_ENV = "env";
    /**
     * 对应xml文件的env节点: 主调->备调:0
     */
    public static final String NODE_ENV_0 = "0";

    /**
     * 对应xml文件的env节点: 主调:0
     */
    public static final String NODE_ENV_1 = "1";
    /**
     * 源数据库
     */
    public static final String SRC_DB = "srcDb";
    /**
     * 目标数据库
     */
    public static final String DEST_DB = "destDb";
    /**
     * 任务信息
     */
    public static final String JOB_INFO = "jobInfo";
    /**
     * 日志标头
     */
    public static final String LOG_TITLE = "logTitle";
    /**
     * job前缀
     */
    public static final String JOB_PREFIX = "job-";
    /**
     * trigger前缀
     */
    public static final String TRIGGER_PREFIX = "trigger-";

    /***
    * @Description: 查询某个表的所有字段：MYSQL/ORACLE
    * @Param: [typeDb]
    * @return: java.lang.String
    * @Author: bjchen
    * @Date: 2021/4/14
    */
    public static String getColumnNameSql(String typeDb,String database,String destTable){
        switch (typeDb){
            case TYPE_DB_MYSQL : return "select COLUMN_NAME from information_schema.COLUMNS where table_schema = '"+database+"' and table_name = '"+destTable+"'";
            case TYPE_DB_ORACLE : return "select (column_name) from user_tab_columns where table_name= upper('"+destTable+"')";
            default: return null;
        }
    }

    /***
    * @Description: 根据主调运行状态更新同步表_syn 如 lf_his_96lc_syn
    * @Param: [destTable, destTableKey, syncount, downtime, islive]
    * @return: java.lang.String
    * @Author: bjchen
    * @Date: 2021/5/2
    */
    public static String getSqlTablekey(String destTable,String destTableKey,int syncount,Timestamp downtime,int islive){
        switch (islive){
            case SERVER_ISLIVE_0 : return getSqlTablekeyAndLasttime(destTable,destTableKey,syncount);
            case SERVER_ISLIVE_1 : return getSqlTablekeyAndLasttimeAfterDowntime(destTable,destTableKey,downtime);
            default:return "";
        }
    }


    /***
    * @Description: 获取同步表(主机正常运行): 距离系统时间 前后syncount天 的联合主键 数据
    * @Param: [destTable, destTableKey, syncount]
    * @return: java.lang.String
    * @Author: bjchen
    * @Date: 2021/4/27
    */
    public static String getSqlTablekeyAndLasttime(String destTable,String destTableKey,int syncount){
        return "select "+destTableKey+",LASTTIME from "+destTable +" where abs(datediff(LASTTIME,SYSDATE())) <='"+syncount+"'";
    }

    /***
    * @Description: 获取同步表(主机宕机): 在主机宕机时间之后的联合主键 数据
    * @Param: [destTable, destTableKey, downtime]
    * @return: java.lang.String
    * @Author: bjchen
    * @Date: 2021/5/2
    */
    public static String getSqlTablekeyAndLasttimeAfterDowntime(String destTable, String destTableKey, Timestamp downtime){
        return "select "+destTableKey+",LASTTIME from "+destTable +" where LASTTIME >= '"+downtime+"'";
    }



    /***
    * @Description: 表名 syn_server_status
    * @Author: bjchen
    * @Date: 2021/4/29
    */
    public static final String TABLE_SYN_SERVER_STATUS = "syn_server_status";
    /***
    * @Description: 表名 syn_server_status
    * @Author: bjchen
    * @Date: 2021/4/29
    */
    public static final String TABLE_LF_CTRL_NET = "lf_ctrl_net";
    /***
    * @Description: 字段名 NETID
    * @Author: bjchen
    * @Date: 2021/4/29
    */
    public static final String FIEL_NEIID = "NETID";
    /***
    * @Description: 字段名
    * @Author: bjchen
    * @Date: 2021/4/29
    */
    public static final String FIEL_ID = "ID";

    /***
    * @Description: 在获取连接后，执行此sql 查询是否能正常访问数据库
    * @Param: [destTable, destTableKey]
    * @return: java.lang.String
    * @Author: bjchen
    * @Date: 2021/4/29
    */
    public static String getSqlTestConnection(String destTable,String destTableKey ){
        return "select "+destTableKey+" from "+destTable ;
    }

    /***
    * @Description: 根据表名和排序字段获取查询最新记录的sql
    * @Param: [destTable, destTableKey]
    * @return: java.lang.String
    * @Author: bjchen
    * @Date: 2021/4/29
    */
    public static String getMaxEntityFromTable(String destTable,String destTableKey,String limit ){
        return "select * from "+destTable + " order by "+destTableKey + " desc limit " +limit;
    }

    /***
    * @Description: 获取 主调正常(包括恢复正常) 更新表 sql
    * @Param: [serverIsLive, id]
    * @return: java.lang.String
    * @Author: bjchen
    * @Date: 2021/4/30
    */
    public static String getSqlSavelive(int serverIsLive,Long id){
        switch (serverIsLive){
            //情况1:上次查询也是正常运行
            case SERVER_ISLIVE_0 : return new StringBuilder("update syn_server_status set UPDATETIME=SYSDATE() WHERE ID = '").append(id).append("'").toString();
            //情况2:宕机后恢复正常运行
            case SERVER_ISLIVE_1 : return new StringBuilder("INSERT INTO syn_server_status (CREATETIME,UPDATETIME,DOWNTIME,ISLIVE) VALUES (SYSDATE(),SYSDATE(),NULL,'")
                    .append(SERVER_ISLIVE_0).append("')").toString() ;
            default: return null;
        }
    }

    /***
    * @Description: 获取 主调 宕机 保存表 sql
    * @Param: [serverIsLive, id]
    * @return: java.lang.String
    * @Author: bjchen
    * @Date: 2021/4/30
    */
    public static String getSqlSaveDown(Integer serverIsDown,Long id){
        return new StringBuilder("update syn_server_status set DOWNTIME=SYSDATE(),ISLIVE='").append(serverIsDown)
                .append("' WHERE ID ='").append(id).append("'").toString();
    }

}
