package com.example.solomon;

import android.util.*;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import java.net.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class LoginActivity extends AppCompatActivity {

    public static Resources r;
    public static Context context;

    //Threads
    public static Thread connectClientThread;
    public static Thread manageClientConnectionThread;

    //networking variables
    public static Socket socket;
    public static ObjectOutputStream objectOutputStream;
    public static ObjectInputStream objectInputStream;

    //UI variables
    public static LinearLayout mainLinearLayout;
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
        r = getResources();
        context = this.getApplicationContext();



        //connect to servers
        connectToJavaServer();


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



    //SERVER METHODS
    public void connectToJavaServer()
    {
        connectClientThread = new Thread(new ConnectToJavaServerRunnable());

        connectClientThread.start();
    }





    //UI METHODS


    public void initUI()
    {
        mainLinearLayout = findViewById(R.id.MainMenuLinearLayout);
        loginLinearLayout = findViewById(R.id.CustomAutenthificationLinearLayout);
        loginRadioButton = findViewById(R.id.LoginRadioButton);
        signupRadioButton = findViewById(R.id.SignUpRadioButton);
        titleTextView = findViewById(R.id.TitleTextView);
        Drawable backround = ContextCompat.getDrawable(context, R.drawable.orangecoral);
        backround.setAlpha(200);
        mainLinearLayout.setBackground(backround);
    }
    public void setLoginLayout()
    {
        //uncheck the SignUpRadioButton if it's checked
        if (signupRadioButton.isChecked())
            signupRadioButton.setChecked(false);

        //remove all the views from the linear layout
        loginLinearLayout.removeAllViews();

        //add login UI in the linear Layout

        TextView usernameTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsUsernameTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsUsernameTextView.setMargins(0, 10, 0, 0);
        usernameTextView.setLayoutParams(layoutParamsUsernameTextView);
        usernameTextView.setTextSize(15);
        usernameTextView.setTextColor(Color.BLACK);
        usernameTextView.setPadding(14, 200, 14, 14);
        usernameTextView.setText("Username: ");


        float dip = 50f;

        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );

        int width = (int)(3 * px);
        int height = (int) px;

        EditText usernameEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsUsernameEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsUsernameEditText.setMargins(0, 10, 0, 0);
        layoutParamsUsernameEditText.gravity = Gravity.CENTER;
        usernameEditText.setLayoutParams(layoutParamsUsernameEditText);


        TextView passwordTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsPasswordTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsPasswordTextView.setMargins(0, 10, 0, 0);
        passwordTextView.setLayoutParams(layoutParamsPasswordTextView);
        passwordTextView.setTextSize(15);
        passwordTextView.setTextColor(Color.BLACK);
        passwordTextView.setPadding(14, 200, 14, 14);
        passwordTextView.setText("Password: ");


        EditText passwordEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsPasswordEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsPasswordEditText.setMargins(0, 10, 0, 0);
        layoutParamsPasswordEditText.gravity = Gravity.CENTER;
        passwordEditText.setLayoutParams(layoutParamsPasswordEditText);


        loginLinearLayout.addView(usernameTextView);
        loginLinearLayout.addView(usernameEditText);
        loginLinearLayout.addView(passwordTextView);
        loginLinearLayout.addView(passwordEditText);
    }
    public void setSignUpLayout()
    {
        //uncheck the LogInRadioButton if it's checked
        if (loginRadioButton.isChecked())
            loginRadioButton.setChecked(false);

        //remove all the views from the linear layout
        loginLinearLayout.removeAllViews();


        //add login UI in the linear Layout

        TextView usernameTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsUsernameTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsUsernameTextView.setMargins(0, 10, 0, 0);
        usernameTextView.setLayoutParams(layoutParamsUsernameTextView);
        usernameTextView.setTextSize(15);
        usernameTextView.setTextColor(Color.BLACK);
        usernameTextView.setPadding(14, 100, 14, 14);
        usernameTextView.setText("Username: ");


        float dip = 50f;

        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );

        int width = (int)(3 * px);
        int height = (int) px;

        EditText usernameEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsUsernameEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsUsernameEditText.setMargins(0, 10, 0, 0);
        layoutParamsUsernameEditText.gravity = Gravity.CENTER;
        usernameEditText.setLayoutParams(layoutParamsUsernameEditText);


        TextView passwordTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsPasswordTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsPasswordTextView.setMargins(0, 10, 0, 0);
        passwordTextView.setLayoutParams(layoutParamsPasswordTextView);
        passwordTextView.setTextSize(15);
        passwordTextView.setTextColor(Color.BLACK);
        passwordTextView.setPadding(14, 100, 14, 14);
        passwordTextView.setText("Password: ");


        EditText passwordEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsPasswordEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsPasswordEditText.setMargins(0, 10, 0, 0);
        layoutParamsPasswordEditText.gravity = Gravity.CENTER;
        passwordEditText.setLayoutParams(layoutParamsPasswordEditText);



        TextView passwordConfirationTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsPasswordConfirmationTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsPasswordConfirmationTextView.setMargins(0, 10, 0, 0);
        passwordConfirationTextView.setLayoutParams(layoutParamsPasswordConfirmationTextView);
        passwordConfirationTextView.setTextSize(15);
        passwordConfirationTextView.setTextColor(Color.BLACK);
        passwordConfirationTextView.setPadding(14, 100, 14, 14);
        passwordConfirationTextView.setText("Confirm password: ");


        EditText passwordConfirmationEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsPasswordConfirmationEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsPasswordConfirmationEditText.setMargins(0, 10, 0, 0);
        layoutParamsPasswordConfirmationEditText.gravity = Gravity.CENTER;
        passwordConfirmationEditText.setLayoutParams(layoutParamsPasswordConfirmationEditText);


        loginLinearLayout.addView(usernameTextView);
        loginLinearLayout.addView(usernameEditText);
        loginLinearLayout.addView(passwordTextView);
        loginLinearLayout.addView(passwordEditText);
        loginLinearLayout.addView(passwordConfirationTextView);
        loginLinearLayout.addView(passwordConfirmationEditText);
    }
}