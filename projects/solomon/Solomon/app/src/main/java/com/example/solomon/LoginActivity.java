package com.example.solomon;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    public static Context context;
    //UI variables
    public static LinearLayout loginLinearLayout;   //the linear layout that is common for both login and signup instances
    public static RadioButton loginRadioButton;
    public static RadioButton signupRadioButton;
    public static TextView titleTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //initialize variables
        context = this.getApplicationContext();

        //initialize the UI
        initUI();

        //set login layout
        setLoginLayout();

        //login radio button listener
        loginRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                setLoginLayout();
            }
        });

        //sign up radio button listener
        signupRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                setSignUpLayout();
            }
        });

    }
    public void initUI()
    {
        loginLinearLayout = findViewById(R.id.CustomAutenthificationLinearLayout);
        loginRadioButton = findViewById(R.id.LoginRadioButton);
        signupRadioButton = findViewById(R.id.SignUpRadioButton);
        titleTextView = findViewById(R.id.TitleTextView);
    }
    public void setLoginLayout()
    {
        //uncheck the SignUpRadioButton if it's checked
        if (signupRadioButton.isChecked())
            signupRadioButton.setChecked(false);

        //remove all the views from the linear layout
        loginLinearLayout.removeAllViews();

        //add login UI in the linear Layout

        TextView textView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layout_params_textView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layout_params_textView.setMargins(0, 0, 0, 0);
        textView.setLayoutParams(layout_params_textView);
        textView.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textView.setPadding(14, 14, 14, 14);
        textView.setText("Username: ");

        loginLinearLayout.addView(textView);
    }
    public void setSignUpLayout()
    {
        //uncheck the LogInRadioButton if it's checked
        if (loginRadioButton.isChecked())
            loginRadioButton.setChecked(false);

        //remove all the views from the linear layout
        loginLinearLayout.removeAllViews();

        //add login UI in the linear Layout

        TextView textView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layout_params_textView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layout_params_textView.setMargins(0, 0, 0, 0);
        textView.setLayoutParams(layout_params_textView);
        textView.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textView.setPadding(14, 14, 14, 14);
        textView.setText("Username: ");

        loginLinearLayout.addView(textView);
    }
}