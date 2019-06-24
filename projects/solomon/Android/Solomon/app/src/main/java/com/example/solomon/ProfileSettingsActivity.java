package com.example.solomon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.solomon.networkPackets.UpdateUserData;
import com.example.solomon.runnables.SendUserUpdateData;
import com.mikhaellopez.circularimageview.CircularImageView;

public class ProfileSettingsActivity extends AppCompatActivity {

    private ImageView backButton;
    private CircularImageView profilePicture;
    private TextView nameTextView;
    private TextView usernameTextView;
    private TextView passwordTextView;
    private TextView ageTextView;
    private ImageView usernameEditButton;//imageview used like a button
    private ImageView passwordEditButton;//imageview used like a button
    private ImageView ageEditButton;//imageview used like a button
    private Button cancelUsernameChangesButton;
    private Button cancelPasswordChangesButton;
    private Button cancelAgeChangesButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText ageEditText;
    private Button saveChangesButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        initUI();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        usernameEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameTextView.setVisibility(View.GONE);
                usernameEditText.setVisibility(View.VISIBLE);
                usernameEditButton.setVisibility(View.GONE);
                cancelUsernameChangesButton.setVisibility(View.VISIBLE);
            }
        });

        passwordEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordTextView.setVisibility(View.GONE);
                passwordEditText.setVisibility(View.VISIBLE);
                passwordEditButton.setVisibility(View.GONE);
                cancelPasswordChangesButton.setVisibility(View.VISIBLE);
            }
        });

        ageEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ageTextView.setVisibility(View.GONE);
                ageEditText.setVisibility(View.VISIBLE);
                ageEditButton.setVisibility(View.GONE);
                cancelAgeChangesButton.setVisibility(View.VISIBLE);
            }
        });

        cancelUsernameChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameEditText.setVisibility(View.GONE);
                usernameTextView.setVisibility(View.VISIBLE);
                cancelUsernameChangesButton.setVisibility(View.GONE);
                usernameEditButton.setVisibility(View.VISIBLE);
            }
        });

        cancelPasswordChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordEditText.setVisibility(View.GONE);
                passwordTextView.setVisibility(View.VISIBLE);
                cancelPasswordChangesButton.setVisibility(View.GONE);
                passwordEditButton.setVisibility(View.VISIBLE);
            }
        });

        cancelAgeChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ageEditText.setVisibility(View.GONE);
                ageTextView.setVisibility(View.VISIBLE);
                cancelAgeChangesButton.setVisibility(View.GONE);
                ageEditButton.setVisibility(View.VISIBLE);
            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //get the data from the UI
                String username = null, password = null;
                int age = 0;
                if(usernameEditText.getVisibility() == View.VISIBLE)
                {
                    username = usernameEditText.getText().toString().trim();
                    if(username.equals("")) {
                        username = null;
                    }
                }
                if(passwordEditText.getVisibility() == View.VISIBLE)
                {
                    password = passwordEditText.getText().toString().trim();
                    if(password.equals(""))
                        password = null;
                }
                if(ageEditText.getVisibility() == View.VISIBLE)
                {
                    try
                    {
                        age = Integer.parseInt(ageEditText.getText().toString().trim());
                        if(age < 0) {
                            age = 0;
                        }
                    }
                    catch (NumberFormatException ex)
                    {
                        age = 0;
                    }
                }

                //send update data to the server if at least one field was modified
                if(username != null || password != null || age != 0) {
                    UpdateUserData updateUserData = new UpdateUserData(MainActivity.userId, username, password, age);
                    Thread updateUserDataThread = new Thread(new SendUserUpdateData(MainActivity.objectOutputStream, updateUserData));
                    updateUserDataThread.start();
                }

                //change the user data
                if(username != null) {
                    usernameTextView.setText(MainActivity.username);
                    MainActivity.username = username;
                    usernameTextView.setText(username);
                }
                if(age != 0) {
                    ageTextView.setText(Integer.toString(MainActivity.age));
                    MainActivity.age = age;
                    ageTextView.setText(Integer.toString(age));
                }

                //set the UI as it was before updating
                cancelUsernameChangesButton.setVisibility(View.GONE);
                cancelPasswordChangesButton.setVisibility(View.GONE);
                cancelAgeChangesButton.setVisibility(View.GONE);
                usernameEditText.setVisibility(View.GONE);
                passwordEditText.setVisibility(View.GONE);
                ageEditText.setVisibility(View.GONE);
                usernameEditText.setText("");
                passwordEditText.setText("");
                ageEditText.setText("");
                usernameEditButton.setVisibility(View.VISIBLE);
                passwordEditButton.setVisibility(View.VISIBLE);
                ageEditButton.setVisibility(View.VISIBLE);
                usernameTextView.setVisibility(View.VISIBLE);
                passwordTextView.setVisibility(View.VISIBLE);
                ageTextView.setVisibility(View.VISIBLE);
            }
        });

    }
    public void initUI()
    {
        backButton = findViewById(R.id.profileSettingsBackButton);
        profilePicture = findViewById(R.id.profilePicture);
        nameTextView = findViewById(R.id.nameTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        passwordTextView = findViewById(R.id.passwordTexView);
        ageTextView = findViewById(R.id.ageTextView);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        ageEditText = findViewById(R.id.ageEditText);
        usernameEditButton = findViewById(R.id.editUsernameButton);
        passwordEditButton = findViewById(R.id.editPasswordButton);
        ageEditButton = findViewById(R.id.editAgeButton);
        cancelUsernameChangesButton = findViewById(R.id.cancelUsernameChangeButton);
        cancelPasswordChangesButton = findViewById(R.id.cancelPasswordChangeButton);
        cancelAgeChangesButton = findViewById(R.id.cancelAgeChangeButton);
        saveChangesButton = findViewById(R.id.saveChangesButton);


        //set the UI based on user data
        nameTextView.setText(MainActivity.lastName + " " + MainActivity.firstName);
        usernameTextView.setText(MainActivity.username);
        ageTextView.setText(Integer.toString(MainActivity.age));
    }
}
