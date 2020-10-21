package com.beia.solomon.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.beia.solomon.GsonRequest;
import com.beia.solomon.R;
import com.beia.solomon.model.Gender;
import com.beia.solomon.model.Role;
import com.beia.solomon.model.Topic;
import com.beia.solomon.model.User;
import com.google.firebase.messaging.FirebaseMessaging;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static Resources r;
    public Context context;
    public RequestQueue volleyQueue;
    public LinearLayout mainLinearLayout;

    //sign in UI variables
    public ImageView solomonPicture;
    public CardView usernameSignInCardView;
    public EditText usernameSignInEditText;
    public CardView passwordSignInCardView;
    public EditText passwordSignInEditText;
    public CardView loginButton;
    public TextView feedbackTextView;
    public CardView loginAsGuestButton;
    public TextView createAccountTextView;
    //sign up UI variables
    public ImageView backButton;
    public TextView createAccountTitle;
    public CardView firstNameSignUpCardView;
    public EditText firstNameSignUpEditText;
    public TextView firstNameFeedbackText;
    public CardView lastNameSignUpCardView;
    public EditText lastNameSignUpEditText;
    public TextView lastNameFeedbackText;
    public CardView genderSignUpCardView;
    public Spinner genderSignUpSpinner;
    public String gender;
    public TextView genderFeedbackTextView;
    public CardView ageSignUpCardView;
    public EditText ageSignUpEditText;
    public TextView ageFeedbackText;
    public CardView usernameSignUpCardView;
    public EditText usernameSignUpEditText;
    public TextView usernameFeedbackText;
    public CardView passwordSignUpCardView;
    public EditText passwordSignUpEditText;
    public TextView passwordFeedbackText;
    public CardView passwordConfirmationSignUpCardView;
    public EditText passwordConfirmationSignUpEditText;
    public TextView passwordConfirmationFeedbackText;
    public CardView signUpButton;
    public Activity loginActivityInstance;

    private User user;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        volleyQueue = Volley.newRequestQueue(this);

        r = getResources();
        context = this.getApplicationContext();
        loginActivityInstance = this;

        initUI();
        setLoginLayout();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    public void sendSignInData(String username, String password) {
        String url = getResources().getString(R.string.login_url)
                + "?username=" + username
                + "&password=" + password;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<User> request = new GsonRequest<>(
                Request.Method.GET,
                url,
                null,
                User.class,
                headers,
                response -> {
                    Log.d("RESPONSE", response.toString());
                    feedbackTextView.setText(getResources().getString(R.string.login_successful));
                    feedbackTextView.setTextColor(getResources().getColor(R.color.greenAccent));
                    this.user = response;
                    this.password = password;
                    initFCM();
                },
                error -> {
                    if(error.networkResponse != null) {
                        Log.d("ERROR", new String(error.networkResponse.data));
                    }
                    else {
                        error.printStackTrace();
                    }
                    feedbackTextView.setText(getResources().getString(R.string.login_failed));
                    feedbackTextView.setTextColor(getResources().getColor(R.color.redAccent));
                });

        volleyQueue.add(request);
    }

    public void sendFCMToken(long userId, String token) {
        String url = getResources().getString(R.string.fcm_token_url)
                + "?userId=" + userId;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<Object> request = new GsonRequest<>(
                Request.Method.POST,
                url,
                token,
                Object.class,
                headers,
                response -> {
                    Log.d("RESPONSE", "FCM TOKEN SENT");
                    startMainActivity(user, password);
                },
                error -> {
                    if(error.networkResponse != null) {
                        Log.d("ERROR", new String(error.networkResponse.data));
                    }
                    else {
                        error.printStackTrace();
                    }
                    feedbackTextView.setText(getResources().getString(R.string.login_failed));
                    feedbackTextView.setTextColor(getResources().getColor(R.color.redAccent));
                });

        volleyQueue.add(request);
    }



    public void sendSignUpData(User user, String password) {
        String url = getResources().getString(R.string.signUp_url)
                + "?password=" + password;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<Boolean> request = new GsonRequest<>(
                Request.Method.POST,
                url,
                user,
                Boolean.class,
                headers,
                response -> {
                    Log.d("RESPONSE", response.toString());
                    if(response) {
                        Toast.makeText(context, "account created!", Toast.LENGTH_SHORT).show();
                        setLoginLayout();
                    }
                    else {
                        usernameFeedbackText.setText(getResources().getString(R.string.sign_up_failed));
                        usernameFeedbackText.setVisibility(View.VISIBLE);
                        usernameSignInCardView.setCardBackgroundColor(getResources().getColor(R.color.red));
                    }
                },
                error -> {
                    if(error.networkResponse.data != null)
                        Log.d("ERROR", "requestLocalization: " + new String(error.networkResponse.data));
                    else
                        error.printStackTrace();
                });

        volleyQueue.add(request);
    }


    public void initFCM() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FIREBASE", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    sendFCMToken(user.getId(), token);
                });
        FirebaseMessaging.getInstance().subscribeToTopic(Topic.AGENT.name());
    }


    public void startMainActivity(User user, String password) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    public void initUI() {
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
        genderSignUpCardView = findViewById(R.id.genderSignUpCardView);
        genderSignUpSpinner = findViewById(R.id.genderSignUpSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSignUpSpinner.setAdapter(adapter);
        genderSignUpSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 1:
                        gender = "MALE";
                        break;
                    case 2:
                        gender = "FEMALE";
                        break;
                    default:
                        gender = null;
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                gender = null;
            }
        });
        genderFeedbackTextView = findViewById(R.id.genderFeedbackText);
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

        createAccountTextView.setOnClickListener(v -> setSignUpLayout());
        backButton.setOnClickListener(v -> setLoginLayout());
        loginButton.setOnClickListener(v -> {
            hideKeyboard(this);
            feedbackTextView.setVisibility(View.VISIBLE);
            String username, password;
            boolean correctLoginData = true;
            username = usernameSignInEditText.getText().toString();
            password = passwordSignInEditText.getText().toString();
            if(username.trim().equals("")) {
                usernameSignInCardView.setCardBackgroundColor(getResources().getColor(R.color.red));
                correctLoginData = false;
            }
            if(password.trim().equals("")) {
                passwordSignInCardView.setCardBackgroundColor(getResources().getColor(R.color.red));
                correctLoginData = false;
            }
            if(correctLoginData) {
                sendSignInData(username, password);
            }
        });

        signUpButton.setOnClickListener(v -> {
            clearErrorTexts();
            int age = 0;
            String firstName, lastName, username, password, passwordConfirmation, ageString;
            firstName = firstNameSignUpEditText.getText().toString().trim();
            lastName = lastNameSignUpEditText.getText().toString().trim();
            ageString = ageSignUpEditText.getText().toString().trim();
            username = usernameSignUpEditText.getText().toString().trim();
            password = passwordSignUpEditText.getText().toString().trim();
            passwordConfirmation = passwordConfirmationSignUpEditText.getText().toString().trim();
            boolean correctSignUpData = validSignUpDetails(username, firstName,
                    lastName, ageString, age, gender, password, passwordConfirmation);
            if(correctSignUpData) {
                User user = User
                        .builder()
                        .username(username)
                        .firstName(firstName)
                        .lastName(lastName)
                        .age(age)
                        .gender(gender.equals("MALE") ? Gender.MALE : Gender.FEMALE)
                        .role(Role.USER.name())
                        .build();
                sendSignUpData(user, password);
            }
        });
    }

    public void setLoginLayout()
    {
        //hide the sign up views
        backButton.setVisibility(View.GONE);
        createAccountTitle.setVisibility(View.GONE);
        firstNameSignUpCardView.setVisibility(View.GONE);
        firstNameFeedbackText.setVisibility(View.INVISIBLE);
        lastNameSignUpCardView.setVisibility(View.GONE);
        lastNameFeedbackText.setVisibility(View.INVISIBLE);
        genderSignUpCardView.setVisibility(View.GONE);
        genderFeedbackTextView.setVisibility(View.INVISIBLE);
        ageSignUpCardView.setVisibility(View.GONE);
        ageFeedbackText.setVisibility(View.INVISIBLE);
        usernameSignUpCardView.setVisibility(View.GONE);
        usernameFeedbackText.setVisibility(View.INVISIBLE);
        passwordSignUpCardView.setVisibility(View.GONE);
        passwordFeedbackText.setVisibility(View.INVISIBLE);
        passwordConfirmationSignUpCardView.setVisibility(View.GONE);
        passwordConfirmationFeedbackText.setVisibility(View.INVISIBLE);
        signUpButton.setVisibility(View.GONE);
        firstNameSignUpEditText.setText("");
        lastNameSignUpEditText.setText("");
        ageSignUpEditText.setText("");
        passwordSignUpEditText.setText("");
        passwordConfirmationSignUpEditText.setText("");
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
        firstNameFeedbackText.setVisibility(View.INVISIBLE);
        lastNameSignUpCardView.setVisibility(View.VISIBLE);
        lastNameFeedbackText.setVisibility(View.INVISIBLE);
        genderSignUpCardView.setVisibility(View.VISIBLE);
        genderSignUpSpinner.setVisibility(View.VISIBLE);
        genderFeedbackTextView.setVisibility(View.INVISIBLE);
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

    public void clearErrorTexts() {
        firstNameFeedbackText.setVisibility(View.INVISIBLE);
        lastNameFeedbackText.setVisibility(View.INVISIBLE);
        ageFeedbackText.setVisibility(View.INVISIBLE);
        genderFeedbackTextView.setVisibility(View.INVISIBLE);
        usernameFeedbackText.setVisibility(View.INVISIBLE);
        passwordFeedbackText.setVisibility(View.INVISIBLE);
        passwordConfirmationFeedbackText.setVisibility(View.INVISIBLE);
    }

    public boolean validSignUpDetails(String username, String firstName,
                                      String lastName, String ageString, int age, String gender,
                                      String password, String passwordConfirmation) {

        if(firstName.equals("")) {
            firstNameFeedbackText.setText(getResources().getString(R.string.first_name_error));
            firstNameFeedbackText.setVisibility(View.VISIBLE);
            firstNameSignUpCardView.setCardBackgroundColor(getResources().getColor(R.color.red));
            return false;
        }

        if(lastName.equals("")) {
            lastNameFeedbackText.setText(getResources().getString(R.string.last_name_error));
            lastNameFeedbackText.setVisibility(View.VISIBLE);
            lastNameSignUpCardView.setCardBackgroundColor(getResources().getColor(R.color.red));
            return false;
        }

        if(gender == null) {
            genderFeedbackTextView.setText(getResources().getString(R.string.gender_error));
            genderFeedbackTextView.setVisibility(View.VISIBLE);
            genderSignUpCardView.setCardBackgroundColor(getResources().getColor(R.color.red));
            return false;
        }

        if(!ageString.equals("")) {
            age = Integer.parseInt(ageString);
            if(age < 0 || age > 120) {
                ageFeedbackText.setText(getResources().getString(R.string.age_error_1));
                ageFeedbackText.setVisibility(View.VISIBLE);
                ageSignUpCardView.setCardBackgroundColor(getResources().getColor(R.color.red));
                return false;
            }
        }
        else {
            ageFeedbackText.setText(getResources().getString(R.string.age_error_2));
            ageFeedbackText.setVisibility(View.VISIBLE);
            ageSignUpCardView.setCardBackgroundColor(getResources().getColor(R.color.red));
            return false;
        }

        if(username.equals("")) {
            usernameFeedbackText.setText(getResources().getString(R.string.username_error));
            usernameFeedbackText.setVisibility(View.VISIBLE);
            usernameSignUpCardView.setCardBackgroundColor(getResources().getColor(R.color.red));
            return false;
        }

        if(password.equals("")) {
            passwordFeedbackText.setText(getResources().getString(R.string.password_error_1));
            passwordFeedbackText.setVisibility(View.VISIBLE);
            passwordSignUpCardView.setCardBackgroundColor(getResources().getColor(R.color.red));
            return false;
        }

        if(passwordConfirmation.equals("")) {
            passwordConfirmationFeedbackText.setText(getResources().getString(R.string.password_error_1));
            passwordConfirmationFeedbackText.setVisibility(View.VISIBLE);
            passwordConfirmationSignUpCardView.setCardBackgroundColor(getResources().getColor(R.color.red));
            return false;
        }

        if(!password.equals(passwordConfirmation)) {
            passwordConfirmationFeedbackText.setText(getResources().getString(R.string.password_error_2));
            passwordConfirmationFeedbackText.setVisibility(View.VISIBLE);
            passwordSignUpCardView.setCardBackgroundColor(getResources().getColor(R.color.red));
            passwordConfirmationSignUpCardView.setCardBackgroundColor(getResources().getColor(R.color.red));
            return false;
        }

        return true;
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
        }
        catch (Exception ignored) {
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