package com.cc.zxingbar;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.v7.widget.ViewUtils;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cc.zxingbar.camera.CameraManager;
import com.cc.zxingbar.decoding.CaptureActivityHandler;
import com.google.zxing.BarcodeFormat;

import java.io.IOException;
import java.util.Vector;



/**
 * Created by Administrator on 2017/2/28.
 */
public abstract class BaseScanActivity extends Activity implements
        View.OnClickListener,SurfaceHolder.Callback,
        DialogInterface.OnKeyListener{
    /**
     * The M sound utils.
     */
    public static SoundUtils mSoundUtils;
    /**
     * The View utils.
     */
    public ViewUtils viewUtils;


    /**
     * The Handler.
     */
//手机扫描
    protected static CaptureActivityHandler handler;
    //    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;
    private RelativeLayout mContainer = null;
    private RelativeLayout mCropLayout = null;
    private ImageView mQrLineView;
    private boolean isNeedCapture = false;

    /**
     * Is need capture boolean.
     *
     * @return the boolean
     */
    public boolean isNeedCapture() {
        return isNeedCapture;
    }

    /**
     * Sets need capture.
     *
     * @param isNeedCapture the is need capture
     */
    public void setNeedCapture(boolean isNeedCapture) {
        this.isNeedCapture = isNeedCapture;
    }

    /**
     * Gets x.
     *
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * Sets x.
     *
     * @param x the x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets y.
     *
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * Sets y.
     *
     * @param y the y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets crop width.
     *
     * @return the crop width
     */
    public int getCropWidth() {
        return cropWidth;
    }

    /**
     * Sets crop width.
     *
     * @param cropWidth the crop width
     */
    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    /**
     * Gets crop height.
     *
     * @return the crop height
     */
    public int getCropHeight() {
        return cropHeight;
    }

    /**
     * Sets crop height.
     *
     * @param cropHeight the crop height
     */
    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }




    // 扫描模式
    private Handler mHandler = new Handler();



    /**
     * The constant lightControlHandler.
     */
// 灯光控制的一个Handler
//    protected static LightControlHandler lightControlHandler = null;



    protected int type;//跳转源

    protected String expressCode;

    public int scanFrame;//扫描框型号
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //判断当前app载体（摄像头扫描手机、pda、红外、新石器。。。）
        judgePhone();

        initParmers();
        initScan();//初始化3中类型手机的扫描


        setContentView(R.layout.activity_main);



            mContainer =  (RelativeLayout) findViewById(R.id.capture_containter);
            mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);

            mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
            TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
                    TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
            mAnimation.setDuration(1500);
            mAnimation.setRepeatCount(-1);
            mAnimation.setRepeatMode(Animation.REVERSE);
            mAnimation.setInterpolator(new LinearInterpolator());
            mQrLineView.setAnimation(mAnimation);

        mSoundUtils = SoundUtils.getInstance(this);
        mSoundUtils.init(this);
        initViewsAndValues(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



    }

    /**
     * 判断当前app载体（摄像头扫描手机、pda、红外、新石器。。。）
     */
    private void judgePhone() {
        String model= Build.MODEL;

    }

    /**
     * 初始化摄像头，红外扫描
     */
    private void initScan() {

            CameraManager.init(getApplication(),this);
            hasSurface = false;

    }





    /**
     * 初始化变量
     */
    public abstract void initParmers();

    /**
     * 初始化页面控件
     *
     * @param savedInstanceState the saved instance state
     */
    public abstract void initViewsAndValues(Bundle savedInstanceState);

    /**
     * 在onDestory()手动释放资源
     */
    public abstract void releaseOnDestory();

    /**
     * 点击事件的回调
     *
     * @param view the view
     */
    public abstract void onClickable(View view);

    /**
     * 返回自己的Application实例
     *
     * @return app
     */
    public final BaseApplication getApp() {
        return (BaseApplication) getApplication();
    }



    @Override
    public void onClick(View view) {
        onClickable(view);
    }

    /**
     * 检查空页面
     *
     * @param needParams 拓展参数
     */
    public abstract void doEmptyCheck(Object... needParams);

    /**
     * Scan result.
     *
     * @param scanResult 扫描结果（单号）
     * @param isCamera   （是否是摄像头扫描）
     */
    public abstract void scanResult(String scanResult,boolean isCamera);

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        SoundUtils.getInstance(this).release();

        System.gc();

        releaseOnDestory();

        System.gc();


    }

    @Override
    protected void onResume() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                restartScan();
            }
        }).start();

        super.onResume();

    }

    /**
     * Restart scan.
     */
    public void restartScan() {

            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            if (hasSurface) {
                initCamera(surfaceHolder);
            } else {
                surfaceHolder.addCallback(this);
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }
            decodeFormats = null;
            characterSet = null;

            playBeep = true;
            AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
            if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                playBeep = false;
            }
            vibrate = true;

    }

    /**
     * Close scan.
     */
    public void closeScan() {

            if (handler != null) {
                handler.quitSynchronously();
                handler = null;
            }
            CameraManager.get().closeDriver();

    }
    @Override
    protected void onPause() {
        super.onPause();
        closeScan();

    }

    /**
     * Handle decode.
     *
     * @param str the str
     */
    public void handleDecode(String str) {
        playBeepSoundAndVibrate();


        scanResult(expressCode, true);
    }


    private void playBeepSoundAndVibrate() {
//		if (playBeep && mediaPlayer != null) {
//			mediaPlayer.start();
//		}
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }
    private static final long VIBRATE_DURATION = 200L;
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    /**
     * 摄像头
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            Point point = CameraManager.get().getCameraResolution();
            int width = point.y;
            int height = point.x;

            int x = mCropLayout.getLeft() * width / mContainer.getWidth();
            int y = mCropLayout.getTop() * height / mContainer.getHeight();

            int cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();

            setX(x);
            setY(y);
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);
            // 设置是否需要截图
            setNeedCapture(false);

        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(Looper.getMainLooper(),this, decodeFormats,
                    characterSet);
        }
    }

    /**
     * 摄像头
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * 摄像头
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    /**
     * 摄像头
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    /**
     * 摄像头
     *
     * @return handler
     */
    public Handler getHandler() {
        return handler;
    }









    /**
     * The type Light control handler.
     */
//    public class LightControlHandler extends Handler {
//        public void handleMessage(Message msg) {
//            // 这边是为了在非 自动补光的情境下,可以动过手动的开关灯光进行补光
//            switch (msg.what) {
//                case Constant.OPENLIGHT:
//                    if (mDecoderMgr != null) {
//                        mDecoderMgr.enableLight(Constants.LOCATION_LIGHT, true);
//                        if (mDecoderMgr.getFlashMode() == Constants.FLASH_ALWAYS_ON_MODE) {
//                            mDecoderMgr.enableLight(Constants.FLASH_LIGHT, true);
//                            mDecoderMgr.enableLight(Constants.FLOOD_LIGHT, true);
//                        }
//                    }
//                    break;
//                case Constant.CLOSELIGHT:
//                    if (mDecoderMgr != null) {
//                        mDecoderMgr.enableLight(Constants.LOCATION_LIGHT, false);
//                        if (mDecoderMgr.getFlashMode() == Constants.FLASH_ALWAYS_ON_MODE) {
//                            mDecoderMgr.enableLight(Constants.FLASH_LIGHT, false);
//                            mDecoderMgr.enableLight(Constants.FLOOD_LIGHT, false);
//                        }
//                    }
//                    break;
//
//                default:
//                    break;
//            }
//        }
//        ;
//    }



    /**
     * 显示Toast
     *
     * @param text 要显示的内容
     */
    public void showToast(String text)
    {
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            if (isFastDoubleClick()) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    long lastClickTime;
    private  boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     *设置扫描范围，CameraManager中调用
     * @param screenResolution
     */
    public Rect setFramingRect(Point screenResolution){
        int width = screenResolution.x;
        if (scanFrame == 0) {
            //普通扫描框，正方形位于页面中部
            int height = screenResolution.y;
            int leftOffset = dp2px(this,50);
            int topOffset = dp2px(this,50);
            return new Rect(leftOffset, topOffset, width - leftOffset * 2, height - topOffset * 2);
        }else if (scanFrame == 1){
            //长方形扫描框，位于页面上部
            int height = dp2px(this,120);
            int leftOffset = 0;
            int topOffset = 0;
            return new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
        }else if (scanFrame == 2){
            //长方形扫描框，位于页面上部
            int height = dp2px(this,120);
            int leftOffset = dp2px(this,50);
            int topOffset = 0;
            return new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
        }else{
            //长方形扫描框，位于页面上部
            int height = dp2px(this,120);
            int leftOffset = dp2px(this,50);
            int topOffset = 0;
            return new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
        }
    }
    public  int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
