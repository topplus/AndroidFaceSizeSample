package example.com.androidfacesize;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import topplus.com.commonutils.Library;
import topplus.com.facemeasure.MeasureTexture;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mImageView;
    private int mPromptIndex = 0;
    private MeasureTexture mMeasureTexture;
    private TextView mNoteText;
    private MeasureTexture.MeasureCallback mMeasureCallback;
    private View mHeadWrap;

    private SensorManager mSensorManager;
    private Sensor mAccelerSensor;
    private float[] mAccelerData;
    private Sensor mMagneticSensor;
    private float[] mMagneticData;
    //姿态角数据，由mMagneticData和mAccelerSensor计算出来
    private float[] mOrientData = new float[3];
    private ProgressWheel mProgressWheel;
    private Timer mTimer;

    private float mFaceDistance = 0f;
    private float mPupilDistance = 0f;
    private float mNoseDistance = 0f;
    private float mInnerEyeDistance = 0f;
    private float mOuterEyeDistance = 0f;
    private int mResutlCount = 0;
    private boolean mMeasuring = false;
    private boolean mMeasureComplete = false;

    //传感器数据接收
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mAccelerData = event.values.clone();
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mMagneticData = event.values.clone();
            }
            if (mAccelerData != null && mMagneticData != null) {
                float I[] = new float[9];
                float R[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, mAccelerData, mMagneticData);
                if (success) {
                    SensorManager.getOrientation(R, mOrientData);
                    if (-1.60f < mOrientData[1] && mOrientData[1] < -1.40f) {
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        mHeadWrap.setBackgroundResource(example.com.androidfacesize.R.drawable.green_rect);
                                        if (!mMeasuring) {
                                            mNoteText.setText("请将人脸置于引导框中，卡片贴于下巴处");
                                        }
                                    }
                                }
                        );
                    } else {
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        mHeadWrap.setBackgroundResource(example.com.androidfacesize.R.drawable.red_rect);
                                        if (!mMeasuring) {
                                            mNoteText.setText("请将手机调整为竖直状态");
                                        }
                                    }
                                }
                        );
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Library.init(this, "", "", false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_measure_page3);
        mImageView = (ImageView) findViewById(R.id.page3_prompt_image);
        mHeadWrap = findViewById(R.id.page3_head_layout);
        if (!MeasureTexture.isWaterMark()) {
            findViewById(R.id.face_size_water).setVisibility(View.GONE);
        } else {
            findViewById(R.id.face_size_water).setVisibility(View.VISIBLE);
        }
        mNoteText = (TextView) findViewById(R.id.note_text);
        mMeasureTexture = (MeasureTexture) findViewById(R.id.page3_measure_texture);
        mMeasureCallback = new MeasureTexture.MeasureCallback() {
            @Override
            public void onMeasured(final Map<String, Float> map, int resultCode) {
                if (mResutlCount == 0) {
                    Bitmap bitmap = mMeasureTexture.takeScreenShot();
                    FileOutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(getFilesDir() + "/test.jpg");
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.flush();
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bitmap.recycle();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMeasuring = true;
                            mProgressWheel.setVisibility(View.VISIBLE);
                            mNoteText.setText("请保持当前姿势");
                        }
                    });
                    mProgressWheel.setProgress(0);
                    mProgressWheel.setRimColor(Color.TRANSPARENT);
                    mProgressWheel.setBarColor(Color.GREEN);
                    mProgressWheel.setText("3");
                    if (resultCode == 4) {
                        if (mDialog != null && mDialog.isShowing()) {
                            return;
                        }
                        Looper.prepare();
                        mDialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("提示")
                                .setMessage("授权失败，请检查您的授权码！")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (mDialog != null && mDialog.isShowing()) {
                                            mDialog.dismiss();
                                        }

                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (mDialog != null && mDialog.isShowing()) {
                                            mDialog.dismiss();
                                        }
                                    }
                                })
                                .show();
                        Looper.loop();

                        return;
                    }
                    mTimer = new Timer();
                    mTimer.scheduleAtFixedRate(new TimerTask() {
                        private int progress = 0;

                        @Override
                        public void run() {
                            progress += 9;
                            if (progress >= 360) {
                                mProgressWheel.setRimColor(Color.GREEN);
                                mProgressWheel.setBarColor(Color.YELLOW);
                                mProgressWheel.setText("2");
                            }
                            if (progress >= 360 * 2) {
                                mProgressWheel.setRimColor(Color.YELLOW);
                                mProgressWheel.setBarColor(Color.GREEN);
                                mProgressWheel.setText("1");
                            }
                            if (progress >= 360 * 3) {
                                mTimer.cancel();
                                mMeasureTexture.stopMeasure();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressWheel.setVisibility(View.INVISIBLE);
                                        Intent intent = new Intent(MainActivity.this, MeasureResultActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putFloat(MeasureTexture.KEY_FACE_DISTANCE, mFaceDistance);
                                        bundle.putFloat(MeasureTexture.KEY_PUPIL_DISTANCE, mPupilDistance);
                                        bundle.putFloat(MeasureTexture.KEY_NOSE_DISTANCE, mNoseDistance);
                                        bundle.putFloat(MeasureTexture.KEY_INNER_EYE_DISTANCE, mInnerEyeDistance);
                                        bundle.putFloat(MeasureTexture.KEY_OUTER_EYE_DISTANCE, mOuterEyeDistance);
                                        bundle.putString("jpgPath", "/sdcard/test.jpg");
                                        intent.putExtras(bundle);
                                        mFaceDistance = 0f;
                                        mPupilDistance = 0f;
                                        mNoseDistance = 0f;
                                        mInnerEyeDistance = 0f;
                                        mOuterEyeDistance = 0f;
                                        mResutlCount = 0;
                                        mMeasureComplete = true;
                                        startActivity(intent);
                                    }
                                });
                            }
                            mProgressWheel.incrementProgress(9);
                        }
                    }, 0, 25);
                }
                mResutlCount++;
                //计算多次测量的平均数
                mFaceDistance += (map.get(MeasureTexture.KEY_FACE_DISTANCE) - mFaceDistance) / mResutlCount;
                mPupilDistance += (map.get(MeasureTexture.KEY_PUPIL_DISTANCE) - mPupilDistance) / mResutlCount;
                mNoseDistance += (map.get(MeasureTexture.KEY_NOSE_DISTANCE) - mNoseDistance) / mResutlCount;
                mInnerEyeDistance += (map.get(MeasureTexture.KEY_INNER_EYE_DISTANCE) - mInnerEyeDistance) / mResutlCount;
                mOuterEyeDistance += (map.get(MeasureTexture.KEY_OUTER_EYE_DISTANCE) - mOuterEyeDistance) / mResutlCount;
            }
        };
        mMeasureTexture.setMeasureCallback(mMeasureCallback);
        mImageView.setImageResource(R.drawable.page3_prompt_image0);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(mSensorEventListener, mAccelerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorEventListener, mMagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mProgressWheel = (ProgressWheel) findViewById(R.id.page3_counter_down);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.page3_prompt_image:
                switch (mPromptIndex) {
                    case 0:
                        mImageView.setImageResource(R.drawable.page3_prompt_image1);
                        break;
                    case 1:
                        mImageView.setVisibility(View.GONE);
                        MediaPlayer player = MediaPlayer.create(MainActivity.this, R.raw.measure);
                        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });
                        player.start();
                        break;
                }
                mPromptIndex++;
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMeasureComplete) {
            mMeasuring = false;
            mMeasureComplete = false;
        }
    }
}
