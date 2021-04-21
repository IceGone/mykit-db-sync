package io.mykit.db.common.entity;


/***
* @Description:'主备数据同步配置表'
* @Author: bjchen
* @Date: 2021/4/21
*/
public class LfSynJobConf extends BaseLfsyn{
  /***
   * @Description:自增id，每条新增记录自增
   */
  private long jobId;
  /***
   * @Description:任务名称,作数据库区分 0：短期,1:220kv,2:500kv
   */
  private String jobname;
  /***
   * @Description:'任务表达式'
   */
  private String cron;
  /***
   * @Description:源数据源sql(默认为select * from tablename)
   */
  private String srcsql;
  /***
   * @Description:源数据表字段(默认为所有字段)
   */
  private String srctablefields;
  /***
   * @Description:目标数据表字段(默认为所有字段)
   */
  private String destTableFields;

  /***
   * @Description:'目标表更新字段'
   */
  private String destTableupdate;


  public long getJobId() {
    return jobId;
  }

  public void setJobId(long jobId) {
    this.jobId = jobId;
  }


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


  public String getSrcsql() {
    return srcsql;
  }

  public void setSrcsql(String srcsql) {
    this.srcsql = srcsql;
  }


  public String getSrctablefields() {
    return srctablefields;
  }

  public void setSrctablefields(String srctablefields) {
    this.srctablefields = srctablefields;
  }

  public String getDestTableFields() {
    return destTableFields;
  }

  public void setDestTableFields(String destTableFields) {
    this.destTableFields = destTableFields;
  }

  public String getDestTableupdate() {
    return destTableupdate;
  }

  public void setDestTableupdate(String destTableupdate) {
    this.destTableupdate = destTableupdate;
  }

}
