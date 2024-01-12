package com.example.psswd.dao;

import com.example.psswd.model.Password;

import java.util.List;

public interface PasswordsDao {
    public List<Password> getPasswords();
    public void insertPassword(Password password);
    public void updatePassword(int id, Password password);
    public void deletePassword(int id);
}
