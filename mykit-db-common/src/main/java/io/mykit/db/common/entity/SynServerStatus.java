package io.mykit.db.common.entity;


import java.sql.Timestamp;

public class SynServerStatus {

  /***
  * @Description: 自增id，主机每次down->up 自增
  */
  private Long id;
  /***
   * @Description:创建islive时间(主库维护)
   */
  private Timestamp createtime;
  /***
  * @Description:更新islive时间(主库维护)
  */
  private Timestamp updatetime;
  /***
   * @Description:主库宕机时间(备库维护)
   */
  private Timestamp downtime;
  /***
   * @Description:主机状态(主备调维护 0：运行，1：宕机)
   */
  private Integer islive;

  public SynServerStatus() {
  }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Timestamp createtime) {
        this.createtime = createtime;
    }

    public Timestamp getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Timestamp updatetime) {
        this.updatetime = updatetime;
    }

    public Timestamp getDowntime() {
        return downtime;
    }

    public void setDowntime(Timestamp downtime) {
        this.downtime = downtime;
    }

    public Integer getIslive() {
        return islive;
    }

    public void setIslive(Integer islive) {
        this.islive = islive;
    }
}
