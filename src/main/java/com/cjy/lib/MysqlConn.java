package com.cjy.lib;

import java.sql.*;
import java.util.*;

public class MysqlConn {
    private Connection conn = null;
    PreparedStatement statement = null;
    private String url = "jdbc:mysql://103.76.60.42:3306/rail_beijing?characterEncoding=UTF-8&useSSL=true";
    private String username = "emer";
    private String password = "emer123456";

    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
        }
        // 捕获加载驱动程序异常
        catch (ClassNotFoundException cnfex) {
            System.err.println("装载 JDBC/ODBC 驱动程序失败。");
            cnfex.printStackTrace();
        }
        // 捕获连接数据库异常
        catch (SQLException sqlex) {
            System.err.println("无法连接数据库");
            sqlex.printStackTrace();
        }
    }

    // 关闭数据库
    public void close() {
        try {
            if (conn != null) {
                conn.close();
            }
            if(statement != null) {
                statement.close();
            }
        } catch (Exception e) {
            System.out.println("关闭数据库异常：");
            e.printStackTrace();
        }
    }

    /**
     * 执行查询sql语句
     *
     * @param sql
     * @return
     */
    public List<Dictionary<String, String>> select(String sql, String[] columns) {
        ResultSet rs = null;
        List<Dictionary<String, String>> result = new ArrayList<Dictionary<String, String>>();;
        try {
            statement = conn.prepareStatement(sql);
            rs = statement.executeQuery(sql);

            while(rs.next()){
                Dictionary<String, String> tmp = new Hashtable<String, String>();
                for(int i=0;i<columns.length;i++){
                    String key = columns[i];
                    tmp.put(key, rs.getString(key));
                }
                result.add(tmp);
            }
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 执行插入sql语句
     *
     * @param sql
     * @return
     */
    public boolean insert(String sql) {
        try {
            statement = conn.prepareStatement(sql);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("插入数据库时出错：");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("插入时出错：");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 执行删除sql语句
     *
     * @param sql
     * @return
     */
    public boolean delete(String sql) {
        try {
            statement = conn.prepareStatement(sql);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("删除数据库时出错：");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("删除时出错：");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 执行更新sql语句
     *
     * @param sql
     * @return
     */
    public boolean update(String sql) {
        try {
            statement = conn.prepareStatement(sql);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("更新数据库时出错：");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("更新时出错：");
            e.printStackTrace();
        }
        return false;
    }

}
