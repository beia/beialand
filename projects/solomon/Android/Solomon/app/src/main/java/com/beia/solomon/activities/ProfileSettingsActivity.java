package com.beia.solomon.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.beia.solomon.GsonRequest;
import com.beia.solomon.R;
import com.beia.solomon.model.User;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ProfileSettingsActivity extends AppCompatActivity {

    //UI variables
    private LinearLayout profileSettingsActivityLinearLayout;
    private ImageView backButton;
    public static CircularImageView profilePicture;
    private TextView nameTextView;
    private TextView usernameTextView;
    private TextView passwordTextView;
    private TextView ageTextView;
    private ImageView usernameEditButton;
    private ImageView passwordEditButton;
    private ImageView ageEditButton;
    private Button cancelUsernameChangesButton;
    private Button cancelPasswordChangesButton;
    private Button cancelAgeChangesButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText ageEditText;
    private Button saveChangesButton;
    public int GALLERY_REQUEST_CODE = 0;

    private User user;
    private String password;
    private RequestQueue volleyQueue;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        user = (User) getIntent().getSerializableExtra("user");
        password = getIntent().getStringExtra("password");
        volleyQueue = Volley.newRequestQueue(this);
        initUI();
    }

    public void initUI() {
        profileSettingsActivityLinearLayout = findViewById(R.id.profileSettingsActivityLinearLayout);
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

        setupProfileUI();
        setupClickListeners();
    }

    public void setupClickListeners() {
        setupUsernameClickListener();
        setupPasswordClickListener();
        setupAgeClickListener();
        setupUsernameCancelClickListener();
        setupPasswordCancelClickListener();
        setupAgeCancelClickListener();
        setupSaveChangesClickListener();

        profilePicture.setOnClickListener(v -> pickFromGallery());
        backButton.setOnClickListener(v -> finish());
    }

    public void setupUsernameClickListener() {
        usernameEditButton.setOnClickListener(v -> {
            if(saveChangesButton.getVisibility() != View.VISIBLE)
                saveChangesButton.setVisibility(View.VISIBLE);
            usernameTextView.setVisibility(View.GONE);
            usernameEditText.setVisibility(View.VISIBLE);
            usernameEditButton.setVisibility(View.GONE);
            cancelUsernameChangesButton.setVisibility(View.VISIBLE);
        });
    }

    public void setupUsernameCancelClickListener() {
        cancelUsernameChangesButton.setOnClickListener(v -> {
            if(passwordEditButton.getVisibility() == View.VISIBLE
                    && ageEditButton.getVisibility() == View.VISIBLE) {
                saveChangesButton.setVisibility(View.GONE);
            }
            usernameEditText.setVisibility(View.GONE);
            usernameTextView.setVisibility(View.VISIBLE);
            cancelUsernameChangesButton.setVisibility(View.GONE);
            usernameEditButton.setVisibility(View.VISIBLE);
        });
    }

    public void setupPasswordClickListener() {
        passwordEditButton.setOnClickListener(v -> {
            if(saveChangesButton.getVisibility() != View.VISIBLE)
                saveChangesButton.setVisibility(View.VISIBLE);
            passwordTextView.setVisibility(View.GONE);
            passwordEditText.setVisibility(View.VISIBLE);
            passwordEditButton.setVisibility(View.GONE);
            cancelPasswordChangesButton.setVisibility(View.VISIBLE);
        });
    }

    public void setupPasswordCancelClickListener() {
        cancelPasswordChangesButton.setOnClickListener(v -> {
            if(usernameEditButton.getVisibility() == View.VISIBLE
                    && ageEditButton.getVisibility() == View.VISIBLE) {
                saveChangesButton.setVisibility(View.GONE);
            }
            passwordEditText.setVisibility(View.GONE);
            passwordTextView.setVisibility(View.VISIBLE);
            cancelPasswordChangesButton.setVisibility(View.GONE);
            passwordEditButton.setVisibility(View.VISIBLE);
        });
    }

    public void setupAgeClickListener() {
        ageEditButton.setOnClickListener(v -> {
            if(saveChangesButton.getVisibility() != View.VISIBLE)
                saveChangesButton.setVisibility(View.VISIBLE);
            ageTextView.setVisibility(View.GONE);
            ageEditText.setVisibility(View.VISIBLE);
            ageEditButton.setVisibility(View.GONE);
            cancelAgeChangesButton.setVisibility(View.VISIBLE);
        });
    }

    public void setupAgeCancelClickListener() {
        cancelAgeChangesButton.setOnClickListener(v -> {
            if(usernameEditButton.getVisibility() == View.VISIBLE
                    && passwordEditButton.getVisibility() == View.VISIBLE) {
                saveChangesButton.setVisibility(View.GONE);
            }
            ageEditText.setVisibility(View.GONE);
            ageTextView.setVisibility(View.VISIBLE);
            cancelAgeChangesButton.setVisibility(View.GONE);
            ageEditButton.setVisibility(View.VISIBLE);
        });
    }

    public void setupSaveChangesClickListener() {
        saveChangesButton.setOnClickListener(v -> {
            String username = user.getUsername(), password = this.password;
            int age = user.getAge();
            if(usernameEditText.getVisibility() == View.VISIBLE
                    && !usernameEditText.getText().toString().equals("")) {
                username = usernameEditText.getText().toString().trim();
            }
            if(passwordEditText.getVisibility() == View.VISIBLE
                    && !passwordEditText.getText().toString().equals("")) {
                password = passwordEditText.getText().toString().trim();
            }
            if(ageEditText.getVisibility() == View.VISIBLE
                    && !ageEditText.getText().toString().equals("")) {
                try {
                    age = Integer.parseInt(ageEditText.getText().toString().trim());
                }
                catch (NumberFormatException ex) {
                    age = user.getAge();
                }
            }

            user.setUsername(username);
            user.setAge(age);
            this.password = password;
            MainActivity.user = user;

            sendUserData(user);
            saveUserDataIntoCache();
            resetUI();
        });
    }

    public void saveUserDataIntoCache() {
        SharedPreferences.Editor editor = MainActivity.sharedPref.edit();
        editor.putString("user", new Gson().toJson(user));
        editor.putString("password", new Gson().toJson(password));
        editor.apply();
    }

    public void resetUI() {
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
        saveChangesButton.setVisibility(View.GONE);
    }


    private void setupProfileUI() {
        nameTextView.setText(
                String.format("%s %s",
                user.getFirstName(),
                user.getLastName()));
        usernameTextView.setText(user.getUsername());
        ageTextView.setText(
                String.format("%s",
                user.getAge()));
        fetchAndSetProfilePicture();
    }

    private void fetchAndSetProfilePicture() {
        if (user.getImage() != null) {
            Bitmap imageBitmap = decodeBase64(user.getImage());
            profilePicture.setImageBitmap(imageBitmap);
        } else {
            getAndSetProfilePicture(user.getId());
        }
    }

    private void pickFromGallery(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = fetchBitmapFromURI(selectedImageUri);
                    profilePicture.setImageURI(selectedImageUri);
                    Toast.makeText(this,
                            "Updated the profile picture",
                            Toast.LENGTH_LONG)
                            .show();
                    String encodedImage = ProfileSettingsActivity.encodeToBase64(bitmap);
                    sendProfilePicture(encodedImage, user.getId());
                    user.setImage(encodedImage);
                    saveUserDataIntoCache();
                } catch (IOException e) {
                    Toast.makeText(this,
                            "Image format error",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }

    public Bitmap fetchBitmapFromURI(Uri uri) throws IOException {
        Bitmap bitmap = MediaStore
                .Images
                .Media
                .getBitmap(this.getContentResolver(), uri);
        return getCorrectBitmap(
                getRealPathFromURI(uri),
                bitmap);
    }

    public Bitmap getCorrectBitmap(String photoPath, Bitmap bitmap) throws IOException {
        ExifInterface ei = new ExifInterface(photoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Bitmap rotatedBitmap = null;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }
        return rotatedBitmap;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.getMimeEncoder().encodeToString(byteArray);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.getMimeDecoder().decode(input);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }


    //REQUESTS
    public void sendProfilePicture(String encodedImage, long userId) {
        String url = getResources().getString(R.string.update_profile_picture_url) + "/"
                + userId;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<Object> request = new GsonRequest<>(
                Request.Method.POST,
                url,
                encodedImage,
                Object.class,
                headers,
                response -> {
                    Log.d("RESPONSE", response.toString());
                },
                error -> {
                    if(error.networkResponse.data != null)
                        Log.d("ERROR", "Update profile picture: " + new String(error.networkResponse.data));
                    else
                        error.printStackTrace();
                });

        volleyQueue.add(request);
    }

    public void getAndSetProfilePicture(long userId) {
        String url = getResources().getString(R.string.get_profile_picture_url) + "/"
                + userId;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<String> request = new GsonRequest<>(
                Request.Method.GET,
                url,
                null,
                String.class,
                headers,
                response -> {
                    if(response != null) {
                        Log.d("RESPONSE", response);
                        profilePicture.setImageBitmap(decodeBase64(response));
                        user.setImage(response);
                        saveUserDataIntoCache();
                    }
                },
                error -> {
                    if(error.networkResponse.data != null)
                        Log.d("ERROR", "Update profile picture: " + new String(error.networkResponse.data));
                    else
                        error.printStackTrace();
                });

        volleyQueue.add(request);
    }

    public void sendUserData(User user) {
        String url = getResources().getString(R.string.update_user_data_url)
                + "?password=" + password;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<Object> request = new GsonRequest<>(
                Request.Method.POST,
                url,
                user,
                Object.class,
                headers,
                response -> {
                    Log.d("RESPONSE", response.toString());
                },
                error -> {
                    if(error.networkResponse.data != null)
                        Log.d("ERROR", "Update user data: " + new String(error.networkResponse.data));
                    else
                        error.printStackTrace();
                });

        volleyQueue.add(request);
    }
}
