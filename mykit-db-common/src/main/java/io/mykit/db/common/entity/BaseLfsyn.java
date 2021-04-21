package io.mykit.db.common.entity;

/**
 * @program: mykit-db-sync
 * @description: 同步表，联合主键
 * @author: bjchen
 * @create: 2021-04-21
 **/
public class BaseLfsyn {
    /***
     * @Description:目的数据表(若srcsql不设置，则默认源和目标同表)
     */
    private String desttable;

    /***
     * @Description:'目标表主键'
     */
    private String destTablekey;

    public String getDesttable() {
        return desttable;
    }

    public void setDesttable(String desttable) {
        this.desttable = desttable;
    }

    public String getDestTablekey() {
        return destTablekey;
    }

    public void setDestTablekey(String destTablekey) {
        this.destTablekey = destTablekey;
    }
}
