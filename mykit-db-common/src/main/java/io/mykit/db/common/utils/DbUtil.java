package io.mykit.db.common.utils;

import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

/**
 * @program: mykit-db-sync
 * @description: 与数据库操作相关的类
 * @author: bjchen
 * @create: 2021-04-20
 **/
public class DbUtil {
    public static QueryRunner qr = new QueryRunner();

    /***
    * @Description: 以sql为键，
    */
    public static HashMap<String,PreparedStatement> reusePsmMap =null;

    /***
     * @Description:私有化构造器
     */
    private DbUtil(){
    }

    /***
    * @Description:
    * @Param: [conn, sql]
    * @Date: 2021/4/25
    */
    private  static PreparedStatement getPsm(Connection conn, String sql) {
        PreparedStatement psm =null;
        try {
            psm = conn.prepareStatement(sql);
        }catch (Exception e){
            e.printStackTrace();
        }
        return psm;
    }

    /***
    * @Description: 通过此方法获取psm
    * @Param: [conn, sql]
    * @return: java.sql.PreparedStatement
    * @Author: bjchen
    * @Date: 2021/4/25
    */
    public static PreparedStatement getInstancePsm(Connection conn,String sql){
        if(reusePsmMap==null){
            reusePsmMap =new HashMap<>(16);
        }else if(reusePsmMap.get(sql)!=null) {
            //有缓存
            return reusePsmMap.get(sql);
        }
        //未缓存过，实例化
        PreparedStatement psm = getPsm(conn, sql);
        reusePsmMap.put(sql,psm);
        return psm;
    }

}
