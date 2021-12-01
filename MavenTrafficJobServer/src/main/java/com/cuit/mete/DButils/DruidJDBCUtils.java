package com.cuit.mete.DButils;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.cuit.job.utils.ConstantUtil;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

/**
 * Druid是一个JDBC组件库，包括数据库连接池、SQL Parser等组件。DruidDataSource是最好的数据库连接池。
 */
public class DruidJDBCUtils {
    private static final Logger logger = Logger.getLogger(DruidJDBCUtils.class);
    //1.定义成员变量 DataSource
    private static DataSource ds = null;
    private static Properties pro = null;

    static {
        try {
            //1.加载配置文件

            pro = getDBConfigPro();
            if (pro == null) {
                //数据库配置不正确，返回
                throw new Exception("数据库配置文件未设置");
            }

            //2.获取DataSource
            ds = DruidDataSourceFactory.createDataSource(pro);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接
     */
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    /**
     * 释放资源，释放数据库连接，不是关闭数据库连接，而是放回了数据库连接池中
     */
    public static void close(Statement stmt, Connection conn) {
        close(null, stmt, conn);
    }


    /**
     * 释放资源,释放数据库连接，不是关闭数据库连接，而是放回了数据库连接池中
     *
     * @param rs
     * @param stmt
     * @param conn
     */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                conn.close();//归还连接
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(ResultSet rs, Statement stmt) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取连接池方法
     */
    public static DataSource getDataSource() {
        return ds;
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 提交
     *
     * @param conn
     */
    public static void commit(Connection conn) {
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException e) {
                System.out.println("事务处理数据提交时出错：" + e.getMessage());
                //e.printStackTrace();
            }
        }
    }

    /**
     * 回滚数据
     *
     * @param conn
     */
    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                //e.printStackTrace();
                System.out.println("事务处理数据回滚时出错：" + e.getMessage());
            }
        }
    }

    private static Properties getDBConfigPro() {
        Properties dbpro = new Properties();
        //先取数据库配置文件
        Map<String, Object> dbConfigList = (Map<String, Object>) ConstantUtil.get("dbconfig");
        if (dbConfigList == null || dbConfigList.size() < 3) {
            logger.debug("数据库未配置，无法运行！");
            dbpro = null;
        }

        String driverClassName = (String) dbConfigList.get("driverClassName");
        if (driverClassName != null) {
            dbpro.setProperty("driverClassName", driverClassName);
        } else {
            logger.debug("数据库未配置正确，driverClassName未配置！");
            dbpro = null;
        }

        String url = (String) dbConfigList.get("url");
        if (url != null) {
            dbpro.setProperty("url", url);
        } else {
            logger.debug("数据库未配置正确，url未配置！");
            dbpro = null;
        }

        String username = (String) dbConfigList.get("username");
        if (username != null) {
            dbpro.setProperty("username", username);
        } else {
            logger.debug("数据库未配置正确，username未配置！");
            dbpro = null;
        }

        String password = (String) dbConfigList.get("password");
        if (password != null) {
            dbpro.setProperty("password", password);
        }

        String initialSize = (String) dbConfigList.get("initialSize");
        if (initialSize != null) {
            dbpro.setProperty("initialSize", initialSize);
        }
        String maxActive = (String) dbConfigList.get("maxActive");
        if (maxActive != null) {
            dbpro.setProperty("maxActive", maxActive);
        }
        String maxWait = (String) dbConfigList.get("maxWait");
        if (maxWait != null) {
            dbpro.setProperty("maxWait", maxWait);
        }
        System.setProperty("druid.mysql.usePingMethod", "false");
        return dbpro;
    }
}

