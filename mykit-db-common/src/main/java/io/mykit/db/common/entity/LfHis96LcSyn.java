package io.mykit.db.common.entity;


public class LfHis96LcSyn {
  /***
   * @Description:
   */
  private long id;

  /***
  * @Description:
  */
  private long type;
  /***
   * @Description:
   */
  private long jobid;
  /***
   * @Description:
   */
  private long busid;
  /***
   * @Description:
   */
  private String caliberid;
  /***
   * @Description:
   */
  private String ymd;
  /***
   * @Description:
   */
  private java.sql.Timestamp createtime;
  /***
   * @Description:
   */
  private java.sql.Timestamp syntime;
  /***
   * @Description:
   */
  private long syncount;
  /***
   * @Description:
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


  public long getJobid() {
    return jobid;
  }

  public void setJobid(long jobid) {
    this.jobid = jobid;
  }


  public long getBusid() {
    return busid;
  }

  public void setBusid(long busid) {
    this.busid = busid;
  }


  public String getCaliberid() {
    return caliberid;
  }

  public void setCaliberid(String caliberid) {
    this.caliberid = caliberid;
  }


  public String getYmd() {
    return ymd;
  }

  public void setYmd(String ymd) {
    this.ymd = ymd;
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


  public long getSyncount() {
    return syncount;
  }

  public void setSyncount(long syncount) {
    this.syncount = syncount;
  }


  public long getSynstatus() {
    return synstatus;
  }

  public void setSynstatus(long synstatus) {
    this.synstatus = synstatus;
  }

}
