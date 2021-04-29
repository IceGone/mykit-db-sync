package io.mykit.db.common.entity;


public class SynServerStatus {

  /***
  * @Description: 自增id，主机每次down->up 自增
  */
  private long id;
  /***
   * @Description:创建islive时间(主库维护)
   */
  private java.sql.Timestamp createtime;
  /***
  * @Description:更新islive时间(主库维护)
  */
  private java.sql.Timestamp updatetime;
  /***
   * @Description:主库宕机时间(备库维护)
   */
  private java.sql.Timestamp downtime;
  /***
   * @Description:主机状态(主备调维护 0：运行，1：宕机)
   */
  private long islive;

  public SynServerStatus() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public java.sql.Timestamp getCreatetime() {
    return createtime;
  }

  public void setCreatetime(java.sql.Timestamp createtime) {
    this.createtime = createtime;
  }


  public java.sql.Timestamp getUpdatetime() {
    return updatetime;
  }

  public void setUpdatetime(java.sql.Timestamp updatetime) {
    this.updatetime = updatetime;
  }


  public java.sql.Timestamp getDowntime() {
    return downtime;
  }

  public void setDowntime(java.sql.Timestamp downtime) {
    this.downtime = downtime;
  }


  public long getIslive() {
    return islive;
  }

  public void setIslive(long islive) {
    this.islive = islive;
  }

}
