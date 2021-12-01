package com.cuit.mete.DButils;


import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DruidDBOper {
    private static final Logger logger = Logger.getLogger(DruidDBOper.class);
    /**
     * 返回多行查询结果
     *
     * @param sql    SQL语句
     * @param t      类，如 Student.class,  UserLog.class
     * @param values SQL参数值
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> queryRows(String sql, Class<T> t, Object... values) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<T> list = null;
        try {
            conn = DruidJDBCUtils.getConnection();
            pst = conn.prepareStatement(sql);
            for (int i = 1; i <= values.length; i++) {
                pst.setObject(i, values[i - 1]);
            }
            rs = pst.executeQuery();
            list = new ArrayList<>();
            while (rs.next()) {
                T t2 = t.newInstance();
                Map<String, Object> map = getMap(rs);
                
                DateTimeConverter dtConverter = new DateTimeConverter();
            	ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
            	
            	convertUtilsBean.deregister(Date.class);
            	convertUtilsBean.register(dtConverter, Date.class);
            	
            	convertUtilsBean.deregister(Timestamp.class);
            	convertUtilsBean.register(dtConverter, Timestamp.class);
            	
            	BeanUtilsBean beanUtilsBean = new BeanUtilsBean(convertUtilsBean, new PropertyUtilsBean());
            	beanUtilsBean.populate(t2, map);
                
                list.add(t2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DruidJDBCUtils.close(rs, pst, conn);
        }

        return list;
    }
    
    public <T> List<T> queryRowsByName(String sql, Class<T> t, String colName, Object... values) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<T> list = null;
        try {
            conn = DruidJDBCUtils.getConnection();
            pst = conn.prepareStatement(sql);
            for (int i = 1; i <= values.length; i++) {
                pst.setObject(i, values[i - 1]);
            }
            rs = pst.executeQuery();
            list = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> map = getMap(rs);
                T t2 = (T) map.get(colName);
                list.add(t2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DruidJDBCUtils.close(rs, pst, conn);
        }
        return list;
    }
    
    public <T> T queryOneRowByName(String sql, Class<T> t, String colName, Object... values) {
    	Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        T t2 = null;
        try {
            conn = DruidJDBCUtils.getConnection();
            pst = conn.prepareStatement(sql);
            for (int i = 1; i <= values.length; i++) {
                pst.setObject(i, values[i - 1]);
            }
            rs = pst.executeQuery();
            if (rs.next()) {
                Map<String, Object> map = getMap(rs);
                t2 = (T) map.get(colName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DruidJDBCUtils.close(rs, pst, conn);
        }
        return t2;
    }
    

    /**
     * 查询，返回一行
     *
     * @param sql    SQL语
     * @param t      类，如 Student.class,  UserLog.class
     * @param values SQL参数值
     * @param <T>
     * @return 对象（T类）
     * @throws Exception
     */
    public <T> T queryOneRow(String sql, Class<T> t, Object... values) {
         /*
    实现思路：
        查询出结果集 rs
         T trs =t.newInstance();   调用无参的构造函数实例化一个对象
    	 Map<String, Object> map = getMap(rs);  //调用后面方法，将结果集封装为Map
         BeanUtils.populate(trs, map);   //将结果集对对象赋值

         返回 trs
     */

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        T t2 = null;
        try {
            conn = DruidJDBCUtils.getConnection();
            pst = conn.prepareStatement(sql);
            for (int i = 1; i <= values.length; i++) {
                pst.setObject(i, values[i - 1]);
            }
            rs = pst.executeQuery();
            if (rs.next()) {
                t2 = t.newInstance();
                Map<String, Object> map = getMap(rs);
                BeanUtils.populate(t2, map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            DruidJDBCUtils.close(rs, pst, conn);
        }
        return t2;
    }

    public <T> T queryOneRow(Connection conn, String sql, Class<T> t, Object... values) {
        PreparedStatement pst = null;
        ResultSet rs = null;
        T t2 = null;
        try {
            conn = DruidJDBCUtils.getConnection();
            pst = conn.prepareStatement(sql);
            for (int i = 1; i <= values.length; i++) {
                pst.setObject(i, values[i - 1]);
            }
            rs = pst.executeQuery();
            if (rs.next()) {
                t2 = t.newInstance();
                Map<String, Object> map = getMap(rs);
                BeanUtils.populate(t2, map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            DruidJDBCUtils.close(rs, pst, conn);
        }
        return t2;
    }

    /**
     * 保存一组数据
     *
     * @param tClass
     * @param tList  多个数据List
     * @param <T>
     * @return
     */
    public <T> boolean InsertList2DB(Class<T> tClass, List<T> tList) {
        boolean retFlag = false;
        Connection conn = null;
        PreparedStatement pst = null;
        //数据段有可脂为空
        if (tList != null && tList.size() >= 1) {
            //数据插入
            String sql = null;
            try {
                conn = DruidJDBCUtils.getConnection();
                sql = getInsertSql(tClass, tList.get(0));
                pst = conn.prepareStatement(sql);
                retFlag = true;
                for (T dt : tList) {
                    pst.clearParameters();

                    setSQLParameter(tClass, dt, pst);
                    try {
                        if (pst.executeUpdate() != 1) {
                            retFlag = false;
                            break;
                        }
                    } catch (Exception e) {
                        logger.debug("插入数据时失败：SQL:" + sql + "错误信息：" + e.getMessage());
                        retFlag = false;
                        break;
                    }
                }
            } catch (NoSuchMethodException e) {
                logger.debug("插入数据时失败1：" + e.getMessage());
            } catch (InvocationTargetException e) {
                logger.debug("插入数据时失败2：" + e.getMessage());
            } catch (IllegalAccessException e) {
                logger.debug("插入数据时失败3：" + e.getMessage());
            } catch (SQLException e) {
                logger.debug("插入数据时失败4：" + e.getMessage());
            } finally {
                DruidJDBCUtils.close(pst, conn);
            }
        }
        return retFlag;
    }
    
    public boolean InsertList2DB(String sql, List<Object[]> data){
    	Connection conn = null;
    	PreparedStatement preparedStatement = null;
    	try {
			conn = DruidJDBCUtils.getConnection();
			//关闭自动提交
			conn.setAutoCommit(false);
			preparedStatement = conn.prepareStatement(sql);
			
			//如果列表太多，分段执行
			for(int k = 0; k < data.size(); k++){
				Object[] params = data.get(k);
				for(int i = 0; i < params.length; i++){
					Object obj = params[i];
					preparedStatement.setObject(i + 1, obj);
				}
				preparedStatement.addBatch();
				
				//每50条执行一次
				if(k % 50 == 0 || k == data.size() - 1){
					preparedStatement.executeBatch();
				}
			}
			
			//提交结果
			conn.commit();
			
			return true;
		} catch (SQLException e) {
			//回滚
			if(conn != null){
				try {
					conn.rollback();
				} catch (SQLException e1) {
					logger.error("回滚失败：" + e.getMessage(), e);
				}
			}
			
			logger.error("批量插入数据失败：" + e.getMessage(), e);
			return false;
		}finally {
			 DruidJDBCUtils.close(preparedStatement, conn);
		}
    }
    
    public <T> boolean Insert2DB(Class<T> tclass, T data, String table){
    	 boolean retFlag = true;
         Connection conn = null;
         PreparedStatement pst = null;
         //数据可脂为空
         if (data != null) {
             //数据插入
             String sql = null;
             try {
                 conn = DruidJDBCUtils.getConnection();
                 sql = getInsertNoNullFieldsSql(table, data);
                 pst = conn.prepareStatement(sql);
                 setNoNullFieldsSQLParameter(tclass, data, pst);
                 try {
                     if (pst.executeUpdate() != 1) {
                         retFlag = false;

                     }
                 } catch (Exception e) {
                     logger.debug("插入数据时失败：SQL:" + sql + "错误信息：" + e.getMessage());
                     retFlag = false;

                 }

             } catch (NoSuchMethodException e) {
                 logger.debug("插入数据时失败1：" + e.getMessage());
             } catch (InvocationTargetException e) {
                 logger.debug("插入数据时失败2：" + e.getMessage());
             } catch (IllegalAccessException e) {
                 logger.debug("插入数据时失败3：" + e.getMessage());
             } catch (SQLException e) {
                 logger.debug("插入数据时失败4：" + e.getMessage());
             } finally {
                 DruidJDBCUtils.close(pst, conn);
             }
         }
         return retFlag;
    }

    /**
     * 保存单条数据
     * @param tClass
     * @param data
     * @param <T>
     * @return
     */
    public <T> boolean Insert2DB(Class<T> tClass, T data) {
        boolean retFlag = true;
        Connection conn = null;
        PreparedStatement pst = null;
        //数据可脂为空
        if (data != null) {
            //数据插入
            String sql = null;
            try {
                conn = DruidJDBCUtils.getConnection();
                sql = getInsertNoNullFieldsSql(tClass, data);
                pst = conn.prepareStatement(sql);
                setNoNullFieldsSQLParameter(tClass, data, pst);
                try {
                    if (pst.executeUpdate() != 1) {
                        retFlag = false;

                    }
                } catch (Exception e) {
                	e.printStackTrace();
                    logger.debug("插入数据时失败：SQL:" + sql + "错误信息：" + e.getMessage());
                    retFlag = false;

                }

            } catch (NoSuchMethodException e) {
                logger.debug("插入数据时失败1：" + e.getMessage());
            } catch (InvocationTargetException e) {
                logger.debug("插入数据时失败2：" + e.getMessage());
            } catch (IllegalAccessException e) {
                logger.debug("插入数据时失败3：" + e.getMessage());
            } catch (SQLException e) {
                logger.debug("插入数据时失败4：" + e.getMessage());
            } finally {
                DruidJDBCUtils.close(pst, conn);
            }
        }
        return retFlag;
    }



    /**
     * 执行SQL存储过程
     *
     * @param pro    存储过程名
     * @param values 预编译SQL对象 SetObject
     * @return 影响行数
     * @throws Exception
     */
    public boolean excutePro(String pro, Object... values) {

        Connection conn = null;
        PreparedStatement pst = null;
        String sql = "{call "+pro+"(";
        for (int i = 1; i <= values.length; i++) {
            sql += "?,";
        }
        if (sql.endsWith("?,")) sql = sql.substring(0,sql.length()-1);
        sql += ")}";

        boolean res = false;
        try {
            conn = DruidJDBCUtils.getConnection();
            pst = conn.prepareCall(sql);
            for (int i = 1; i <= values.length; i++) {
                pst.setObject(i, values[i - 1]);
            }
            res = pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DruidJDBCUtils.close(pst, conn);
        }
        return res;
    }


    /**
     * 执行SQL更新、插入、删除  UID
     *
     * @param sql    SQL语句
     * @param values 预编译SQL对象 SetObject
     * @return 影响行数
     * @throws Exception
     */
    public int excuteUpdate(String sql, Object... values) {

        Connection conn = null;
        PreparedStatement pst = null;
        int res = 0;
        try {
            conn = DruidJDBCUtils.getConnection();
            pst = conn.prepareStatement(sql);
            for (int i = 1; i <= values.length; i++) {
                pst.setObject(i, values[i - 1]);
            }
            res = pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DruidJDBCUtils.close(pst, conn);
        }
        return res;
    }

    public int excuteUpdate(Connection conn, String sql, Object... values) {
        PreparedStatement pst = null;
        int res = 0;
        try {
            pst = conn.prepareStatement(sql);
            for (int i = 1; i <= values.length; i++) {
                pst.setObject(i, values[i - 1]);
            }
            res = pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DruidJDBCUtils.close(pst);
        }
        return res;
    }

    /**
     * 结果集转成Map（Key，值 ）
     *
     * @param rs 结果集
     * @return
     * @throws SQLException
     */
    public Map<String, Object> getMap(ResultSet rs) throws SQLException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        ResultSetMetaData rsmd = rs.getMetaData();

        int count = rsmd.getColumnCount();
        for (int i = 1; i <= count; i++) {
            String key = rsmd.getColumnName(i).toLowerCase();
            if(rs.getObject(i) != null){
            	map.put(key, rs.getObject(i));
            }
        }
        return map;
    }

    /**
     * 取插入误句SQL(不包括数据为NULL的字段）
     * 注意：用在插入List的时候，可能不同的List成员的空值不同，仅适用于单行
     * @param tClass
     * @param t
     * @param <T>
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    protected <T> String getInsertNoNullFieldsSql(Class<T> tClass, T t) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = tClass.getMethod("getTableName");
        Object obj = method.invoke(t);

        return getInsertNoNullFieldsSql(obj + "", t);
    }
    
    protected <T> String getInsertNoNullFieldsSql(String table, T t){
    	String sql = "insert into " + table + "(";
        String param = "";
        Field[] fields = t.getClass().getDeclaredFields();
        try {
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                Class<?> type = fields[i].getType();
                if (fields[i].get(t) != null) {
                    if ("int".equals(type.getName()) && (int) fields[i].get(t) == -1)
                        continue;
                    sql += fields[i].getName() + ",";
                    param += "?,";
                }
            }
            sql = sql.substring(0, sql.length() - 1) + ") values(" + param;
            sql = sql.substring(0, sql.length() - 1) + ")";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sql;
    }


    /**
     * 参数设置
     * 注意：用在插入List的时候，可能不同的List成员的空值不同，仅适用于单行
     * @param tClass
     * @param t
     * @param ps
     * @param <T>
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws SQLException
     */
    protected <T> void setNoNullFieldsSQLParameter(Class<T> tClass, T t, PreparedStatement ps) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLException {
        Field[] fields = tClass.getDeclaredFields();
        int c = 1;
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Class<?> type = fields[i].getType();
            //填坑
            if (fields[i].get(t) != null) {
                if ("int".equals(type.getName()) && (int) fields[i].get(t) == -1)
                    continue;
                String fieldName = fields[i].getName();
                Method method = tClass.getMethod(getGetter(fieldName));
                Object obj = method.invoke(t);
                ps.setObject(c, obj);
                fields[i].setAccessible(false);
                c++;
            }
        }
    }
    

    protected <T> String getInsertSql(Class<T> tClass, T t) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = tClass.getMethod("getTableName");
        Object obj = method.invoke(t);

        String sql = "insert into " + obj + "(";
        String param = "";
        Field[] fields = tClass.getDeclaredFields();
        try {
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                Class<?> type = fields[i].getType();
                //if (fields[i].get(t) != null) {
                Object value = fields[i].get(t);
                if(value == null)
                	continue;
                
                if ("int".equals(type.getName()) && (int) fields[i].get(t) == -1)
                    continue;
                
                sql += fields[i].getName() + ",";
                param += "?,";
                //}
            }
            sql = sql.substring(0, sql.length() - 1) + ") values(" + param;
            sql = sql.substring(0, sql.length() - 1) + ")";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sql;
    }


    protected <T> void setSQLParameter(Class<T> tClass, T t, PreparedStatement ps) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLException {
        Field[] fields = tClass.getDeclaredFields();
        int c = 1;
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Class<?> type = fields[i].getType();
            //填坑
            //if (fields[i].get(t) != null) {
            Object obj = fields[i].get(t);
            if(obj == null)
            	continue;
            
            if ("int".equals(type.getName()) && (int) fields[i].get(t) == -1)
                continue;
//            String fieldName = fields[i].getName();
//            Method method = tClass.getMethod(getGetter(fieldName));
//            Object obj = method.invoke(t);
            
            ps.setObject(c, obj);

            fields[i].setAccessible(false);
            c++;
            //}
        }
    }


    //获取get方法的方法名
    protected static String getGetter(String fieldName) {
        //传入属性名 拼接set方法
        String temp;
        temp = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return temp;
    }

}