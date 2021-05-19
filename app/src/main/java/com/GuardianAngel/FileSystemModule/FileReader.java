package com.GuardianAngel.FileSystemModule;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileReader {
    private static final String PasswordFileName="PasswordFile.txt";
    private static final String EmailFileName="EmailFile.txt";
    private String mStoreDir;
    FileOutputStream fos=null;

    private static final String TAG = "FileReaderClass";
   public FileReader(Context context){

        File externalFilesDir = context.getExternalFilesDir(null);
        if (externalFilesDir != null) {
            mStoreDir = externalFilesDir.getAbsolutePath() + "/Data/";
            File storeDirectory = new File(mStoreDir);
            if (!storeDirectory.exists()) {
                boolean success = storeDirectory.mkdirs();
                if (!success) {
                    Log.e(TAG, "failed to create file storage directory.");
                }
            }
        } else {
            Log.e(TAG, "failed to create file storage directory, getExternalFilesDir is null.");
        }

}
   public  void writeFile(Context context,String data,String FileName)  {

       try {
           fos = new FileOutputStream(mStoreDir +   FileName);
           fos.write(data.getBytes());
           Log.e("hi","done"+context.getFilesDir()+FileName);
       } catch (FileNotFoundException ex) {
           ex.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
           if(fos!=null) {
               try {
                   fos.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }

   }
    public String ReadFile(Context context,String FileName)  {
       String data=null;
        FileInputStream fis=null;
        try {
            fis=new FileInputStream(mStoreDir +   FileName);
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br=new BufferedReader(isr);
            StringBuilder sb=new StringBuilder();
            String text;
            while ((text=br.readLine() )!=null)
            {
                sb.append(text);

            }
            Log.i("mytext",sb.toString());
            data=sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(fis!=null)
            {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }
    public boolean isExists(Context context)
    {


        File f = new File(mStoreDir+PasswordFileName);
        Log.e("file",f.toString());
        return f.exists();

    }

}
