package com.GuardianAngel.FileSystemModule;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT Email FROM user")
    String GetUserEmail();

    @Query("SELECT Password FROM user")
    String GetUserPassword();

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    User findByName(String first, String last);

    @Insert
    void insertAll(User user);

    @Query("Select * FROM user")
    List<User> getAllUsers();
    @Query("UPDATE User SET Password=:password WHERE uid = :id")
    void updatePassword(String password, int id);
    @Query("UPDATE User SET Email=:Email WHERE uid = :id")
    void updateEmail(String Email, int id);
    @Delete
    void delete(User user);
}
