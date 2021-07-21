package com.GuardianAngel.FileSystemModule;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey
    public int uid;
    @ColumnInfo(name = "Password")
    public String Password;
    @ColumnInfo(name = "Email")
    public String Email;



}
