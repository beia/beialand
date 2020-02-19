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
import androidx.cardview.widget.CardView;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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
                            //set the username field red
                            Toast.makeText(context, "username is taken", Toast.LENGTH_SHORT).show();
                            break;
                        case "registered successfully":
                            break;
                        case "username or password are wrong":
                            //set the login fields red
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
        if(username == null || password == null) {
            //set login layout
            setLoginLayout();
        }
        else {
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
        //login layout
        solomonPicture = findViewById(R.id.solomonPicture);
        mainLinearLayout = findViewById(R.id.MainMenuLinearLayout);
        usernameSignInCardView = findViewById(R.id.usernameLoginCardView);
        usernameSignInEditText = findViewById(R.id.usernameLoginEditText);
        passwordSignInCardView = findViewById(R.id.passwordLoginCardView);
        passwordSignInEditText = findViewById(R.id.passwordLoginEditText);
        feedbackTextView = findViewById(R.id.feedbackText);
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
    }
    public void setLoginLayout()
    {
        //hide the sign up views
        backButton.setVisibility(View.GONE);
        createAccountTitle.setVisibility(View.GONE);
        firstNameSignUpCardView.setVisibility(View.GONE);
        firstNameFeedbackText.setVisibility(View.GONE);
        lastNameSignUpCardView.setVisibility(View.GONE);
        lastNameFeedbackText.setVisibility(View.GONE);
        ageSignUpCardView.setVisibility(View.GONE);
        ageFeedbackText.setVisibility(View.GONE);
        usernameSignUpCardView.setVisibility(View.GONE);
        usernameFeedbackText.setVisibility(View.GONE);
        passwordSignUpCardView.setVisibility(View.GONE);
        passwordFeedbackText.setVisibility(View.GONE);
        passwordConfirmationSignUpCardView.setVisibility(View.GONE);
        passwordConfirmationFeedbackText.setVisibility(View.GONE);
        signUpButton.setVisibility(View.GONE);
        //show the login views
        solomonPicture.setVisibility(View.VISIBLE);
        usernameSignInCardView.setVisibility(View.VISIBLE);
        passwordSignInCardView.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        loginAsGuestButton.setVisibility(View.VISIBLE);
        createAccountTextView.setVisibility(View.VISIBLE);
    }
    public void setSignUpLayout()
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
        firstNameFeedbackText.setVisibility(View.VISIBLE);
        lastNameSignUpCardView.setVisibility(View.VISIBLE);
        lastNameFeedbackText.setVisibility(View.VISIBLE);
        ageSignUpCardView.setVisibility(View.VISIBLE);
        ageFeedbackText.setVisibility(View.VISIBLE);
        usernameSignUpCardView.setVisibility(View.VISIBLE);
        usernameFeedbackText.setVisibility(View.VISIBLE);
        passwordSignUpCardView.setVisibility(View.VISIBLE);
        passwordFeedbackText.setVisibility(View.VISIBLE);
        passwordConfirmationSignUpCardView.setVisibility(View.VISIBLE);
        passwordConfirmationFeedbackText.setVisibility(View.VISIBLE);
        signUpButton.setVisibility(View.VISIBLE);
    }
    public void setAutomaticLogin()
    {
        mainLinearLayout.setVisibility(View.GONE);
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