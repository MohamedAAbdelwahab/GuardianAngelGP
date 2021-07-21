package com.GuardianAngel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioManager;
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
import android.view.WindowManager;
import androidx.annotation.RequiresApi;
import androidx.core.util.Pair;
import com.GuardianAngel.FileSystemModule.FileReader;
import com.GuardianAngel.FileSystemModule.Global;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
@RequiresApi(api = Build.VERSION_CODES.O)
public class ScreenCaptureService extends Service {
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
    public int i=0;
        FileReader reader;
    private boolean send=true;
    AudioManager audioManager;
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
                        WindowManager.LayoutParams windowManagerParams;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            windowManagerParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY  ,
                                    WindowManager.LayoutParams. FLAG_DIM_BEHIND, PixelFormat.TRANSLUCENT);
                        } else {
                            windowManagerParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_PHONE  ,
                                    WindowManager.LayoutParams. FLAG_DIM_BEHIND, PixelFormat.TRANSLUCENT);
                        }


                        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        myView = inflater.inflate(R.layout.blackout_activity, null);

                        wm.addView(myView, windowManagerParams);
                        removeMessages(0);
                        safe=false;
                        audioManager= (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                        audioManager.adjustVolume(AudioManager.ADJUST_MUTE,AudioManager.FLAG_SHOW_UI);
                    }
                    else if (m.obj.toString().equals("Safe")){
                        if(!safe)
                        {
                            wm.removeView(myView);
                            audioManager= (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                            audioManager.adjustVolume(AudioManager.ADJUST_UNMUTE,AudioManager.FLAG_SHOW_UI);

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
            FileOutputStream fos = null;
            Bitmap bitmap = null;
            try (Image image = mImageReader.acquireLatestImage()) {
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;
                    bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    //Convert bitmap to byte array
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 25 /*ignored for PNG*/, bos);
                     byte[] bitmapdata = bos.toByteArray();
                    IMAGES_PRODUCED++;
                    Log.e(TAG, "captured image: " + IMAGES_PRODUCED);
                    MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                    multipartBodyBuilder.addFormDataPart("email", Global.email);
                    if(Global.mailbox){
                        multipartBodyBuilder.addFormDataPart("send", "yes");
                    }else{
                        multipartBodyBuilder.addFormDataPart("send", "no");
                    }
                    multipartBodyBuilder.addFormDataPart("APIpwd", "94Vtwn0iHVQSpBT");
                    multipartBodyBuilder.addFormDataPart("img", "img", RequestBody.create(MediaType.parse("image/*jpg"), bitmapdata));
                    RequestBody postBodyImage = multipartBodyBuilder.build();
                    if(send)
                    {
                        sendRequest(postBodyImage);
                    }

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
        Request request = new Request.Builder().url("http://"+HomeActivity.IP+":8000/classify").post(postBodyImage).build();
        send=false;
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                send=true;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    i++;
                    int responecode = 0;
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        responecode = jsonObject.getInt("block");
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
                        SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date c = Calendar.getInstance().getTime();

                        reader.AppendToFile(getApplicationContext(),format.format(c),"statistics.txt");
                    }
                    else {
                        send=true;

                    }
                }

            }
        });

    }
    @Override
    public void onCreate() {
        super.onCreate();
        // create store dir
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
        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight,
                mDensity, getVirtualDisplayFlags(), mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    }
}