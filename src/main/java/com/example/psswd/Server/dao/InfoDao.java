package com.example.psswd.Server.dao;

import com.example.psswd.Server.model.Info;

/**
 * Contract defining methods that access account info table in the database
 */
public interface InfoDao {

    /**
     * Adds account info to the database
     * @param info account info to add
     */
    public void insertInfo(Info info);

    /**
     * Deletes account info, required for the user password change
     */
    public void deleteInfo();

    /**
     * Gets account info from the database
     */
    public Info getInfo();
}

