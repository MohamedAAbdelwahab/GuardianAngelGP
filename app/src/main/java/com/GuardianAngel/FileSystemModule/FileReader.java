package com.GuardianAngel.FileSystemModule;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class FileReader {
    private static final String time="time.txt";
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
    public  void AppendToFile(Context context,String data,String FileName)  {
        File file = new File(mStoreDir +   FileName);
        FileWriter fr = null;
        BufferedWriter br = null;
        try {
            // to append to file, you need to initialize FileWriter using below constructor
            fr = new FileWriter(file, true);
            br = new BufferedWriter(fr);
            for (int i = 0; i < 1; i++) {
                br.newLine();
                // you can use write or append method
                br.write(data);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        }
        public long  CountStatsAllTime(String fileName) throws IOException {
            FileInputStream fis = null;
            File myObj = new File(mStoreDir+fileName);
            try {
                myObj.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fis = new FileInputStream(mStoreDir + fileName);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            InputStreamReader isr = new InputStreamReader(fis);

            long lines = 0;
            try (BufferedReader reader = new BufferedReader(isr)) {
                String Line=reader.readLine();
                while (Line != null )
                {
                    if(!Line.trim().isEmpty())
                        lines++;
                    Line=reader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(lines);
            return lines;
        }
    public  void writeTime(Context context,String data1,String data2)  {

        try {
            fos = new FileOutputStream(mStoreDir +   time);
            fos.write(data1.getBytes());
            fos.write("\n".getBytes());
            fos.write(data2.getBytes());
            Log.e("hi","done"+context.getFilesDir()+time);
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
    public Pair<Long,Long> readTime(){
        FileInputStream fis = null;
        File myObj = new File(mStoreDir+time);
       try {
            myObj.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fis = new FileInputStream(mStoreDir + time);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String today = null;
        long Today = 0;
        long Total = -1;
        InputStreamReader isr = new InputStreamReader(fis);
        try (BufferedReader reader = new BufferedReader(isr)) {
            String Line=reader.readLine();
            while (Line != null )
            {
                if(!Line.trim().isEmpty() && today == null)
                    today = Line;
                else if(!Line.trim().isEmpty() && Total == -1)
                    Total = Integer.parseInt(Line);
                Line=reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(today != null){
            String[] t = today.split(" ");
            Today = Integer.parseInt(t[0]);
            String date = t[1];
            Date day = null;
            try {
                day = new SimpleDateFormat("yyyy/MM/dd").parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(day);
            cal2.setTime(new Date());
            if (!(cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))){
                Total+=Today;
                Today = 0;
            }
        }else {
            Today = 0;
        }
        if(Total == -1)
            Total = 0;

        return new Pair<>(Today,Total);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public long  CountStatsOFDay(String fileName)
    {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(mStoreDir + fileName);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd");
        Date c = Calendar.getInstance().getTime();

        String today= format.format(c);
        long lines = 0;
        try (BufferedReader reader = new BufferedReader(isr)) {
            String Line=reader.readLine();
            while (Line != null )
            {
                if(Line.contains(today))
                lines++;
                Line=reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    }


