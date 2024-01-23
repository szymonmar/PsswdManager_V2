package com.example.psswd.dao.sqlite;

import com.example.psswd.dao.InfoDao;
import com.example.psswd.model.Info;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Klasa implementująca InfoDao, obsługująca dostęp do bazy danych (do tabeli zawierającej informacje o bazie danych potrzebne do odszyfrowania)
 */
public class SqliteDataSourceInfoDAOImpl implements InfoDao {

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
