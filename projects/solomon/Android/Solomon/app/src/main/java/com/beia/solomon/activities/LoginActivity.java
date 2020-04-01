package com.beia.solomon.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.ColorInt;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beia.solomon.R;
import com.beia.solomon.networkPackets.SignInData;
import com.beia.solomon.networkPackets.SignUpData;
import com.beia.solomon.networkPackets.UserData;
import com.beia.solomon.runnables.SendAuthenticationDataRunnable;
import com.beia.solomon.runnables.WaitForServerDataRunnable;

import java.lang.reflect.Field;
import java.net.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    public static Resources r;
    public static Context context;

    //Threads
    public static Thread waitForServerData;

    //networking variables
    public static volatile Socket socket;
    public static volatile ObjectOutputStream objectOutputStream;
    public static volatile ObjectInputStream objectInputStream;

    public static volatile boolean active = false;


    //UI variables
    public static LinearLayout mainLinearLayout;    //the linear layout that contains the title and the other linear layout
    public static int hintTextColor = Color.argb(50, 0, 0, 0);
    public static int orangeAccentColor = Color.argb(200,29, 222, 190);
    //sign in UI variables
    public static ImageView solomonPicture;
    public static CardView usernameSignInCardView;
    public static EditText usernameSignInEditText;
    public static CardView passwordSignInCardView;
    public static EditText passwordSignInEditText;
    public static CardView loginButton;
    public static TextView feedbackTextView;
    public static CardView loginAsGuestButton;
    public static TextView createAccountTextView;
    //sign up UI variables
    public static ImageView backButton;
    public static TextView createAccountTitle;
    public static CardView firstNameSignUpCardView;
    public static EditText firstNameSignUpEditText;
    public static TextView firstNameFeedbackText;
    public static CardView lastNameSignUpCardView;
    public static EditText lastNameSignUpEditText;
    public static TextView lastNameFeedbackText;
    public static CardView ageSignUpCardView;
    public static EditText ageSignUpEditText;
    public static TextView ageFeedbackText;
    public static CardView usernameSignUpCardView;
    public static EditText usernameSignUpEditText;
    public static TextView usernameFeedbackText;
    public static CardView passwordSignUpCardView;
    public static EditText passwordSignUpEditText;
    public static TextView passwordFeedbackText;
    public static CardView passwordConfirmationSignUpCardView;
    public static EditText passwordConfirmationSignUpEditText;
    public static TextView passwordConfirmationFeedbackText;
    public static CardView signUpButton;

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
                            usernameFeedbackText.setText("username is taken");
                            usernameFeedbackText.setVisibility(View.VISIBLE);
                            usernameSignInCardView.setBackgroundColor(LoginActivity.loginActivityInstance.getResources().getColor(R.color.red));
                            break;
                        case "registered successfully":
                            firstNameFeedbackText.setVisibility(View.INVISIBLE);
                            lastNameFeedbackText.setVisibility(View.INVISIBLE);
                            ageFeedbackText.setVisibility(View.INVISIBLE);
                            usernameFeedbackText.setVisibility(View.INVISIBLE);
                            passwordFeedbackText.setVisibility(View.INVISIBLE);
                            passwordConfirmationFeedbackText.setVisibility(View.INVISIBLE);
                            firstNameSignUpEditText.setText("");
                            lastNameSignUpEditText.setText("");
                            ageSignUpEditText.setText("");
                            passwordSignUpEditText.setText("");
                            passwordConfirmationSignUpEditText.setText("");
                            Toast.makeText(context, "account created", Toast.LENGTH_SHORT).show();
                            setLoginLayout();
                            break;
                        case "username or password are wrong":
                            //set the login fields red
                            usernameSignInCardView.setBackgroundColor(LoginActivity.loginActivityInstance.getResources().getColor(R.color.red));
                            passwordSignInCardView.setBackgroundColor(LoginActivity.loginActivityInstance.getResources().getColor(R.color.red));
                            feedbackTextView.setVisibility(View.INVISIBLE);
                            Toast.makeText(context, "username or password are wrong", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                    break;

                case 2://login successful
                    feedbackTextView.setText("login successful");
                    feedbackTextView.setTextColor(LoginActivity.context.getResources().getColor(R.color.greenAccent));
                    UserData userData = (UserData) msg.obj;

                    //check if it's the first login of the user so we can setup his preferences first
                    if(userData.isFirstLogin())
                    {
                        //start the preferences activity
                        usernameSignInEditText.setText("");
                        passwordSignInEditText.setText("");
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
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //request permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        //initialize variables
        r = getResources();
        active = true;
        context = this.getApplicationContext();
        loginActivityInstance = this;

        waitForServerData = new Thread(new WaitForServerDataRunnable("LoginActivity"));
        waitForServerData.start();

        //initialize the UI
        initUI();
        setLoginLayout();
    }
    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    //CLIENT COMMUNICATION METHODS
    public void sendSignInData(SignInData signInData)
    {
        Thread sendSignInDataThread = new Thread(new SendAuthenticationDataRunnable("sign in", signInData, objectOutputStream));
        sendSignInDataThread.start();
    }
    public void sendSignUpData(SignUpData signUpData)
    {
        Thread sendSignUpDataThread = new Thread(new SendAuthenticationDataRunnable("sign up", signUpData, objectOutputStream));
        sendSignUpDataThread.start();
    }


    //UI METHODS
    public void initUI()
    {
        //login layout
        solomonPicture = findViewById(R.id.solomonPicture);
        mainLinearLayout = findViewById(R.id.MainMenuLinearLayout);
        usernameSignInCardView = findViewById(R.id.usernameLoginCardView);
        usernameSignInEditText = findViewById(R.id.usernameLoginEditText);
        passwordSignInCardView = findViewById(R.id.passwordLoginCardView);
        passwordSignInEditText = findViewById(R.id.passwordLoginEditText);
        feedbackTextView = findViewById(R.id.feedbackText);
        feedbackTextView.setVisibility(View.INVISIBLE);
        loginButton = findViewById(R.id.loginButton);
        loginAsGuestButton = findViewById(R.id.guestLoginButton);
        createAccountTextView = findViewById(R.id.createAccountText);
        //sign up layout
        backButton = findViewById(R.id.signUpBackButton);
        createAccountTitle = findViewById(R.id.createAccountTitle);
        firstNameSignUpCardView = findViewById(R.id.firstNameSignUpCardView);
        firstNameSignUpEditText = findViewById(R.id.firstNameSignUpEditText);
        firstNameFeedbackText = findViewById(R.id.firstNameFeedbackText);
        lastNameSignUpCardView = findViewById(R.id.lastNameSignUpCardView);
        lastNameSignUpEditText = findViewById(R.id.lastNameSignUpEditText);
        lastNameFeedbackText = findViewById(R.id.lastNameFeedbackText);
        ageSignUpCardView = findViewById(R.id.ageSignUpCardView);
        ageSignUpEditText = findViewById(R.id.ageSignUpEditText);
        ageFeedbackText = findViewById(R.id.ageFeedbackText);
        usernameSignUpCardView = findViewById(R.id.usernameSignUpCardView);
        usernameSignUpEditText = findViewById(R.id.usernameSignUpEditText);
        usernameFeedbackText = findViewById(R.id.usernameFeedbackText);
        passwordSignUpCardView = findViewById(R.id.passwordSignUpCardView);
        passwordSignUpEditText = findViewById(R.id.passwordSignUpEditText);
        passwordFeedbackText = findViewById(R.id.passwordFeedbackText);
        passwordConfirmationSignUpCardView = findViewById(R.id.passwordConfirmationSignUpCardView);
        passwordConfirmationSignUpEditText = findViewById(R.id.passwordConfirmationSignUpEditText);
        passwordConfirmationFeedbackText = findViewById(R.id.passwordConfirmationFeedbackText);
        signUpButton = findViewById(R.id.signUpButton);

        //click listeners
        createAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSignUpLayout();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoginLayout();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(LoginActivity.loginActivityInstance);
                feedbackTextView.setVisibility(View.VISIBLE);
                String username, password;
                boolean correctLoginData = true;
                username = usernameSignInEditText.getText().toString();
                password = passwordSignInEditText.getText().toString();
                if(username.trim().equals("")) {
                    usernameSignInCardView.setBackgroundColor(getResources().getColor(R.color.red));
                    correctLoginData = false;
                }
                if(password.trim().equals("")) {
                    passwordSignInCardView.setBackgroundColor(getResources().getColor(R.color.red));
                    correctLoginData = false;
                }
                if(correctLoginData) {
                    String encryptedPassword = null;
                    try {//encrypt the password
                        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                        encryptedPassword = new String(messageDigest.digest(password.getBytes()), StandardCharsets.UTF_8);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    if(encryptedPassword != null) {
                        sendSignInData(new SignInData(username, encryptedPassword));
                        feedbackTextView.setVisibility(View.VISIBLE);
                    }
                    else
                        Log.d("LOGIN", "error: can't encrypt password");
                }
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear the error texts
                firstNameFeedbackText.setVisibility(View.INVISIBLE);
                lastNameFeedbackText.setVisibility(View.INVISIBLE);
                ageFeedbackText.setVisibility(View.INVISIBLE);
                usernameFeedbackText.setVisibility(View.INVISIBLE);
                passwordFeedbackText.setVisibility(View.INVISIBLE);
                passwordConfirmationFeedbackText.setVisibility(View.INVISIBLE);
                //get the sign up data
                String firstName, lastName, username, password, passwordConfirmation;
                int age;
                firstName = firstNameSignUpEditText.getText().toString().trim();
                lastName = lastNameSignUpEditText.getText().toString().trim();
                age = Integer.parseInt(ageSignUpEditText.getText().toString().trim());
                username = usernameSignUpEditText.getText().toString().trim();
                password = passwordSignUpEditText.getText().toString().trim();
                passwordConfirmation = passwordConfirmationSignUpEditText.getText().toString().trim();
                boolean correctSignUpData = true;
                if(firstName.equals(""))
                {
                    firstNameFeedbackText.setText("first name not filled");
                    firstNameFeedbackText.setVisibility(View.VISIBLE);
                    firstNameSignUpCardView.setBackgroundColor(getResources().getColor(R.color.red));
                    correctSignUpData = false;
                }
                if(lastName.equals(""))
                {
                    lastNameFeedbackText.setText("last name not filled");
                    lastNameFeedbackText.setVisibility(View.VISIBLE);
                    lastNameSignUpCardView.setBackgroundColor(getResources().getColor(R.color.red));
                    correctSignUpData = false;
                }
                if(age < 0 || age > 120)
                {
                    ageFeedbackText.setText("age not possible");
                    ageFeedbackText.setVisibility(View.VISIBLE);
                    ageSignUpCardView.setBackgroundColor(getResources().getColor(R.color.red));
                    correctSignUpData = false;
                }
                if(Integer.toString(age).equals(""))
                {
                    ageFeedbackText.setText("age not filled");
                    ageFeedbackText.setVisibility(View.VISIBLE);
                    ageSignUpCardView.setBackgroundColor(getResources().getColor(R.color.red));
                    correctSignUpData = false;
                }
                if(username.equals(""))
                {
                    usernameFeedbackText.setText("username not filled");
                    usernameFeedbackText.setVisibility(View.VISIBLE);
                    usernameSignUpCardView.setBackgroundColor(getResources().getColor(R.color.red));
                    correctSignUpData = false;
                }
                if(password.equals(""))
                {
                    passwordFeedbackText.setText("password not filled");
                    passwordFeedbackText.setVisibility(View.VISIBLE);
                    passwordSignUpCardView.setBackgroundColor(getResources().getColor(R.color.red));
                    correctSignUpData = false;
                }
                if(passwordConfirmation.equals(""))
                {
                    passwordConfirmationFeedbackText.setText("password confirmation not filled");
                    passwordConfirmationFeedbackText.setVisibility(View.VISIBLE);
                    passwordConfirmationSignUpCardView.setBackgroundColor(getResources().getColor(R.color.red));
                    correctSignUpData = false;
                }
                if(!password.equals(passwordConfirmation))
                {
                    passwordConfirmationFeedbackText.setText("passwords don't match");
                    passwordConfirmationFeedbackText.setVisibility(View.VISIBLE);
                    passwordSignUpCardView.setBackgroundColor(getResources().getColor(R.color.red));
                    passwordConfirmationSignUpCardView.setBackgroundColor(getResources().getColor(R.color.red));
                    correctSignUpData = false;
                }
                if(correctSignUpData)
                {
                    String encryptedPassword = null;
                    try {//encrypt the password
                        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                        encryptedPassword = new String(messageDigest.digest(password.getBytes()), StandardCharsets.UTF_8);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    if(encryptedPassword != null)
                        sendSignUpData(new SignUpData(lastName, firstName, age, username, encryptedPassword, encryptedPassword));
                    else
                        Log.d("LOGIN", "error: can't encrypt password");
                }
            }
        });
    }
    public static void setLoginLayout()
    {
        //hide the sign up views
        backButton.setVisibility(View.GONE);
        createAccountTitle.setVisibility(View.GONE);
        firstNameSignUpCardView.setVisibility(View.GONE);
        firstNameFeedbackText.setVisibility(View.INVISIBLE);
        lastNameSignUpCardView.setVisibility(View.GONE);
        lastNameFeedbackText.setVisibility(View.INVISIBLE);
        ageSignUpCardView.setVisibility(View.GONE);
        ageFeedbackText.setVisibility(View.INVISIBLE);
        usernameSignUpCardView.setVisibility(View.GONE);
        usernameFeedbackText.setVisibility(View.INVISIBLE);
        passwordSignUpCardView.setVisibility(View.GONE);
        passwordFeedbackText.setVisibility(View.INVISIBLE);
        passwordConfirmationSignUpCardView.setVisibility(View.GONE);
        passwordConfirmationFeedbackText.setVisibility(View.INVISIBLE);
        signUpButton.setVisibility(View.GONE);
        //show the login views
        solomonPicture.setVisibility(View.VISIBLE);
        usernameSignInCardView.setVisibility(View.VISIBLE);
        passwordSignInCardView.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        loginAsGuestButton.setVisibility(View.VISIBLE);
        createAccountTextView.setVisibility(View.VISIBLE);
    }
    public static void setSignUpLayout()
    {
        //hide the login views
        solomonPicture.setVisibility(View.GONE);
        usernameSignInCardView.setVisibility(View.GONE);
        passwordSignInCardView.setVisibility(View.GONE);
        feedbackTextView.setVisibility(View.INVISIBLE);
        loginButton.setVisibility(View.GONE);
        loginAsGuestButton.setVisibility(View.GONE);
        createAccountTextView.setVisibility(View.GONE);
        //show the sign up views
        backButton.setVisibility(View.VISIBLE);
        createAccountTitle.setVisibility(View.VISIBLE);
        firstNameSignUpCardView.setVisibility(View.VISIBLE);
        firstNameFeedbackText.setVisibility(View.INVISIBLE);
        lastNameSignUpCardView.setVisibility(View.VISIBLE);
        lastNameFeedbackText.setVisibility(View.INVISIBLE);
        ageSignUpCardView.setVisibility(View.VISIBLE);
        ageFeedbackText.setVisibility(View.INVISIBLE);
        usernameSignUpCardView.setVisibility(View.VISIBLE);
        usernameFeedbackText.setVisibility(View.INVISIBLE);
        passwordSignUpCardView.setVisibility(View.VISIBLE);
        passwordFeedbackText.setVisibility(View.INVISIBLE);
        passwordConfirmationSignUpCardView.setVisibility(View.VISIBLE);
        passwordConfirmationFeedbackText.setVisibility(View.INVISIBLE);
        signUpButton.setVisibility(View.VISIBLE);
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

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}