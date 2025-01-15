package com.example.psswd.Server.dao.sqlite;

import com.example.psswd.Server.dao.PasswordsDao;
import com.example.psswd.Server.model.Password;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Template for data access objects for passwords table
 */
public class SqliteDataSourcePasswordsDAOImpl implements PasswordsDao {

    /**
     * Instance of SqliteDataSourceDAOFactory
     */
    private static final SqliteDataSourceDAOFactory daoFactory = SqliteDataSourceDAOFactory.getInstance();

    @Override
    public List<Password> getPasswords() {
        String query = """
                SELECT * FROM passwords;
                """;
        List<Password> passwords = new ArrayList<>();

        try(Connection conn = daoFactory.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Password password = new Password();
                password.setId(rs.getInt("id"));
                password.setName(rs.getString("name"));
                password.setUrl(rs.getString("url"));
                password.setPassword(rs.getBytes("password"));
                passwords.add(password);
            }

        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return passwords;
    }

    @Override
    public void insertPassword(Password password) {
        String query = """
                INSERT INTO passwords ("name", "url", "password")
                VALUES (?, ?, ?);
                """;
        try(Connection conn = daoFactory.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, password.getName());
            ps.setString(2, password.getUrl());
            ps.setBytes(3, password.getPassword());
            ps.executeUpdate();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void updatePassword(int id, Password password) {
        String query = """
                UPDATE passwords
                SET "name" = ?,
                    "url" = ?,
                    "password" = ?
                WHERE id = ?;
                """;
        try(Connection conn = daoFactory.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, password.getName());
            ps.setString(2, password.getUrl());
            ps.setBytes(3, password.getPassword());
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void deletePassword(int id) {
        String query = """
                DELETE FROM passwords
                WHERE id = ?;
                """;
        try(Connection conn = daoFactory.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
