package io.mykit.db.common.entity;


public class SynJobConf extends BaseSyn {
  /***
  * @Description: 自增id，每条新增记录自增
  */
  private long jobid;
  /***
   * @Description:0：短期,1:220kv,2:500kv
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
   * @Description:目标数据表
   */
  private String desttable;
  /***
   * @Description:目标数据表字段(默认为所有字段)
   */
  private String desttablefields;
  public long getJobid() {
    return jobid;
  }

  public void setJobid(long jobid) {
    this.jobid = jobid;
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


  public String getDesttable() {
    return desttable;
  }

  public void setDesttable(String desttable) {
    this.desttable = desttable;
  }


  public String getDesttablefields() {
    return desttablefields;
  }

  public void setDesttablefields(String desttablefields) {
    this.desttablefields = desttablefields;
  }
}
