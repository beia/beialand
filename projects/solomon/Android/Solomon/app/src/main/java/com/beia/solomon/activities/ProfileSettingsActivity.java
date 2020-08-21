package com.beia.solomon.activities;

import android.app.Activity;
import android.content.Context;
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
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.beia.solomon.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    //intent variables
    public int GALLERY_REQUEST_CODE = 0;

    //other variables
    public static Context profileSettingsContext;
    public static ProfileSettingsActivity profileSettingsActivity;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        profileSettingsContext = getApplicationContext();
        profileSettingsActivity = this;

        initUI();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });

        usernameEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveChangesButton.getVisibility() != View.VISIBLE)
                    saveChangesButton.setVisibility(View.VISIBLE);
                usernameTextView.setVisibility(View.GONE);
                usernameEditText.setVisibility(View.VISIBLE);
                usernameEditButton.setVisibility(View.GONE);
                cancelUsernameChangesButton.setVisibility(View.VISIBLE);
            }
        });

        passwordEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveChangesButton.getVisibility() != View.VISIBLE)
                    saveChangesButton.setVisibility(View.VISIBLE);
                passwordTextView.setVisibility(View.GONE);
                passwordEditText.setVisibility(View.VISIBLE);
                passwordEditButton.setVisibility(View.GONE);
                cancelPasswordChangesButton.setVisibility(View.VISIBLE);
            }
        });

        ageEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveChangesButton.getVisibility() != View.VISIBLE)
                    saveChangesButton.setVisibility(View.VISIBLE);
                ageTextView.setVisibility(View.GONE);
                ageEditText.setVisibility(View.VISIBLE);
                ageEditButton.setVisibility(View.GONE);
                cancelAgeChangesButton.setVisibility(View.VISIBLE);
            }
        });

        cancelUsernameChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordEditButton.getVisibility() == View.VISIBLE && ageEditButton.getVisibility() == View.VISIBLE)
                {
                    saveChangesButton.setVisibility(View.GONE);
                }
                usernameEditText.setVisibility(View.GONE);
                usernameTextView.setVisibility(View.VISIBLE);
                cancelUsernameChangesButton.setVisibility(View.GONE);
                usernameEditButton.setVisibility(View.VISIBLE);
            }
        });

        cancelPasswordChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameEditButton.getVisibility() == View.VISIBLE && ageEditButton.getVisibility() == View.VISIBLE)
                {
                    saveChangesButton.setVisibility(View.GONE);
                }
                passwordEditText.setVisibility(View.GONE);
                passwordTextView.setVisibility(View.VISIBLE);
                cancelPasswordChangesButton.setVisibility(View.GONE);
                passwordEditButton.setVisibility(View.VISIBLE);
            }
        });

        cancelAgeChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameEditButton.getVisibility() == View.VISIBLE && passwordEditButton.getVisibility() == View.VISIBLE)
                {
                    saveChangesButton.setVisibility(View.GONE);
                }
                ageEditText.setVisibility(View.GONE);
                ageTextView.setVisibility(View.VISIBLE);
                cancelAgeChangesButton.setVisibility(View.GONE);
                ageEditButton.setVisibility(View.VISIBLE);
            }
        });


//        saveChangesButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v)
//            {
//
//                //get the data from the UI
//                String username = MainActivity.userData.getUsername(), password = null;
//                int age = MainActivity.userData.getAge();
//                if(usernameEditText.getVisibility() == View.VISIBLE && !usernameEditText.getText().toString().equals(""))
//                    username = usernameEditText.getText().toString().trim();
//                if(passwordEditText.getVisibility() == View.VISIBLE && !passwordEditText.getText().toString().equals(""))
//                    password = passwordEditText.getText().toString().trim();
//                if(ageEditText.getVisibility() == View.VISIBLE && !ageEditText.getText().toString().equals("")) {
//                    try {
//                        age = Integer.parseInt(ageEditText.getText().toString().trim());
//                    }
//                    catch (NumberFormatException ex) {
//                        age = MainActivity.userData.getAge();
//                    }
//                }
//                if(password != null) {//encrypt the password
//                    try {
//                        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
//                        password = new String(messageDigest.digest(password.getBytes()), StandardCharsets.UTF_8);
//                    }
//                    catch (NoSuchAlgorithmException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                //send the update request to the server
//                //String request = "{\"requestType\":\"updateUserData\", \"userID\":" + MainActivity.userData.getUserId() + ", \"username\":\"" + username + "\", \"password\":\"" + password + "\", \"age\":" + age + "}";
//                //new Thread(new RequestRunnable(request, MainActivity.objectOutputStream)).start();
//                Toast.makeText(ProfileSettingsActivity.profileSettingsContext, "info updated", Toast.LENGTH_LONG).show();
//
//                //change the user data
//                if(!username.equals(MainActivity.userData.getUsername())) {
//                    usernameTextView.setText(MainActivity.userData.getUsername());
//                    MainActivity.userData.setUsername(username);
//                    usernameTextView.setText(username);
//                }
//                if(age != MainActivity.userData.getAge()) {
//                    ageTextView.setText(Integer.toString(MainActivity.userData.getAge()));
//                    MainActivity.userData.setAge(age);
//                    ageTextView.setText(Integer.toString(age));
//                }
//
//                //set the UI as it was before updating
//                cancelUsernameChangesButton.setVisibility(View.GONE);
//                cancelPasswordChangesButton.setVisibility(View.GONE);
//                cancelAgeChangesButton.setVisibility(View.GONE);
//                usernameEditText.setVisibility(View.GONE);
//                passwordEditText.setVisibility(View.GONE);
//                ageEditText.setVisibility(View.GONE);
//                usernameEditText.setText("");
//                passwordEditText.setText("");
//                ageEditText.setText("");
//                usernameEditButton.setVisibility(View.VISIBLE);
//                passwordEditButton.setVisibility(View.VISIBLE);
//                ageEditButton.setVisibility(View.VISIBLE);
//                usernameTextView.setVisibility(View.VISIBLE);
//                passwordTextView.setVisibility(View.VISIBLE);
//                ageTextView.setVisibility(View.VISIBLE);
//                saveChangesButton.setVisibility(View.GONE);
//            }
//        });

    }
    public void initUI()
    {
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
//
//        //set the UI based on user data
//       // nameTextView.setText(MainActivity.userData.getLastName() + " " + MainActivity.userData.getFirstName());
//        //usernameTextView.setText(MainActivity.userData.getUsername());
//        //ageTextView.setText(Integer.toString(MainActivity.userData.getAge()));
//
//        //if we can't find the profile picture in the cache we check in the users prefs or we get it from the server
//        if(MainActivity.picturesCache == null || MainActivity.picturesCache.get("profilePicture") == null)
//        {
//            //we check for the profile picture in the memory and if we find it we check if the same user is logged in as the one who's picture is saved
//            //if that's the case we get it from the memory(the picture is saved as a String and then it's concatenated with the user id with a space in between)
//            //if it's not the case then we download it from the server
//            if (MainActivity.sharedPref.contains("profilePicture"))
//            {
//                String userImageString = "";
//                //get the image string from the disk
//                userImageString = MainActivity.sharedPref.getString("profilePicture", "noImage");
//                if(!userImageString.equals("noImage"))
//                {
//                    String[] data = userImageString.split(" ");
//                    String imageString = data[0];
//                    int userId = Integer.parseInt(data[1]);
//                    if(userId == MainActivity.userData.getUserId()) {
//                        Bitmap imageBitmap = decodeBase64(imageString);
//                        ProfileSettingsActivity.profilePicture.setImageBitmap(imageBitmap);
//                        //save the picture into cache memory
//                        MainActivity.picturesCache.put("profilePicture", imageBitmap);
//                        return;
//                    }
//                }
//            }
//        }
//        else
//        {
//            //get the picture from cache
//            Bitmap bitmap = MainActivity.picturesCache.get("profilePicture");
//            ProfileSettingsActivity.profilePicture.setImageBitmap(bitmap);
//        }
    }


    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        // Result code is RESULT_OK only if the user selects an Image
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//            switch (requestCode) {
//
//                case 0://GALLERY_REQUEST_CODE
//                    //data.getData returns the content URI for the selected Image
//                    Uri selectedImageUri = data.getData();
//                    String path = getRealPathFromURI(selectedImageUri);
//                    try {
//                        //extract bytes from the imageUri and create an object that contains the profile picture and the user id
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
//                        bitmap = getCorrectBitmap(path, bitmap);
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        //compress the bitmap data and save it into a byte array
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                        byte[] imageContent = baos.toByteArray();
//                        int userId = MainActivity.userData.getUserId();
//                        ImageData imageData = new ImageData(imageContent, userId);
//                        baos.close();
//
//                        //set the profile picture with the selected one
//                        profilePicture.setImageURI(selectedImageUri);
//                        Toast.makeText(this, "Updated the profile picture", Toast.LENGTH_LONG).show();
//
//                        //start sending image thread
//                        synchronized (MainActivity.objectOutputStream) {
//                            //Thread sendImageThread = new Thread(new SendImageRunable(imageData, MainActivity.objectOutputStream));
//                            //sendImageThread.start();
//                        }
//                        //save the picture into cache memory
//                        MainActivity.picturesCache.put("profilePicture", bitmap);
//                        //get preferences
//                        SharedPreferences.Editor userPrefs = MainActivity.sharedPref.edit();
//                        //save the profile picture into the memory
//                        String imageString = ProfileSettingsActivity.encodeTobase64(bitmap);
//                        String userImageString = imageString + " " + MainActivity.userData.getUserId();
//                        userPrefs.putString("profilePicture", userImageString);
//                        userPrefs.apply();
//                    } catch (IOException e) {
//                        Toast.makeText(this, "Image format error", Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                    }
//            }
//        }
    }

    public Bitmap getCorrectBitmap(String photoPath, Bitmap bitmap) throws IOException
    {
        ExifInterface ei = new ExifInterface(photoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Bitmap rotatedBitmap = null;
        switch (orientation)
        {
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
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static Bitmap rotateImage(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    // method for bitmap to base64
    public static String encodeTobase64(Bitmap image) throws IOException {
        Bitmap bitmap = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        baos.close();
        return imageEncoded;
    }
    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
