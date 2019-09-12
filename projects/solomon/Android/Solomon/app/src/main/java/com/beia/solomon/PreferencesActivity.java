package com.beia.solomon;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.PathInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beia.solomon.networkPackets.UserData;
import com.beia.solomon.networkPackets.UserPreferences;
import com.beia.solomon.runnables.SendPreferencesRunnable;
import com.beia.solomon.runnables.UpdateImageLocationRunnable;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class PreferencesActivity extends AppCompatActivity {

    public static int displayWidth;
    public static int displayHeight;
    public static RelativeLayout preferencesRelativeLayout;

    //preferences images and data related to each preference
    //shoes
    public static LinearLayout shoesLayout;
    public static ImageView shoesImageView;
    public static TextView shoesTextView;
    public static int shoesImageWidth;
    public static int shoesImageHeight;
    public static Thread updateShoesImageLocation;
    public static volatile boolean shoesSelected = false;

    //electronics
    public static LinearLayout electronicsLayout;
    public static ImageView electronicsImageView;
    public static TextView electronicsTextView;
    public static int electronicsImageWidth;
    public static int electronicsImageHeight;
    public static Thread updateElectronicsImageLocation;
    public static volatile boolean electronicsSelected = false;

    //clothes
    public static LinearLayout clothesLayout;
    public static ImageView clothesImageView;
    public static TextView clothesTextView;
    public static int clothesImageWidth;
    public static int clothesImageHeight;
    public static Thread updateClothesImageLocation;
    public static volatile boolean clothesSelected = false;

    //food
    public static LinearLayout foodLayout;
    public static ImageView foodImageView;
    public static TextView foodTextView;
    public static int foodImageWidth;
    public static int foodImageHeight;
    public static Thread updateFoodImageLocation;
    public static volatile boolean foodSelected = false;

    //cofee
    public static LinearLayout cofeeLayout;
    public static ImageView cofeeImageView;
    public static TextView cofeeTextView;
    public static int cofeeImageWidth;
    public static int cofeeImageHeight;
    public static Thread updateCofeeImageLocation;
    public static volatile boolean cofeeSelected = false;

    //sports
    public static LinearLayout sportsLayout;
    public static ImageView sportsImageView;
    public static TextView sportsTextView;
    public static int sportsImageWidth;
    public static int sportsImageHeight;
    public static Thread updateSportsImageLocation;
    public static volatile boolean sportsSelected = false;

    //shopping cart
    public static ImageView shoppingCart;
    public static int shoppingCartSize = 0;

    //submit preferences variables
    public static HashSet<String> preferencesSet;
    public static Button submitButton;

    //communication variables
    public static ObjectOutputStream objectOutputStream;
    public static ObjectInputStream objectInputStream;

    //user data
    public static UserData userData;




    public static Handler handler1 = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 1://change shoes position
                    synchronized (shoesLayout) {
                        int[] incrementXYShoes = (int[]) msg.obj;
                        RelativeLayout.LayoutParams paramsShoes = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                        paramsShoes.leftMargin = shoesLayout.getLeft() + incrementXYShoes[0];//XCOORD
                        paramsShoes.topMargin = shoesLayout.getTop() + incrementXYShoes[1]; //YCOORD
                        if (!shoesSelected)
                            shoesLayout.setLayoutParams(paramsShoes);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    public static Handler handler2 = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 1://change electronics position
                    synchronized (electronicsLayout) {
                        int[] incrementXYElectronics = (int[]) msg.obj;
                        RelativeLayout.LayoutParams paramsElectronics = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                        paramsElectronics.leftMargin = electronicsLayout.getLeft() + incrementXYElectronics[0];//XCOORD
                        paramsElectronics.topMargin = electronicsLayout.getTop() + incrementXYElectronics[1]; //YCOORD
                        if(!electronicsSelected)
                            electronicsLayout.setLayoutParams(paramsElectronics);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    public static Handler handler3 = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 1://change clothes position
                    synchronized (clothesLayout) {
                        int[] incrementXYClothes = (int[]) msg.obj;
                        RelativeLayout.LayoutParams paramsClothes = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                        paramsClothes.leftMargin = clothesLayout.getLeft() + incrementXYClothes[0];//XCOORD
                        paramsClothes.topMargin = clothesLayout.getTop() + incrementXYClothes[1]; //YCOORD
                        if(!clothesSelected)
                            clothesLayout.setLayoutParams(paramsClothes);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    public static Handler handler4 = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 1://change food position
                    synchronized (foodLayout) {
                        int[] incrementXYFood = (int[]) msg.obj;
                        RelativeLayout.LayoutParams paramsFood = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                        paramsFood.leftMargin = foodLayout.getLeft() + incrementXYFood[0];//XCOORD
                        paramsFood.topMargin = foodLayout.getTop() + incrementXYFood[1]; //YCOORD
                        if(!foodSelected)
                            foodLayout.setLayoutParams(paramsFood);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    public static Handler handler5 = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 1://change cofee position
                    synchronized (cofeeLayout) {
                        int[] incrementXYCofee = (int[]) msg.obj;
                        RelativeLayout.LayoutParams paramsCofee = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                        paramsCofee.leftMargin = cofeeLayout.getLeft() + incrementXYCofee[0];//XCOORD
                        paramsCofee.topMargin = cofeeLayout.getTop() + incrementXYCofee[1]; //YCOORD
                        if(!cofeeSelected)
                            cofeeLayout.setLayoutParams(paramsCofee);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    public static Handler handler6 = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 1://change sports position
                    synchronized (sportsLayout) {
                        int[] incrementXYSports = (int[]) msg.obj;
                        RelativeLayout.LayoutParams paramsSports = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                        paramsSports.leftMargin = sportsLayout.getLeft() + incrementXYSports[0];//XCOORD
                        paramsSports.topMargin = sportsLayout.getTop() + incrementXYSports[1]; //YCOORD
                        if(!sportsSelected)
                            sportsLayout.setLayoutParams(paramsSports);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_preferences);
        initUI();

        //get the communication variables from the previous activity
        objectOutputStream = LoginActivity.objectOutputStream;
        objectInputStream = LoginActivity.objectInputStream;

        //get the userdata from the intent
        userData = (UserData) getIntent().getSerializableExtra("UserData");

        preferencesSet = new HashSet<>();

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;
        preferencesRelativeLayout.post(new Runnable() {
            @Override
            public void run() {
                //set the initial location of the shoes preference
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                Random random = new Random();
                params.leftMargin = random.nextInt(displayWidth - shoesLayout.getMeasuredWidth()); //XCOORD
                params.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                shoesLayout.setLayoutParams(params);
                //update the shoes location
                final UpdateImageLocationRunnable updateShoesRunnable = new UpdateImageLocationRunnable(shoesLayout, shoesImageWidth, shoesImageHeight, displayWidth, displayHeight, false, "shoes");
                updateShoesImageLocation = new Thread(updateShoesRunnable);
                updateShoesImageLocation.start();
                shoesLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        if(!shoesSelected) {
                            shoesSelected = true;
                            preferencesSet.add("shoes");
                            updateShoesImageLocation.interrupt();
                            synchronized (shoesLayout)
                            {
                                int[] shoppingCartLocation = new int[2];
                                shoppingCart.getLocationOnScreen(shoppingCartLocation);
                                //add the shoes preference in the shopping cart
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                                params.leftMargin = shoppingCartLocation[0] + (shoppingCart.getWidth() / 3) + (shoppingCartSize++ * shoppingCart.getWidth() / 10); //XCOORD
                                params.topMargin = shoppingCartLocation[1] + (shoppingCart.getHeight() / 5); //YCOORD
                                shoesLayout.setLayoutParams(params);
                            }
                        }
                        else {
                            shoesSelected = false;
                            preferencesSet.remove("shoes");
                            shoppingCartSize--;
                            //set a new random position for the shoes preference
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                            Random random = new Random();
                            params.leftMargin = random.nextInt(displayWidth - shoesLayout.getMeasuredWidth()); //XCOORD
                            params.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                            shoesLayout.setLayoutParams(params);
                            //update the shoes location
                            final UpdateImageLocationRunnable updateShoesRunnable = new UpdateImageLocationRunnable(shoesLayout, shoesImageWidth, shoesImageHeight, displayWidth, displayHeight, false, "shoes");
                            updateShoesImageLocation = new Thread(updateShoesRunnable);
                            updateShoesImageLocation.start();
                        }

                        //check if there are 3 preferences selected
                        if(shoppingCartSize == 3)
                        {
                            submitButton.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            submitButton.setVisibility(View.GONE);
                        }
                    }
                });


                //set the initial location of the electronics preference
                RelativeLayout.LayoutParams paramsElectronics = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                Random randomElectronics = new Random();
                paramsElectronics.leftMargin = randomElectronics.nextInt(displayWidth - electronicsLayout.getMeasuredWidth()); //XCOORD
                paramsElectronics.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                electronicsLayout.setLayoutParams(paramsElectronics);
                //update the electronics location
                updateElectronicsImageLocation = new Thread(new UpdateImageLocationRunnable(electronicsLayout, electronicsImageWidth, electronicsImageHeight, displayWidth, displayHeight, false, "electronics"));
                updateElectronicsImageLocation.start();
                electronicsLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!electronicsSelected) {
                            electronicsSelected = true;
                            preferencesSet.add("electronics");
                            updateElectronicsImageLocation.interrupt();
                            synchronized (electronicsLayout)
                            {
                                int[] shoppingCartLocation = new int[2];
                                shoppingCart.getLocationOnScreen(shoppingCartLocation);
                                //add the electronics preference in the shopping cart
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                                params.leftMargin = shoppingCartLocation[0] + (shoppingCart.getWidth() / 3) + (shoppingCartSize++ * shoppingCart.getWidth() / 8); //XCOORD
                                params.topMargin = shoppingCartLocation[1] + (shoppingCart.getHeight() / 5); //YCOORD
                                electronicsLayout.setLayoutParams(params);
                            }
                        }
                        else {
                            electronicsSelected = false;
                            preferencesSet.remove("electronics");
                            shoppingCartSize--;
                            //set a new random position for the electronics preference
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                            Random random = new Random();
                            params.leftMargin = random.nextInt(displayWidth - electronicsLayout.getMeasuredWidth()); //XCOORD
                            params.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                            electronicsLayout.setLayoutParams(params);
                            //update the electronics location
                            updateElectronicsImageLocation = new Thread(new UpdateImageLocationRunnable(electronicsLayout, electronicsImageWidth, electronicsImageHeight, displayWidth, displayHeight, false, "electronics"));
                            updateElectronicsImageLocation.start();
                        }

                        //check if there are 3 preferences selected
                        if(shoppingCartSize == 3)
                        {
                            submitButton.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            submitButton.setVisibility(View.GONE);
                        }
                    }
                });

                //set the initial location of the clothes preference
                RelativeLayout.LayoutParams paramsClothes = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                Random randomClothes = new Random();
                paramsClothes.leftMargin = randomClothes.nextInt(displayWidth - clothesLayout.getMeasuredWidth()); //XCOORD
                paramsClothes.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                clothesLayout.setLayoutParams(paramsClothes);
                //update the clothes location
                updateClothesImageLocation = new Thread(new UpdateImageLocationRunnable(clothesLayout, clothesImageWidth, clothesImageHeight, displayWidth, displayHeight, false, "clothes"));
                updateClothesImageLocation.start();
                clothesLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!clothesSelected) {
                            clothesSelected = true;
                            preferencesSet.add("clothes");
                            updateClothesImageLocation.interrupt();
                            synchronized (clothesLayout)
                            {
                                int[] shoppingCartLocation = new int[2];
                                shoppingCart.getLocationOnScreen(shoppingCartLocation);
                                //add the clothes preference in the shopping cart
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                                params.leftMargin = shoppingCartLocation[0] + (shoppingCart.getWidth() / 3) + (shoppingCartSize++ * shoppingCart.getWidth() / 8); //XCOORD
                                params.topMargin = shoppingCartLocation[1] + (shoppingCart.getHeight() / 5); //YCOORD
                                clothesLayout.setLayoutParams(params);
                            }
                        }
                        else {
                            clothesSelected = false;
                            preferencesSet.remove("clothes");
                            shoppingCartSize--;
                            //set a new random position for the clothes preference
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                            Random random = new Random();
                            params.leftMargin = random.nextInt(displayWidth - clothesLayout.getMeasuredWidth()); //XCOORD
                            params.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                            clothesLayout.setLayoutParams(params);
                            //update the clothes location
                            updateClothesImageLocation = new Thread(new UpdateImageLocationRunnable(clothesLayout, clothesImageWidth, clothesImageHeight, displayWidth, displayHeight, false, "clothes"));
                            updateClothesImageLocation.start();
                        }

                        //check if there are 3 preferences selected
                        if(shoppingCartSize == 3)
                        {
                            submitButton.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            submitButton.setVisibility(View.GONE);
                        }
                    }
                });

                //set the initial location of the food preference
                RelativeLayout.LayoutParams paramsFood = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                Random randomFood = new Random();
                paramsFood.leftMargin = randomFood.nextInt(displayWidth - foodLayout.getMeasuredWidth()); //XCOORD
                paramsFood.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                foodLayout.setLayoutParams(paramsFood);
                //update the food location
                updateFoodImageLocation = new Thread(new UpdateImageLocationRunnable(foodLayout, foodImageWidth, foodImageHeight, displayWidth, displayHeight, false, "food"));
                updateFoodImageLocation.start();
                foodLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!foodSelected) {
                            foodSelected = true;
                            preferencesSet.add("food");
                            updateFoodImageLocation.interrupt();
                            synchronized (foodLayout)
                            {
                                int[] shoppingCartLocation = new int[2];
                                shoppingCart.getLocationOnScreen(shoppingCartLocation);
                                //add the food preference in the shopping cart
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                                params.leftMargin = shoppingCartLocation[0] + (shoppingCart.getWidth() / 3) + (shoppingCartSize++ * shoppingCart.getWidth() / 8); //XCOORD
                                params.topMargin = shoppingCartLocation[1] + (shoppingCart.getHeight() / 5); //YCOORD
                                foodLayout.setLayoutParams(params);
                            }
                        }
                        else {
                            foodSelected = false;
                            preferencesSet.remove("food");
                            shoppingCartSize--;
                            //set a new random position for the food preference
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                            Random random = new Random();
                            params.leftMargin = random.nextInt(displayWidth - foodLayout.getMeasuredWidth()); //XCOORD
                            params.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                            foodLayout.setLayoutParams(params);
                            //update the food location
                            updateFoodImageLocation = new Thread(new UpdateImageLocationRunnable(foodLayout, foodImageWidth, foodImageHeight, displayWidth, displayHeight, false, "food"));
                            updateFoodImageLocation.start();
                        }

                        //check if there are 3 preferences selected
                        if(shoppingCartSize == 3)
                        {
                            submitButton.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            submitButton.setVisibility(View.GONE);
                        }
                    }
                });

                //set the initial location of the cofee preference
                RelativeLayout.LayoutParams paramsCofee = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                Random randomCofee = new Random();
                paramsCofee.leftMargin = randomCofee.nextInt(displayWidth - cofeeLayout.getMeasuredWidth()); //XCOORD
                paramsCofee.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                cofeeLayout.setLayoutParams(paramsCofee);
                //update the cofee location
                updateCofeeImageLocation = new Thread(new UpdateImageLocationRunnable(cofeeLayout, cofeeImageWidth, cofeeImageHeight, displayWidth, displayHeight, false, "cofee"));
                updateCofeeImageLocation.start();
                cofeeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!cofeeSelected) {
                            cofeeSelected = true;
                            preferencesSet.add("cofee");
                            updateCofeeImageLocation.interrupt();
                            synchronized (cofeeLayout)
                            {
                                int[] shoppingCartLocation = new int[2];
                                shoppingCart.getLocationOnScreen(shoppingCartLocation);
                                //add the cofee preference in the shopping cart
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                                params.leftMargin = shoppingCartLocation[0] + (shoppingCart.getWidth() / 3) + (shoppingCartSize++ * shoppingCart.getWidth() / 8); //XCOORD
                                params.topMargin = shoppingCartLocation[1] + (shoppingCart.getHeight() / 5); //YCOORD
                                cofeeLayout.setLayoutParams(params);
                            }
                        }
                        else {
                            cofeeSelected = false;
                            preferencesSet.remove("cofee");
                            shoppingCartSize--;
                            //set a new random position for the cofee preference
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                            Random random = new Random();
                            params.leftMargin = random.nextInt(displayWidth - cofeeLayout.getMeasuredWidth()); //XCOORD
                            params.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                            cofeeLayout.setLayoutParams(params);
                            //update the cofee location
                            updateCofeeImageLocation = new Thread(new UpdateImageLocationRunnable(cofeeLayout, cofeeImageWidth, cofeeImageHeight, displayWidth, displayHeight, false, "cofee"));
                            updateCofeeImageLocation.start();
                        }

                        //check if there are 3 preferences selected
                        if(shoppingCartSize == 3)
                        {
                            submitButton.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            submitButton.setVisibility(View.GONE);
                        }
                    }
                });

                //set the initial location of the sports preference
                RelativeLayout.LayoutParams paramsSports = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                Random randomSports = new Random();
                paramsSports.leftMargin = randomSports.nextInt(displayWidth - sportsLayout.getMeasuredWidth()); //XCOORD
                paramsSports.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                sportsLayout.setLayoutParams(paramsSports);
                //update the sports location
                updateSportsImageLocation = new Thread(new UpdateImageLocationRunnable(sportsLayout, sportsImageWidth, sportsImageHeight, displayWidth, displayHeight, false, "sports"));
                updateSportsImageLocation.start();
                sportsLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!sportsSelected) {
                            sportsSelected = true;
                            preferencesSet.add("sports");
                            updateSportsImageLocation.interrupt();
                            synchronized (sportsLayout)
                            {
                                int[] shoppingCartLocation = new int[2];
                                shoppingCart.getLocationOnScreen(shoppingCartLocation);
                                //add the cofee preference in the shopping cart
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                                params.leftMargin = shoppingCartLocation[0] + (shoppingCart.getWidth() / 3) + (shoppingCartSize++ * shoppingCart.getWidth() / 8); //XCOORD
                                params.topMargin = shoppingCartLocation[1] + (shoppingCart.getHeight() / 5); //YCOORD
                                sportsLayout.setLayoutParams(params);
                            }
                        }
                        else {
                            sportsSelected = false;
                            preferencesSet.remove("sports");
                            shoppingCartSize--;
                            //set a new random position for the sports preference
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                            Random random = new Random();
                            params.leftMargin = random.nextInt(displayWidth - sportsLayout.getMeasuredWidth()); //XCOORD
                            params.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                            sportsLayout.setLayoutParams(params);
                            //update the sports location
                            updateSportsImageLocation = new Thread(new UpdateImageLocationRunnable(sportsLayout, sportsImageWidth, sportsImageHeight, displayWidth, displayHeight, false, "sports"));
                            updateSportsImageLocation.start();
                        }

                        //check if there are 3 preferences selected
                        if(shoppingCartSize == 3)
                        {
                            submitButton.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            submitButton.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        //submit button listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> preferences = new ArrayList<>();
                for(String preference : preferencesSet)
                {
                    preferences.add(preference);
                }
                UserPreferences userPreferences = new UserPreferences(userData.getUserId(), preferences);
                //start a thread that sends the data to the server
                Thread sendPreferencesThread = new Thread(new SendPreferencesRunnable(userPreferences, objectOutputStream));
                sendPreferencesThread.start();
                //start the main activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("UserData", userData);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
    public void initUI()
    {
        preferencesRelativeLayout = findViewById(R.id.preferencesLayout);

        //find shoes data
        shoesImageView = findViewById(R.id.shoes);
        shoesTextView = findViewById(R.id.shoesText);
        shoesLayout = findViewById(R.id.shoesLayout);


        //find electronics data
        electronicsImageView = findViewById(R.id.eletronics);
        electronicsTextView = findViewById(R.id.electronicsText);
        electronicsLayout = findViewById(R.id.electronicsLayout);

        //find clothes data
        clothesImageView = findViewById(R.id.clothes);
        clothesTextView = findViewById(R.id.clothesText);
        clothesLayout = findViewById(R.id.clothesLayout);

        //find food data
        foodImageView = findViewById(R.id.food);
        foodTextView = findViewById(R.id.foodText);
        foodLayout = findViewById(R.id.foodLayout);

        //find cofee data
        cofeeImageView = findViewById(R.id.cofee);
        cofeeTextView = findViewById(R.id.cofeeText);
        cofeeLayout = findViewById(R.id.cofeeLayout);

        //find sports data
        sportsImageView = findViewById(R.id.sports);
        sportsTextView = findViewById(R.id.sportsText);
        sportsLayout = findViewById(R.id.sportsLayout);

        //find shopping cart data
        shoppingCart = findViewById(R.id.shoppingCart);

        //find the submit button
        submitButton = findViewById(R.id.submitButton);
    }
}
