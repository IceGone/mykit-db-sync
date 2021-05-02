package io.mykit.db.common.db;

import io.mykit.db.common.entity.BaseDBInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @description 实现数据库的连接与关闭
 */
public class DbConnection {

    private final Logger logger = LoggerFactory.getLogger(DbConnection.class);
    /**
     * 创建数据库连接
     */
    protected Connection getConnection(String dbType, BaseDBInfo db) {
        try {
            Connection connection = DataSourceFactory.getDruidDataSource(dbType, db).getConnection();
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭并销毁数据库连接
     */
    protected void destoryConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
                conn = null;
                this.logger.info("数据库连接关闭");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
