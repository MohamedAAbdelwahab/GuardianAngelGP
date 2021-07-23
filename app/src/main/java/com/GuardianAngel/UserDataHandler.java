package com.GuardianAngel;

import android.content.Context;

import androidx.room.Room;

import com.GuardianAngel.FileSystemModule.AppDatabase;
import com.GuardianAngel.FileSystemModule.User;

public class UserDataHandler {
    AppDatabase db ;
    PasswordHash hasher=new PasswordHash();
    UserDataHandler(Context context)
    {
        db= Room.databaseBuilder(context,
                AppDatabase.class, "database-name").build();
    }
    public  void  register(String email,String password)
    {
        final User user=new User();
        user.Email=email;
        user.Password=hasher.hashPassword(password);
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                db.userDao().insertAll(user);

            }
        });
        thread.start();

    }
    public boolean CheckPassword(String ExpectedPassword,String ActualPassword)
    {
        return hasher.checkPassword(ExpectedPassword, ActualPassword);
    }
    public String getUserEmail()
    {
        return db.userDao().GetUserEmail();
    }
    public String getUserPassword()
    {
        return db.userDao().GetUserPassword();
    }
    public void updatePassword(String password,int id)
    {

        db.userDao().updatePassword(hasher.hashPassword(password),id);
    }
    public void updateEmail(String email,int id)
    {

        db.userDao().updateEmail(email,id);
    }
}
