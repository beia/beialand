package com.beia.solomon;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import static android.view.View.TRANSLATION_X;
import static android.view.View.TRANSLATION_Y;

public class PreferencesActivity extends AppCompatActivity {

    public static LinearLayout shoesLayout;
    public static ImageView ballImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        initUI();
        PropertyValuesHolder xTranslation = PropertyValuesHolder.ofFloat(TRANSLATION_X, 972f);
        PropertyValuesHolder yTranslation = PropertyValuesHolder.ofFloat(TRANSLATION_Y, 1724f);
        ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(shoesLayout, xTranslation, yTranslation);
        animation.setDuration(3500);
        animation.start();
    }
    public void initUI()
    {
        ballImageView = findViewById(R.id.ball);
        shoesLayout = findViewById(R.id.shoesLayout);
    }
}
