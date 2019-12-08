package com.zero.poetry;

import com.zero.poetry.bean.PoetryBean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {

    private final static String URL = "jdbc:mysql://localhost:3306/poetry?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF8";

    private final static String USER = "root";

    private final static String PASSWORD = "root";

    private static Connection connection=null;

    static{
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection=DriverManager.getConnection(URL, USER, PASSWORD);//地址，用户名，密码
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection(){
        return connection;
    }



}
