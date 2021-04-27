package io.mykit.db.common.utils;

import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * @program: mykit-db-sync
 * @description: 与数据库操作相关的类
 * @author: bjchen
 * @create: 2021-04-20
 **/
public  class DbUtil {
    public static QueryRunner qr = new QueryRunner();

    /***
    * @Description: 以sql为键，psm为值
    */
    public static HashMap<String,PreparedStatement> reusePsmMap =null;
    /***
    * @Description: 以sql为键，psm为值
    */
    public static HashMap<Connection, Statement> reuseStmMap =null;


    /***
    * @Description: 根据conn和sql获取stm
    * @Param: [conn, sql]
    * @Date: 2021/4/25
    */
    private  static Statement getStm(Connection conn) {
        Statement stm =null;
        try {
            stm = conn.createStatement();
        }catch (Exception e){
            e.printStackTrace();
        }
        return stm;
    }
    /***
    * @Description: 根据conn和sql获取psm
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

    /***
    * @Description: 通过此方法获取stm
    * @Param: [conn, sql]
    * @return: java.sql.PreparedStatement
    * @Author: bjchen
    * @Date: 2021/4/25
    */
    public static Statement getInstanceStm(Connection conn){
        if(reuseStmMap==null){
            reuseStmMap =new HashMap<>(4);
        }else if(reuseStmMap.get(conn)!=null) {
            //有缓存
            return reuseStmMap.get(conn);
        }
        //未缓存过，实例化
        Statement psm = getStm(conn);
        reuseStmMap.put(conn,psm);
        return psm;
    }

    public static void executeSQL( Connection conn,String ...sqls) throws SQLException {
        PreparedStatement pst = conn.prepareStatement("");
        for(String sql:sqls){
            String[] sqlList = sql.split(";");
            for (int index = 0; index < sqlList.length; index++) {
                pst.addBatch(sqlList[index]);
            }
            pst.executeBatch();
            conn.commit();
            pst.close();
        }

    }

}
