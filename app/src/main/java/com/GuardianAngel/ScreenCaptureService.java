package com.GuardianAngel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.RequiresApi;
import androidx.annotation.WorkerThread;
import androidx.core.util.Pair;

import com.GuardianAngel.FileSystemModule.FileReader;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
@RequiresApi(api = Build.VERSION_CODES.O)
public class ScreenCaptureService extends Service {
    final DisplayMetrics metrics = new DisplayMetrics();
    private static final String TAG = "ScreenCaptureService";
    private static final String RESULT_CODE = "RESULT_CODE";
    private static final String DATA = "DATA";
    private static final String ACTION = "ACTION";
    private static final String START = "START";
    private static final String STOP = "STOP";
    private static final String SCREENCAP_NAME = "screencap";
    private static int IMAGES_PRODUCED;
    public WindowManager wm;
    public View myView;
    private MediaProjection mMediaProjection;
    private String mStoreDir;
    private ImageReader mImageReader;
    private Handler mHandler;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    private int mRotation;
    private OrientationChangeCallback mOrientationChangeCallback;
    Boolean safe=true;
    private LocalTime TimeObj;
    private DateTimeFormatter dateformatter=DateTimeFormatter.ofPattern("HH:mm:ss");
    public int i=0;
    Date d1 = null;
    Date d2 = null;
    FileReader reader;
    SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
    private boolean send=true;

    public static Intent getStartIntent(Context context, int resultCode, Intent data) {
        Intent intent = new Intent(context, ScreenCaptureService.class);
        intent.putExtra(ACTION, START);
        intent.putExtra(RESULT_CODE, resultCode);
        intent.putExtra(DATA, data);
        return intent;
    }

            @SuppressLint("HandlerLeak") Handler handler=new Handler(){
                @Override
                public void handleMessage(Message m){

                    if(m.obj.toString().equals("Blackout"))
                    {
                        Log.i("Message1",m.obj.toString());
                        WindowManager.LayoutParams windowManagerParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY  ,
                                WindowManager.LayoutParams. FLAG_DIM_BEHIND, PixelFormat.TRANSLUCENT);

                        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        myView = inflater.inflate(R.layout.blackout_activity, null);

                        wm.addView(myView, windowManagerParams);
                        removeMessages(0);
                        safe=false;
                    }
                    else if (m.obj.toString().equals("Safe")){
                        Log.i("Message2",m.obj.toString());
                        if(!safe)
                        {
                            wm.removeView(myView);
                            removeMessages(0);
                            TimerTask task = new TimerTask() {
                                public void run() {
                                    safe=true;
                                    send=true;

                                }
                            };
                            Timer timer = new Timer("Timer");

                            long delay = 200L;
                            timer.schedule(task, delay);

                        }

                    }

                }
            };


    public static Intent getStopIntent(Context context) {
        Intent intent = new Intent(context, ScreenCaptureService.class);
        intent.putExtra(ACTION, STOP);
        return intent;
    }

    private static boolean isStartCommand(Intent intent) {
        return intent.hasExtra(RESULT_CODE) && intent.hasExtra(DATA)
                && intent.hasExtra(ACTION) && Objects.equals(intent.getStringExtra(ACTION), START);
    }

    private static boolean isStopCommand(Intent intent) {
        return intent.hasExtra(ACTION) && Objects.equals(intent.getStringExtra(ACTION), STOP);
    }

    private static int getVirtualDisplayFlags() {
        return DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    }

    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onImageAvailable(ImageReader reader) {
//            LocalTime ImageTime=LocalTime.now();
//            String ImageTimeString=dateformatter.format(ImageTime);
//            try {
//                d2=format.parse(ImageTimeString);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            long differenceTime=-d1.getTime()+d2.getTime();
            FileOutputStream fos = null;
            Bitmap bitmap = null;
            try (Image image = mImageReader.acquireLatestImage()) {
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;


                    // create bitmap
//                    Log.i("Image Width: ", String.valueOf(mWidth + rowPadding / pixelStride));
//                    Log.i("Image Height: ", String.valueOf(mHeight));
                    bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);



                    // write bitmap to a file
//                    fos = new FileOutputStream(mStoreDir + "/myscreen_" + IMAGES_PRODUCED + ".png");
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                      //  Log.i("bitmap",bitmap.toString());

                    //create a file to write bitmap data
                 //   final File f = new File(mStoreDir, "LAST HOPE2.txt");
                 //   Log.i("bitmap",mStoreDir);

                //    f.createNewFile();

                    //Convert bitmap to byte array
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 25 /*ignored for PNG*/, bos);
                     byte[] bitmapdata = bos.toByteArray();
//                    Log.i("byte array", Arrays.toString(bitmapdata));
                    //TODO : wedit , height , exention,ByteArray
                    //write the bytes in file
//                    FileOutputStream fos2 = null;
//                    try {
//                        fos2 = new FileOutputStream(f);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        assert fos2 != null;
//                        Log.i("file path",f.toPath().toString());
//                        byte[] fileContent = Files.readAllBytes(f.toPath());
//                        Log.i("byte array new", Arrays.toString(fileContent));
//                        //   ArrayList<Byte> tempArr = new ArrayList<>();
////                        for (byte bitmapdatum : bitmapdata)
////                        {
////                            tempArr.add(bitmapdatum);
////                        }
////                        Log.i("Hamda Value: ", String.valueOf(bitmapdata[0]));
////                        fos2.write(bitmapdata[0]);
//                        Log.i("Size",String.valueOf(bitmapdata.length));
//                        fos2.write(bitmapdata);
//                        fos2.flush();
//                        fos2.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    IMAGES_PRODUCED++;
                    Log.e(TAG, "captured image: " + IMAGES_PRODUCED);
                    MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                    multipartBodyBuilder.addFormDataPart("img", "img", RequestBody.create(MediaType.parse("image/*jpg"), bitmapdata));
                    RequestBody postBodyImage = multipartBodyBuilder.build();
//                    Log.i("DifferenceTime", String.valueOf(differenceTime));
//                    Log.i("ImageTime", String.valueOf(d1.getTime()));
//                    Log.i("TimeObj",String.valueOf(d2.getTime()));
                    if(send)
                    {
//                            TimeObj = LocalTime.now();
//                            String time = dateformatter.format(TimeObj);
//                            try {
//                                d1 = format.parse(time);
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
                        sendRequest(postBodyImage);
                        }




//                    RequestBody formbody=new FormBody.Builder().add("img", Arrays.toString(bitmapdata)).build();










//                    new Thread(new Runnable() {
//                        @Override
//                        public void run()  {
//                            RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), bitmapdata);
//                            Retrofit retrofit = new Retrofit.Builder()
//                                    .baseUrl("http://192.168.1.103:5000/")
//                                    .addConverterFactory(GsonConverterFactory.create())
//                                    .build();
//                            APIInterface jsonPlaceHolderApi = retrofit.create(APIInterface.class);
//                            Call<String> call=jsonPlaceHolderApi.upload(body);
//                            call.enqueue(new Callback<String>() {
//                                @Override
//                                public void onResponse(Call<String> call, Response<String> response) {
//                                    if(response.isSuccessful())
//                                    {
//                                        if(response.body().equals("Blackout"))
//                                        {
//                                            blackout();
//                                            Log.i("Blackout","successful request");
//
//                                        }
//                                        else if (response.body().equals("safe")){
//                                            RemoveBlackout();
//                                            Log.i("RemoveBlackout","successful request");
//
//                                        }
//                                    }
//                                    Log.i("successful","successful request");
//                                }
//
//                                @Override
//                                public void onFailure(Call<String> call, Throwable t) {
//                                    Log.i("Unsuccessful","Unsuccessful request");
//                                    t.printStackTrace();
//
//                                }
//                            });
//                        }
//                    }).start();

//
//                    MultipartBody.Part body = MultipartBody.Part.createFormData("upload", f.getName(), reqFile);

//                    if(IMAGES_PRODUCED==20)
//                    {
//                        blackout();
//                        Retrofit retrofit = new Retrofit.Builder()
//                                .baseUrl("https://jsonplaceholder.typicode.com/")
//                                .addConverterFactory(GsonConverterFactory.create())
//                                .build();
//                        APIInterface jsonPlaceHolderApi = retrofit.create(APIInterface.class);
//                        Call<RequestBody> call = jsonPlaceHolderApi.CheckImage(body);
//                        call.enqueue(new Callback<RequestBody>() {
//                            @Override
//                            public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {
//                                if (response.isSuccessful()) {
//
//                                    blackout();
//                                }
//
//                            }
//
//                            @Override
//                            public void onFailure(Call<RequestBody> call, Throwable t) {
//
//                            }
//                        });
//                    }
//                    if(IMAGES_PRODUCED==100)
//                    {
//                        RemoveBlackout();
//                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

                if (bitmap != null) {
                    bitmap.recycle();
                }

            }
        }
    }

    private class OrientationChangeCallback extends OrientationEventListener {

        OrientationChangeCallback(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            final int rotation = mDisplay.getRotation();
            if (rotation != mRotation) {
                mRotation = rotation;
                try {
                    // clean up
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);

                    // re-create virtual display depending on device width / height
                    createVirtualDisplay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.e(TAG, "stopping projection.");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
                    if (mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
                    mMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                }
            });
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void sendRequest(RequestBody postBodyImage)
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://192.168.1.103:8000/classify").post(postBodyImage).build();
        send=false;
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i("Unsuccessful", "Unsuccessful");
                e.printStackTrace();
                send=true;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("Response", "Response" + i);
                    i++;
                    int responecode = 0;
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        responecode = jsonObject.getInt("block");
                        Log.i("Block", String.valueOf(responecode));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (responecode == 1) {
                        Message m1 = Message.obtain();
                        m1.obj = "Blackout";
                        handler.sendMessage(m1);
                        send=false;
                        TimerTask task = new TimerTask() {
                            public void run() {
                                Message m1 = Message.obtain();
                                m1.obj = "Safe";
                                handler.sendMessage(m1);
                            }
                        };
                        Timer timer = new Timer("Timer");

                        long delay = 5000L;
                        timer.schedule(task, delay);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();

                        reader.AppendToFile(getApplicationContext(),dtf.format(now),"statistics.txt");
                    }
                    else {
                        send=true;

                    }
                    Log.i("successful", "successful");
                }

            }
        });

    }
    @Override
    public void onCreate() {
        super.onCreate();

        // create store dir
        File externalFilesDir = getExternalFilesDir(null);
        if (externalFilesDir != null) {
            mStoreDir = externalFilesDir.getAbsolutePath() + "/screenshots/";
            File storeDirectory = new File(mStoreDir);
            if (!storeDirectory.exists()) {
                boolean success = storeDirectory.mkdirs();
                if (!success) {
                    Log.e(TAG, "failed to create file storage directory.");
                    stopSelf();
                }
            }
        } else {
            Log.e(TAG, "failed to create file storage directory, getExternalFilesDir is null.");
            stopSelf();
        }
        reader=new FileReader(this);

        // start capture handling thread
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler();
                Looper.loop();
            }
        }.start();



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isStartCommand(intent)) {
//            TimeObj=LocalTime.now();
//            String time= dateformatter.format(TimeObj);
//            try {
//                d1=format.parse(time);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            // create notification
            Pair<Integer, Notification> notification = NotificationUtils.getNotification(this);
            startForeground(notification.first, notification.second);
            // start projection
            int resultCode = intent.getIntExtra(RESULT_CODE, Activity.RESULT_CANCELED);
            Intent data = intent.getParcelableExtra(DATA);
            startProjection(resultCode, data);
        } else if (isStopCommand(intent)) {
            stopProjection();
            stopSelf();
        } else {
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private void startProjection(int resultCode, Intent data) {
        MediaProjectionManager mpManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mMediaProjection == null) {
            mMediaProjection = mpManager.getMediaProjection(resultCode, data);
            if (mMediaProjection != null) {
                // display metrics

                mDensity = Resources.getSystem().getDisplayMetrics().densityDpi;
                WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                mDisplay = windowManager.getDefaultDisplay();
//                windowManager.getDefaultDisplay().getRealMetrics(metrics);
//                mDensity=metrics.densityDpi;


                // create virtual display depending on device width / height
                createVirtualDisplay();

                // register orientation change callback
                mOrientationChangeCallback = new OrientationChangeCallback(this);
                if (mOrientationChangeCallback.canDetectOrientation()) {
                    mOrientationChangeCallback.enable();
                }

                // register media projection stop callback
                mMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
            }
        }
        com.GuardianAngel.HomeActivity.startDate = new Date();
        com.GuardianAngel.HomeActivity.timerHandler.postDelayed(com.GuardianAngel.HomeActivity.runnable, 0);
    }

    private void stopProjection() {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mMediaProjection != null) {
                        mMediaProjection.stop();
                    }
                }
            });
        }
    }

    @SuppressLint("WrongConstant")
    private void createVirtualDisplay() {
        // get width and height
        mWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        mHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
//        mWidth=metrics.widthPixels;
//        mHeight=metrics.heightPixels;
        Log.i("mWidth",String.valueOf(mWidth));
        Log.i("mHeight",String.valueOf(mHeight));

        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight,
                mDensity, getVirtualDisplayFlags(), mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    }
}