package io.mykit.db.common.entity;

import java.io.Serializable;

/**
 * @description 基础的任务信息
 */
public class BaseJobInfo implements Serializable {
    private static final long serialVersionUID = 8512765449801275225L;
    //任务主键自增id，每条新增记录自增
    private long jobId;
    //任务名称
    private String jobname;
    //任务表达式
    private String cron;

    public String getJobname() {
        return jobname;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }
}
