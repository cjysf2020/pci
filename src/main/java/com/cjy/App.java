package com.cjy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    private static Connection conn = null;

    private static String driver = "oracle.jdbc.driver.OracleDriver"; //驱动

    private static String url = "jdbc:oracle:thin:@//103.76.60.43:1521/orcl"; //连接字符串

    private static String username = "metroT"; //用户名

    private static String password = "metro241";

    public static void main(String[] args) throws SQLException {
        System.out.println("Hello World");
        conn = DriverManager.getConnection(url, username, password);
        System.out.println(conn);
        conn.close();
    }
}
