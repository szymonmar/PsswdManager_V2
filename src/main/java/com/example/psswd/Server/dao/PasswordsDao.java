package com.example.psswd.Server.dao;

import com.example.psswd.Server.model.Password;

import java.util.List;

/**
 * Contract defining methods that access passwords table in the database
 */
public interface PasswordsDao {

    /**
     * Gets list of passwords from the database
     * @return list of passwords
     */
    public List<Password> getPasswords();

    /**
     * Adds new password to the database
     * @param password new password entity
     */
    public void insertPassword(Password password);

    /**
     * Updates password entity in the database
     * @param id id of the password to update
     * @param password modified password entity
     */
    public void updatePassword(int id, Password password);

    /**
     * Deletes password from the database
     * @param id id of the password to delete
     */
    public void deletePassword(int id);
}
