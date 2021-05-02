package io.mykit.db.transfer.entity;

import io.mykit.db.common.entity.BaseJobInfo;

/**
 * @description 任务信息
 */
public class JobInfo extends BaseJobInfo {
    private static final long serialVersionUID = -1907092113028096170L;

    //源数据源sql 保存
    private String srcSql;
    //源数据源sql 删除
    private String srcSqlDel;
    //源数据表字段
    private String srcTableFields;
    //目标数据表
    private String destTable;
    //目标表数据字段
    private String destTableFields;
    //目标表主键
    private String destTableKey;
    //目标表可更新的字段
    private String destTableUpdate;
    //更新 同步配置表 如lf_his_96lc_syn 所使用的sql
    private String relateSynSql;

    public String getRelateSynSql() {
        return relateSynSql;
    }

    public void setRelateSynSql(String relateSynSql) {
        this.relateSynSql = relateSynSql;
    }

    public String getSrcTableFields() {
        return srcTableFields;
    }

    public void setSrcTableFields(String srcTableFields) {
        this.srcTableFields = srcTableFields;
    }

    public String getSrcSql() {
        return srcSql;
    }

    public void setSrcSql(String srcSql) {
        this.srcSql = srcSql;
    }

    public String getDestTable() {
        return destTable;
    }

    public void setDestTable(String destTable) {
        this.destTable = destTable;
    }

    public String getDestTableFields() {
        return destTableFields;
    }

    public void setDestTableFields(String destTableFields) {
        this.destTableFields = destTableFields;
    }

    public String getDestTableKey() {
        return destTableKey;
    }

    public void setDestTableKey(String destTableKey) {
        this.destTableKey = destTableKey;
    }

    public String getDestTableUpdate() {
        return destTableUpdate;
    }

    public void setDestTableUpdate(String destTableUpdate) {
        this.destTableUpdate = destTableUpdate;
    }

    public String getSrcSqlDel() {
        return srcSqlDel;
    }

    public void setSrcSqlDel(String srcSqlDel) {
        this.srcSqlDel = srcSqlDel;
    }
}
