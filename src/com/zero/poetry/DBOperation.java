package com.zero.poetry;

import com.zero.poetry.bean.PoetryBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBOperation {

    private static Connection connection = DBConnect.getConnection();

    public void insert(PoetryBean poetry, String about, String authorInfo, int grade, int semester, int expand, int type, int obligatory) throws Exception{
        String sql = "INSERT INTO poetry (`author`,`dynasty`,`poetry`,`about`,`author_info`,`grade`,`semester`,`expand`,`type`,`obligatory`,`tag`) " +
                "VALUE (?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        ps = connection.prepareStatement(sql);
        ps.setString(1, poetry.getAuthor());
        ps.setString(2, poetry.getDynasty());
        ps.setString(3, poetry.getContent());
        ps.setString(4, about);
        ps.setString(5, authorInfo);
        ps.setInt(6, grade);
        ps.setInt(7, semester);
        ps.setInt(8, expand);
        ps.setInt(9, type);
        ps.setInt(10, obligatory);
        ps.setString(11, poetry.getTag());
        ps.execute();
    }

    public static void query() throws Exception{
        String sql = "SELECT * FROM poetry WHERE id = 100";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()){
            String poetry = rs.getString(4);
            System.out.println(poetry);
            String about = rs.getString(5);
            System.out.println(about);
        }
    }

    public static void main(String[] args) throws Exception {
        query();
    }
}
