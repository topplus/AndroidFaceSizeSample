package example.com.androidfacesize;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import topplus.com.facemeasure.MeasureTexture;

public class MeasureResultActivity extends AppCompatActivity {
    private ImageView mUserImage;
    private ImageView mRotateCircle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_result_page4);
        Intent startIntent=getIntent();
        Bundle bundle=startIntent.getExtras();
        String jpegPath=bundle.getString("jpgPath");
        mUserImage=(ImageView)findViewById(R.id.page4_user_image);
        Uri photoPath=Uri.parse("file://" + jpegPath);
        mUserImage.setImageURI(photoPath);
        int pd=Math.round(bundle.getFloat(MeasureTexture.KEY_PUPIL_DISTANCE));
        TextView textView=(TextView)findViewById(R.id.page4_pd_value);
        textView.setText(""+pd);
        int faceD=Math.round(bundle.getFloat(MeasureTexture.KEY_FACE_DISTANCE));
        textView=(TextView)findViewById(R.id.page4_faceD_value);
        textView.setText(""+faceD);
        int noseD=Math.round(bundle.getFloat(MeasureTexture.KEY_NOSE_DISTANCE));
        textView=(TextView)findViewById(R.id.page4_noseD_value);
        textView.setText(""+noseD);
        int innerEyeD=Math.round(bundle.getFloat(MeasureTexture.KEY_INNER_EYE_DISTANCE));
        textView=(TextView)findViewById(R.id.page4_innerEyeD_value);
        textView.setText(""+innerEyeD);
        int outerEyeD=Math.round(bundle.getFloat(MeasureTexture.KEY_OUTER_EYE_DISTANCE));
        textView=(TextView)findViewById(R.id.page4_outerEyeD_value);
        textView.setText(""+outerEyeD);
        mRotateCircle=(ImageView)findViewById(R.id.page4_left_rotate_circle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startRotateCircle();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRotateCircle();
    }

    private void startRotateCircle(){

    }

    private void stopRotateCircle(){

    }
}
