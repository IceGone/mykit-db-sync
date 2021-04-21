/**
 * Copyright 2018-2118 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mykit.db.common.constants;


/**
 * @description 常量类
 * @version 1.0.0
 */
public class MykitDbSyncConstants {
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
}
