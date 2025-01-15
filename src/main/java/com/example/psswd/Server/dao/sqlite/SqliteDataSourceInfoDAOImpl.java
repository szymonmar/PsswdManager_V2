package com.example.psswd.Server.dao.sqlite;

import com.example.psswd.Server.dao.InfoDao;
import com.example.psswd.Server.model.Info;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Template for data access objects for info table
 */
public class SqliteDataSourceInfoDAOImpl implements InfoDao {

    @Override
    public void insertInfo(Info info) {
        String query = """
                INSERT INTO info ("name", "challenge", "salt")
                VALUES (?, ?, ?);
                """;
        SqliteDataSourceDAOFactory daoFactory = SqliteDataSourceDAOFactory.getInstance();
        try(Connection conn = daoFactory.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, info.getName());
            ps.setBytes(2, info.getChallenge());
            ps.setBytes(3, info.getSalt());
            ps.executeUpdate();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void deleteInfo() {
        String query = """
                DELETE FROM info;
                """;
        SqliteDataSourceDAOFactory daoFactory = SqliteDataSourceDAOFactory.getInstance();
        try {
            Connection conn = daoFactory.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            System.out.println(ps.executeUpdate());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Info getInfo() {
        String query = """
                SELECT * FROM info LIMIT 1;
                """;
        SqliteDataSourceDAOFactory daoFactory = SqliteDataSourceDAOFactory.getInstance();
        Info info = new Info();
        try(Connection conn = daoFactory.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ResultSet rs = ps.executeQuery();
            info.setName(rs.getString("name"));
            info.setChallenge(rs.getBytes("challenge"));
            info.setSalt(rs.getBytes("salt"));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return info;
    }
}
