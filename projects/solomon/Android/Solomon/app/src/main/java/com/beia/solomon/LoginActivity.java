package com.beia.solomon;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.ColorInt;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import android.text.InputType;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.beia.solomon.networkPackets.SignInData;
import com.beia.solomon.networkPackets.SignUpData;
import com.beia.solomon.networkPackets.UserData;
import com.beia.solomon.runnables.AuthenticationRunnable;
import com.beia.solomon.runnables.SendAuthenticationDataRunnable;

import java.lang.reflect.Field;
import java.net.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class LoginActivity extends AppCompatActivity {

    public static Resources r;
    public static Context context;

    //Threads
    public static Thread connectClientThread;
    public static Thread authenticateClient;

    //networking variables
    public static Socket socket;
    public static ObjectOutputStream objectOutputStream;
    public static ObjectInputStream objectInputStream;
    public static SharedPreferences sharedPref;
    public static SharedPreferences.Editor editor;


    //UI variables
    public static LinearLayout mainLinearLayout;    //the linear layout that contains the title and the other linear layout
    public static LinearLayout loginLinearLayout;   //the linear layout that is common for both login and signup instances
    public static RadioButton loginRadioButton;
    public static RadioButton signupRadioButton;
    public static TextView loginTitleTextView;
    public static TextView feedbackTextView;
    public static int hintTextColor = Color.argb(50, 0, 0, 0);
    public static int orangeAccentColor = Color.argb(200,29, 222, 190);
    //sign in UI variables
    public static EditText usernameSignInEditText;
    public static EditText passwordSignInEditText;
    public static Button signInButton;
    //sign up UI variables
    public static EditText lastNameSignUpEditText;
    public static EditText firstNameSignUpEditText;
    public static EditText ageSignUpEditText;
    public static EditText usernameSignUpEditText;
    public static EditText passwordSignUpEditText;
    public static EditText passwordConfirmationSignUpEditText;
    public static Button signUpButton;

    public static Activity loginActivityInstance;


    public static Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 1: //change the feedback text

                    String feedbackText = (String) msg.obj;
                    feedbackTextView.setText(feedbackText);
                    switch (feedbackText)
                    {
                        case "username is taken":
                            feedbackTextView.setTextColor(Color.RED);
                            break;
                        case "registered successfully":
                            feedbackTextView.setTextColor(Color.GREEN);
                            break;
                        case "username or password are wrong":
                            feedbackTextView.setTextColor(Color.RED);
                            break;
                        default:
                            break;
                    }
                    break;

                case 2://login successful
                    feedbackTextView.setText("login successful");
                    feedbackTextView.setTextColor(LoginActivity.context.getResources().getColor(R.color.greenAccent));
                    UserData userData = (UserData) msg.obj;

                    //check if the user signed in before for automatic login
                    editor.putString("username", userData.getUsername());
                    editor.putString("password", userData.getPassword());
                    editor.commit();

                    //check if it's the first login of the user so we can setup his preferences first
                    if(userData.isFirstLogin())
                    {
                        //start the preferences activity
                        Intent intent = new Intent(LoginActivity.context, PreferencesActivity.class);
                        intent.putExtra("UserData", userData);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        LoginActivity.context.startActivity(intent);
                    }
                    else
                    {
                        //start main activity
                        Intent intent = new Intent(LoginActivity.context, MainActivity.class);
                        intent.putExtra("UserData", userData);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        LoginActivity.context.startActivity(intent);
                    }
                    LoginActivity.loginActivityInstance.finish();
                    break;

                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //request permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        //initialize the cache
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        //initialize variables
        r = getResources();
        context = this.getApplicationContext();
        loginActivityInstance = this;

        //initialize the UI
        initUI();

        //check if the user needs to sign in automatically
        String username, password;
        username = sharedPref.getString("username", null);
        password = sharedPref.getString("password", null);
        //manual login
        if(username == null || password == null)
        {
            //set login layout
            setLoginLayout();
            //login radio button listener
            loginRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLoginLayout();
                }
            });
            //sign up radio button listener
            signupRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSignUpLayout();
                }
            });
        }
        else
        {
            //automatic login
            setAutomaticLogin();
        }

        if(objectOutputStream == null || objectInputStream == null) {
            //connect to the server
            connectToJavaServer();
        }
        //start the login thread - in the handler this method is bad
        while(true)
        {
            if(objectInputStream != null && objectOutputStream != null) {
                authenticateClient = new Thread(new AuthenticationRunnable(objectOutputStream, objectInputStream));
                authenticateClient.start();
                break;
            }
        }

        if(username != null && password != null)
        {
            automaticSignIn(username, password);
        }
    }



    //SERVER METHODS
    public void connectToJavaServer()
    {
        connectClientThread = new Thread(new ConnectToJavaServerRunnable());
        connectClientThread.start();
    }

    //CLIENT COMMUNICATION METHODS
    public void sendSignInData()
    {
        String username = usernameSignInEditText.getText().toString();
        String password = passwordSignInEditText.getText().toString();
        usernameSignInEditText.setText("");
        passwordSignInEditText.setText("");
        //send the sign in data to the server
        SignInData signInData = new SignInData(username, password);
        Thread sendSignInDataThread = new Thread(new SendAuthenticationDataRunnable("sign in", signInData, objectOutputStream));
        sendSignInDataThread.start();
    }
    public void automaticSignIn(String username, String password)
    {
        //send the sign in data to the server
        SignInData signInData = new SignInData(username, password);
        Thread sendSignInDataThread = new Thread(new SendAuthenticationDataRunnable("sign in", signInData, objectOutputStream));
        sendSignInDataThread.start();
    }
    public void sendSignUpData()
    {
        String lastName = lastNameSignUpEditText.getText().toString();
        String firstName = firstNameSignUpEditText.getText().toString();
        int age = Integer.parseInt(ageSignUpEditText.getText().toString());
        String username = usernameSignUpEditText.getText().toString();
        String password = passwordSignUpEditText.getText().toString();
        String passwordConfirmation = passwordConfirmationSignUpEditText.getText().toString();
        lastNameSignUpEditText.setText("");
        firstNameSignUpEditText.setText("");
        ageSignUpEditText.setText("");
        usernameSignUpEditText.setText("");
        passwordSignUpEditText.setText("");
        passwordConfirmationSignUpEditText.setText("");
        SignUpData signUpData = new SignUpData(lastName, firstName, age, username, password, passwordConfirmation);
        Thread sendSignUpDataThread = new Thread(new SendAuthenticationDataRunnable("sign up", signUpData, objectOutputStream));
        sendSignUpDataThread.start();
    }




    //UI METHODS


    public void initUI()
    {
        mainLinearLayout = findViewById(R.id.MainMenuLinearLayout);
        loginLinearLayout = findViewById(R.id.CustomAutenthificationLinearLayout);
        loginRadioButton = findViewById(R.id.LoginRadioButton);
        signupRadioButton = findViewById(R.id.SignUpRadioButton);
        loginTitleTextView = findViewById(R.id.loginTitleTextView);
        Drawable backround = ContextCompat.getDrawable(context, R.color.solomonWallpaperColor);
        mainLinearLayout.setBackground(backround);

        //add feedback text
        feedbackTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsFeedbackTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsFeedbackTextView.setMargins(0, 0 , 0, 0);
        feedbackTextView.setLayoutParams(layoutParamsFeedbackTextView);
        feedbackTextView.setTextSize(20);
        feedbackTextView.setGravity(Gravity.CENTER);
        feedbackTextView.setTypeface(null, Typeface.BOLD);
        feedbackTextView.setPadding(14, 14, 14, 14);
        feedbackTextView.setText("");

    }
    public void setLoginLayout()
    {
        loginRadioButton.setVisibility(View.VISIBLE);
        signupRadioButton.setVisibility(View.VISIBLE);
        loginTitleTextView.setVisibility(View.VISIBLE);

        //set the login title
        loginTitleTextView.setText("Sign in");

        //uncheck the SignUpRadioButton if it's checked
        if (signupRadioButton.isChecked())
            signupRadioButton.setChecked(false);

        //remove all the views from the linear layout
        loginLinearLayout.removeAllViews();

        // UI dimensions
        float dip = 50f;

        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );

        int width = (int)(3 * px);
        int height = (int) px;

        int loginButtonWidth = (int) (3 * px);
        int loginButtonHeight = (int) (px / 1.2f);

        //add login UI in the linear Layout


        LoginActivity.usernameSignInEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsUsernameEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsUsernameEditText.setMargins(0, 100, 0, 0);
        layoutParamsUsernameEditText.gravity = Gravity.CENTER;
        usernameSignInEditText.setLayoutParams(layoutParamsUsernameEditText);
        usernameSignInEditText.setHint("username");
        setCursorColor(usernameSignInEditText, orangeAccentColor);
        ColorStateList colorStateList = ColorStateList.valueOf(orangeAccentColor);
        ViewCompat.setBackgroundTintList(usernameSignInEditText, colorStateList);
        usernameSignInEditText.setHintTextColor(hintTextColor);


        LoginActivity.passwordSignInEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsPasswordEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsPasswordEditText.setMargins(0, 100, 0, 0);
        layoutParamsPasswordEditText.gravity = Gravity.CENTER;
        passwordSignInEditText.setLayoutParams(layoutParamsPasswordEditText);
        passwordSignInEditText.setHint("password");
        setCursorColor(passwordSignInEditText, orangeAccentColor);
        ViewCompat.setBackgroundTintList(passwordSignInEditText, colorStateList);
        passwordSignInEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordSignInEditText.setHintTextColor(hintTextColor);


        LoginActivity.signInButton = new Button(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsLoginButton = new LinearLayout.LayoutParams(loginButtonWidth, loginButtonHeight);
        layoutParamsLoginButton.gravity = Gravity.CENTER;
        layoutParamsLoginButton.setMargins(0, 100, 0, 0);
        signInButton.setLayoutParams(layoutParamsLoginButton);
        signInButton.setBackgroundColor(Color.argb(255, 255, 255, 255));
        signInButton.setText("Login");

        //sign in button listener
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignInData();
            }
        });

        feedbackTextView.setText("");


        loginLinearLayout.addView(LoginActivity.usernameSignInEditText);
        loginLinearLayout.addView(LoginActivity.passwordSignInEditText);
        loginLinearLayout.addView(LoginActivity.signInButton);
        loginLinearLayout.addView(feedbackTextView);
    }

    public void setAutomaticLogin()
    {
        loginLinearLayout.removeAllViews();
        loginRadioButton.setVisibility(View.GONE);
        signupRadioButton.setVisibility(View.GONE);
    }
    public void setSignUpLayout()
    {
        //set the login title
        loginTitleTextView.setText("Sign up");

        //uncheck the LogInRadioButton if it's checked
        if (loginRadioButton.isChecked())
            loginRadioButton.setChecked(false);

        //remove all the views from the linear layout
        loginLinearLayout.removeAllViews();


        //UI dimensions
        float dip = 50f;

        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );

        int width = (int)(3 * px);
        int height = (int) px;

        int signUpButtonWidth = (int) (3 * px);
        int signUpButtonHeight = (int) (px / 1.2f);


        //add login UI in the linear Layout


        TextView lastNameTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsLastNameTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsLastNameTextView.setMargins(100, 100, 0, 0);
        lastNameTextView.setLayoutParams(layoutParamsLastNameTextView);
        lastNameTextView.setTextSize(15);
        lastNameTextView.setTextColor(Color.BLACK);
        lastNameTextView.setPadding(14, 10, 14, 14);
        lastNameTextView.setText("Last name: ");


        //add a horizontal linear layout and add the last name EditText and the feedback text for the coresponding edittext
        LoginActivity.lastNameSignUpEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsLastNameEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsLastNameEditText.setMargins(0, 10, 0, 0);
        layoutParamsLastNameEditText.gravity = Gravity.CENTER;
        lastNameSignUpEditText.setLayoutParams(layoutParamsLastNameEditText);
        lastNameSignUpEditText.setHint("enter last name");
        setCursorColor(lastNameSignUpEditText, orangeAccentColor);
        ColorStateList colorStateList = ColorStateList.valueOf(orangeAccentColor);
        ViewCompat.setBackgroundTintList(lastNameSignUpEditText, colorStateList);
        lastNameSignUpEditText.setHintTextColor(hintTextColor);


        TextView firstNameTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsFirstNameTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsFirstNameTextView.setMargins(100, 10, 0, 0);
        firstNameTextView.setLayoutParams(layoutParamsFirstNameTextView);
        firstNameTextView.setTextSize(15);
        firstNameTextView.setTextColor(Color.BLACK);
        firstNameTextView.setPadding(14, 100, 14, 14);
        firstNameTextView.setText("First name: ");

        //add a horizontal linear layout and add the first name EditText and the feedback text for the coresponding edittext
        LoginActivity.firstNameSignUpEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsFirstNameEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsFirstNameEditText.setMargins(0, 10, 0, 0);
        layoutParamsFirstNameEditText.gravity = Gravity.CENTER;
        firstNameSignUpEditText.setLayoutParams(layoutParamsFirstNameEditText);
        firstNameSignUpEditText.setHint("enter first name");
        setCursorColor(firstNameSignUpEditText, orangeAccentColor);
        ViewCompat.setBackgroundTintList(firstNameSignUpEditText, colorStateList);
        firstNameSignUpEditText.setHintTextColor(hintTextColor);


        TextView ageTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsAgeTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsAgeTextView.setMargins(100, 10, 0, 0);
        ageTextView.setLayoutParams(layoutParamsAgeTextView);
        ageTextView.setTextSize(15);
        ageTextView.setTextColor(Color.BLACK);
        ageTextView.setPadding(14, 100, 14, 14);
        ageTextView.setText("Age: ");


        //add a horizontal linear layout and add the age EditText and the feedback text for the coresponding edittext
        LoginActivity.ageSignUpEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsAgeEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsAgeEditText.setMargins(0, 10, 0, 0);
        layoutParamsAgeEditText.gravity = Gravity.CENTER;
        ageSignUpEditText.setLayoutParams(layoutParamsAgeEditText);
        ageSignUpEditText.setHint("enter age");
        setCursorColor(ageSignUpEditText, orangeAccentColor);
        ViewCompat.setBackgroundTintList(ageSignUpEditText, colorStateList);
        ageSignUpEditText.setHintTextColor(hintTextColor);


        TextView usernameTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsUsernameTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsUsernameTextView.setMargins(100, 10, 0, 0);
        usernameTextView.setLayoutParams(layoutParamsUsernameTextView);
        usernameTextView.setTextSize(15);
        usernameTextView.setTextColor(Color.BLACK);
        usernameTextView.setPadding(14, 100, 14, 14);
        usernameTextView.setText("Username: ");


        //add a horizontal linear layout and add the username EditText and the feedback text for the coresponding edittext
        LoginActivity.usernameSignUpEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsUsernameEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsUsernameEditText.setMargins(0, 10, 0, 0);
        layoutParamsUsernameEditText.gravity = Gravity.CENTER;
        usernameSignUpEditText.setLayoutParams(layoutParamsUsernameEditText);
        usernameSignUpEditText.setHint("enter username");
        setCursorColor(usernameSignUpEditText, orangeAccentColor);
        ViewCompat.setBackgroundTintList(usernameSignUpEditText, colorStateList);
        usernameSignUpEditText.setHintTextColor(hintTextColor);


        TextView passwordTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsPasswordTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsPasswordTextView.setMargins(100, 10, 0, 0);
        passwordTextView.setLayoutParams(layoutParamsPasswordTextView);
        passwordTextView.setTextSize(15);
        passwordTextView.setTextColor(Color.BLACK);
        passwordTextView.setPadding(14, 100, 14, 14);
        passwordTextView.setText("Password: ");


        //add a horizontal linear layout and add the password EditText and the feedback text for the coresponding edittext
        LoginActivity.passwordSignUpEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsPasswordEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsPasswordEditText.setMargins(0, 10, 0, 0);
        layoutParamsPasswordEditText.gravity = Gravity.CENTER;
        passwordSignUpEditText.setLayoutParams(layoutParamsPasswordEditText);
        passwordSignUpEditText.setHint("enter password");
        setCursorColor(passwordSignUpEditText, orangeAccentColor);
        ViewCompat.setBackgroundTintList(passwordSignUpEditText, colorStateList);
        passwordSignUpEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordSignUpEditText.setHintTextColor(hintTextColor);



        TextView passwordConfirationTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsPasswordConfirmationTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsPasswordConfirmationTextView.setMargins(100, 10, 0, 0);
        passwordConfirationTextView.setLayoutParams(layoutParamsPasswordConfirmationTextView);
        passwordConfirationTextView.setTextSize(15);
        passwordConfirationTextView.setTextColor(Color.BLACK);
        passwordConfirationTextView.setPadding(14, 100, 14, 14);
        passwordConfirationTextView.setText("Confirm password: ");


        //add a horizontal linear layout and add password confirmation EditText and the feedback text for the coresponding edittext
        LoginActivity.passwordConfirmationSignUpEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsPasswordConfirmationEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsPasswordConfirmationEditText.setMargins(0, 10, 0, 0);
        layoutParamsPasswordConfirmationEditText.gravity = Gravity.CENTER;
        passwordConfirmationSignUpEditText.setLayoutParams(layoutParamsPasswordConfirmationEditText);
        passwordConfirmationSignUpEditText.setHint("enter password");
        setCursorColor(passwordConfirmationSignUpEditText, orangeAccentColor);
        ViewCompat.setBackgroundTintList(passwordConfirmationSignUpEditText, colorStateList);
        passwordConfirmationSignUpEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordConfirmationSignUpEditText.setHintTextColor(hintTextColor);


        LoginActivity.signUpButton = new Button(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsSignUpButton = new LinearLayout.LayoutParams(signUpButtonWidth, signUpButtonHeight);
        layoutParamsSignUpButton.gravity = Gravity.CENTER;
        layoutParamsSignUpButton.setMargins(0, 100, 0, 100);
        signUpButton.setLayoutParams(layoutParamsSignUpButton);
        signUpButton.setBackgroundColor(Color.argb(255, 255, 255, 255));
        signUpButton.setText("Sign up");

        //sign up button listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignUpData();
            }
        });

        feedbackTextView.setText("");



        loginLinearLayout.addView(lastNameTextView);
        loginLinearLayout.addView(lastNameSignUpEditText);
        loginLinearLayout.addView(firstNameTextView);
        loginLinearLayout.addView(firstNameSignUpEditText);
        loginLinearLayout.addView(ageTextView);
        loginLinearLayout.addView(ageSignUpEditText);
        loginLinearLayout.addView(usernameTextView);
        loginLinearLayout.addView(usernameSignUpEditText);
        loginLinearLayout.addView(passwordTextView);
        loginLinearLayout.addView(passwordSignUpEditText);
        loginLinearLayout.addView(passwordConfirationTextView);
        loginLinearLayout.addView(passwordConfirmationSignUpEditText);
        loginLinearLayout.addView(signUpButton);
        loginLinearLayout.addView(feedbackTextView);
    }



    //change cursor color
    public static void setCursorColor(EditText view, @ColorInt int color) {
        try {
            // Get the cursor resource id
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(view);

            // Get the editor
            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(view);

            // Get the drawable and set a color filter
            Drawable drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {drawable, drawable};

            // Set the drawables
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception ignored) {
        }
    }

}