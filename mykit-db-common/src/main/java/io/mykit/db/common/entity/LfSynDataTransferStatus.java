package io.mykit.db.common.entity;


/***
* @Description:主备数据同步状态表
* @Param: 
* @return: 
* @Author: bjchen
* @Date: 2021/4/21
*/
public class LfSynDataTransferStatus extends BaseLfsyn{
  /***
   * @Description:自增id，每条新增记录自增
   */
  private long id;
  /***
  * @Description:主调->备调(0),备调->主调(1)
  */
  private long type;
  /***
   * @Description:关联lf_syn_job_conf表的jobid 获取 表名及联合主键名
   */
  private long jobId;
  /***
   * @Description:联合主键如busid,ymd,caliberid,busid
   */
  private String uniquekeyvalue;
  /***
   * @Description:'创建时间'
   */
  private java.sql.Timestamp createtime;
  /***
   * @Description:'同步完成时间'
   */
  private java.sql.Timestamp syntime;
  /***
   * @Description:同步状态(0：未同步；1：开始同步；2：完成同步)
   */
  private long synstatus;



  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public long getType() {
    return type;
  }

  public void setType(long type) {
    this.type = type;
  }


  public long getJobId() {
    return jobId;
  }

  public void setJobId(long jobId) {
    this.jobId = jobId;
  }


  public String getUniquekeyvalue() {
    return uniquekeyvalue;
  }

  public void setUniquekeyvalue(String uniquekeyvalue) {
    this.uniquekeyvalue = uniquekeyvalue;
  }


  public java.sql.Timestamp getCreatetime() {
    return createtime;
  }

  public void setCreatetime(java.sql.Timestamp createtime) {
    this.createtime = createtime;
  }


  public java.sql.Timestamp getSyntime() {
    return syntime;
  }

  public void setSyntime(java.sql.Timestamp syntime) {
    this.syntime = syntime;
  }


  public long getSynstatus() {
    return synstatus;
  }

  public void setSynstatus(long synstatus) {
    this.synstatus = synstatus;
  }

}
